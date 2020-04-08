package ph.devcon.rapidpass.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ScannerDevice;
import ph.devcon.rapidpass.enums.IdTypeVehicle;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.ScannerDeviceRepository;

@Service
public class CheckpointServiceImpl implements ICheckpointService {
    private AccessPassRepository accessPassRepository;
    private ScannerDeviceRepository scannerDeviceRepository;
    private QrPdfService qrPdfService;

    @Autowired
    public CheckpointServiceImpl(AccessPassRepository accessPassRepository, ScannerDeviceRepository scannerDeviceRepository, QrPdfService qrPdfService)
    {
        this.accessPassRepository = accessPassRepository;
        this.scannerDeviceRepository = scannerDeviceRepository;
        this.qrPdfService = qrPdfService;
    }

    @Override
    public AccessPass retrieveAccessPassByControlCode(String controlCode) {
        Integer id = qrPdfService.decode(controlCode);
        AccessPass accessPass = this.accessPassRepository.findById(id).orElse(null);

        if (accessPass != null)
            accessPass = qrPdfService.bindControlCodeForAccessPass(accessPass);

        return accessPass;
    }

    @Override
    public AccessPass retrieveAccessPassByPlateNo(String plateNo) {
        AccessPass accessPass = this.accessPassRepository.findByPassTypeAndIdentifierNumber(PassType.VEHICLE.toString(), plateNo);
        accessPass = qrPdfService.bindControlCodeForAccessPass(accessPass);
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
