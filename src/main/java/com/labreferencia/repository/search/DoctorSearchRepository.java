package com.labreferencia.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.labreferencia.domain.Doctor;
import com.labreferencia.repository.DoctorRepository;
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
 * Spring Data Elasticsearch repository for the {@link Doctor} entity.
 */
public interface DoctorSearchRepository extends ReactiveElasticsearchRepository<Doctor, Long>, DoctorSearchRepositoryInternal {}

interface DoctorSearchRepositoryInternal {
    Flux<Doctor> search(String query, Pageable pageable);

    Flux<Doctor> search(Query query);
}

class DoctorSearchRepositoryInternalImpl implements DoctorSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    DoctorSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Doctor> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return search(nativeSearchQuery);
    }

    @Override
    public Flux<Doctor> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Doctor.class).map(SearchHit::getContent);
    }
}
