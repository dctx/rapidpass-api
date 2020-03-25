package com.devcon.rapidpass.models;

import java.util.Objects;
import com.devcon.rapidpass.models.Link;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Vehicle
 */
@Validated
public class Vehicle   {
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("serviceOwnerLink")
  private Link serviceOwnerLink = null;

  @JsonProperty("brand")
  private String brand = null;

  @JsonProperty("model")
  private String model = null;

  @JsonProperty("plateNumber")
  private String plateNumber = null;

  @JsonProperty("orNumber")
  private String orNumber = null;

  @JsonProperty("crNumber")
  private String crNumber = null;

  @JsonProperty("photo")
  private String photo = null;

  public Vehicle id(Long id) {
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

  public Vehicle serviceOwnerLink(Link serviceOwnerLink) {
    this.serviceOwnerLink = serviceOwnerLink;
    return this;
  }

  /**
   * Get serviceOwnerLink
   * @return serviceOwnerLink
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public Link getServiceOwnerLink() {
    return serviceOwnerLink;
  }

  public void setServiceOwnerLink(Link serviceOwnerLink) {
    this.serviceOwnerLink = serviceOwnerLink;
  }

  public Vehicle brand(String brand) {
    this.brand = brand;
    return this;
  }

  /**
   * Brand of the vehicle
   * @return brand
  **/
  @ApiModelProperty(value = "Brand of the vehicle")
  
    public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public Vehicle model(String model) {
    this.model = model;
    return this;
  }

  /**
   * Model of the vehicle
   * @return model
  **/
  @ApiModelProperty(example = "CX-3 AWD 2018", value = "Model of the vehicle")
  
    public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Vehicle plateNumber(String plateNumber) {
    this.plateNumber = plateNumber;
    return this;
  }

  /**
   * Plate number of the vehicle
   * @return plateNumber
  **/
  @ApiModelProperty(value = "Plate number of the vehicle")
  
    public String getPlateNumber() {
    return plateNumber;
  }

  public void setPlateNumber(String plateNumber) {
    this.plateNumber = plateNumber;
  }

  public Vehicle orNumber(String orNumber) {
    this.orNumber = orNumber;
    return this;
  }

  /**
   * Official Receipt of the vehicle registration
   * @return orNumber
  **/
  @ApiModelProperty(value = "Official Receipt of the vehicle registration")
  
    public String getOrNumber() {
    return orNumber;
  }

  public void setOrNumber(String orNumber) {
    this.orNumber = orNumber;
  }

  public Vehicle crNumber(String crNumber) {
    this.crNumber = crNumber;
    return this;
  }

  /**
   * Certificate of Registration Number of the vehicle.
   * @return crNumber
  **/
  @ApiModelProperty(value = "Certificate of Registration Number of the vehicle.")
  
    public String getCrNumber() {
    return crNumber;
  }

  public void setCrNumber(String crNumber) {
    this.crNumber = crNumber;
  }

  public Vehicle photo(String photo) {
    this.photo = photo;
    return this;
  }

  /**
   * Base64 image stored to image/blob storage on posting of vehicle
   * @return photo
  **/
  @ApiModelProperty(example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAkCAYAAADhAJiYAAAAAXNSR0IArs4c6QAAADhlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAAqACAAQAAAABAAAAJKADAAQAAAABAAAAJAAAAAAJxsHGAAACIUlEQVRYCe1X0VHDMAxtOf7pBngDskG9ASMQJoAN6AawQcMGZYMwAWWCZgOyAbxXrJ7rRrabpAcffXc6yZLes7BD2k4neTBou4EVzmbwNK67sEayhTXOuP50MVw/WNCeYRvY90hGLWpaWDYsOj9gYw2h6XAP7hUFp9cETpXnnjtMd9FkUiJeeuuu8A1JPg/HoEDzbYJwj3oV9qyQSJ2CDUkZa3JSutx7i0sJ4GderIVzFCh+DMhJYbe3P1CKxPoip2lIz8UQ8qm4JYS1O65QM7AUhG8TjQb1Cib9oS9RU4sNi5kQYZvZ36JPOHs+dmVNpniftrVGig2kcU6aPw+UOt7zCeWcED/Y/gv2Zqkxlf9O4PeVXAjPZhI26BMOfS282DNUoKmUxhE9NY2ml/pwXYL4BGs0gSDPL1t8C2swKNBUpAYi0ThjnAJPdRBiVzZIuC85NdA7hPnzZSxQi5pZqNHlP/n+8T8GNb8vN6aGgNo+r5aC75mUppVfcLHU+vi2Q69GTrQYb6Fd2bU0OD8L1scur0AINZg7gDZQgc4H100h/jsPBTVkKGpzjyhqVOUIxX915KTW13dpcu8ttBOSuvxFsh7DRzX9gV7G2K2nRi28qQTO81452DzIy/IVQSOLTG/Qd6f08p1UOVNafn8Mas+HVVl6gRxNbxHS/CsLa3+y7hqoiUyyjtS00ih6fI5amBw1RUtYX5QgNjDRozb3OMAPFqsUgzS7WrkAAAAASUVORK5CYII=", value = "Base64 image stored to image/blob storage on posting of vehicle")
  
    public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Vehicle vehicle = (Vehicle) o;
    return Objects.equals(this.id, vehicle.id) &&
        Objects.equals(this.serviceOwnerLink, vehicle.serviceOwnerLink) &&
        Objects.equals(this.brand, vehicle.brand) &&
        Objects.equals(this.model, vehicle.model) &&
        Objects.equals(this.plateNumber, vehicle.plateNumber) &&
        Objects.equals(this.orNumber, vehicle.orNumber) &&
        Objects.equals(this.crNumber, vehicle.crNumber) &&
        Objects.equals(this.photo, vehicle.photo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, serviceOwnerLink, brand, model, plateNumber, orNumber, crNumber, photo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Vehicle {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    serviceOwnerLink: ").append(toIndentedString(serviceOwnerLink)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
    sb.append("    plateNumber: ").append(toIndentedString(plateNumber)).append("\n");
    sb.append("    orNumber: ").append(toIndentedString(orNumber)).append("\n");
    sb.append("    crNumber: ").append(toIndentedString(crNumber)).append("\n");
    sb.append("    photo: ").append(toIndentedString(photo)).append("\n");
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
