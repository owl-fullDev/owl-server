package com.owl.owlserver.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.owl.owlserver.model.Refund;
import com.owl.owlserver.model.Sale;
import com.owl.owlserver.model.SaleDetail;
import com.owl.owlserver.model.StoreQuantity;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class RefundService {

    //injecting repositories for database access
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
    public void newRefund(JsonNode wholeJSON) {

        Sale sale = saleRepository.findById(wholeJSON.get("saleId").asInt()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "No sale with Id of: "+wholeJSON.get("saleId").asInt()+" found"));
        String remarks = wholeJSON.get("remarks").asText();
        JsonNode saleDetailList = wholeJSON.get("products");

        List<String> productIds = new ArrayList<>();
        for (SaleDetail saleDetail:sale.getSaleDetailList()){
            productIds.add(saleDetail.getProduct().getProductId());
        }

        List<StoreQuantity> storeQuantityList = storeQuantityRepository.findAllByStore_StoreIdAndProduct_ProductIdIn(sale.getStore().getStoreId(), productIds);

        for (int c = 0; c<sale.getSaleDetailList().size(); c++){
            if (storeQuantityList.get(c).getProduct().getProductId().equals(saleDetailList.get(c).get("productId").asText())){
                if (saleDetailList.get(c).get("isReturned").asBoolean()){
                    storeQuantityList.get(c).setInstoreQuantity(storeQuantityList.get(c).getInstoreQuantity()+saleDetailList.get(c).get("quantity").asInt());
                }
            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mismatch between received saleDetailsList and stored");
            }
        }

        storeQuantityRepository.saveAll(storeQuantityList);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("saleId", sale.getSaleId());
        jsonNode.put("employeeId", sale.getEmployeeId());
        jsonNode.put("grandTotal", sale.getGrandTotal());
        jsonNode.put("initialDepositDate", sale.getInitialDepositDate().toString());
        jsonNode.put("initialDepositType", sale.getInitialDepositType());
        jsonNode.put("initialDepositAmount", sale.getInitialDepositAmount());
        jsonNode.put("fullyPaid", sale.isFullyPaid());

        if (sale.getFinalDepositDate()!=null) {
            jsonNode.put("finalDepositDate", sale.getFinalDepositDate().toString());
            jsonNode.put("finalDepositType", sale.getFinalDepositType());
            jsonNode.put("finalDepositAmount", sale.getFinalDepositAmount());
        }

        if (sale.getPickupDate()!=null) {
            jsonNode.put("pickupDate", sale.getPickupDate().toString());
        }

        if (sale.getPromotion()!=null){
            ObjectNode promotionNode = jsonNode.putObject("promotion");
            promotionNode.put("promotionId", sale.getPromotion().getPromotionId());
            promotionNode.put("promotionName", sale.getPromotion().getPromotionName());
        }

        ObjectNode storeNode = jsonNode.putObject("store");
        storeNode.put("storeId", sale.getStore().getStoreId());
        storeNode.put("storeName", sale.getStore().getName());

        ArrayNode arrayNode = objectMapper.valueToTree(sale.getSaleDetailList());
        jsonNode.set("saleDetails",arrayNode);

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
