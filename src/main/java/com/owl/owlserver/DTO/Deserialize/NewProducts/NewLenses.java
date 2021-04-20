package com.owl.owlserver.DTO.Deserialize.NewProducts;

import lombok.Data;

import java.util.List;

@Data
public class NewLenses {

    public int lensCategoryId;
    public int lensThicknessId;

    public int lensModelId;
    public String lensModel;

    public double lensPrice;

    public List<NewLensesPrescription> newLensesPrescriptionList;
}
