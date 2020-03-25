package com.devcon.rapidpass.models;

import java.util.Objects;
import com.devcon.rapidpass.models.Address;
import com.devcon.rapidpass.models.DocumentCollection;
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
 * Authority details
 */
@ApiModel(description = "Authority details")
@Validated
public class RapidPassAuthority extends RapidPassUser  {
  @JsonProperty("role")
  private String role = null;

  @JsonProperty("organization")
  private String organization = null;

  public RapidPassAuthority role(String role) {
    this.role = role;
    return this;
  }

  /**
   * Role or title of the approving person relative to the organizaton or task force.
   * @return role
  **/
  @ApiModelProperty(example = "Head of Infectious Disease Outbreak Control", value = "Role or title of the approving person relative to the organizaton or task force.")
  
    public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public RapidPassAuthority organization(String organization) {
    this.organization = organization;
    return this;
  }

  /**
   * Name of the approving organization or task force
   * @return organization
  **/
  @ApiModelProperty(example = "AFP", value = "Name of the approving organization or task force")
  
    public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RapidPassAuthority rapidPassAuthority = (RapidPassAuthority) o;
    return Objects.equals(this.role, rapidPassAuthority.role) &&
        Objects.equals(this.organization, rapidPassAuthority.organization) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(role, organization, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RapidPassAuthority {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    organization: ").append(toIndentedString(organization)).append("\n");
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
