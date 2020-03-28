package ph.devcon.rapidpass.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.entities.Registrar;

import java.util.List;

@Repository
public interface RegistrarRepository extends JpaRepository<Registrar, Integer> {
    //TODO: Update the repository if needed
    List<Registrar> findAll();

    Registrar findById(String id);
}
