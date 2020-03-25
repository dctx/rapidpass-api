package com.devcon.rapidpass.models;

import java.util.Objects;
import com.devcon.rapidpass.models.Address;
import com.devcon.rapidpass.models.AddressCollection;
import com.devcon.rapidpass.models.CheckpointCollection;
import com.devcon.rapidpass.models.IndividualRapidPass;
import com.devcon.rapidpass.models.RapidPassRequestResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * An individual who applied for an Rapid Pass with a role of a driver. Additional data element is added.
 */
@ApiModel(description = "An individual who applied for an Rapid Pass with a role of a driver. Additional data element is added.")
@Validated
public class VehicleDriver extends IndividualRapidPass  {
  @JsonProperty("driversLicenseNo")
  private String driversLicenseNo = null;

  @JsonProperty("driversLicenseValidFrom")
  private LocalDate driversLicenseValidFrom = null;

  @JsonProperty("driversLicenseValidUntil")
  private LocalDate driversLicenseValidUntil = null;

  public VehicleDriver driversLicenseNo(String driversLicenseNo) {
    this.driversLicenseNo = driversLicenseNo;
    return this;
  }

  /**
   * Driver's License Number
   * @return driversLicenseNo
  **/
  @ApiModelProperty(value = "Driver's License Number")
  
    public String getDriversLicenseNo() {
    return driversLicenseNo;
  }

  public void setDriversLicenseNo(String driversLicenseNo) {
    this.driversLicenseNo = driversLicenseNo;
  }

  public VehicleDriver driversLicenseValidFrom(LocalDate driversLicenseValidFrom) {
    this.driversLicenseValidFrom = driversLicenseValidFrom;
    return this;
  }

  /**
   * Validity of the driver's license
   * @return driversLicenseValidFrom
  **/
  @ApiModelProperty(value = "Validity of the driver's license")
  
    @Valid
    public LocalDate getDriversLicenseValidFrom() {
    return driversLicenseValidFrom;
  }

  public void setDriversLicenseValidFrom(LocalDate driversLicenseValidFrom) {
    this.driversLicenseValidFrom = driversLicenseValidFrom;
  }

  public VehicleDriver driversLicenseValidUntil(LocalDate driversLicenseValidUntil) {
    this.driversLicenseValidUntil = driversLicenseValidUntil;
    return this;
  }

  /**
   * Validity of the driver's license
   * @return driversLicenseValidUntil
  **/
  @ApiModelProperty(value = "Validity of the driver's license")
  
    @Valid
    public LocalDate getDriversLicenseValidUntil() {
    return driversLicenseValidUntil;
  }

  public void setDriversLicenseValidUntil(LocalDate driversLicenseValidUntil) {
    this.driversLicenseValidUntil = driversLicenseValidUntil;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VehicleDriver vehicleDriver = (VehicleDriver) o;
    return Objects.equals(this.driversLicenseNo, vehicleDriver.driversLicenseNo) &&
        Objects.equals(this.driversLicenseValidFrom, vehicleDriver.driversLicenseValidFrom) &&
        Objects.equals(this.driversLicenseValidUntil, vehicleDriver.driversLicenseValidUntil) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(driversLicenseNo, driversLicenseValidFrom, driversLicenseValidUntil, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VehicleDriver {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    driversLicenseNo: ").append(toIndentedString(driversLicenseNo)).append("\n");
    sb.append("    driversLicenseValidFrom: ").append(toIndentedString(driversLicenseValidFrom)).append("\n");
    sb.append("    driversLicenseValidUntil: ").append(toIndentedString(driversLicenseValidUntil)).append("\n");
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
