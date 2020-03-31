package ph.devcon.rapidpass.models;

import lombok.Data;

@Data
public class RapidPassCSVdata {
    private String passType;
    private String aporType;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private String company;
    private String idType;
    private String identifierNumber;
    private String mobileNumber;
    private String email;
    private String originName;
    private String originStreet;
    private String originCity;
    private String destName;
    private String destStreet;
    private String destCity;
    private String remarks;
}