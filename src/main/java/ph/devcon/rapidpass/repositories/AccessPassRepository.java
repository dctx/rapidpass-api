package ph.devcon.rapidpass.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.devcon.rapidpass.entities.AccessPass;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AccessPassRepository extends JpaRepository<AccessPass, Integer> {
    List<AccessPass> findAll();

    Page<AccessPass> findAll(Pageable page);
    Page<AccessPass> findAllByAporType(String aporType, Pageable pageable);

    AccessPass findByReferenceID(String referenceID);

    List<AccessPass> findAllByReferenceIDOrderByValidToDesc(String referenceId);

    List<AccessPass> findAllByReferenceIDAndValidToAfter(String referenceId, OffsetDateTime validTo);

    List<AccessPass> findAllByReferenceIDAndStatusAndValidToAfter(String referenceId, String status,
                                                                  OffsetDateTime validTo);
    
    AccessPass findByControlCode(String controlCode);

    AccessPass findByPassTypeAndIdentifierNumber(String passType, String identifierNumber);
}
