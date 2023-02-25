package com.labreferencia.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.labreferencia.domain.Ciudad;
import com.labreferencia.repository.CiudadRepository;
import java.util.List;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Ciudad} entity.
 */
public interface CiudadSearchRepository extends ReactiveElasticsearchRepository<Ciudad, Long>, CiudadSearchRepositoryInternal {}

interface CiudadSearchRepositoryInternal {
    Flux<Ciudad> search(String query, Pageable pageable);

    Flux<Ciudad> search(Query query);
}

class CiudadSearchRepositoryInternalImpl implements CiudadSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    CiudadSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Ciudad> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<Ciudad> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Ciudad.class).map(SearchHit::getContent);
    }
}
