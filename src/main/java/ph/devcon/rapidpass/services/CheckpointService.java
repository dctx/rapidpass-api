package ph.devcon.rapidpass.services;

import ph.devcon.rapidpass.api.models.AccessPass;

public interface CheckpointService
{
    AccessPass retrieveAccessPassByControlCode(Integer controlCode);
    AccessPass retrieveAccessPassByLicenseNumber(String licenseNumber);
    AccessPass retrieveAccessPassByQrCode(String qrCode);
}
