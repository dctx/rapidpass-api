package ph.devcon.rapidpass.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.models.QueryFilter;

public interface AccessPassRepositoryCustom {

    Page<AccessPass> findAllBySearchTerm(String searchTerm, Pageable pageable);

    Page<AccessPass> findAllByQueryFilter(QueryFilter queryFilter, Pageable pageable);
}
