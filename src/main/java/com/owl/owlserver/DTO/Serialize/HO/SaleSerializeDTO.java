package com.owl.owlserver.DTO.Serialize.HO;

import com.owl.owlserver.DTO.Deserialize.SaleDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SaleSerializeDTO {

    private Integer saleId;
    private String customerName;
    private String phoneNumber;
    private String promotionName;
    private Integer promotionParentId;
    private String employeeName;
    private String storeName;
    private Double grandTotal;
    private Boolean isFullyPaid;
    private String initialDepositDate;
    private String initialDepositType;
    private Double initialDepositAmount;
    private String finalDepositDate;
    private String finalDepositType;
    private Double finalDepositAmount;
    private String saleRemarks;
    private String pickupDate;

    private List<SaleDetailDTO> saleDetailDTOS;
}
