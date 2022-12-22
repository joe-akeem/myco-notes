package ch.joeakeem.myconotes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import ch.joeakeem.myconotes.domain.Strain;
import ch.joeakeem.myconotes.repository.StrainRepository;
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
 * Spring Data Elasticsearch repository for the {@link Strain} entity.
 */
public interface StrainSearchRepository extends ElasticsearchRepository<Strain, Long>, StrainSearchRepositoryInternal {}

interface StrainSearchRepositoryInternal {
    Page<Strain> search(String query, Pageable pageable);

    Page<Strain> search(Query query);

    void index(Strain entity);
}

class StrainSearchRepositoryInternalImpl implements StrainSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final StrainRepository repository;

    StrainSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, StrainRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Strain> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Strain> search(Query query) {
        SearchHits<Strain> searchHits = elasticsearchTemplate.search(query, Strain.class);
        List<Strain> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Strain entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
