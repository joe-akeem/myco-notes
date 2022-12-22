package ch.joeakeem.myconotes.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import ch.joeakeem.myconotes.domain.Image;
import ch.joeakeem.myconotes.repository.ImageRepository;
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
 * Spring Data Elasticsearch repository for the {@link Image} entity.
 */
public interface ImageSearchRepository extends ElasticsearchRepository<Image, Long>, ImageSearchRepositoryInternal {}

interface ImageSearchRepositoryInternal {
    Page<Image> search(String query, Pageable pageable);

    Page<Image> search(Query query);

    void index(Image entity);
}

class ImageSearchRepositoryInternalImpl implements ImageSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ImageRepository repository;

    ImageSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ImageRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Image> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Image> search(Query query) {
        SearchHits<Image> searchHits = elasticsearchTemplate.search(query, Image.class);
        List<Image> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Image entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
