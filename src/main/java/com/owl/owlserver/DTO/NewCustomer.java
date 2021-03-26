
package com.owl.owlserver.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
public class NewCustomer {

    @JsonProperty("firstName")
    public String firstName;
    @JsonProperty("lastName")
    public String lastName;
    @JsonProperty("phoneNumber")
    public String phoneNumber;
    @JsonProperty("email")
    public String email;
    @JsonProperty("leftEyeSphere")
    public Integer leftEyeSphere;
    @JsonProperty("leftEyeCylinder")
    public Integer leftEyeCylinder;
    @JsonProperty("leftEyeAxis")
    public Integer leftEyeAxis;
    @JsonProperty("leftEyeAdd")
    public Integer leftEyeAdd;
    @JsonProperty("leftEyePrism")
    public String leftEyePrism;
    @JsonProperty("rightEyeSphere")
    public Integer rightEyeSphere;
    @JsonProperty("rightEyeCylinder")
    public Integer rightEyeCylinder;
    @JsonProperty("rightEyeAxis")
    public Integer rightEyeAxis;
    @JsonProperty("rightEyeAdd")
    public Integer rightEyeAdd;
    @JsonProperty("rightEyePrism")
    public String rightEyePrism;
    @JsonProperty("pupilDistance")
    public Integer pupilDistance;

}
