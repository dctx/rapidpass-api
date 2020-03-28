package ph.devcon.rapidpass.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.entities.Province;

import java.util.List;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {
    //TODO: Update the repository if needed

    List<Province> findAll();

    Province findById(String id);
}
