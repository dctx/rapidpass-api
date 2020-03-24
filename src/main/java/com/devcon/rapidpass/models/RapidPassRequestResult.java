package com.devcon.rapidpass.models;

import java.util.Objects;
import com.devcon.rapidpass.models.RapidPassAuthority;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Approval result detail of the Rapid Pass
 */
@ApiModel(description = "Approval result detail of the Rapid Pass")
@Validated
public class RapidPassRequestResult   {
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("qrCode")
  private String qrCode = null;

  @JsonProperty("effectivityDate")
  private LocalDate effectivityDate = null;

  @JsonProperty("duration")
  private Integer duration = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("approvingEntity")
  private RapidPassAuthority approvingEntity = null;

  public RapidPassRequestResult id(Long id) {
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

  public RapidPassRequestResult qrCode(String qrCode) {
    this.qrCode = qrCode;
    return this;
  }

  /**
   * Get qrCode
   * @return qrCode
  **/
  @ApiModelProperty(value = "")
  
    public String getQrCode() {
    return qrCode;
  }

  public void setQrCode(String qrCode) {
    this.qrCode = qrCode;
  }

  public RapidPassRequestResult effectivityDate(LocalDate effectivityDate) {
    this.effectivityDate = effectivityDate;
    return this;
  }

  /**
   * Effective date that the pass is active. Minimum is today + 1
   * @return effectivityDate
  **/
  @ApiModelProperty(example = "Wed Apr 01 08:00:00 PST 2020", value = "Effective date that the pass is active. Minimum is today + 1")
  
    @Valid
    public LocalDate getEffectivityDate() {
    return effectivityDate;
  }

  public void setEffectivityDate(LocalDate effectivityDate) {
    this.effectivityDate = effectivityDate;
  }

  public RapidPassRequestResult duration(Integer duration) {
    this.duration = duration;
    return this;
  }

  /**
   * Duration in hours that the pass can be active relative to its effectivity date
   * @return duration
  **/
  @ApiModelProperty(value = "Duration in hours that the pass can be active relative to its effectivity date")
  
    public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public RapidPassRequestResult status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  **/
  @ApiModelProperty(value = "")
  
    public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public RapidPassRequestResult approvingEntity(RapidPassAuthority approvingEntity) {
    this.approvingEntity = approvingEntity;
    return this;
  }

  /**
   * Get approvingEntity
   * @return approvingEntity
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public RapidPassAuthority getApprovingEntity() {
    return approvingEntity;
  }

  public void setApprovingEntity(RapidPassAuthority approvingEntity) {
    this.approvingEntity = approvingEntity;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RapidPassRequestResult rapidPassRequestResult = (RapidPassRequestResult) o;
    return Objects.equals(this.id, rapidPassRequestResult.id) &&
        Objects.equals(this.qrCode, rapidPassRequestResult.qrCode) &&
        Objects.equals(this.effectivityDate, rapidPassRequestResult.effectivityDate) &&
        Objects.equals(this.duration, rapidPassRequestResult.duration) &&
        Objects.equals(this.status, rapidPassRequestResult.status) &&
        Objects.equals(this.approvingEntity, rapidPassRequestResult.approvingEntity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, qrCode, effectivityDate, duration, status, approvingEntity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RapidPassRequestResult {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    qrCode: ").append(toIndentedString(qrCode)).append("\n");
    sb.append("    effectivityDate: ").append(toIndentedString(effectivityDate)).append("\n");
    sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    approvingEntity: ").append(toIndentedString(approvingEntity)).append("\n");
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
