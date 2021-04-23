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
        if (lensCategory.getLensCategoryId() < 10) {
            newLensIdBuilder.append('0');
        }
        newLensIdBuilder.append(lensCategory.getLensCategoryId());

        //Lens thickness
        if (newLenses.getLensThickness() < 10) {
            newLensIdBuilder.append('0');
        }
        newLensIdBuilder.append(newLenses.getLensThickness());

        LensModel lensModel = lensModelRepository.findByLensCategory_LensCategoryIdAndLensCategoryModelId(lensCategory.getLensCategoryId(), newLenses.getLensModelId());
        //new Lens model
        if (lensModel == null) {
            lensModel = new LensModel(lensCategory, newLenses.getLensModelId(), newLenses.getLensModel());
            lensModelRepository.save(lensModel);
        }
        if (lensModel.getLensCategoryModelId() < 10) {
            newLensIdBuilder.append('0');
        }
        newLensIdBuilder.append(lensCategory.getLensCategoryId());

        //prescriptions
        List<Product> newLensList = new ArrayList<>();
        String lensName = lensCategory.getCategoryName()+" 1."+newLenses.getLensThickness()+" "+lensModel.getLensModel()+" ";

        for (NewLensesPrescription newLensPrescription : newLenses.getNewLensesPrescriptionList()) {
            StringBuilder lensPowerIdBuilder = new StringBuilder();
            StringBuilder newLensNameBuilder = new StringBuilder();

            double lensPower = newLensPrescription.getPower();

            if (lensPower == 0) {
                lensPowerIdBuilder.append("0000");
                newLensNameBuilder.append("+0.00 -0.00");
            } else {
                int powerId = 0;
                double powerDivisibleCount = lensPower / .25;
                boolean add2or3 = true;
                while (powerDivisibleCount != 0) {
                    if (add2or3) {
                        powerId = powerId + 2;
                    } else {
                        powerId = powerId + 3;
                    }
                    add2or3 = !add2or3;
                    powerDivisibleCount = powerDivisibleCount - 1;
                }

                if (newLensPrescription.isPowerPolarity()) {
                    lensPowerIdBuilder.append(powerId).append("00");
                    newLensNameBuilder.append("+").append(newLensPrescription.getPower()).append(" -0.00");
                } else {
                    newLensNameBuilder.append("+0.00").append(" -").append(newLensPrescription.getPower());
                    lensPowerIdBuilder.append("00").append(powerId);
                }
            }

            //cylinder values
            double cylinder = newLensPrescription.getCylinder();

            Product newLens = new Product(newLensIdBuilder.toString() + lensPowerIdBuilder.toString() + "00");
            newLens.setProductName(lensName + newLensNameBuilder.toString() + " -0.00");
            newLens.setProductPrice(newLenses.getLensPrice());
            newLensList.add(newLens);

            System.out.println("\n");
            System.out.println(newLens.getProductId());
            System.out.println(newLens.getProductName());

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

                newLens = new Product(newLensIdBuilder.toString() + lensPowerIdBuilder.toString()+cylinderIdStr);
                newLens.setProductName(lensName + newLensNameBuilder.toString()+" -"+0.25*cylinderDivisibleCount);
                newLens.setProductPrice(newLenses.getLensPrice());
                newLensList.add(newLens);
                System.out.println("\n");
                System.out.println(newLens.getProductId());
                System.out.println(newLens.getProductName());

            }
        }
        //productRepository.saveAll(newLensList);
    }
}