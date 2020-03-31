package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.ScannerDevice;

@Data
@Builder
public class MobileDevice {
    private String imei;
    private String brand;
    private String model;
    private String mobileNubmer;
    private String status;

    public static MobileDevice buildFro(ScannerDevice scannerDevice) {
        return MobileDevice.builder()
                .brand(scannerDevice.getBrand())
                .model(scannerDevice.getModel())
                .imei(scannerDevice.getUniqueDeviceId())
                .status(scannerDevice.getStatus())
                .mobileNubmer(scannerDevice.getMobileNumber())
                .build();
    }
}
