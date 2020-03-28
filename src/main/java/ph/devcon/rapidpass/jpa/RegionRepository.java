package ph.devcon.rapidpass.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.entities.Region;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    //TODO: Update repository if needed

    List<Region> findAll();

    Region findById(String id);
}
