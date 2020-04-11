package ph.devcon.rapidpass.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<AccessPass> findAllByReferenceIDAndPassTypeAndStatusAndValidToAfter(String referenceId, String passType,
                                                                             String status, OffsetDateTime validTo);

    List<AccessPass> findAllByReferenceIDAndPassTypeAndValidToAfterAndStatusIn(String referenceId, String passType,
                                                                               OffsetDateTime validTo, List<String> statuses);

    AccessPass findByPassTypeAndIdentifierNumber(String passType, String identifierNumber);
    
    Page<AccessPass> findAllByStatus(Pageable page,String status);
    
    @Query("select ap from AccessPass ap where (ap.status = 'APPROVED' or ap.status = 'SUSPENDED') " +
        "and (ap.dateTimeCreated > :since or ap.dateTimeUpdated > :since)")
    Page<AccessPass> findAllApprovedAndSuspendedSince(@Param("since") OffsetDateTime since, Pageable page);

    Page<AccessPass> findAllByStatusAndDateTimeUpdatedIsAfter(String status, OffsetDateTime lastUpdatedOn, Pageable page);
}
