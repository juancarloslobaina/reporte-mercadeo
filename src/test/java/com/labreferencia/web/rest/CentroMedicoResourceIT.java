package com.labreferencia.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.labreferencia.IntegrationTest;
import com.labreferencia.domain.CentroMedico;
import com.labreferencia.repository.CentroMedicoRepository;
import com.labreferencia.repository.EntityManager;
import com.labreferencia.repository.search.CentroMedicoSearchRepository;
import com.labreferencia.service.CentroMedicoService;
import com.labreferencia.service.dto.CentroMedicoDTO;
import com.labreferencia.service.mapper.CentroMedicoMapper;
import java.time.Duration;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link CentroMedicoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CentroMedicoResourceIT {

    private static final String DEFAULT_NOMBRE_CENTRO_MEDICO = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_CENTRO_MEDICO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/centro-medicos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/centro-medicos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CentroMedicoRepository centroMedicoRepository;

    @Mock
    private CentroMedicoRepository centroMedicoRepositoryMock;

    @Autowired
    private CentroMedicoMapper centroMedicoMapper;

    @Mock
    private CentroMedicoService centroMedicoServiceMock;

    @Autowired
    private CentroMedicoSearchRepository centroMedicoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CentroMedico centroMedico;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CentroMedico createEntity(EntityManager em) {
        CentroMedico centroMedico = new CentroMedico().nombreCentroMedico(DEFAULT_NOMBRE_CENTRO_MEDICO);
        return centroMedico;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CentroMedico createUpdatedEntity(EntityManager em) {
        CentroMedico centroMedico = new CentroMedico().nombreCentroMedico(UPDATED_NOMBRE_CENTRO_MEDICO);
        return centroMedico;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CentroMedico.class).block();
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
        centroMedicoSearchRepository.deleteAll().block();
        assertThat(centroMedicoSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        centroMedico = createEntity(em);
    }

    @Test
    void createCentroMedico() throws Exception {
        int databaseSizeBeforeCreate = centroMedicoRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        CentroMedico testCentroMedico = centroMedicoList.get(centroMedicoList.size() - 1);
        assertThat(testCentroMedico.getNombreCentroMedico()).isEqualTo(DEFAULT_NOMBRE_CENTRO_MEDICO);
    }

    @Test
    void createCentroMedicoWithExistingId() throws Exception {
        // Create the CentroMedico with an existing ID
        centroMedico.setId(1L);
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        int databaseSizeBeforeCreate = centroMedicoRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNombreCentroMedicoIsRequired() throws Exception {
        int databaseSizeBeforeTest = centroMedicoRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        // set the field null
        centroMedico.setNombreCentroMedico(null);

        // Create the CentroMedico, which fails.
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllCentroMedicos() {
        // Initialize the database
        centroMedicoRepository.save(centroMedico).block();

        // Get all the centroMedicoList
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
            .value(hasItem(centroMedico.getId().intValue()))
            .jsonPath("$.[*].nombreCentroMedico")
            .value(hasItem(DEFAULT_NOMBRE_CENTRO_MEDICO));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCentroMedicosWithEagerRelationshipsIsEnabled() {
        when(centroMedicoServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(centroMedicoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCentroMedicosWithEagerRelationshipsIsNotEnabled() {
        when(centroMedicoServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(centroMedicoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getCentroMedico() {
        // Initialize the database
        centroMedicoRepository.save(centroMedico).block();

        // Get the centroMedico
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, centroMedico.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(centroMedico.getId().intValue()))
            .jsonPath("$.nombreCentroMedico")
            .value(is(DEFAULT_NOMBRE_CENTRO_MEDICO));
    }

    @Test
    void getNonExistingCentroMedico() {
        // Get the centroMedico
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCentroMedico() throws Exception {
        // Initialize the database
        centroMedicoRepository.save(centroMedico).block();

        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().collectList().block().size();
        centroMedicoSearchRepository.save(centroMedico).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());

        // Update the centroMedico
        CentroMedico updatedCentroMedico = centroMedicoRepository.findById(centroMedico.getId()).block();
        updatedCentroMedico.nombreCentroMedico(UPDATED_NOMBRE_CENTRO_MEDICO);
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(updatedCentroMedico);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, centroMedicoDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        CentroMedico testCentroMedico = centroMedicoList.get(centroMedicoList.size() - 1);
        assertThat(testCentroMedico.getNombreCentroMedico()).isEqualTo(UPDATED_NOMBRE_CENTRO_MEDICO);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CentroMedico> centroMedicoSearchList = IterableUtils.toList(
                    centroMedicoSearchRepository.findAll().collectList().block()
                );
                CentroMedico testCentroMedicoSearch = centroMedicoSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCentroMedicoSearch.getNombreCentroMedico()).isEqualTo(UPDATED_NOMBRE_CENTRO_MEDICO);
            });
    }

    @Test
    void putNonExistingCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, centroMedicoDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateCentroMedicoWithPatch() throws Exception {
        // Initialize the database
        centroMedicoRepository.save(centroMedico).block();

        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().collectList().block().size();

        // Update the centroMedico using partial update
        CentroMedico partialUpdatedCentroMedico = new CentroMedico();
        partialUpdatedCentroMedico.setId(centroMedico.getId());

        partialUpdatedCentroMedico.nombreCentroMedico(UPDATED_NOMBRE_CENTRO_MEDICO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCentroMedico.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCentroMedico))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        CentroMedico testCentroMedico = centroMedicoList.get(centroMedicoList.size() - 1);
        assertThat(testCentroMedico.getNombreCentroMedico()).isEqualTo(UPDATED_NOMBRE_CENTRO_MEDICO);
    }

    @Test
    void fullUpdateCentroMedicoWithPatch() throws Exception {
        // Initialize the database
        centroMedicoRepository.save(centroMedico).block();

        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().collectList().block().size();

        // Update the centroMedico using partial update
        CentroMedico partialUpdatedCentroMedico = new CentroMedico();
        partialUpdatedCentroMedico.setId(centroMedico.getId());

        partialUpdatedCentroMedico.nombreCentroMedico(UPDATED_NOMBRE_CENTRO_MEDICO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCentroMedico.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCentroMedico))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        CentroMedico testCentroMedico = centroMedicoList.get(centroMedicoList.size() - 1);
        assertThat(testCentroMedico.getNombreCentroMedico()).isEqualTo(UPDATED_NOMBRE_CENTRO_MEDICO);
    }

    @Test
    void patchNonExistingCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, centroMedicoDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteCentroMedico() {
        // Initialize the database
        centroMedicoRepository.save(centroMedico).block();
        centroMedicoRepository.save(centroMedico).block();
        centroMedicoSearchRepository.save(centroMedico).block();

        int databaseSizeBeforeDelete = centroMedicoRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the centroMedico
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, centroMedico.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll().collectList().block();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(centroMedicoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchCentroMedico() {
        // Initialize the database
        centroMedico = centroMedicoRepository.save(centroMedico).block();
        centroMedicoSearchRepository.save(centroMedico).block();

        // Search the centroMedico
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + centroMedico.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(centroMedico.getId().intValue()))
            .jsonPath("$.[*].nombreCentroMedico")
            .value(hasItem(DEFAULT_NOMBRE_CENTRO_MEDICO));
    }
}
