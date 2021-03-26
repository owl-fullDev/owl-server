
package com.owl.owlserver.DTO;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
public class NewSaleDTO {

    @JsonProperty("customerId")
    public Integer customerId;
    @JsonProperty("itemsSold")
    public Integer itemsSold;
    @JsonProperty("sale")
    public Sale sale;
    @JsonProperty("products")
    public List<Product> products = null;
    @JsonProperty("newCustomer")
    public NewCustomer newCustomer;

}
