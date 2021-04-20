
package com.owl.owlserver.DTO.Deserialize.NewSale;

import lombok.Data;

@Data
public class NewSaleDTO {

    public Integer customerId;
    public CustomerDTO customer;
    public SaleDTO sale;

}
