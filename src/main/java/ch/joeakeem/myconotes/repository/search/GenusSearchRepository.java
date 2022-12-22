package ch.joeakeem.myconotes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import ch.joeakeem.myconotes.domain.Genus;
import ch.joeakeem.myconotes.repository.GenusRepository;
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
 * Spring Data Elasticsearch repository for the {@link Genus} entity.
 */
public interface GenusSearchRepository extends ElasticsearchRepository<Genus, Long>, GenusSearchRepositoryInternal {}

interface GenusSearchRepositoryInternal {
    Page<Genus> search(String query, Pageable pageable);

    Page<Genus> search(Query query);

    void index(Genus entity);
}

class GenusSearchRepositoryInternalImpl implements GenusSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final GenusRepository repository;

    GenusSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, GenusRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Genus> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Genus> search(Query query) {
        SearchHits<Genus> searchHits = elasticsearchTemplate.search(query, Genus.class);
        List<Genus> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Genus entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
