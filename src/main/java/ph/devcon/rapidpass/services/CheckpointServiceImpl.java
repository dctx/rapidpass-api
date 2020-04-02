package ph.devcon.rapidpass.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.enums.IdTypeVehicle;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.repositories.*;

@Service
public class CheckpointServiceImpl implements ICheckpointService {
    private AccessPassRepository accessPassRepository;
    private ScannerDeviceRepository scannerDeviceRepository;

    @Autowired
    public CheckpointServiceImpl(AccessPassRepository accessPassRepository, ScannerDeviceRepository scannerDeviceRepository)
    {
        this.accessPassRepository = accessPassRepository;
        this.scannerDeviceRepository = scannerDeviceRepository;
    }

    @Override
    public AccessPass retrieveAccessPassByControlCode(String controlCode) {
        return this.accessPassRepository.findByControlCode(controlCode);
    }

    @Override
    public AccessPass retrieveAccessPassByPlateNo(String plateNo) {
        AccessPass accessPass = this.accessPassRepository.findByPassTypeAndIdentifierNumber(PassType.VEHICLE.toString(), plateNo);
        return (null != accessPass && StringUtils.equals(IdTypeVehicle.PLT.toString(), accessPass.getIdType())) ? accessPass : null;
    }

    @Override
    public AccessPass retrieveAccessPassByQrCode(String qrCode)
    {
        return null;
    }

    @Override
    public ScannerDevice retrieveDeviceByImei(String imei) {
        ScannerDevice scannerDevice = this.scannerDeviceRepository.findByUniqueDeviceId(imei);
        if (scannerDevice == null) {
            throw new IllegalArgumentException(String.format("Device with IMEI %s is not registered.", imei));
        }

        return scannerDevice;
    }
}
