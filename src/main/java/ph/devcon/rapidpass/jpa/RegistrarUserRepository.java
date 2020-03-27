package ph.devcon.rapidpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.RegistrarUser;

import java.util.List;

@Repository
public interface RegistrarUserRepository extends JpaRepository<RegistrarUser, Integer> {

    List<RegistrarUser> findByReferenceId(String referenceId);

}
