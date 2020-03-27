package ph.devcon.rapidpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.ScannerDevice;

import java.util.List;

@Repository
public interface ScannerDeviceRepository extends JpaRepository<ScannerDevice, Integer> {

    List<ScannerDevice> findByReferenceId(String referenceId);

}
