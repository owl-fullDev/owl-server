package com.owl.owlserver.Controller.HeadOfficeController;

import com.owl.owlserver.DTO.Serialize.HO.SaleSerializeDTO;
import com.owl.owlserver.Service.SaleService;
import com.owl.owlserver.model.Sale;
import com.owl.owlserver.repositories.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/hoExportData")
public class HOExportData {

    //injecting repositories for database access
    @Autowired
    SaleRepository saleRepository;

    //injecting services for database access
    @Autowired
    SaleService saleService;


    //REST endpoints
    @GetMapping
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Head office api for exporting data, GET request received", HttpStatus.OK);
    }

    @GetMapping(value = "/getAllSalesForSpecificPeriod", produces = "text/csv")
    public String getAllSalesForSpecificPeriod(String start, String end) {

        LocalDate localDateStart = LocalDate.parse(start);
        LocalDate localDateEnd = LocalDate.parse(end);

        LocalDateTime startPeriod = localDateStart.atStartOfDay();
        LocalDateTime endPeriod = localDateEnd.atTime(LocalTime.MAX);

        List<Sale> saleList = saleRepository.getAllByInitialDepositDateIsBetweenOrderByInitialDepositDate(startPeriod, endPeriod);
        return saleService.saleToCSV(saleList);
    }
}