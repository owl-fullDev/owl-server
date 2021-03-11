package com.owl.owlserver.Service;

import com.owl.owlserver.model.Product;
import com.owl.owlserver.model.Shipment;
import com.owl.owlserver.model.ShipmentDetail;
import com.owl.owlserver.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Service
public class ShipmentService {

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

    public void persistShipment(Shipment shipment) {

        //extracts all product Ids form shipmentDetailList, steam is a for loop, foreach
        List<String> productIds = emptyIfNull(shipment.getShipmentDetailList()).stream()
                //extracts productId from shipmentDetail array
                .map(ShipmentDetail::getProductId)
                //collect appends each extracted productId into the List
                .collect(Collectors.toList());

        //validation
        List<String> validProductIds = productRepository.findProductIdByProductIdIn(productIds);



        if (productIds.size() != validProductIds.size()) {
            Set<String> result = productIds.stream()
                    .distinct()
                    .filter(id -> !validProductIds.contains(id))
                    .collect(Collectors.toSet());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.toString());

        }

        shipmentRepository.save(shipment);

        //for every shipmentDetail
        shipmentDetailRepository.saveAll(emptyIfNull(shipment.getShipmentDetailList()).stream()
                //peek means accesing the element in the array, MAP means transform/modify, peek means accesing
                .peek(shipmentDetail -> shipmentDetail.setProduct(new Product(shipmentDetail.getProductId())))
                .peek(shipmentDetail -> shipmentDetail.setShipment(shipment))
                .collect(Collectors.toList()));
    }
}
