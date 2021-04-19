package com.owl.owlserver.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.owl.owlserver.DTO.Deserialize.NewFrameColours;
import com.owl.owlserver.DTO.Deserialize.NewFrames;
import com.owl.owlserver.model.Product;
import com.owl.owlserver.model.Products.FrameCategory;
import com.owl.owlserver.model.Products.FrameMaterial;
import com.owl.owlserver.model.Products.FrameModel;
import com.owl.owlserver.repositories.*;
import com.owl.owlserver.repositories.Frame.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
public class ProductService {

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
    @Autowired
    FrameCategoryRepository frameCategoryRepository;
    @Autowired
    FrameModelRepository frameModelRepository;
    @Autowired
    FrameColourRepository frameColourRepository;
    @Autowired
    FrameMaterialRepository frameMaterialRepository;
    @Autowired
    LensCategoryRepository lensCategoryRepository;
    @Autowired
    LensModelRepository lensModelRepository;

    @Transactional
    public void newFrame(NewFrames newFrames) {

        FrameModel frameModel = frameModelRepository.findAllByFrameCategory_FrameCategoryIdAndAndFrameCategoryModelId(newFrames.getFrameCategoryId(),newFrames.getFrameModelId());

        //new Frame model
        if (frameModel==null){
            StringBuilder newProductIdBuilder = new StringBuilder();
            FrameCategory frameCategory = frameCategoryRepository.findById(newFrames.getFrameCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No frame category with ID of: " + newFrames.getFrameCategoryId() + " exists!"));
            FrameModel newFrameModel = new FrameModel(frameCategory, newFrames.getFrameModelId(), newFrames.getSupplierModelCode());

            //frames start with 11
            newProductIdBuilder.append("11");

            //Frame brand
            newProductIdBuilder.append(newFrames.getFrameBrandId());
            String brand = null;
            if (newFrames.getFrameBrandId()==11){
                brand = "OWL ";
            }

            //Frame category
            newProductIdBuilder.append(newFrames.getFrameCategoryId());

            //Frame model
            newProductIdBuilder.append(newFrameModel.getFrameCategoryModelId());
            FrameMaterial frameMaterial = frameMaterialRepository.findById(newFrames.getFrameMaterialId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No frame material with ID of: " + newFrames.getFrameMaterialId() + " exists!"));

            //Material
            newProductIdBuilder.append(newFrames.getFrameMaterialId());

            //Colors
            String newProductId = newProductIdBuilder.toString();
            List<Product> newProductsList = new ArrayList<>();
            StringBuilder newProductNameBuilder = new StringBuilder();
            for (NewFrameColours newFrameColour : newFrames.getNewFrameColoursList()){
                Product newProduct = new Product(newProductId.concat(String.valueOf(newFrameColour.getFrameColourId())));
                newProduct.setProductPrice(newFrames.getFramePrice());
                newProduct.setProductName(newProductNameBuilder.append(brand).append(frameCategory.getCategoryName()).append(" ").append(newFrameModel.getFrameCategoryModelId()).append(frameMaterial.getFrameMaterial()).toString());
                newProduct.setSupplierCode(newFrames.getSupplierModelCode()+newFrameColour.getSupplierColourCode());
                newProductsList.add(newProduct);
            }

            productRepository.saveAll(newProductsList);

        }
    }

}