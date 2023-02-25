package com.labreferencia.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.labreferencia.IntegrationTest;
import com.labreferencia.domain.Reporte;
import com.labreferencia.repository.EntityManager;
import com.labreferencia.repository.ReporteRepository;
import com.labreferencia.repository.search.ReporteSearchRepository;
import com.labreferencia.service.ReporteService;
import com.labreferencia.service.dto.ReporteDTO;
import com.labreferencia.service.mapper.ReporteMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link ReporteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ReporteResourceIT {

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reportes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/reportes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReporteRepository reporteRepository;

    @Mock
    private ReporteRepository reporteRepositoryMock;

    @Autowired
    private ReporteMapper reporteMapper;

    @Mock
    private ReporteService reporteServiceMock;

    @Autowired
    private ReporteSearchRepository reporteSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Reporte reporte;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reporte createEntity(EntityManager em) {
        Reporte reporte = new Reporte().descripcion(DEFAULT_DESCRIPCION).fecha(DEFAULT_FECHA);
        return reporte;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reporte createUpdatedEntity(EntityManager em) {
        Reporte reporte = new Reporte().descripcion(UPDATED_DESCRIPCION).fecha(UPDATED_FECHA);
        return reporte;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Reporte.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        reporteSearchRepository.deleteAll().block();
        assertThat(reporteSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        reporte = createEntity(em);
    }

    @Test
    void createReporte() throws Exception {
        int databaseSizeBeforeCreate = reporteRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Reporte testReporte = reporteList.get(reporteList.size() - 1);
        assertThat(testReporte.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testReporte.getFecha()).isEqualTo(DEFAULT_FECHA);
    }

    @Test
    void createReporteWithExistingId() throws Exception {
        // Create the Reporte with an existing ID
        reporte.setId(1L);
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        int databaseSizeBeforeCreate = reporteRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkFechaIsRequired() throws Exception {
        int databaseSizeBeforeTest = reporteRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        // set the field null
        reporte.setFecha(null);

        // Create the Reporte, which fails.
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllReportes() {
        // Initialize the database
        reporteRepository.save(reporte).block();

        // Get all the reporteList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(reporte.getId().intValue()))
            .jsonPath("$.[*].descripcion")
            .value(hasItem(DEFAULT_DESCRIPCION.toString()))
            .jsonPath("$.[*].fecha")
            .value(hasItem(DEFAULT_FECHA.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReportesWithEagerRelationshipsIsEnabled() {
        when(reporteServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(reporteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReportesWithEagerRelationshipsIsNotEnabled() {
        when(reporteServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(reporteRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getReporte() {
        // Initialize the database
        reporteRepository.save(reporte).block();

        // Get the reporte
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, reporte.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(reporte.getId().intValue()))
            .jsonPath("$.descripcion")
            .value(is(DEFAULT_DESCRIPCION.toString()))
            .jsonPath("$.fecha")
            .value(is(DEFAULT_FECHA.toString()));
    }

    @Test
    void getNonExistingReporte() {
        // Get the reporte
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingReporte() throws Exception {
        // Initialize the database
        reporteRepository.save(reporte).block();

        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        reporteSearchRepository.save(reporte).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());

        // Update the reporte
        Reporte updatedReporte = reporteRepository.findById(reporte.getId()).block();
        updatedReporte.descripcion(UPDATED_DESCRIPCION).fecha(UPDATED_FECHA);
        ReporteDTO reporteDTO = reporteMapper.toDto(updatedReporte);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, reporteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        Reporte testReporte = reporteList.get(reporteList.size() - 1);
        assertThat(testReporte.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testReporte.getFecha()).isEqualTo(UPDATED_FECHA);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Reporte> reporteSearchList = IterableUtils.toList(reporteSearchRepository.findAll().collectList().block());
                Reporte testReporteSearch = reporteSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testReporteSearch.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
                assertThat(testReporteSearch.getFecha()).isEqualTo(UPDATED_FECHA);
            });
    }

    @Test
    void putNonExistingReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, reporteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateReporteWithPatch() throws Exception {
        // Initialize the database
        reporteRepository.save(reporte).block();

        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();

        // Update the reporte using partial update
        Reporte partialUpdatedReporte = new Reporte();
        partialUpdatedReporte.setId(reporte.getId());

        partialUpdatedReporte.fecha(UPDATED_FECHA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReporte.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReporte))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        Reporte testReporte = reporteList.get(reporteList.size() - 1);
        assertThat(testReporte.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testReporte.getFecha()).isEqualTo(UPDATED_FECHA);
    }

    @Test
    void fullUpdateReporteWithPatch() throws Exception {
        // Initialize the database
        reporteRepository.save(reporte).block();

        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();

        // Update the reporte using partial update
        Reporte partialUpdatedReporte = new Reporte();
        partialUpdatedReporte.setId(reporte.getId());

        partialUpdatedReporte.descripcion(UPDATED_DESCRIPCION).fecha(UPDATED_FECHA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReporte.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedReporte))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        Reporte testReporte = reporteList.get(reporteList.size() - 1);
        assertThat(testReporte.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testReporte.getFecha()).isEqualTo(UPDATED_FECHA);
    }

    @Test
    void patchNonExistingReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, reporteDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamReporte() throws Exception {
        int databaseSizeBeforeUpdate = reporteRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        reporte.setId(count.incrementAndGet());

        // Create the Reporte
        ReporteDTO reporteDTO = reporteMapper.toDto(reporte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(reporteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Reporte in the database
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteReporte() {
        // Initialize the database
        reporteRepository.save(reporte).block();
        reporteRepository.save(reporte).block();
        reporteSearchRepository.save(reporte).block();

        int databaseSizeBeforeDelete = reporteRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the reporte
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, reporte.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Reporte> reporteList = reporteRepository.findAll().collectList().block();
        assertThat(reporteList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reporteSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchReporte() {
        // Initialize the database
        reporte = reporteRepository.save(reporte).block();
        reporteSearchRepository.save(reporte).block();

        // Search the reporte
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + reporte.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(reporte.getId().intValue()))
            .jsonPath("$.[*].descripcion")
            .value(hasItem(DEFAULT_DESCRIPCION.toString()))
            .jsonPath("$.[*].fecha")
            .value(hasItem(DEFAULT_FECHA.toString()));
    }
}
