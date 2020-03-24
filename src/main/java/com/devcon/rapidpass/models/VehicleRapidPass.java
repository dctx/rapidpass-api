package com.devcon.rapidpass.models;

import java.util.Objects;
import com.devcon.rapidpass.models.Address;
import com.devcon.rapidpass.models.CheckpointCollection;
import com.devcon.rapidpass.models.RapidPassRequestResult;
import com.devcon.rapidpass.models.Vehicle;
import com.devcon.rapidpass.models.VehicleCrewCollection;
import com.devcon.rapidpass.models.VehicleDriver;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Details about the vehicle applied for the Vehicle Rapid Pass.
 */
@ApiModel(description = "Details about the vehicle applied for the Vehicle Rapid Pass.")
@Validated
public class VehicleRapidPass   {
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("accessType")
  private String accessType = null;

  @JsonProperty("origin")
  private Address origin = null;

  @JsonProperty("destinations")
  private Address destinations = null;

  @JsonProperty("checkpoints")
  private CheckpointCollection checkpoints = null;

  @JsonProperty("authorizedDriver")
  private VehicleDriver authorizedDriver = null;

  @JsonProperty("vehicle")
  private Vehicle vehicle = null;

  @JsonProperty("additionalCrewMembers")
  private VehicleCrewCollection additionalCrewMembers = null;

  @JsonProperty("passRequestResult")
  private RapidPassRequestResult passRequestResult = null;

  public VehicleRapidPass id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(value = "")
  
    public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public VehicleRapidPass accessType(String accessType) {
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

  public VehicleRapidPass origin(Address origin) {
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

  public VehicleRapidPass destinations(Address destinations) {
    this.destinations = destinations;
    return this;
  }

  /**
   * Get destinations
   * @return destinations
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public Address getDestinations() {
    return destinations;
  }

  public void setDestinations(Address destinations) {
    this.destinations = destinations;
  }

  public VehicleRapidPass checkpoints(CheckpointCollection checkpoints) {
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

  public VehicleRapidPass authorizedDriver(VehicleDriver authorizedDriver) {
    this.authorizedDriver = authorizedDriver;
    return this;
  }

  /**
   * Get authorizedDriver
   * @return authorizedDriver
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public VehicleDriver getAuthorizedDriver() {
    return authorizedDriver;
  }

  public void setAuthorizedDriver(VehicleDriver authorizedDriver) {
    this.authorizedDriver = authorizedDriver;
  }

  public VehicleRapidPass vehicle(Vehicle vehicle) {
    this.vehicle = vehicle;
    return this;
  }

  /**
   * Get vehicle
   * @return vehicle
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public Vehicle getVehicle() {
    return vehicle;
  }

  public void setVehicle(Vehicle vehicle) {
    this.vehicle = vehicle;
  }

  public VehicleRapidPass additionalCrewMembers(VehicleCrewCollection additionalCrewMembers) {
    this.additionalCrewMembers = additionalCrewMembers;
    return this;
  }

  /**
   * Get additionalCrewMembers
   * @return additionalCrewMembers
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public VehicleCrewCollection getAdditionalCrewMembers() {
    return additionalCrewMembers;
  }

  public void setAdditionalCrewMembers(VehicleCrewCollection additionalCrewMembers) {
    this.additionalCrewMembers = additionalCrewMembers;
  }

  public VehicleRapidPass passRequestResult(RapidPassRequestResult passRequestResult) {
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
    VehicleRapidPass vehicleRapidPass = (VehicleRapidPass) o;
    return Objects.equals(this.id, vehicleRapidPass.id) &&
        Objects.equals(this.accessType, vehicleRapidPass.accessType) &&
        Objects.equals(this.origin, vehicleRapidPass.origin) &&
        Objects.equals(this.destinations, vehicleRapidPass.destinations) &&
        Objects.equals(this.checkpoints, vehicleRapidPass.checkpoints) &&
        Objects.equals(this.authorizedDriver, vehicleRapidPass.authorizedDriver) &&
        Objects.equals(this.vehicle, vehicleRapidPass.vehicle) &&
        Objects.equals(this.additionalCrewMembers, vehicleRapidPass.additionalCrewMembers) &&
        Objects.equals(this.passRequestResult, vehicleRapidPass.passRequestResult);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, accessType, origin, destinations, checkpoints, authorizedDriver, vehicle, additionalCrewMembers, passRequestResult);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VehicleRapidPass {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    accessType: ").append(toIndentedString(accessType)).append("\n");
    sb.append("    origin: ").append(toIndentedString(origin)).append("\n");
    sb.append("    destinations: ").append(toIndentedString(destinations)).append("\n");
    sb.append("    checkpoints: ").append(toIndentedString(checkpoints)).append("\n");
    sb.append("    authorizedDriver: ").append(toIndentedString(authorizedDriver)).append("\n");
    sb.append("    vehicle: ").append(toIndentedString(vehicle)).append("\n");
    sb.append("    additionalCrewMembers: ").append(toIndentedString(additionalCrewMembers)).append("\n");
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
