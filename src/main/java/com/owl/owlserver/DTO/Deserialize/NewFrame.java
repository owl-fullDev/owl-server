package com.owl.owlserver.DTO.Deserialize;

import lombok.Data;

@Data
public class NewFrame {

    public int frameCategoryId;

    public int frameModelId;
    public String supplierModelCode;

    public int frameMaterial;

    public String[] supplierColourCodeArray;
    public int[] frameColourIdArray;
}
