package ph.devcon.rapidpass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.model.SystemUser;

import java.util.List;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Integer> {

    List<SystemUser> findByReferenceId(String referenceId);

}
