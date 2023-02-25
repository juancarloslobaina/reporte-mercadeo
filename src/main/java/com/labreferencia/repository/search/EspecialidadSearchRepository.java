package com.labreferencia.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.labreferencia.domain.Especialidad;
import com.labreferencia.repository.EspecialidadRepository;
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
 * Spring Data Elasticsearch repository for the {@link Especialidad} entity.
 */
public interface EspecialidadSearchRepository
    extends ReactiveElasticsearchRepository<Especialidad, Long>, EspecialidadSearchRepositoryInternal {}

interface EspecialidadSearchRepositoryInternal {
    Flux<Especialidad> search(String query, Pageable pageable);

    Flux<Especialidad> search(Query query);
}

class EspecialidadSearchRepositoryInternalImpl implements EspecialidadSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    EspecialidadSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Especialidad> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<Especialidad> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Especialidad.class).map(SearchHit::getContent);
    }
}
