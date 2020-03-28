package ph.devcon.rapidpass.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.entities.SystemUser;

import java.util.List;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Integer> {
    //TODO: Update repository if needed
    List<SystemUser> findAll();

    SystemUser findById(String id);
}
