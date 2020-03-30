package ph.devcon.rapidpass.services;

import ph.devcon.rapidpass.entities.AccessPass;

public interface ICheckpointService
{
    AccessPass retrieveAccessPassByControlCode(String controlCode);
    AccessPass retrieveAccessPassByLicenseNumber(String licenseNumber);
    AccessPass retrieveAccessPassByQrCode(String qrCode);
}
