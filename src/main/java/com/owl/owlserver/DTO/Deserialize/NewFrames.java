package com.owl.owlserver.DTO.Deserialize;

import lombok.Data;

import java.util.List;

@Data
public class NewFrames {

    public int frameCategoryId;

    public int frameModelId;
    public String supplierModelCode;

    public int frameMaterial;

    public List<NewFrameColours> newFrameColoursList;
}
