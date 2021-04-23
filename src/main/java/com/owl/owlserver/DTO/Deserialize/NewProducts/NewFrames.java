package com.owl.owlserver.DTO.Deserialize.NewProducts;

import lombok.Data;

import java.util.List;

@Data
public class NewFrames {

    public int frameCategoryId;
    public int frameBrandId;

    public String supplierName;
    public int frameModelId;
    public String supplierModelCode;

    public int frameMaterialId;
    public double framePrice;

    public List<NewFrameColours> newFrameColoursList;
}
