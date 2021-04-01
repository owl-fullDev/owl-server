
package com.owl.owlserver.DTO.Deserialize;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SaleDTO {

    @JsonProperty("promotionId")
    public Integer promotionId;
    @JsonProperty("promotionParentSaleId")
    public Integer promotionParentSaleId;
    @JsonProperty("grandTotal")
    public Double grandTotal;
    @JsonProperty("employeeId")
    public Integer employeeId;
    @JsonProperty("storeId")
    public Integer storeId;
    @JsonProperty("initialDepositDate")
    public String initialDepositDate;
    @JsonProperty("initialDepositType")
    public String initialDepositType;
    @JsonProperty("initialDepositAmount")
    public Double initialDepositAmount;
    @JsonProperty("remarks")
    public String saleRemarks;

    @JsonProperty("products")
    public List<SaleDetailDTO> saleDetailDTOS;
}
