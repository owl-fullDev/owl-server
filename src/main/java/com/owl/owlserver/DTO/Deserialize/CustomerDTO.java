
package com.owl.owlserver.DTO.Deserialize;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDTO {

    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String email;
    public Double leftEyeSphere;
    public Double leftEyeCylinder;
    public Integer leftEyeAxis;
    public Double leftEyeAdd;
    public String leftEyePrism;
    public Double rightEyeSphere;
    public Double rightEyeCylinder;
    public Integer rightEyeAxis;
    public Double rightEyeAdd;
    public String rightEyePrism;
    public Integer pupilDistance;

}
