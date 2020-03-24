package com.devcon.rapidpass.models;

import java.util.Objects;
import com.devcon.rapidpass.models.ServiceOwner;
import com.devcon.rapidpass.models.VehicleCollection;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Vehicle Pass Request from a Service Owner
 */
@ApiModel(description = "Vehicle Pass Request from a Service Owner")
@Validated
public class VehicleRapidPassRequest   {
  @JsonProperty("serviceOwner")
  private ServiceOwner serviceOwner = null;

  @JsonProperty("vehicles")
  private VehicleCollection vehicles = null;

  public VehicleRapidPassRequest serviceOwner(ServiceOwner serviceOwner) {
    this.serviceOwner = serviceOwner;
    return this;
  }

  /**
   * Get serviceOwner
   * @return serviceOwner
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public ServiceOwner getServiceOwner() {
    return serviceOwner;
  }

  public void setServiceOwner(ServiceOwner serviceOwner) {
    this.serviceOwner = serviceOwner;
  }

  public VehicleRapidPassRequest vehicles(VehicleCollection vehicles) {
    this.vehicles = vehicles;
    return this;
  }

  /**
   * Get vehicles
   * @return vehicles
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public VehicleCollection getVehicles() {
    return vehicles;
  }

  public void setVehicles(VehicleCollection vehicles) {
    this.vehicles = vehicles;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VehicleRapidPassRequest vehicleRapidPassRequest = (VehicleRapidPassRequest) o;
    return Objects.equals(this.serviceOwner, vehicleRapidPassRequest.serviceOwner) &&
        Objects.equals(this.vehicles, vehicleRapidPassRequest.vehicles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serviceOwner, vehicles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VehicleRapidPassRequest {\n");
    
    sb.append("    serviceOwner: ").append(toIndentedString(serviceOwner)).append("\n");
    sb.append("    vehicles: ").append(toIndentedString(vehicles)).append("\n");
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
