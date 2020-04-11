package ph.devcon.rapidpass.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.entities.RegistrarUser;

import java.util.List;

@Repository
public interface RegistrarUserRepository extends JpaRepository<RegistrarUser, Integer> {
    //TODO: Update repository if needed

    List<RegistrarUser> findAll();

    RegistrarUser findById(String id);

    RegistrarUser findByUsername(String username);
}
