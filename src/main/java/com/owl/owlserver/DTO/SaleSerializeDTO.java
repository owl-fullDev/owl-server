package com.owl.owlserver.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SaleSerializeDTO {

    private Integer saleId;
    private String customerName;
    private String promotionName;
    private Integer promotionParentId;
    public String employeeName;
    public String storeName;
    public Double grandTotal;
    public Boolean isFullyPaid;
    private String initialDepositDate;
    private String initialDepositType;
    private Double initialDepositAmount;
    private String finalDepositDate;
    private String finalDepositType;
    private Double finalDepositAmount;
    public String saleRemarks;
    public String pickupDate;

    public List<SaleDetailDTO> saleDetailDTOS;
}
