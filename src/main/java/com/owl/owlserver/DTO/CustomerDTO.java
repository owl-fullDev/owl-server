
package com.owl.owlserver.DTO;

import lombok.Data;

@Data
public class CustomerDTO {

    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String email;
    public Integer leftEyeSphere;
    public Integer leftEyeCylinder;
    public Integer leftEyeAxis;
    public Integer leftEyeAdd;
    public String leftEyePrism;
    public Integer rightEyeSphere;
    public Integer rightEyeCylinder;
    public Integer rightEyeAxis;
    public Integer rightEyeAdd;
    public String rightEyePrism;
    public Integer pupilDistance;

}
