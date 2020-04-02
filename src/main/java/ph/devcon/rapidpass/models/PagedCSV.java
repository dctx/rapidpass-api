package ph.devcon.rapidpass.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Objects;

/**
 * Paged CSV with meta data for client to know what page has been downloaded
 */
@ApiModel(description = "Paged CSV with meta data for client to know what page has been downloaded")
@Validated
public class PagedCSV   {
  @JsonProperty("meta")
  private PageMetaData meta = null;

  @JsonProperty("csv")
  private String csv = null;

  public PagedCSV meta(PageMetaData meta) {
    this.meta = meta;
    return this;
  }

  /**
   * Get meta
   * @return meta
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public PageMetaData getMeta() {
    return meta;
  }

  public void setMeta(PageMetaData meta) {
    this.meta = meta;
  }

  public PagedCSV csv(String csv) {
    this.csv = csv;
    return this;
  }

  /**
   * string containg CSV
   * @return csv
  **/
  @ApiModelProperty(value = "string containg CSV")
  
    public String getCsv() {
    return csv;
  }

  public void setCsv(String csv) {
    this.csv = csv;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PagedCSV pagedCSV = (PagedCSV) o;
    return Objects.equals(this.meta, pagedCSV.meta) &&
        Objects.equals(this.csv, pagedCSV.csv);
  }

  @Override
  public int hashCode() {
    return Objects.hash(meta, csv);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PagedCSV {\n");
    
    sb.append("    meta: ").append(toIndentedString(meta)).append("\n");
    sb.append("    csv: ").append(toIndentedString(csv)).append("\n");
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
