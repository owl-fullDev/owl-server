
package com.owl.owlserver.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
public class Product {

    @JsonProperty("productId")
    public String productId;
    @JsonProperty("quantity")
    public Integer quantity;

}
