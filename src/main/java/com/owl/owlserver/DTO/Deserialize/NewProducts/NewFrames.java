package com.owl.owlserver.DTO.Deserialize.NewProducts;

import lombok.Data;

import java.util.List;

@Data
public class NewFrames {

    public int brandId;//11 for OWL, 22 FOR Lee Cooper
    public int supplierId;//supplier Id
    public int categoryId;
    public int modelId;
    public String supplierModelCode;
    public int materialId;
    public double price;

    public List<NewFrameColours> newFrameColoursList;
}
