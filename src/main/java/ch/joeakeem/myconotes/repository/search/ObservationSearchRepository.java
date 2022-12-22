package ch.joeakeem.myconotes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import ch.joeakeem.myconotes.domain.Observation;
import ch.joeakeem.myconotes.repository.ObservationRepository;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Observation} entity.
 */
public interface ObservationSearchRepository extends ElasticsearchRepository<Observation, Long>, ObservationSearchRepositoryInternal {}

interface ObservationSearchRepositoryInternal {
    Page<Observation> search(String query, Pageable pageable);

    Page<Observation> search(Query query);

    void index(Observation entity);
}

class ObservationSearchRepositoryInternalImpl implements ObservationSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ObservationRepository repository;

    ObservationSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ObservationRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Observation> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Observation> search(Query query) {
        SearchHits<Observation> searchHits = elasticsearchTemplate.search(query, Observation.class);
        List<Observation> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Observation entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
