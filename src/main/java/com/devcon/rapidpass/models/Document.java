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
 * Document Image that is submitted when applying for an Rapid Pass
 */
@ApiModel(description = "Document Image that is submitted when applying for an Rapid Pass")
@Validated
public class Document   {
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("ownerId")
  private Long ownerId = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("image")
  private String image = null;

  public Document id(Long id) {
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

  public Document ownerId(Long ownerId) {
    this.ownerId = ownerId;
    return this;
  }

  /**
   * Get ownerId
   * @return ownerId
  **/
  @ApiModelProperty(value = "")
  
    public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public Document name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the document presented
   * @return name
  **/
  @ApiModelProperty(example = "Sample Dummy Document", value = "Name of the document presented")
  
    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Document image(String image) {
    this.image = image;
    return this;
  }

  /**
   * Base64 encoded image of the document
   * @return image
  **/
  @ApiModelProperty(example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAkCAYAAADhAJiYAAAAAXNSR0IArs4c6QAAADhlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAAqACAAQAAAABAAAAJKADAAQAAAABAAAAJAAAAAAJxsHGAAACIUlEQVRYCe1X0VHDMAxtOf7pBngDskG9ASMQJoAN6AawQcMGZYMwAWWCZgOyAbxXrJ7rRrabpAcffXc6yZLes7BD2k4neTBou4EVzmbwNK67sEayhTXOuP50MVw/WNCeYRvY90hGLWpaWDYsOj9gYw2h6XAP7hUFp9cETpXnnjtMd9FkUiJeeuuu8A1JPg/HoEDzbYJwj3oV9qyQSJ2CDUkZa3JSutx7i0sJ4GderIVzFCh+DMhJYbe3P1CKxPoip2lIz8UQ8qm4JYS1O65QM7AUhG8TjQb1Cib9oS9RU4sNi5kQYZvZ36JPOHs+dmVNpniftrVGig2kcU6aPw+UOt7zCeWcED/Y/gv2Zqkxlf9O4PeVXAjPZhI26BMOfS282DNUoKmUxhE9NY2ml/pwXYL4BGs0gSDPL1t8C2swKNBUpAYi0ThjnAJPdRBiVzZIuC85NdA7hPnzZSxQi5pZqNHlP/n+8T8GNb8vN6aGgNo+r5aC75mUppVfcLHU+vi2Q69GTrQYb6Fd2bU0OD8L1scur0AINZg7gDZQgc4H100h/jsPBTVkKGpzjyhqVOUIxX915KTW13dpcu8ttBOSuvxFsh7DRzX9gV7G2K2nRi28qQTO81452DzIy/IVQSOLTG/Qd6f08p1UOVNafn8Mas+HVVl6gRxNbxHS/CsLa3+y7hqoiUyyjtS00ih6fI5amBw1RUtYX5QgNjDRozb3OMAPFqsUgzS7WrkAAAAASUVORK5CYII=", value = "Base64 encoded image of the document")
  
    public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Document document = (Document) o;
    return Objects.equals(this.id, document.id) &&
        Objects.equals(this.ownerId, document.ownerId) &&
        Objects.equals(this.name, document.name) &&
        Objects.equals(this.image, document.image);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ownerId, name, image);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Document {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    ownerId: ").append(toIndentedString(ownerId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    image: ").append(toIndentedString(image)).append("\n");
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
