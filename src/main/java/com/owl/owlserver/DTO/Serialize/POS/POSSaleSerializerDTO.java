package com.owl.owlserver.DTO.Serialize.POS;

import com.owl.owlserver.DTO.Deserialize.CustomerDTO;
import com.owl.owlserver.DTO.Deserialize.SaleDetailDTO;
import com.owl.owlserver.DTO.Serialize.SaleDetailSerializerDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class POSSaleSerializerDTO {

    private Integer saleId;
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

    private CustomerDTO customerDTO;

    private List<SaleDetailSerializerDTO> saleDetailSerializerDTOList;
}
