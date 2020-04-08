package ph.devcon.rapidpass.services;

import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.ScannerDevice;

public interface ICheckpointService
{
    AccessPass retrieveAccessPassByPlateNo(String plateNo);
    AccessPass retrieveAccessPassByQrCode(String qrCode);
    ScannerDevice retrieveDeviceByImei(String imei);
}
