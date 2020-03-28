package ph.devcon.rapidpass.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.entities.ScannerDevice;

import java.util.List;

@Repository
public interface ScannerDeviceRepository extends JpaRepository<ScannerDevice, Integer> {
    //TODO: Update repository if needed

    List<ScannerDevice> findAll();

    ScannerDevice findById(String id);
}
