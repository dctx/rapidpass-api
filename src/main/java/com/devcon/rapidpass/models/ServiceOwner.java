package com.devcon.rapidpass.models;

import java.util.Objects;
import com.devcon.rapidpass.models.Address;
import com.devcon.rapidpass.models.DocumentCollection;
import com.devcon.rapidpass.models.Link;
import com.devcon.rapidpass.models.RapidPassUser;
import com.devcon.rapidpass.models.WorkCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ServiceOwner
 */
@Validated
public class ServiceOwner extends RapidPassUser  {
  @JsonProperty("serviceAccessTypes")
  @Valid
  private List<String> serviceAccessTypes = null;

  @JsonProperty("vehicles")
  @Valid
  private List<Link> vehicles = null;

  @JsonProperty("supportingBusinessDocuments")
  @Valid
  private List<Link> supportingBusinessDocuments = null;

  public ServiceOwner serviceAccessTypes(List<String> serviceAccessTypes) {
    this.serviceAccessTypes = serviceAccessTypes;
    return this;
  }

  public ServiceOwner addServiceAccessTypesItem(String serviceAccessTypesItem) {
    if (this.serviceAccessTypes == null) {
      this.serviceAccessTypes = new ArrayList<>();
    }
    this.serviceAccessTypes.add(serviceAccessTypesItem);
    return this;
  }

  /**
   * Get serviceAccessTypes
   * @return serviceAccessTypes
  **/
  @ApiModelProperty(value = "")
  
    public List<String> getServiceAccessTypes() {
    return serviceAccessTypes;
  }

  public void setServiceAccessTypes(List<String> serviceAccessTypes) {
    this.serviceAccessTypes = serviceAccessTypes;
  }

  public ServiceOwner vehicles(List<Link> vehicles) {
    this.vehicles = vehicles;
    return this;
  }

  public ServiceOwner addVehiclesItem(Link vehiclesItem) {
    if (this.vehicles == null) {
      this.vehicles = new ArrayList<>();
    }
    this.vehicles.add(vehiclesItem);
    return this;
  }

  /**
   * Link to vehicle records
   * @return vehicles
  **/
  @ApiModelProperty(value = "Link to vehicle records")
      @Valid
    public List<Link> getVehicles() {
    return vehicles;
  }

  public void setVehicles(List<Link> vehicles) {
    this.vehicles = vehicles;
  }

  public ServiceOwner supportingBusinessDocuments(List<Link> supportingBusinessDocuments) {
    this.supportingBusinessDocuments = supportingBusinessDocuments;
    return this;
  }

  public ServiceOwner addSupportingBusinessDocumentsItem(Link supportingBusinessDocumentsItem) {
    if (this.supportingBusinessDocuments == null) {
      this.supportingBusinessDocuments = new ArrayList<>();
    }
    this.supportingBusinessDocuments.add(supportingBusinessDocumentsItem);
    return this;
  }

  /**
   * Links to business documents
   * @return supportingBusinessDocuments
  **/
  @ApiModelProperty(value = "Links to business documents")
      @Valid
    public List<Link> getSupportingBusinessDocuments() {
    return supportingBusinessDocuments;
  }

  public void setSupportingBusinessDocuments(List<Link> supportingBusinessDocuments) {
    this.supportingBusinessDocuments = supportingBusinessDocuments;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServiceOwner serviceOwner = (ServiceOwner) o;
    return Objects.equals(this.serviceAccessTypes, serviceOwner.serviceAccessTypes) &&
        Objects.equals(this.vehicles, serviceOwner.vehicles) &&
        Objects.equals(this.supportingBusinessDocuments, serviceOwner.supportingBusinessDocuments) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serviceAccessTypes, vehicles, supportingBusinessDocuments, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServiceOwner {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    serviceAccessTypes: ").append(toIndentedString(serviceAccessTypes)).append("\n");
    sb.append("    vehicles: ").append(toIndentedString(vehicles)).append("\n");
    sb.append("    supportingBusinessDocuments: ").append(toIndentedString(supportingBusinessDocuments)).append("\n");
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
