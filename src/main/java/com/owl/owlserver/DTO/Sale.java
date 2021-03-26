
package com.owl.owlserver.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Sale {

    @JsonProperty("promotionId")
    public Integer promotionId;
    @JsonProperty("promotionParentSaleId")
    public Integer promotionParentSaleId;
    @JsonProperty("grandTotal")
    public String grandTotal;
    @JsonProperty("employeeId")
    public Integer employeeId;
    @JsonProperty("storeId")
    public Integer storeId;
    @JsonProperty("initialDepositDate")
    public String initialDepositDate;
    @JsonProperty("initialDepositType")
    public String initialDepositType;
    @JsonProperty("initialDepositAmount")
    public String initialDepositAmount;

}
