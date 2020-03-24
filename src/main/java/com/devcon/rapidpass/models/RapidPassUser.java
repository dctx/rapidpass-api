package com.devcon.rapidpass.models;

import java.util.Objects;
import com.devcon.rapidpass.models.Address;
import com.devcon.rapidpass.models.DocumentCollection;
import com.devcon.rapidpass.models.WorkCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Basic Data about the Rapid Pass user. This will be the base entity for all individuals who will use and have different roles in the Rapid Pass
 */
@ApiModel(description = "Basic Data about the Rapid Pass user. This will be the base entity for all individuals who will use and have different roles in the Rapid Pass")
@Validated
public class RapidPassUser   {
  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("firstName")
  private String firstName = null;

  @JsonProperty("middleName")
  private String middleName = null;

  @JsonProperty("lastName")
  private String lastName = null;

  @JsonProperty("emailAddress")
  private String emailAddress = null;

  @JsonProperty("mobilePhoneNumber")
  private String mobilePhoneNumber = null;

  @JsonProperty("company")
  private String company = null;

  @JsonProperty("workCategory")
  private WorkCategory workCategory = null;

  @JsonProperty("identificationCardNumber")
  private String identificationCardNumber = null;

  @JsonProperty("identificationcardType")
  private String identificationcardType = null;

  @JsonProperty("address")
  private Address address = null;

  @JsonProperty("documentsPresented")
  private DocumentCollection documentsPresented = null;

  public RapidPassUser id(Long id) {
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

  public RapidPassUser firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Firstname of the rapid pass holder
   * @return firstName
  **/
  @ApiModelProperty(example = "Joana", value = "Firstname of the rapid pass holder")
  
    public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public RapidPassUser middleName(String middleName) {
    this.middleName = middleName;
    return this;
  }

  /**
   * Firstname of the rapid pass holder
   * @return middleName
  **/
  @ApiModelProperty(example = "Joana", value = "Firstname of the rapid pass holder")
  
    public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public RapidPassUser lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Lastname of the rapid pass holder
   * @return lastName
  **/
  @ApiModelProperty(example = "Dipahuhuli", value = "Lastname of the rapid pass holder")
  
    public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public RapidPassUser emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Email of the rapid pass holder.
   * @return emailAddress
  **/
  @ApiModelProperty(value = "Email of the rapid pass holder.")
  
    public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public RapidPassUser mobilePhoneNumber(String mobilePhoneNumber) {
    this.mobilePhoneNumber = mobilePhoneNumber;
    return this;
  }

  /**
   * Mobile phone number
   * @return mobilePhoneNumber
  **/
  @ApiModelProperty(value = "Mobile phone number")
  
    public String getMobilePhoneNumber() {
    return mobilePhoneNumber;
  }

  public void setMobilePhoneNumber(String mobilePhoneNumber) {
    this.mobilePhoneNumber = mobilePhoneNumber;
  }

  public RapidPassUser company(String company) {
    this.company = company;
    return this;
  }

  /**
   * Company Name
   * @return company
  **/
  @ApiModelProperty(value = "Company Name")
  
    public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public RapidPassUser workCategory(WorkCategory workCategory) {
    this.workCategory = workCategory;
    return this;
  }

  /**
   * Get workCategory
   * @return workCategory
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public WorkCategory getWorkCategory() {
    return workCategory;
  }

  public void setWorkCategory(WorkCategory workCategory) {
    this.workCategory = workCategory;
  }

  public RapidPassUser identificationCardNumber(String identificationCardNumber) {
    this.identificationCardNumber = identificationCardNumber;
    return this;
  }

  /**
   * ID number presented
   * @return identificationCardNumber
  **/
  @ApiModelProperty(example = "95-001", value = "ID number presented")
  
    public String getIdentificationCardNumber() {
    return identificationCardNumber;
  }

  public void setIdentificationCardNumber(String identificationCardNumber) {
    this.identificationCardNumber = identificationCardNumber;
  }

  public RapidPassUser identificationcardType(String identificationcardType) {
    this.identificationcardType = identificationcardType;
    return this;
  }

  /**
   * Type of the ID
   * @return identificationcardType
  **/
  @ApiModelProperty(example = "Company ID", value = "Type of the ID")
  
    public String getIdentificationcardType() {
    return identificationcardType;
  }

  public void setIdentificationcardType(String identificationcardType) {
    this.identificationcardType = identificationcardType;
  }

  public RapidPassUser address(Address address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public RapidPassUser documentsPresented(DocumentCollection documentsPresented) {
    this.documentsPresented = documentsPresented;
    return this;
  }

  /**
   * Get documentsPresented
   * @return documentsPresented
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public DocumentCollection getDocumentsPresented() {
    return documentsPresented;
  }

  public void setDocumentsPresented(DocumentCollection documentsPresented) {
    this.documentsPresented = documentsPresented;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RapidPassUser rapidPassUser = (RapidPassUser) o;
    return Objects.equals(this.id, rapidPassUser.id) &&
        Objects.equals(this.firstName, rapidPassUser.firstName) &&
        Objects.equals(this.middleName, rapidPassUser.middleName) &&
        Objects.equals(this.lastName, rapidPassUser.lastName) &&
        Objects.equals(this.emailAddress, rapidPassUser.emailAddress) &&
        Objects.equals(this.mobilePhoneNumber, rapidPassUser.mobilePhoneNumber) &&
        Objects.equals(this.company, rapidPassUser.company) &&
        Objects.equals(this.workCategory, rapidPassUser.workCategory) &&
        Objects.equals(this.identificationCardNumber, rapidPassUser.identificationCardNumber) &&
        Objects.equals(this.identificationcardType, rapidPassUser.identificationcardType) &&
        Objects.equals(this.address, rapidPassUser.address) &&
        Objects.equals(this.documentsPresented, rapidPassUser.documentsPresented);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, middleName, lastName, emailAddress, mobilePhoneNumber, company, workCategory, identificationCardNumber, identificationcardType, address, documentsPresented);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RapidPassUser {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    middleName: ").append(toIndentedString(middleName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    mobilePhoneNumber: ").append(toIndentedString(mobilePhoneNumber)).append("\n");
    sb.append("    company: ").append(toIndentedString(company)).append("\n");
    sb.append("    workCategory: ").append(toIndentedString(workCategory)).append("\n");
    sb.append("    identificationCardNumber: ").append(toIndentedString(identificationCardNumber)).append("\n");
    sb.append("    identificationcardType: ").append(toIndentedString(identificationcardType)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    documentsPresented: ").append(toIndentedString(documentsPresented)).append("\n");
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
