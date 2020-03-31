package ph.devcon.rapidpass.models;

import lombok.Data;

@Data
public class MobileDevice {
    private String imei;
    private String brand;
    private String model;
    private String status;
}
