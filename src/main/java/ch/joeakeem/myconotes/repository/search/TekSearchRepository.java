package ch.joeakeem.myconotes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import ch.joeakeem.myconotes.domain.Tek;
import ch.joeakeem.myconotes.repository.TekRepository;
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
 * Spring Data Elasticsearch repository for the {@link Tek} entity.
 */
public interface TekSearchRepository extends ElasticsearchRepository<Tek, Long>, TekSearchRepositoryInternal {}

interface TekSearchRepositoryInternal {
    Page<Tek> search(String query, Pageable pageable);

    Page<Tek> search(Query query);

    void index(Tek entity);
}

class TekSearchRepositoryInternalImpl implements TekSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final TekRepository repository;

    TekSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, TekRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Tek> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Tek> search(Query query) {
        SearchHits<Tek> searchHits = elasticsearchTemplate.search(query, Tek.class);
        List<Tek> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Tek entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
