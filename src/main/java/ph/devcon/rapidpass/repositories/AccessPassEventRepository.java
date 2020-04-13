package ph.devcon.rapidpass.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ph.devcon.rapidpass.entities.AccessPassEvent;

import java.util.List;

public interface AccessPassEventRepository extends JpaRepository<AccessPassEvent, Integer> {

    Page<AccessPassEvent> findAllByIdIsGreaterThanEqual(Integer eventId, Pageable page);

}
