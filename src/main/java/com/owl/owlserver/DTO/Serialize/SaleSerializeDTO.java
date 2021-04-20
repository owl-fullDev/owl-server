package com.owl.owlserver.DTO.Serialize;

import com.owl.owlserver.DTO.Deserialize.NewSale.SaleDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SaleSerializeDTO {

    private Integer saleId;
    private String storeName;
    private String promotionName;
    private Double grandTotal;
    private String initialDepositDate;
    private Integer promotionParentId;
    private String saleRemarks;

    private List<SaleDetailDTO> saleDetailDTOS;
}
