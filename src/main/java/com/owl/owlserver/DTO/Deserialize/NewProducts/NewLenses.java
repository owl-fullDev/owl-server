package com.owl.owlserver.DTO.Deserialize.NewProducts;

import lombok.Data;

import java.util.List;

@Data
public class NewLenses {

    public int categoryId;//33 for lenses
    public int modelId;
    public String model;

    public double price;

    public List<NewLensesPrescription> newLensesPrescriptionList;
}
