package com.devcon.rapidpass.models;

import java.util.Objects;
import com.devcon.rapidpass.models.RapidPassAuthority;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * RapidPassStatusUpdate
 */
@Validated
public class RapidPassStatusUpdate   {
  @JsonProperty("status")
  private String status = null;

  @JsonProperty("authority")
  private RapidPassAuthority authority = null;

  public RapidPassStatusUpdate status(String status) {
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

  public RapidPassStatusUpdate authority(RapidPassAuthority authority) {
    this.authority = authority;
    return this;
  }

  /**
   * Get authority
   * @return authority
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public RapidPassAuthority getAuthority() {
    return authority;
  }

  public void setAuthority(RapidPassAuthority authority) {
    this.authority = authority;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RapidPassStatusUpdate rapidPassStatusUpdate = (RapidPassStatusUpdate) o;
    return Objects.equals(this.status, rapidPassStatusUpdate.status) &&
        Objects.equals(this.authority, rapidPassStatusUpdate.authority);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, authority);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RapidPassStatusUpdate {\n");
    
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    authority: ").append(toIndentedString(authority)).append("\n");
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
