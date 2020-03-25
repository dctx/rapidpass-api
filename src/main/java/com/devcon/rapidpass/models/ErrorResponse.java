package com.devcon.rapidpass.models;

import java.util.Objects;
import com.devcon.rapidpass.models.FieldError;
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
 * ErrorResponse
 */
@Validated
public class ErrorResponse   {
  @JsonProperty("errorKey")
  private String errorKey = null;

  @JsonProperty("errorMessage")
  private String errorMessage = null;

  @JsonProperty("fieldErrors")
  @Valid
  private List<FieldError> fieldErrors = null;

  public ErrorResponse errorKey(String errorKey) {
    this.errorKey = errorKey;
    return this;
  }

  /**
   * error key that can be used in error bundle message
   * @return errorKey
  **/
  @ApiModelProperty(example = "invalid.data", value = "error key that can be used in error bundle message")
  
    public String getErrorKey() {
    return errorKey;
  }

  public void setErrorKey(String errorKey) {
    this.errorKey = errorKey;
  }

  public ErrorResponse errorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  /**
   * user readable error message.
   * @return errorMessage
  **/
  @ApiModelProperty(example = "Invalid data", value = "user readable error message.")
  
    public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public ErrorResponse fieldErrors(List<FieldError> fieldErrors) {
    this.fieldErrors = fieldErrors;
    return this;
  }

  public ErrorResponse addFieldErrorsItem(FieldError fieldErrorsItem) {
    if (this.fieldErrors == null) {
      this.fieldErrors = new ArrayList<>();
    }
    this.fieldErrors.add(fieldErrorsItem);
    return this;
  }

  /**
   * Get fieldErrors
   * @return fieldErrors
  **/
  @ApiModelProperty(value = "")
      @Valid
    public List<FieldError> getFieldErrors() {
    return fieldErrors;
  }

  public void setFieldErrors(List<FieldError> fieldErrors) {
    this.fieldErrors = fieldErrors;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorResponse errorResponse = (ErrorResponse) o;
    return Objects.equals(this.errorKey, errorResponse.errorKey) &&
        Objects.equals(this.errorMessage, errorResponse.errorMessage) &&
        Objects.equals(this.fieldErrors, errorResponse.fieldErrors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorKey, errorMessage, fieldErrors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorResponse {\n");
    
    sb.append("    errorKey: ").append(toIndentedString(errorKey)).append("\n");
    sb.append("    errorMessage: ").append(toIndentedString(errorMessage)).append("\n");
    sb.append("    fieldErrors: ").append(toIndentedString(fieldErrors)).append("\n");
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
