package ph.devcon.rapidpass.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * Page Meta Data
 */
@ApiModel(description = "Page Meta Data")
@Validated
@Data
@Builder
public class PageMetaData   {

  private Integer currentPage = null;
  private Integer currentPageRows = null;
  private Integer totalPages = null;
  private Long totalRows = null;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageMetaData pageMetaData = (PageMetaData) o;
    return Objects.equals(this.currentPage, pageMetaData.currentPage) &&
        Objects.equals(this.currentPageRows, pageMetaData.currentPageRows) &&
        Objects.equals(this.totalPages, pageMetaData.totalPages) &&
        Objects.equals(this.totalRows, pageMetaData.totalRows);
  }

}
