package ph.devcon.rapidpass.repositories;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.models.QueryFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.data.util.StreamUtils.zip;

/**
 * the {@link AccessPassRepository} is built as a composite repository, as defined by the
 * requirements of {@link AccessPassRepositoryCustom}.
 */
@RequiredArgsConstructor
public class AccessPassRepositoryCustomImpl implements AccessPassRepositoryCustom {

    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Performs a 'startsWith' matching on an access pass.
     *
     * @param searchTerm The search keywords. Note that this performs a `startsWith` matching.
     * @param pageable The pageable options.
     * @return a paged view of queries on access passes following the search term, and sorted by
     * validTo in descending order.
     */
    @Override
    public Page<AccessPass> findAllBySearchTerm(String searchTerm, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccessPass> query = cb.createQuery(AccessPass.class);
        Root<AccessPass> accessPass = query.from(AccessPass.class);

        Path<String> companyPath = accessPass.get("company");
        Path<String> namePath = accessPass.get("name");
        Path<String> validTo = accessPass.get("validTo");

        List<Predicate> predicates = new ArrayList<>();

        String companyMatcher = searchTerm + "%";
        String nameMatcher = searchTerm + "%";

        predicates.add(
                cb.like(cb.lower(companyPath),
                        cb.lower(cb.literal(companyMatcher)))
        );

        predicates.add(
                cb.like(cb.lower(namePath),
                        cb.lower(cb.literal(nameMatcher)))
        );

        query.select(accessPass)
            .where(
                cb.or(
                    predicates.toArray(new Predicate[predicates.size()])
                )
            )
            .orderBy(cb.desc(validTo));


        if (pageable != null)
            return new PageImpl<>(entityManager.createQuery(query).getResultList(), pageable, pageable.getPageSize());

        return new PageImpl<>(entityManager.createQuery(query).getResultList());
    }

    /**
     * Supports complex searching, which factors in both the search terms AND the other filtering objects.
     *
     * The implementation for this should use OR on the name and the company, but AND for the remaining
     * non-null query parameters.
     * @param queryFilter An object holding on to the query parameters.
     * @param pageable The pageable options.
     * @return A paged list of access passes.
     */
    public Page<AccessPass> findAllByQueryFilter(QueryFilter queryFilter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<AccessPass> query = cb.createQuery(AccessPass.class);

        Root<AccessPass> accessPasses = query.from(AccessPass.class);

        // Define search-based predicates
        Predicate[] searchPredicates =  determineSearchPredicates(queryFilter, accessPasses, cb);

        // Define property-based predicates
        HashMap<String, String> map = new HashMap<>();
        map.put("passType", queryFilter.getPassType());
        map.put("aporType", queryFilter.getAporType());
        map.put("referenceID", queryFilter.getReferenceId());
        map.put("status", queryFilter.getStatus());
        map.put("plateNumber", queryFilter.getPlateNumber());
        map.put("company", queryFilter.getCompany());
        map.put("source", queryFilter.getSource() == null ? null : queryFilter.getSource().toString());

        Predicate[] propertyPredicates = determineRelevantPropertyPredicates(map, accessPasses, cb);

        // For sorting purposes
        Path<String> validTo = accessPasses.get("validTo");

        // Determine whether to perform matching on criterias or not

        if (searchPredicates.length == 0 && propertyPredicates.length == 0)
            query.select(accessPasses).orderBy(cb.desc(validTo));
        else
            query.select(accessPasses)
                    .where(cb.and(
                            cb.or(searchPredicates),
                            cb.and(propertyPredicates)
                    ))
                    .orderBy(cb.desc(validTo));

        if (pageable != null)
            return new PageImpl<>(entityManager.createQuery(query).getResultList(), pageable, pageable.getPageSize());

        return new PageImpl<>(entityManager.createQuery(query).getResultList());
    }

    /**
     * The search functionality is defined to be an OR-based "startsWith" matching for the name and company column.
     * @param queryFilter The query filter.
     * @param accessPasses The reference to the related database table.
     * @param cb The criteria builder.
     * @return An array of predicates, only if the search terms were specified on the query filter.
     */
    private Predicate[] determineSearchPredicates(QueryFilter queryFilter, Root<AccessPass> accessPasses, CriteriaBuilder cb) {
        // Cb.and() resolves to a true predicate
        // https://stackoverflow.com/questions/14675229/jpa-criteria-api-how-to-express-literal-true-and-literal-false
        Predicate[] searchPredicates = new Predicate[]{ cb.and() };

        String searchTerm = queryFilter.getSearch();

        String companyMatcher = searchTerm + "%";
        String nameMatcher = searchTerm + "%";

        Path<String> companyPath = accessPasses.get("company");
        Path<String> namePath = accessPasses.get("name");


        if (StringUtils.hasLength(searchTerm))
            searchPredicates = ImmutableList.of(
                    cb.like(cb.lower(companyPath),
                            cb.lower(cb.literal(companyMatcher))),
                    cb.like(cb.lower(namePath),
                            cb.lower(cb.literal(nameMatcher)))
            ).toArray(new Predicate[2]);

        return searchPredicates;
    }

    /**
     * <p>This method returns an array of {@link Predicate}s to be used for matching. Note that the relevant
     * property predicates are based on the specified mapping.</p>
     *
     * <p>If the value found on the hash map is null or an empty string, then this method will filter it out
     * and not use it as a criteria.</p>
     *
     * <h2>Limitations</h2>
     *
     * This only works for string-based query parameters. As you can see, the mapping utilises a String to String
     * mapping. This hasn't been tested on integer or other data types.
     *
     * @param mapping Maps out the paths from the database table to the query value.
     * @param databaseRootTable Denotes the Root<E> reference to the database table.
     * @param criteriaBuilder A criteria builder.
     * @param <E> The entity type (e.g. AccessPass)
     * @return an array of relevant property-based predicates.
     */
    private <E> Predicate[] determineRelevantPropertyPredicates(HashMap<String, String> mapping, Root<E> databaseRootTable, CriteriaBuilder criteriaBuilder) {

        ArrayList<String> propertyNames = new ArrayList(mapping.keySet());

        Stream<Path<Object>> pathStream = propertyNames
                .stream().map(databaseRootTable::get);

        Stream<String> passType = propertyNames.stream().map(mapping::get);

        Stream<Pair<Path<Object>, String>> existingQueryParameters = zip(pathStream, passType, Pair::of)
                .filter(p -> StringUtils.hasLength(p.getRight()));

        // If you need to change the matching necessary for the criteria builder, you would change the
        // CriteriaBuilder.equal method call to something else.
        Predicate[] predicates = existingQueryParameters
                .map(p -> criteriaBuilder.equal(p.getLeft(), p.getRight()))
                .toArray(Predicate[]::new);

        if (predicates.length == 0) return new Predicate[] { criteriaBuilder.and() };

        return predicates;
    }
}
