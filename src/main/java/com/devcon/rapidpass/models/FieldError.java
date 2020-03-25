package com.devcon.rapidpass.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FieldError
 */
@Validated
public class FieldError   {
  @JsonProperty("fieldName")
  private String fieldName = null;

  @JsonProperty("fieldValue")
  private String fieldValue = null;

  @JsonProperty("fieldErrorKey")
  private String fieldErrorKey = null;

  @JsonProperty("fieldErrorMessage")
  private String fieldErrorMessage = null;

  public FieldError fieldName(String fieldName) {
    this.fieldName = fieldName;
    return this;
  }

  /**
   * field or attribute name in error.
   * @return fieldName
  **/
  @ApiModelProperty(example = "address.street", value = "field or attribute name in error.")
  
    public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public FieldError fieldValue(String fieldValue) {
    this.fieldValue = fieldValue;
    return this;
  }

  /**
   * value of the field that is causing error
   * @return fieldValue
  **/
  @ApiModelProperty(example = "nobody street", value = "value of the field that is causing error")
  
    public String getFieldValue() {
    return fieldValue;
  }

  public void setFieldValue(String fieldValue) {
    this.fieldValue = fieldValue;
  }

  public FieldError fieldErrorKey(String fieldErrorKey) {
    this.fieldErrorKey = fieldErrorKey;
    return this;
  }

  /**
   * bundle error key that can be used by UI to display the proper error
   * @return fieldErrorKey
  **/
  @ApiModelProperty(example = "invalid.address.street", value = "bundle error key that can be used by UI to display the proper error")
  
    public String getFieldErrorKey() {
    return fieldErrorKey;
  }

  public void setFieldErrorKey(String fieldErrorKey) {
    this.fieldErrorKey = fieldErrorKey;
  }

  public FieldError fieldErrorMessage(String fieldErrorMessage) {
    this.fieldErrorMessage = fieldErrorMessage;
    return this;
  }

  /**
   * User readable error message specific to the field
   * @return fieldErrorMessage
  **/
  @ApiModelProperty(example = "Invalid street in address", value = "User readable error message specific to the field")
  
    public String getFieldErrorMessage() {
    return fieldErrorMessage;
  }

  public void setFieldErrorMessage(String fieldErrorMessage) {
    this.fieldErrorMessage = fieldErrorMessage;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FieldError fieldError = (FieldError) o;
    return Objects.equals(this.fieldName, fieldError.fieldName) &&
        Objects.equals(this.fieldValue, fieldError.fieldValue) &&
        Objects.equals(this.fieldErrorKey, fieldError.fieldErrorKey) &&
        Objects.equals(this.fieldErrorMessage, fieldError.fieldErrorMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldName, fieldValue, fieldErrorKey, fieldErrorMessage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FieldError {\n");
    
    sb.append("    fieldName: ").append(toIndentedString(fieldName)).append("\n");
    sb.append("    fieldValue: ").append(toIndentedString(fieldValue)).append("\n");
    sb.append("    fieldErrorKey: ").append(toIndentedString(fieldErrorKey)).append("\n");
    sb.append("    fieldErrorMessage: ").append(toIndentedString(fieldErrorMessage)).append("\n");
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
