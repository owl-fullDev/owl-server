package com.owl.owlserver.Service;

import com.owl.owlserver.DTO.*;
import com.owl.owlserver.model.*;
import com.owl.owlserver.repositories.*;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    @Transactional
    public List<SaleSerializeDTO> serializeSale(List<Sale> saleList){
        if (saleList==null){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty sale List");
        }

        List<SaleSerializeDTO> saleSerializeDTOList = new ArrayList<>();

        for (Sale sale:saleList){
            SaleSerializeDTO saleSerializeDTO = SaleSerializeDTO.builder()
                    .saleId(sale.getSaleId())
                    .customerName(sale.getCustomer().getFirstName()+" "+sale.getCustomer().getLastName())
                    .employeeName(sale.getEmployee().getFirstName()+" "+sale.getEmployee().getLastname())
                    .storeName(sale.getStore().getName())
                    .grandTotal(sale.getGrandTotal())
                    .isFullyPaid(sale.isFullyPaid())
                    .initialDepositDate(sale.getInitialDepositDate().toString())
                    .initialDepositType(sale.getInitialDepositType())
                    .initialDepositAmount(sale.getInitialDepositAmount())
                    .build();

            if (sale.getPromotion()!=null){
                saleSerializeDTO.setPromotionName(sale.getPromotion().getPromotionName());
                saleSerializeDTO.setPromotionParentId(sale.getPromotionParentSaleId());
            }
            if (sale.getFinalDepositDate()!=null) {
                saleSerializeDTO.setFinalDepositDate(sale.getInitialDepositDate().toString());
                saleSerializeDTO.setFinalDepositType(sale.getFinalDepositType());
                saleSerializeDTO.setFinalDepositAmount(sale.getFinalDepositAmount());
            }
            if (sale.getSaleRemarks()!=null){
                saleSerializeDTO.setSaleRemarks(sale.getSaleRemarks());
            }
            if (sale.getPickupDate()!=null){
                saleSerializeDTO.setPickupDate(sale.getPickupDate().toString());
            }

            List<SaleDetailDTO> saleDetailDTOList = new ArrayList<>();
            for (SaleDetail saleDetail:sale.getSaleDetailList()){
                SaleDetailDTO saleDetailDTO = new SaleDetailDTO(saleDetail.getProduct().getProductName(),saleDetail.getQuantity());
                saleDetailDTOList.add(saleDetailDTO);
            }

            saleSerializeDTO.setSaleDetailDTOS(saleDetailDTOList);
            saleSerializeDTOList.add(saleSerializeDTO);
        }

        return saleSerializeDTOList;
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
        }
        else {
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
        if (saleDTO.getPromotionId()!=null) {
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
        if (saleDTO.getPromotionParentSaleId()!=null) {
            newSale.setPromotionParentSaleId(saleDTO.getPromotionParentSaleId());
            if (saleDTO.getPromotionParentSaleId() > 0) {
                Sale parentSale = saleRepository.findById(saleDTO.getPromotionParentSaleId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No parent sale with specified ID exists"));
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

        if (storeQuantityList.size()!=saleDetailDTOList.size()){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mismatch between store quantity list returned and saleDetail list");
        }

        for (int c = 0; c < storeQuantityList.size(); c++) {
            String saleDetailProductId = saleDetailDTOList.get(c).getProductId();
            String storeQuantityProductId = storeQuantityList.get(c).getProduct().getProductId();
            if (!(saleDetailProductId.equals(storeQuantityProductId))){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mismatch between store quantity list returned and saleDetail list "+ saleDetailProductId +" and "+storeQuantityProductId);
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
        if(saleDTO==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sale is empty!");
        }
        if (saleDTO.getGrandTotal()==null||saleDTO.getInitialDepositAmount()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grand total or initial deposit is null!");
        }
    }
}
