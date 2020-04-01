package ph.devcon.rapidpass.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * Page Meta Data
 */
@ApiModel(description = "Page Meta Data")
@Validated
public class PageMetaData   {
  @JsonProperty("pageNumber")
  private Integer pageNumber = null;

  @JsonProperty("pageSize")
  private Integer pageSize = null;

  @JsonProperty("totalPages")
  private Integer totalPages = null;

  @JsonProperty("totalRows")
  private Long totalRows = null;

  public PageMetaData pageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
    return this;
  }

  /**
   * Get pageNumber
   * @return pageNumber
  **/
  @ApiModelProperty(value = "")
  
    public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public PageMetaData pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * Get pageSize
   * @return pageSize
  **/
  @ApiModelProperty(value = "")
  
    public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public PageMetaData totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Get totalPages
   * @return totalPages
  **/
  @ApiModelProperty(value = "")
  
    public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public PageMetaData totalRows(Long totalRows) {
    this.totalRows = totalRows;
    return this;
  }

  /**
   * total number of rows in whole dataset
   * @return totalRows
  **/
  @ApiModelProperty(value = "total number of rows in whole dataset")
  
    public Long getTotalRows() {
    return totalRows;
  }

  public void setTotalRows(Long totalRows) {
    this.totalRows = totalRows;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageMetaData pageMetaData = (PageMetaData) o;
    return Objects.equals(this.pageNumber, pageMetaData.pageNumber) &&
        Objects.equals(this.pageSize, pageMetaData.pageSize) &&
        Objects.equals(this.totalPages, pageMetaData.totalPages) &&
        Objects.equals(this.totalRows, pageMetaData.totalRows);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, totalPages, totalRows);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PageMetaData {\n");
    
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    totalRows: ").append(toIndentedString(totalRows)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
