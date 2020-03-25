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
 * Link
 */
@Validated
public class Link   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("href")
  private String href = null;

  @JsonProperty("templated")
  private Boolean templated = null;

  public Link name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the document or information in the link.
   * @return name
  **/
  @ApiModelProperty(example = "Supporting document", value = "Name of the document or information in the link.")
  
    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Link href(String href) {
    this.href = href;
    return this;
  }

  /**
   * Get href
   * @return href
  **/
  @ApiModelProperty(example = "/{resource} . . .", value = "")
  
    public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public Link templated(Boolean templated) {
    this.templated = templated;
    return this;
  }

  /**
   * Get templated
   * @return templated
  **/
  @ApiModelProperty(value = "")
  
    public Boolean isTemplated() {
    return templated;
  }

  public void setTemplated(Boolean templated) {
    this.templated = templated;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Link link = (Link) o;
    return Objects.equals(this.name, link.name) &&
        Objects.equals(this.href, link.href) &&
        Objects.equals(this.templated, link.templated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, href, templated);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Link {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    templated: ").append(toIndentedString(templated)).append("\n");
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
