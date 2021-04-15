package com.owl.owlserver.DTO.Deserialize;

import lombok.Data;

import java.util.List;

@Data
public class NewFrames {

    public int frameCategoryId;
    public int frameBrandId;//OWL is 22, Lee cooper is 33

    public int frameModelId;
    public String supplierModelCode;

    public int frameMaterialId;
    public double framePrice;

    public List<NewFrameColours> newFrameColoursList;
}
