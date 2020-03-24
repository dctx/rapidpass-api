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
 * Work Category
 */
@ApiModel(description = "Work Category")
@Validated
public class WorkCategory   {
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("categoryName")
  private String categoryName = null;

  @JsonProperty("description")
  private String description = null;

  public WorkCategory id(Long id) {
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

  public WorkCategory categoryName(String categoryName) {
    this.categoryName = categoryName;
    return this;
  }

  /**
   * Work category name clearly indicating the type of work done.
   * @return categoryName
  **/
  @ApiModelProperty(value = "Work category name clearly indicating the type of work done.")
  
    public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public WorkCategory description(String description) {
    this.description = description;
    return this;
  }

  /**
   * More detailed description of the work category
   * @return description
  **/
  @ApiModelProperty(value = "More detailed description of the work category")
  
    public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WorkCategory workCategory = (WorkCategory) o;
    return Objects.equals(this.id, workCategory.id) &&
        Objects.equals(this.categoryName, workCategory.categoryName) &&
        Objects.equals(this.description, workCategory.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, categoryName, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WorkCategory {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    categoryName: ").append(toIndentedString(categoryName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
