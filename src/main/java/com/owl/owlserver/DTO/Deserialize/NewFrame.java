package com.owl.owlserver.DTO.Deserialize;

import lombok.Data;

@Data
public class NewFrame {

    public int frameCategoryId;
    public int frameModelId;
    public int frameMaterial;

    public int[] frameColourIdArray;
}
