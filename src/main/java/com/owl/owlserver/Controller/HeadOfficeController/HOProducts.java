package com.owl.owlserver.Controller.HeadOfficeController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.owl.owlserver.DTO.Deserialize.NewProduct;
import com.owl.owlserver.model.Products.*;
import com.owl.owlserver.model.Product;
import com.owl.owlserver.repositories.*;
import com.owl.owlserver.repositories.Frame.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoProductsEndpoint")
public class HOProducts {

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


    //JACKSON object Mapper
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //REST endpoints
    @GetMapping
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Head office api for Products, GET request received", HttpStatus.OK);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> productList = productRepository.findAll();
        if (productList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found");
        }
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/getAllFrames")
    public ResponseEntity<List<Product>> getAllFrames() {
        List<Product> productList = productRepository.findAllByProductIdStartsWith("F");
        if (productList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No frames found");
        }
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/getAllLenses")
    public ResponseEntity<List<Product>> getAllLenses() {
        List<Product> productList = productRepository.findAllByProductIdStartsWith("L");
        if (productList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No lenses found");
        }
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @GetMapping("/getAllCustomLenses")
    public ResponseEntity<List<Product>> getAllCustomLenses() {
        List<Product> productList = productRepository.findAllByProductIdStartsWith("CL");
        if (productList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No custom lenses found");
        }
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }

    @PostMapping(value = "/updateProduct")
    public ResponseEntity<String> updateProduct(@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String productId = wholeJSON.get("productId").asText();
        String productName = wholeJSON.get("productName").asText();
        double productPrice = wholeJSON.get("productPrice").asDouble();

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No product with ID of: " + productId + " exists!"));
        product.setProductName(productName);
        product.setProductPrice(productPrice);
        productRepository.saveAndFlush(product);

        return new ResponseEntity<>("Product has been updated", HttpStatus.OK);
    }

//    @PostMapping(value = "/addNewProduct")
//    public ResponseEntity<String> addNewProduct(@RequestBody String jsonString) throws JsonProcessingException {
//        JsonNode wholeJSON = objectMapper.readTree(jsonString);
//
//        String productId = wholeJSON.get("productId").asText();
//        String productName = wholeJSON.get("productName").asText();
//        double productPrice = wholeJSON.get("productPrice").asDouble();
//
//        if (productRepository.existsById(productId)) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specified ID already taken by existing product");
//        }
//
//        Product newProduct = new Product(productId, productName, productPrice);
//        productRepository.saveAndFlush(newProduct);
//
//        return new ResponseEntity<>("Product has been added", HttpStatus.OK);
//    }

    @PostMapping(value = "/addNewProducts")
    public ResponseEntity<String> addNewProducts(@RequestBody NewProduct newProduct) {

        return new ResponseEntity<>(newProduct.toString(), HttpStatus.OK);
    }

    @GetMapping("/getAllFrameCategories")
    public List<FrameCategory> getAllFrameCategories() {
        return frameCategoryRepository.findAll();
    }

    @PostMapping(value = "/addNewFrameCategory")
    public ResponseEntity<String> newFrameCategory (@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String frameCategoryName = wholeJSON.get("frameCategory").asText();

        FrameCategory newFrameCategory = new FrameCategory(frameCategoryName);
        frameCategoryRepository.save(newFrameCategory);
        return new ResponseEntity<>("New Frame category has been added", HttpStatus.CREATED);
    }

    @GetMapping("/getAllFrameModels")
    public List<FrameModel> getAllFrameModels() {
        return frameModelRepository.findAll();
    }

//    @PostMapping(value = "/addNewFrameModel")
//    public ResponseEntity<String> addNewFrameModel (@RequestBody String jsonString) throws JsonProcessingException {
//        JsonNode wholeJSON = objectMapper.readTree(jsonString);
//
//        int frameModelCode = wholeJSON.get("frameModelCode").asInt();
//        String frameModel = wholeJSON.get("frameModel").asText();
//
//
//        FrameModel newFrameModel = new FrameModel(frameCategory,frameModelCode,frameModel);
//        if (frameModelRepository.existsByFrameCategoryAndAndFrameModelCode(frameCategory,frameModelCode)){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Frame model ID of: "+frameModelCode+" already used");
//        }
//
//        frameModelRepository.save(newFrameModel);
//        return new ResponseEntity<>("New Frame model has been added", HttpStatus.CREATED);
//    }

    @GetMapping("/getAllFrameColours")
    public List<FrameColour> getAllFrameColours() {
        return frameColourRepository.findAll();
    }

    @PostMapping(value = "/addNewFrameColour")
    public ResponseEntity<String> addNewFrameColour (@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String frameColour = wholeJSON.get("frameColour").asText();

        FrameColour newFrameColour = new FrameColour(frameColour);
        frameColourRepository.save(newFrameColour);
        return new ResponseEntity<>("New Frame colour has been added", HttpStatus.OK);
    }

    @GetMapping("/getAllFrameMaterials")
    public List<FrameMaterial> getAllFrameMaterials() {
        return frameMaterialRepository.findAll();
    }

    @PostMapping(value = "/addNewFrameMaterial")
    public ResponseEntity<String> addNewFrameMaterial (@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String frameMaterial = wholeJSON.get("frameMaterial").asText();

        FrameMaterial newFrameMaterial = new FrameMaterial(frameMaterial);
        frameMaterialRepository.save(newFrameMaterial);
        return new ResponseEntity<>("New Frame material has been added", HttpStatus.OK);
    }

    @GetMapping("/getAllLensCategories")
    public List<LensCategory> getAllLensCategories() {
        return lensCategoryRepository.findAll();
    }

    @PostMapping(value = "/addNewLensCategory")
    public ResponseEntity<String> addNewLensCategory (@RequestBody String jsonString) throws JsonProcessingException {
        JsonNode wholeJSON = objectMapper.readTree(jsonString);

        String lensCategoryName = wholeJSON.get("lensCategory").asText();

        LensCategory newLensCategory = new LensCategory(lensCategoryName);
        lensCategoryRepository.save(newLensCategory);
        return new ResponseEntity<>("New Lens category has been added", HttpStatus.CREATED);
    }

    @GetMapping("/getAllLensModels")
    public List<LensModel> getAllLensModels() {
        return lensModelRepository.findAll();
    }

//    @PostMapping(value = "/addNewFrameModel")
//    public ResponseEntity<String> addNewFrameModel (@RequestBody String jsonString) throws JsonProcessingException {
//        JsonNode wholeJSON = objectMapper.readTree(jsonString);
//
//        int frameModelCode = wholeJSON.get("frameModelCode").asInt();
//        String frameModel = wholeJSON.get("frameModel").asText();
//
//
//        FrameModel newFrameModel = new FrameModel(frameCategory,frameModelCode,frameModel);
//        if (frameModelRepository.existsByFrameCategoryAndAndFrameModelCode(frameCategory,frameModelCode)){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Frame model ID of: "+frameModelCode+" already used");
//        }
//
//        frameModelRepository.save(newFrameModel);
//        return new ResponseEntity<>("New Frame model has been added", HttpStatus.CREATED);
//    }
}
