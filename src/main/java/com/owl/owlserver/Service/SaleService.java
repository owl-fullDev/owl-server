package com.owl.owlserver.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.owl.owlserver.DTO.Deserialize.*;
import com.owl.owlserver.DTO.Serialize.HO.SaleSerializeDTO;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Service
public class SaleService {

    //injecting repositories for database access
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PromotionRepository promotionRepository;
    @Autowired
    SaleDetailRepository saleDetailRepository;
    @Autowired
    SaleRepository saleRepository;
    @Autowired
    StoreQuantityRepository storeQuantityRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    ShipmentRepository shipmentRepository;
    @Autowired
    ShipmentDetailRepository shipmentDetailRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    WarehouseQuantityRepository warehouseQuantityRepository;
    @Autowired
    SupplierRespository supplierRespository;
    @Autowired
    RefundRepository refundRepository;

    @Transactional
    public void updateSale(Sale sale, JsonNode wholeJSON) {
        LocalDateTime localPickUpTime = LocalDateTime.parse(wholeJSON.get("pickUpDate").asText(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        if (sale.isFullyPaid()) {
            sale.setPickupDate(localPickUpTime);
        }
        else {
            sale.setPickupDate(localPickUpTime);
            sale.setFinalDepositDate(localPickUpTime);
            sale.setFinalDepositType(wholeJSON.get("finalPaymentType").asText());
            if (sale.getGrandTotal()-sale.getInitialDepositAmount()-wholeJSON.get("finalPaymentAmount").asDouble()>sale.getGrandTotal()*0.01){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Final payment does not cover amount due!");
            }
            sale.setFinalDepositAmount(wholeJSON.get("finalPaymentAmount").asDouble());
        }
        saleRepository.save(sale);
    }

    @Transactional
    public List<SaleSerializeDTO> serializeSale(List<Sale> saleList) {
        if (saleList == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty sale List");
        }

        List<SaleSerializeDTO> saleSerializeDTOList = new ArrayList<>();

        for (Sale sale : saleList) {
            SaleSerializeDTO saleSerializeDTO = SaleSerializeDTO.builder()
                    .saleId(sale.getSaleId())
                    .customerName(sale.getCustomer().getFirstName() + " " + sale.getCustomer().getLastName())
                    .phoneNumber(sale.getCustomer().getPhoneNumber())
                    .employeeName(sale.getEmployee().getFirstName() + " " + sale.getEmployee().getLastname())
                    .storeName(sale.getStore().getName())
                    .grandTotal(sale.getGrandTotal())
                    .isFullyPaid(sale.isFullyPaid())
                    .initialDepositDate(sale.getInitialDepositDate().toString())
                    .initialDepositType(sale.getInitialDepositType())
                    .initialDepositAmount(sale.getInitialDepositAmount())
                    .build();

            if (sale.getPromotion() != null) {
                saleSerializeDTO.setPromotionName(sale.getPromotion().getPromotionName());
                saleSerializeDTO.setPromotionParentId(sale.getPromotionParentSaleId());
            }
            if (sale.getFinalDepositDate() != null) {
                saleSerializeDTO.setFinalDepositDate(sale.getInitialDepositDate().toString());
                saleSerializeDTO.setFinalDepositType(sale.getFinalDepositType());
                saleSerializeDTO.setFinalDepositAmount(sale.getFinalDepositAmount());
            }
            if (sale.getSaleRemarks() != null) {
                saleSerializeDTO.setSaleRemarks(sale.getSaleRemarks());
            }
            if (sale.getPickupDate() != null) {
                saleSerializeDTO.setPickupDate(sale.getPickupDate().toString());
            }

            List<SaleDetailDTO> saleDetailDTOList = new ArrayList<>();
            for (SaleDetail saleDetail : sale.getSaleDetailList()) {
                SaleDetailDTO saleDetailDTO = new SaleDetailDTO(saleDetail.getProduct().getProductName(), saleDetail.getQuantity());
                saleDetailDTOList.add(saleDetailDTO);
            }

            saleSerializeDTO.setSaleDetailDTOS(saleDetailDTOList);
            saleSerializeDTOList.add(saleSerializeDTO);
        }
        return saleSerializeDTOList;
    }

    @Transactional
    public String CSVProductSales(List<Sale> saleList) {
        if (saleList == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty sale List");
        }

        List<Store> storeList = storeRepository.findAll();

        HashMap<String,Integer> totalProductsSaleHashmap = new HashMap<>();
        HashMap<Integer,HashMap<String,Integer>> storeProductsHashmap = new HashMap<>();

        for (Sale sale : saleList) {
            int storeId = sale.getStore().getStoreId();
            if (!storeProductsHashmap.containsKey(storeId)) {
                storeProductsHashmap.put(storeId, new HashMap<>());
            }
            HashMap<String, Integer> productSaleHashmap = storeProductsHashmap.get(storeId);
            for (SaleDetail saleDetail:sale.getSaleDetailList()){
                Product product = saleDetail.getProduct();

                //to count for product sales per store
                if (!productSaleHashmap.containsKey(product.getProductId())){
                    productSaleHashmap.put(product.getProductId(),saleDetail.getQuantity());
                }
                else {
                    int quantity = productSaleHashmap.get(product.getProductId());
                    productSaleHashmap.put(product.getProductId(),quantity+saleDetail.getQuantity());
                }

                //to count total sales for all products
                if (!totalProductsSaleHashmap.containsKey(product.getProductId())){
                    totalProductsSaleHashmap.put(product.getProductId(),saleDetail.getQuantity());
                }
                else {
                    int quantity = totalProductsSaleHashmap.get(product.getProductId());
                    totalProductsSaleHashmap.put(product.getProductId(),quantity+saleDetail.getQuantity());
                }
            }
        }

        StringBuilder csvString = new StringBuilder();
        csvString.append("ProdukId,Quantitas Terjual\n");
        for (Map.Entry storeDetailsRow : storeProductsHashmap.entrySet()) {
            int storeId = (int) storeDetailsRow.getKey();
            String storeName = "";
            for (Store store : storeList) {
                if (store.getStoreId() == storeId) {
                    storeName = store.getName();
                    break;
                }
            }

            csvString.append(" - , - ").append("\n");
            csvString.append("Penjualan produk").append("Toko: ").append(storeName).append("\n");

            HashMap<String, Integer> productMap = (HashMap<String, Integer>) storeDetailsRow.getValue();
            for (Map.Entry productRow : productMap.entrySet()) {
                String productId = (String) productRow.getKey();
                int quantity = (int)productRow.getValue();
                csvString.append(productId).append(",");
                csvString.append(quantity).append("\n");
            }
        }

        csvString.append(" - , - ").append("\n");
        csvString.append("Total penjualan produk dari semua toko: ").append("\n");
        for (Map.Entry totalProductSale : totalProductsSaleHashmap.entrySet()) {
            String productId = (String) totalProductSale.getKey();
            int quantity = (int)totalProductSale.getValue();
            csvString.append(productId).append(",");
            csvString.append(quantity).append("\n");
        }
        return csvString.toString();
    }

    @Transactional
    public String CSVSaleList(List<Sale> saleList) {
        if (saleList == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty sale List");
        }

        StringBuilder csvString = new StringBuilder();
        csvString.append("saleId,Nama customer,Nama promosi,Transaksi pertama,Salesman,Toko,Pembayaran total,Bayar sekaligus,Tanggal deposit pertama,deposit pertama waktu,Tipe deposit,Jumlah deposit,Transaksi kedua tanggal,Transaksi kedua waktu,Transaksi kedua tipe,Transaksi kedua jumlah,Remarks,Tanggal pengambilan\n");
        for (Sale sale : saleList) {
            csvString.append(sale.getSaleId()).append(",");
            csvString.append(sale.getCustomer().getFirstName()).append(" ").append(sale.getCustomer().getLastName()).append(",");
            if (sale.getPromotion()!=null)
                csvString.append(sale.getPromotion().getPromotionName()).append(",");
            else
                csvString.append("-").append(",");
            csvString.append(sale.getPromotionParentSaleId()).append(",");
            csvString.append(sale.getEmployee().getFirstName()).append(" ").append(sale.getEmployee().getLastname()).append(",");
            csvString.append(sale.getStore().getName()).append(",");
            csvString.append(sale.getGrandTotal()).append(",");
            csvString.append(sale.isFullyPaid()).append(",");
            csvString.append(sale.getInitialDepositDate().toLocalDate()).append(",");
            csvString.append(sale.getInitialDepositDate().toLocalTime()).append(",");
            csvString.append(sale.getInitialDepositType()).append(",");
            csvString.append(sale.getInitialDepositAmount()).append(",");
            if (sale.getFinalDepositDate()!=null) {
                csvString.append(sale.getFinalDepositDate().toLocalDate()).append(",");
                csvString.append(sale.getFinalDepositDate().toLocalTime()).append(",");
                csvString.append(sale.getFinalDepositType()).append(",");
                csvString.append(sale.getFinalDepositAmount()).append(",");
            }
            else
                csvString.append("-").append(",");
            if(sale.getSaleRemarks()!=null)
            csvString.append(sale.getSaleRemarks()).append("\n");
            else
                csvString.append("-").append(",");
        }

        return csvString.toString();
    }

    @Transactional
    public String CSVTotalSaleRevenue(List<Sale> saleList) {
        if (saleList == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty sale List");
        }

        StringBuilder csvString = new StringBuilder();
        HashMap<LocalDate, HashMap<Integer, Double>> saleHash = new HashMap<>();

        for (Sale sale : saleList) {
            int promotionId = 0;
            if (sale.getPromotion() != null) {
                promotionId = sale.getPromotion().getPromotionId();
            }

            saleHash.putIfAbsent(sale.getInitialDepositDate().toLocalDate(), new HashMap<>());
            HashMap<Integer, Double> totalDayRevenueHash = saleHash.get(sale.getInitialDepositDate().toLocalDate());
            if (!totalDayRevenueHash.containsKey(promotionId)) {
                totalDayRevenueHash.put(promotionId, sale.getGrandTotal());
            }
            else {
                totalDayRevenueHash.put(promotionId, totalDayRevenueHash.get(promotionId) + sale.getGrandTotal());
            }
        }

        csvString.append(" ,");
        List<Promotion> promotionList = promotionRepository.findAll();
        for (Promotion promotion:promotionList) {
            csvString.append(promotion.getPromotionId()).append(" : ").append(promotion.getPromotionName()).append(",");
        }
        csvString.append("0 : No Promotion");
        csvString.append("\n");

        for (Map.Entry dates : saleHash.entrySet()) {
            HashMap<Integer,Double> totalSalesByPromotions = (HashMap<Integer, Double>) dates.getValue();
            csvString.append(dates.getKey().toString()).append(",");
            for (Promotion promotion:promotionList) {
                if (totalSalesByPromotions.containsKey(promotion.getPromotionId())) {
                    csvString.append(totalSalesByPromotions.get(promotion.getPromotionId()));
                }
                else {
                    csvString.append("0");
                }
                csvString.append(",");
            }
            csvString.append(totalSalesByPromotions.get(0));
            csvString.append("\n");
        }
        return csvString.toString();
    }

    @Transactional
    public int newSale(NewSaleDTO newSaleDTO) {
        Customer customer;
        if (newSaleDTO.getCustomerId() == null) {
            customer = new Customer();
            try {
                BeanUtils.copyProperties(customer, newSaleDTO.getCustomer());
                customerRepository.save(customer);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "error with instantiating customer from customerDTO!");
            }
        } else {
            customer = customerRepository.findById(newSaleDTO.getCustomerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No customer with specified ID exist"));
            CustomerDTO customerDTO = newSaleDTO.getCustomer();

            customer.setEmail(customerDTO.getEmail());
            customer.setPhoneNumber(customerDTO.getPhoneNumber());

            customer.setLeftEyeAdd(customerDTO.getLeftEyeAdd());
            customer.setLeftEyeAxis(customerDTO.getLeftEyeAxis());
            customer.setLeftEyeCylinder(customerDTO.getLeftEyeCylinder());
            customer.setLeftEyePrism(customerDTO.getLeftEyePrism());
            customer.setLeftEyeSphere(customerDTO.getLeftEyeSphere());

            customer.setRightEyeAdd(customerDTO.getRightEyeAdd());
            customer.setRightEyeAxis(customerDTO.getRightEyeAxis());
            customer.setRightEyeCylinder(customerDTO.getRightEyeCylinder());
            customer.setRightEyePrism(customerDTO.getRightEyePrism());
            customer.setRightEyeSphere(customerDTO.getRightEyeSphere());
        }

        SaleDTO saleDTO = newSaleDTO.getSale();
        validateSaleDTO(saleDTO);

        Promotion promotion = null;
        if (saleDTO.getPromotionId() != null) {
            promotion = promotionRepository.findById(saleDTO.getPromotionId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No promotion with specified ID exist"));
        }
        Store store = storeRepository.findById(saleDTO.getStoreId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No store with specified ID exist"));
        Employee employee = employeeRepository.findById(saleDTO.getEmployeeId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No employee with specified ID exists"));
        boolean fullyPaid = !(saleDTO.getGrandTotal() > saleDTO.getInitialDepositAmount());

        Sale newSale = new Sale();
        newSale.setStore(store);
        newSale.setCustomer(customer);
        newSale.setEmployee(employee);
        newSale.setGrandTotal(saleDTO.getGrandTotal());
        newSale.setInitialDepositAmount(saleDTO.getInitialDepositAmount());
        newSale.setInitialDepositType(saleDTO.getInitialDepositType());
        newSale.setInitialDepositDate(LocalDateTime.now());
        newSale.setFullyPaid(fullyPaid);
        newSale.setSaleRemarks(saleDTO.getSaleRemarks());

        newSale.setPromotion(promotion);
        if (saleDTO.getPromotionParentSaleId() != null) {
            newSale.setPromotionParentSaleId(saleDTO.getPromotionParentSaleId());
            if (saleDTO.getPromotionParentSaleId() > 0) {
                Sale parentSale = saleRepository.findById(saleDTO.getPromotionParentSaleId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No parent sale with specified ID exists"));
                parentSale.setPromotionParentSaleId(0);
                saleRepository.save(parentSale);
            }
        }

        customer.addSale(newSale);
        saleRepository.save(newSale);
        customerRepository.save(customer);

        List<SaleDetailDTO> saleDetailDTOList = saleDTO.getSaleDetailDTOS();
        //extracts all product Ids form saleDetails, stream is a for loop, foreach
        List<String> productIds = emptyIfNull(saleDetailDTOList).stream()
                //extracts productId from saleDetails array
                .map(SaleDetailDTO::getProductId)
                //collect appends each extracted productId into the List
                .collect(Collectors.toList());

        //validation for product IDs
        List<String> validProductIds = productRepository.findProductIdByProductIdIn(productIds);
        List<Product> productList = productRepository.findAllById(validProductIds);

        if (productIds.size() != validProductIds.size()) {
            Set<String> result = productIds.stream()
                    .distinct()
                    .filter(id -> !validProductIds.contains(id))
                    .collect(Collectors.toSet());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The following product IDs do not exist!: [" + result.toString() + "]");
        }

        List<StoreQuantity> storeQuantityList = storeQuantityRepository.findAllByStore_StoreIdAndProduct_ProductIdIn(newSale.getStore().getStoreId(), validProductIds);
        List<SaleDetail> saleDetailList = newSale.getSaleDetailList();

        if (storeQuantityList.size() != saleDetailDTOList.size()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mismatch between store quantity list returned and saleDetail list");
        }

        for (int c = 0; c < storeQuantityList.size(); c++) {
            String saleDetailProductId = saleDetailDTOList.get(c).getProductId();
            String storeQuantityProductId = storeQuantityList.get(c).getProduct().getProductId();
            if (!(saleDetailProductId.equals(storeQuantityProductId))) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mismatch between store quantity list returned and saleDetail list " + saleDetailProductId + " and " + storeQuantityProductId);
            }
            int available = storeQuantityList.get(c).getInstoreQuantity();
            int requested = saleDetailDTOList.get(c).getQuantity();
            if (available < requested) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough product in stock in store!, Requested quantity: " + requested + ", Available: " + available);
            }
            storeQuantityList.get(c).setInstoreQuantity(available - requested);
            SaleDetail newSaleDetail = new SaleDetail(newSale, productList.get(c), saleDetailDTOList.get(c).getQuantity());
            saleDetailList.add(newSaleDetail);
        }

        saleDetailRepository.saveAll(saleDetailList);
        storeQuantityRepository.saveAll(storeQuantityList);
        return newSale.getSaleId();
    }

    private void validateSaleDTO(SaleDTO saleDTO) {
        if (saleDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sale is empty!");
        }
        if (saleDTO.getGrandTotal() == null || saleDTO.getInitialDepositAmount() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grand total or initial deposit is null!");
        }
    }

    @Transactional
    public void newRefund(JsonNode wholeJSON) {

        Sale sale = saleRepository.findById(wholeJSON.get("saleId").asInt()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No sale with Id of: " + wholeJSON.get("saleId").asInt() + " found"));
        String remarks = wholeJSON.get("remarks").asText();
        JsonNode saleDetailList = wholeJSON.get("refundedProducts");

        List<String> productIds = new ArrayList<>();
        for (SaleDetail saleDetail : sale.getSaleDetailList()) {
            productIds.add(saleDetail.getProduct().getProductId());
        }

        List<StoreQuantity> storeQuantityList = storeQuantityRepository.findAllByStore_StoreIdAndProduct_ProductIdIn(sale.getStore().getStoreId(), productIds);

        for (int c = 0; c < sale.getSaleDetailList().size(); c++) {
            if (storeQuantityList.get(c).getProduct().getProductId().equals(saleDetailList.get(c).get("product").get("productId").asText())) {
                if (saleDetailList.get(c).get("isReturned").asBoolean()) {
                    storeQuantityList.get(c).setInstoreQuantity(storeQuantityList.get(c).getInstoreQuantity() + saleDetailList.get(c).get("quantity").asInt());
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mismatch between received saleDetailsList and stored");
            }
        }

        storeQuantityRepository.saveAll(storeQuantityList);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("saleId", sale.getSaleId());
        jsonNode.put("employeeId", sale.getEmployee().getEmployeeId());
        jsonNode.put("grandTotal", sale.getGrandTotal());
        jsonNode.put("initialDepositDate", sale.getInitialDepositDate().toString());
        jsonNode.put("initialDepositType", sale.getInitialDepositType());
        jsonNode.put("initialDepositAmount", sale.getInitialDepositAmount());
        jsonNode.put("fullyPaid", sale.isFullyPaid());

        if (sale.getFinalDepositDate() != null) {
            jsonNode.put("finalDepositDate", sale.getFinalDepositDate().toString());
            jsonNode.put("finalDepositType", sale.getFinalDepositType());
            jsonNode.put("finalDepositAmount", sale.getFinalDepositAmount());
        }

        if (sale.getPickupDate() != null) {
            jsonNode.put("pickupDate", sale.getPickupDate().toString());
        }

        if (sale.getPromotion() != null) {
            ObjectNode promotionNode = jsonNode.putObject("promotion");
            promotionNode.put("promotionId", sale.getPromotion().getPromotionId());
            promotionNode.put("promotionName", sale.getPromotion().getPromotionName());
        }

        ObjectNode storeNode = jsonNode.putObject("store");
        storeNode.put("storeId", sale.getStore().getStoreId());
        storeNode.put("storeName", sale.getStore().getName());

        ArrayNode arrayNode = objectMapper.valueToTree(sale.getSaleDetailList());
        jsonNode.set("saleDetails", arrayNode);

        Refund newRefund = new Refund();
        newRefund.setRefundDetails(jsonNode.toString());
        newRefund.setRemarks(remarks);
        newRefund.setRefundDate(LocalDateTime.now());

        refundRepository.save(newRefund);
        List<SaleDetail> saleDetailList2 = sale.getSaleDetailList();
        saleDetailRepository.deleteAll(saleDetailList2);
        saleRepository.deleteById(sale.getSaleId());
    }
}
