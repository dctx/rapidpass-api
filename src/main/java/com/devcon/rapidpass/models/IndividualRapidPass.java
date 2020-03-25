package com.devcon.rapidpass.models;

import java.util.Objects;
import com.devcon.rapidpass.models.Address;
import com.devcon.rapidpass.models.AddressCollection;
import com.devcon.rapidpass.models.CheckpointCollection;
import com.devcon.rapidpass.models.DocumentCollection;
import com.devcon.rapidpass.models.RapidPassRequestResult;
import com.devcon.rapidpass.models.RapidPassUser;
import com.devcon.rapidpass.models.WorkCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Detail about the individual who applied for the Rapid Pass.
 */
@ApiModel(description = "Detail about the individual who applied for the Rapid Pass.")
@Validated
public class IndividualRapidPass extends RapidPassUser  {
  @JsonProperty("accessType")
  private String accessType = null;

  @JsonProperty("origin")
  private Address origin = null;

  @JsonProperty("destinations")
  private AddressCollection destinations = null;

  @JsonProperty("checkpoints")
  private CheckpointCollection checkpoints = null;

  @JsonProperty("passRequestResult")
  private RapidPassRequestResult passRequestResult = null;

  public IndividualRapidPass accessType(String accessType) {
    this.accessType = accessType;
    return this;
  }

  /**
   * Get accessType
   * @return accessType
  **/
  @ApiModelProperty(value = "")
  
    public String getAccessType() {
    return accessType;
  }

  public void setAccessType(String accessType) {
    this.accessType = accessType;
  }

  public IndividualRapidPass origin(Address origin) {
    this.origin = origin;
    return this;
  }

  /**
   * Get origin
   * @return origin
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public Address getOrigin() {
    return origin;
  }

  public void setOrigin(Address origin) {
    this.origin = origin;
  }

  public IndividualRapidPass destinations(AddressCollection destinations) {
    this.destinations = destinations;
    return this;
  }

  /**
   * Get destinations
   * @return destinations
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public AddressCollection getDestinations() {
    return destinations;
  }

  public void setDestinations(AddressCollection destinations) {
    this.destinations = destinations;
  }

  public IndividualRapidPass checkpoints(CheckpointCollection checkpoints) {
    this.checkpoints = checkpoints;
    return this;
  }

  /**
   * Get checkpoints
   * @return checkpoints
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public CheckpointCollection getCheckpoints() {
    return checkpoints;
  }

  public void setCheckpoints(CheckpointCollection checkpoints) {
    this.checkpoints = checkpoints;
  }

  public IndividualRapidPass passRequestResult(RapidPassRequestResult passRequestResult) {
    this.passRequestResult = passRequestResult;
    return this;
  }

  /**
   * Get passRequestResult
   * @return passRequestResult
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public RapidPassRequestResult getPassRequestResult() {
    return passRequestResult;
  }

  public void setPassRequestResult(RapidPassRequestResult passRequestResult) {
    this.passRequestResult = passRequestResult;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IndividualRapidPass individualRapidPass = (IndividualRapidPass) o;
    return Objects.equals(this.accessType, individualRapidPass.accessType) &&
        Objects.equals(this.origin, individualRapidPass.origin) &&
        Objects.equals(this.destinations, individualRapidPass.destinations) &&
        Objects.equals(this.checkpoints, individualRapidPass.checkpoints) &&
        Objects.equals(this.passRequestResult, individualRapidPass.passRequestResult) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessType, origin, destinations, checkpoints, passRequestResult, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndividualRapidPass {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    accessType: ").append(toIndentedString(accessType)).append("\n");
    sb.append("    origin: ").append(toIndentedString(origin)).append("\n");
    sb.append("    destinations: ").append(toIndentedString(destinations)).append("\n");
    sb.append("    checkpoints: ").append(toIndentedString(checkpoints)).append("\n");
    sb.append("    passRequestResult: ").append(toIndentedString(passRequestResult)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
