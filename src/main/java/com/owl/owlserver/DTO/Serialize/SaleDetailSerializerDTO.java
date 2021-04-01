package com.owl.owlserver.DTO.Serialize;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaleDetailSerializerDTO {

    private String productId;
    private String productName;
    private Integer quantity;
}
