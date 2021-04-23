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
        StringBuilder newFrameNameBuilder = new StringBuilder();

        //frames start with 11
        newFrameIdBuilder.append("11");

        //Frame brand
        if (newFrames.getFrameBrandId() == 11) {
            newFrameNameBuilder.append("OWL ");
        }
        else if(newFrames.getFrameBrandId() == 22) {
            newFrameNameBuilder.append("LEE COOPER ");
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown frame brand Id");
        }
        newFrameIdBuilder.append(newFrames.getFrameBrandId());

        //Frame category
        FrameCategory frameCategory = frameCategoryRepository.findById(newFrames.getFrameCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No frame category with ID of: " + newFrames.getFrameCategoryId() + " exists!"));
        if (frameCategory.getFrameCategoryId() < 10) {
            newFrameIdBuilder.append('0');
        }
        newFrameIdBuilder.append(newFrames.getFrameCategoryId());
        newFrameNameBuilder.append(frameCategory.getCategoryName()).append(" ");

        //Frame model
        FrameModel frameModel = frameModelRepository.findAllByFrameCategory_FrameCategoryIdAndFrameCategoryModelId(newFrames.getFrameCategoryId(), newFrames.getFrameModelId());
        if (frameModel == null) {//new model
            if (newFrames.getFrameModelId()>=9999){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Model in category ID limit reached");
            }
            frameModel = new FrameModel(frameCategory, newFrames.getFrameModelId(), newFrames.getSupplierModelCode());
            frameModelRepository.save(frameModel);
        }
        if (newFrames.getFrameModelId()<1000){
            newFrameNameBuilder.append('0');
            if (newFrames.getFrameModelId()<100){
                newFrameNameBuilder.append('0');
                if (newFrames.getFrameModelId()<10){
                    newFrameNameBuilder.append('0');
                }
            }
        }
        newFrameIdBuilder.append(frameModel.getFrameCategoryModelId());
        newFrameNameBuilder.append(frameModel.getFrameModel()).append(" ");

        //Material
        FrameMaterial frameMaterial = frameMaterialRepository.findById(newFrames.getFrameMaterialId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No frame material with ID of: " + newFrames.getFrameMaterialId() + " exists!"));
        if (frameMaterial.getFrameMaterialId() < 10) {
            newFrameIdBuilder.append('0');
        }
        newFrameIdBuilder.append(newFrames.getFrameMaterialId());
        newFrameNameBuilder.append(frameMaterial.getFrameMaterial()).append(" ");

        //Colors
        List<FrameColour> frameColourList = frameColourRepository.findAll();
        String newFrameId = newFrameIdBuilder.toString();
        String newFrameName = newFrameNameBuilder.toString();
        List<Product> newProductsList = new ArrayList<>();
        for (NewFrameColours newFrameColour : newFrames.getNewFrameColoursList()) {
            String colourId = "";
            if (newFrameColour.getFrameColourId()<10){
                colourId = "0";
            }

            FrameColour frameColour = frameColourList.stream().filter(frameColour2 -> newFrameColour.frameColourId==frameColour2.getFrameColourId()).findFirst().orElse(null);
            if (frameColour==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Colour Id not recognized");
            }

            if ((newFrameId+colourId+newFrameColour.getFrameColourId()).length()!=10){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Something has gone wrong with barcode generation!");
            }

            Product newProduct = new Product(newFrameId+colourId+newFrameColour.getFrameColourId());
            newProduct.setProductPrice(newFrames.getFramePrice());
            newProduct.setProductName(newFrameName +" "+ frameColour.getFrameColour());
            newProduct.setSupplierCode(newFrames.getSupplierName() +" "+ newFrames.getSupplierModelCode() +" "+ newFrameColour.getSupplierColourCode());
            newProductsList.add(newProduct);
        }
        productRepository.saveAll(newProductsList);
    }



    @Transactional
    public void newLens(NewLenses newLenses) {

        StringBuilder newLensIdBuilder = new StringBuilder();
        StringBuilder newLensNameBuilder = new StringBuilder();

        //lenses start with 22
        newLensIdBuilder.append("22");

        //Lens category
        LensCategory lensCategory = lensCategoryRepository.findById(newLenses.getLensCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No lens category with ID of: " + newLenses.getLensCategoryId() + " exists!"));
        if (lensCategory.getLensCategoryId() < 10) {
            newLensIdBuilder.append('0');
        }
        newLensIdBuilder.append(lensCategory.getLensCategoryId());
        newLensNameBuilder.append(lensCategory.getCategoryName()).append(" ");

        //Lens thickness
        if (newLenses.getLensThickness() < 10) {
            newLensIdBuilder.append('0');
        }
        newLensIdBuilder.append(newLenses.getLensThickness());
        newLensNameBuilder.append("1.").append(newLenses.getLensThickness()).append(" ");

        //Lens model
        LensModel lensModel = lensModelRepository.findByLensCategory_LensCategoryIdAndLensCategoryModelId(lensCategory.getLensCategoryId(), newLenses.getLensModelId());
        if (lensModel == null) {
            lensModel = new LensModel(lensCategory, newLenses.getLensModelId(), newLenses.getLensModel());
            lensModelRepository.save(lensModel);
        }
        if (lensModel.getLensCategoryModelId() < 10) {
            newLensIdBuilder.append('0');
        }
        newLensIdBuilder.append(lensModel.getLensModelId());
        newLensNameBuilder.append(lensModel.getLensModel()).append(" ");

        //prescriptions
        List<Product> newLensList = new ArrayList<>();
        String lensName = newLensNameBuilder.toString();

        for (NewLensesPrescription newLensPrescription : newLenses.getNewLensesPrescriptionList()) {
            StringBuilder lensPowerIdBuilder = new StringBuilder();
            StringBuilder lensPowerNameBuilder = new StringBuilder();

            double lensPower = newLensPrescription.getPower();

            if (lensPower == 0) {
                lensPowerIdBuilder.append("0000");
                lensPowerNameBuilder.append("+0.00 -0.00");
            }
            else {
                int powerId = 0;
                double powerDivisibleCount = lensPower / .25;
                boolean add2or3 = true;
                while (powerDivisibleCount != 0) {
                    if (add2or3) {
                        powerId = powerId + 2;
                    }
                    else {
                        powerId = powerId + 3;
                    }
                    add2or3 = !add2or3;
                    powerDivisibleCount = powerDivisibleCount - 1;
                }

                String powerIdStr = "";
                if (powerId<10){
                    powerIdStr = "0"+ powerId;
                }
                else {
                    powerIdStr = Integer.toString(powerId);
                }

                if (newLensPrescription.isPowerPolarity()) {
                    lensPowerIdBuilder.append(powerIdStr).append("00");
                    lensPowerNameBuilder.append("+").append(newLensPrescription.getPower()).append(" -0.00");
                }
                else {
                    lensPowerIdBuilder.append("00").append(powerIdStr);
                    lensPowerNameBuilder.append("+0.00").append(" -").append(newLensPrescription.getPower());
                }
            }

            //cylinder values
            double cylinder = newLensPrescription.getCylinder();

            if ((newLensIdBuilder.toString() + lensPowerIdBuilder.toString() + "00").length()!=14){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Something has gone wrong with barcode generation!");
            }
            Product newLens = new Product(newLensIdBuilder.toString() + lensPowerIdBuilder.toString() + "00");
            newLens.setProductName(lensName + lensPowerNameBuilder.toString() + " -0.00");
            newLens.setProductPrice(newLenses.getLensPrice());
            newLensList.add(newLens);

            int cylinderId = 0;
            double cylinderDivisibleCount = 0;
            boolean add2or3 = true;
            while (cylinderDivisibleCount < cylinder/0.25) {
                if (add2or3) {
                    cylinderId = cylinderId + 2;
                } else {
                    cylinderId = cylinderId + 3;
                }
                add2or3 = !add2or3;
                cylinderDivisibleCount = cylinderDivisibleCount + 1;

                String cylinderIdStr = Integer.toString(cylinderId);
                if (cylinderId<10){
                    cylinderIdStr = '0'+Integer.toString(cylinderId);
                }

                if ((newLensIdBuilder.toString() + lensPowerIdBuilder.toString()+cylinderIdStr).length()!=14){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Something has gone wrong with barcode generation!");
                }
                newLens = new Product(newLensIdBuilder.toString() + lensPowerIdBuilder.toString()+cylinderIdStr);
                newLens.setProductName(lensName + lensPowerNameBuilder.toString()+" -"+0.25*cylinderDivisibleCount);
                newLens.setProductPrice(newLenses.getLensPrice());
                newLensList.add(newLens);
            }
        }
        productRepository.saveAll(newLensList);
    }
}