package ph.devcon.rapidpass.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ph.devcon.rapidpass.entities.ScannerDevice;
import java.util.List;

public interface ScannerDeviceRepository extends JpaRepository<ScannerDevice, Integer> {
    //TODO: Update repository if needed

    List<ScannerDevice> findAll();

    ScannerDevice findById(String id);
}
