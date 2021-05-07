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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ProductService {

    //injecting repositories for database access
    @Autowired
    ProductRepository productRepository;
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

        //Frame brand
        if (!((newFrames.getBrandId() == 11)||(newFrames.getBrandId() == 22))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown frame brand Id");
        }
        newFrameIdBuilder.append(newFrames.getBrandId());

        //Supplier
        if (newFrames.getSupplierId()<10){
            newFrameIdBuilder.append('0');
        }
        newFrameIdBuilder.append(newFrames.getSupplierId());

        //Frame category
        FrameCategory frameCategory = frameCategoryRepository.findById(newFrames.getCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No frame category with ID of: " + newFrames.getCategoryId() + " exists!"));
        if (frameCategory.getFrameCategoryId() < 10) {
            newFrameIdBuilder.append('0');
        }
        newFrameIdBuilder.append(newFrames.getCategoryId());

        //Frame model
        FrameModel frameModel = frameModelRepository.findAllByFrameCategory_FrameCategoryIdAndFrameCategoryModelId(newFrames.getCategoryId(), newFrames.getModelId());
        //new model
        if (frameModel == null) {
            if (newFrames.getModelId()>=9999){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Model in category ID limit reached");
            }
            frameModel = new FrameModel(frameCategory, newFrames.getModelId(), newFrames.getSupplierModelCode());
            frameModelRepository.save(frameModel);
        }
        if (newFrames.getModelId()<1000){
            newFrameIdBuilder.append("0");
            if (newFrames.getModelId()<100){
                newFrameIdBuilder.append("0");
                if (newFrames.getModelId()<10){
                    newFrameIdBuilder.append("0");
                }
            }
        }
        newFrameIdBuilder.append(frameModel.getFrameCategoryModelId());

        //Material
        FrameMaterial frameMaterial = frameMaterialRepository.findById(newFrames.getMaterialId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No frame material with ID of: " + newFrames.getMaterialId() + " exists!"));
        if (frameMaterial.getFrameMaterialId() < 10) {
            newFrameIdBuilder.append('0');
        }
        newFrameIdBuilder.append(newFrames.getMaterialId());

        //Colors
        List<FrameColour> frameColourList = frameColourRepository.findAll();
        String newFrameName = frameCategory.getCategoryName()+" "+frameModel.getFrameModel()+" ";
        String newFrameId = newFrameIdBuilder.toString();
        List<String> newFrameIdList = new ArrayList<>();
        List<Product> newProductsList = new ArrayList<>();
        for (NewFrameColours newFrameColour : newFrames.getNewFrameColoursList()) {

            FrameColour frameColour = frameColourList.stream().filter(frameColour2 -> newFrameColour.getColourId()==frameColour2.getFrameColourId()).findFirst().orElse(null);
            if (frameColour==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Colour Id not recognized");
            }

            String colourId;
            if (newFrameColour.getColourId()<10){
                colourId = "0"+newFrameColour.getColourId();
            }
            else {
                colourId = Integer.toString(newFrameColour.getColourId());
            }

            LocalDate date = LocalDate.now();
            int year = date.getYear()-2000;

            String finalNewFrameId = newFrameId+colourId+year;
            //Final Length check
            if ((finalNewFrameId).length()!=16){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Something has gone wrong with barcode generation!");
            }

            Product newProduct = new Product(finalNewFrameId);
            newProduct.setProductPrice(newFrames.getPrice());
            newProduct.setProductName(newFrameName+newFrameColour.getSupplierColourCode());
            newProductsList.add(newProduct);
            newFrameIdList.add(finalNewFrameId);
        }

        //error check to see if products already exist
        List<String> existingProductIdList = productRepository.findProductIdByProductIdIn(newFrameIdList);

        if (existingProductIdList.size() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The following frame IDs already exist!: {" + existingProductIdList.toString() + "}");
        }
        productRepository.saveAll(newProductsList);
    }


    @Transactional
    public void newLens(NewLenses newLenses) {

        StringBuilder newLensIdBuilder = new StringBuilder();

        //lenses start with 22
        newLensIdBuilder.append("33");

        //Lens category
        LensCategory lensCategory = lensCategoryRepository.findById(newLenses.getCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No lens category with ID of: " + newLenses.getCategoryId() + " exists!"));
        if (lensCategory.getLensCategoryId() < 10) {
            newLensIdBuilder.append('0');
        }
        newLensIdBuilder.append(lensCategory.getLensCategoryId());

        //Lens model
        LensModel lensModel = lensModelRepository.findByLensCategory_LensCategoryIdAndLensCategoryModelId(lensCategory.getLensCategoryId(), newLenses.getModelId());
        if (lensModel == null) {
            lensModel = new LensModel(lensCategory, newLenses.getModelId(), newLenses.getModel());
            lensModelRepository.save(lensModel);
        }
        if (lensModel.getLensCategoryModelId() < 10) {
            newLensIdBuilder.append('0');
        }
        newLensIdBuilder.append(lensModel.getLensModelId());

        //prescriptions
        List<Product> newLensList = new ArrayList<>();
        List<String> newLensIdList = new ArrayList<>();

        for (NewLensesPrescription newLensPrescription : newLenses.getNewLensesPrescriptionList()) {
            StringBuilder lensPowerIdBuilder = new StringBuilder();
            StringBuilder lensPowerNameBuilder = new StringBuilder();

            double lensPower = newLensPrescription.getPower();
            if (lensPower == 0) {
                lensPowerIdBuilder.append("0000");
                lensPowerNameBuilder.append("+0.00 -0.00");
            } else {
                int powerId = (int) (newLensPrescription.getPower() / 0.25);
                String powerIdStr;
                if (powerId < 10) {
                    powerIdStr = '0' + Integer.toString(powerId);
                } else {
                    powerIdStr = Integer.toString(powerId);
                }

                if (newLensPrescription.isPowerPolarity()) {
                    lensPowerIdBuilder.append(powerIdStr).append("00");
                    lensPowerNameBuilder.append("+").append(newLensPrescription.getPower()).append(" -0.00");
                } else {
                    lensPowerIdBuilder.append("00").append(powerIdStr);
                    lensPowerNameBuilder.append("+0.00").append(" -").append(newLensPrescription.getPower());
                }
            }

            //cylinder values
            double cylinder = newLensPrescription.getCylinder();
            if (cylinder == 0) {
                lensPowerIdBuilder.append("00");
                lensPowerNameBuilder.append(" CYL-0.00");
            }
            else {
                int cylinderId = (int) (newLensPrescription.getCylinder() / 0.25);
                String cylinderIdStr;
                if (cylinderId < 10) {
                    cylinderIdStr = '0' + Integer.toString(cylinderId);
                } else {
                    cylinderIdStr = Integer.toString(cylinderId);
                }

                lensPowerIdBuilder.append(cylinderIdStr);
                lensPowerNameBuilder.append(" CYL-").append(cylinder);
            }

            //Add values
            double add = newLensPrescription.getAdd();
            if (add == 0) {
                lensPowerIdBuilder.append("00");
                lensPowerNameBuilder.append(" ADD+0.00");
            }
            else {
                int addId = (int) (newLensPrescription.getAdd() / 0.25);
                String addIdStr;
                if (addId < 10) {
                    addIdStr = '0' + Integer.toString(addId);
                } else {
                    addIdStr = Integer.toString(addId);
                }

                lensPowerIdBuilder.append(addIdStr);
                lensPowerNameBuilder.append(" ADD+").append(add);
            }

            if ((newLensIdBuilder.toString() + lensPowerIdBuilder.toString()).length() != 16) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something has gone wrong with lens barcode generation!");
            }

            String newLensId = newLensIdBuilder.toString() + lensPowerIdBuilder.toString();
            Product newLens = new Product(newLensId);
            newLens.setProductName(lensCategory.getCategoryName()+" "+lensModel.getLensModel()+" "+lensPowerNameBuilder.toString());
            newLens.setProductPrice(newLenses.getPrice());
            newLensList.add(newLens);
            newLensIdList.add(newLensId);
        }

        //error check to see if products already exist
        List<String> existingProductIdList = productRepository.findProductIdByProductIdIn(newLensIdList);

        if (existingProductIdList.size() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The following lens IDs already exist!: {" + existingProductIdList.toString() + "}");
        }
        productRepository.saveAll(newLensList);
    }


    @Transactional
    public void newCustomLens(String customLensName, double customLensPrice) {
        List<Product> customLensList = productRepository.findAllByProductIdStartsWith("33");
        long largest = 0;
        if (customLensList.size()==0){
            largest = 33000000000000L;
        }
        for (Product product:customLensList){
            if ((Long.parseLong(product.getProductId()))>largest){
                largest = Long.parseLong(product.getProductId());
            }
        }

        while (productRepository.existsById(Long.toString(largest+1))){
            largest = largest+1;
        }
        Product newCustomLens = new Product(Long.toString(largest));
        newCustomLens.setProductName(customLensName);
        newCustomLens.setProductPrice(customLensPrice);
        productRepository.save(newCustomLens);
    }
}