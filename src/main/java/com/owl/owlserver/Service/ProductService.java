package com.owl.owlserver.Service;

import com.owl.owlserver.DTO.Deserialize.NewProducts.NewFrameColours;
import com.owl.owlserver.DTO.Deserialize.NewProducts.NewFrames;
import com.owl.owlserver.DTO.Deserialize.NewProducts.NewLenses;
import com.owl.owlserver.DTO.Deserialize.NewProducts.NewLensesPrescription;
import com.owl.owlserver.model.Product;
import com.owl.owlserver.model.Products.*;
import com.owl.owlserver.repositories.*;
import com.owl.owlserver.repositories.Products.*;
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

        StringBuilder newFrameIdBuilder = new StringBuilder();

        //frames start with 11
        newFrameIdBuilder.append("11");

        //Frame brand
        newFrameIdBuilder.append(newFrames.getFrameBrandId());
        String brand = null;
        if (newFrames.getFrameBrandId() == 11) {
            brand = "OWL ";
        }

        //Frame category
        FrameCategory frameCategory = frameCategoryRepository.findById(newFrames.getFrameCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No frame category with ID of: " + newFrames.getFrameCategoryId() + " exists!"));
        newFrameIdBuilder.append(newFrames.getFrameCategoryId());

        FrameModel frameModel = frameModelRepository.findAllByFrameCategory_FrameCategoryIdAndFrameCategoryModelId(newFrames.getFrameCategoryId(), newFrames.getFrameModelId());
        //new Frame model
        if (frameModel == null) {
            frameModel = new FrameModel(frameCategory, newFrames.getFrameModelId(), newFrames.getSupplierModelCode());
            frameModelRepository.save(frameModel);
        }
        newFrameIdBuilder.append(frameModel.getFrameCategoryModelId());

        //Material
        FrameMaterial frameMaterial = frameMaterialRepository.findById(newFrames.getFrameMaterialId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No frame material with ID of: " + newFrames.getFrameMaterialId() + " exists!"));
        newFrameIdBuilder.append(newFrames.getFrameMaterialId());

        //Colors
        String newFrameId = newFrameIdBuilder.toString();
        List<Product> newProductsList = new ArrayList<>();
        StringBuilder newProductNameBuilder = new StringBuilder();
        for (NewFrameColours newFrameColour : newFrames.getNewFrameColoursList()) {
            Product newProduct = new Product(newFrameId.concat(String.valueOf(newFrameColour.getFrameColourId())));
            newProduct.setProductPrice(newFrames.getFramePrice());
            newProduct.setProductName(newProductNameBuilder.append(brand).append(frameCategory.getCategoryName()).append(" ").append(frameModel.getFrameCategoryModelId()).append(frameMaterial.getFrameMaterial()).toString());
            newProduct.setSupplierCode(newFrames.getSupplierModelCode() + newFrameColour.getSupplierColourCode());
            newProductsList.add(newProduct);
        }

        productRepository.saveAll(newProductsList);
    }


    @Transactional
    public void newLens(NewLenses newLenses) {

        StringBuilder newLensIdBuilder = new StringBuilder();

        //lenses start with 11
        newLensIdBuilder.append("22");

        //Lens category
        LensCategory lensCategory = lensCategoryRepository.findById(newLenses.getLensCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No lens category with ID of: " + newLenses.getLensCategoryId() + " exists!"));
        newLensIdBuilder.append(lensCategory.getLensCategoryId());
        if (lensCategory.getLensCategoryId()<10){
            String lensCategoryId = "0"+lensCategory.getLensCategoryId();
            newLensIdBuilder.append(lensCategoryId);
        }

        //Lens thickness
        newLensIdBuilder.append(newLenses.getLensThicknessId());

        LensModel lensModel = lensModelRepository.findByLensCategory_LensCategoryIdAndLensCategoryModelId(lensCategory.getLensCategoryId(), newLenses.getLensModelId());
        //new Lens model
        if (lensModel == null) {
            lensModel = new LensModel(lensCategory, newLenses.getLensModelId(), newLenses.getLensModel());
            lensModelRepository.save(lensModel);
        }
        newLensIdBuilder.append(lensCategory.getLensCategoryId());
        if (lensModel.getLensCategoryModelId()<10){
            String lensCategoryModelId = "0"+lensModel.getLensCategoryModelId();
            newLensIdBuilder.append(lensCategoryModelId);
        }

        //prescriptions
        String newLensId = newLensIdBuilder.toString();
        List<Product> newLensList = new ArrayList<>();
        StringBuilder newLensNameBuilder = new StringBuilder();
        newLensNameBuilder.append(lensCategory.getCategoryName()).append(" ").append(1.49).append(newLenses.getLensThicknessId()).append(" ").append(lensModel.getLensModel()).append(" ");

        for (NewLensesPrescription newLensPrescription : newLenses.getNewLensesPrescriptionList()) {
            String lensPowerId = "";
            StringBuilder lensPowerIdStr = new StringBuilder();
            //lens power if positive
            if (newLensPrescription.powerPolarity){
                int lensPowerIdRaw = newLensPrescription.getPowerId();
                lensPowerId = lensPowerIdRaw +"00";
                if (lensPowerIdRaw<10) {
                    lensPowerId = "0"+ lensPowerIdRaw +"00";
                }
                lensPowerIdStr.append(" +").append(lensPowerId.charAt(0)).append(".");
                if (lensPowerId.charAt(1)=='0'){
                    lensPowerIdStr.append("00");
                }
                else if (lensPowerId.charAt(1)=='2') {
                    lensPowerIdStr.append("25");
                }
                else if (lensPowerId.charAt(1)=='5') {
                    lensPowerIdStr.append("50");
                }
                else if (lensPowerId.charAt(1)=='7') {
                    lensPowerIdStr.append("75");
                }
                lensPowerIdStr.append(" -0.00");
            }

            //if power is negative
            else {
                int lensPowerIdRaw = newLensPrescription.getPowerId();
                lensPowerId = "00"+ lensPowerIdRaw;
                if (lensPowerIdRaw<10) {
                    lensPowerId = "000"+ lensPowerIdRaw;
                }
                lensPowerIdStr.append(" +0.00 ");
                lensPowerIdStr.append("-").append(lensPowerId.charAt(0)).append(".");
                if (lensPowerId.charAt(1)=='0'){
                    lensPowerIdStr.append("00");
                }
                else if (lensPowerId.charAt(1)=='2') {
                    lensPowerIdStr.append("25");
                }
                else if (lensPowerId.charAt(1)=='5') {
                    lensPowerIdStr.append("50");
                }
                else if (lensPowerId.charAt(1)=='7') {
                    lensPowerIdStr.append("75");
                }
            }

            int startCylinderId = newLensPrescription.getCylinderStartId();
            int endCylinderId = newLensPrescription.getCylinderEndId();
            boolean additionValue;

            //no cylinder value
            if (startCylinderId==endCylinderId){
                Product newLens = new Product(newLensId.concat(lensPowerId + "00"));
                newLens.setProductPrice(newLenses.getLensPrice());
                newLens.setProductName(newLensNameBuilder.toString()+lensPowerIdStr.toString()+" 0.00");
                newLensList.add(newLens);
            }

            //with cylinder values
            else {
                if (startCylinderId % 5 == 0) {
                    additionValue = false;//+2
                } else {
                    additionValue = true;//+3
                }

                while (startCylinderId < endCylinderId) {
                    Product newLens = new Product(newLensId.concat(lensPowerId + startCylinderId));
                    newLens.setProductPrice(newLenses.getLensPrice());
                    String startCylinderIdStr = Integer.toString(startCylinderId);
                    if (startCylinderId<10){
                        startCylinderIdStr = '0'+Integer.toString(startCylinderId);
                    }
                    lensPowerIdStr.append(" ").append(startCylinderIdStr.charAt(0)).append(".");
                    if (startCylinderIdStr.charAt(1)=='0'){
                        lensPowerIdStr.append("00");
                    }
                    else if (startCylinderIdStr.charAt(1)=='2') {
                        lensPowerIdStr.append("25");
                    }
                    else if (startCylinderIdStr.charAt(1)=='5') {
                        lensPowerIdStr.append("50");
                    }
                    else if (startCylinderIdStr.charAt(1)=='7') {
                        lensPowerIdStr.append("75");
                    }
                    newLens.setProductName(newLensNameBuilder.toString()+lensPowerIdStr.toString());
                    newLensList.add(newLens);

                    if(!additionValue){
                        startCylinderId = startCylinderId+2;
                    }
                    else {
                        startCylinderId = startCylinderId+3;
                    }
                }
            }
        }

        productRepository.saveAll(newLensList);
    }
}