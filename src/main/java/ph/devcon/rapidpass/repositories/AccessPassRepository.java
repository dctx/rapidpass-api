package ph.devcon.rapidpass.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.entities.AccessPass;

import java.util.List;

@Repository
public interface AccessPassRepository extends JpaRepository<AccessPass, Integer> {
    List<AccessPass> findAll();

    Page<AccessPass> findAll(Pageable page);

    AccessPass findByReferenceID(String referenceID);

    List<AccessPass> findAllByReferenceIDOrderByValidToDesc(String referenceId);
    
    AccessPass findByControlCode(String controlCode);

    AccessPass findByPassTypeAndIdentifierNumber(String passType, String identifierNumber);
    
    Page<AccessPass> findAllByStatus(Pageable page,String status);
}
