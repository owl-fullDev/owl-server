package com.owl.owlserver.DTO.Deserialize;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NewProduct {

    public boolean isExistingModel;
    public int newProductType;

    @JsonProperty("newFrameList")
    public List<NewFrame> newFrameList;

}
