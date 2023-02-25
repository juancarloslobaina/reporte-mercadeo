package com.labreferencia.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.labreferencia.IntegrationTest;
import com.labreferencia.domain.Especialidad;
import com.labreferencia.repository.EntityManager;
import com.labreferencia.repository.EspecialidadRepository;
import com.labreferencia.repository.search.EspecialidadSearchRepository;
import com.labreferencia.service.dto.EspecialidadDTO;
import com.labreferencia.service.mapper.EspecialidadMapper;
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
 * Integration tests for the {@link EspecialidadResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EspecialidadResourceIT {

    private static final String DEFAULT_NOMBRE_ESPECIALIDAD = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_ESPECIALIDAD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/especialidads";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/especialidads";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private EspecialidadMapper especialidadMapper;

    @Autowired
    private EspecialidadSearchRepository especialidadSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Especialidad especialidad;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Especialidad createEntity(EntityManager em) {
        Especialidad especialidad = new Especialidad().nombreEspecialidad(DEFAULT_NOMBRE_ESPECIALIDAD);
        return especialidad;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Especialidad createUpdatedEntity(EntityManager em) {
        Especialidad especialidad = new Especialidad().nombreEspecialidad(UPDATED_NOMBRE_ESPECIALIDAD);
        return especialidad;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Especialidad.class).block();
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
        especialidadSearchRepository.deleteAll().block();
        assertThat(especialidadSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        especialidad = createEntity(em);
    }

    @Test
    void createEspecialidad() throws Exception {
        int databaseSizeBeforeCreate = especialidadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Especialidad testEspecialidad = especialidadList.get(especialidadList.size() - 1);
        assertThat(testEspecialidad.getNombreEspecialidad()).isEqualTo(DEFAULT_NOMBRE_ESPECIALIDAD);
    }

    @Test
    void createEspecialidadWithExistingId() throws Exception {
        // Create the Especialidad with an existing ID
        especialidad.setId(1L);
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        int databaseSizeBeforeCreate = especialidadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNombreEspecialidadIsRequired() throws Exception {
        int databaseSizeBeforeTest = especialidadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        // set the field null
        especialidad.setNombreEspecialidad(null);

        // Create the Especialidad, which fails.
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllEspecialidads() {
        // Initialize the database
        especialidadRepository.save(especialidad).block();

        // Get all the especialidadList
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
            .value(hasItem(especialidad.getId().intValue()))
            .jsonPath("$.[*].nombreEspecialidad")
            .value(hasItem(DEFAULT_NOMBRE_ESPECIALIDAD));
    }

    @Test
    void getEspecialidad() {
        // Initialize the database
        especialidadRepository.save(especialidad).block();

        // Get the especialidad
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, especialidad.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(especialidad.getId().intValue()))
            .jsonPath("$.nombreEspecialidad")
            .value(is(DEFAULT_NOMBRE_ESPECIALIDAD));
    }

    @Test
    void getNonExistingEspecialidad() {
        // Get the especialidad
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEspecialidad() throws Exception {
        // Initialize the database
        especialidadRepository.save(especialidad).block();

        int databaseSizeBeforeUpdate = especialidadRepository.findAll().collectList().block().size();
        especialidadSearchRepository.save(especialidad).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());

        // Update the especialidad
        Especialidad updatedEspecialidad = especialidadRepository.findById(especialidad.getId()).block();
        updatedEspecialidad.nombreEspecialidad(UPDATED_NOMBRE_ESPECIALIDAD);
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(updatedEspecialidad);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, especialidadDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        Especialidad testEspecialidad = especialidadList.get(especialidadList.size() - 1);
        assertThat(testEspecialidad.getNombreEspecialidad()).isEqualTo(UPDATED_NOMBRE_ESPECIALIDAD);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Especialidad> especialidadSearchList = IterableUtils.toList(
                    especialidadSearchRepository.findAll().collectList().block()
                );
                Especialidad testEspecialidadSearch = especialidadSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEspecialidadSearch.getNombreEspecialidad()).isEqualTo(UPDATED_NOMBRE_ESPECIALIDAD);
            });
    }

    @Test
    void putNonExistingEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, especialidadDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateEspecialidadWithPatch() throws Exception {
        // Initialize the database
        especialidadRepository.save(especialidad).block();

        int databaseSizeBeforeUpdate = especialidadRepository.findAll().collectList().block().size();

        // Update the especialidad using partial update
        Especialidad partialUpdatedEspecialidad = new Especialidad();
        partialUpdatedEspecialidad.setId(especialidad.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEspecialidad.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEspecialidad))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        Especialidad testEspecialidad = especialidadList.get(especialidadList.size() - 1);
        assertThat(testEspecialidad.getNombreEspecialidad()).isEqualTo(DEFAULT_NOMBRE_ESPECIALIDAD);
    }

    @Test
    void fullUpdateEspecialidadWithPatch() throws Exception {
        // Initialize the database
        especialidadRepository.save(especialidad).block();

        int databaseSizeBeforeUpdate = especialidadRepository.findAll().collectList().block().size();

        // Update the especialidad using partial update
        Especialidad partialUpdatedEspecialidad = new Especialidad();
        partialUpdatedEspecialidad.setId(especialidad.getId());

        partialUpdatedEspecialidad.nombreEspecialidad(UPDATED_NOMBRE_ESPECIALIDAD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEspecialidad.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEspecialidad))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        Especialidad testEspecialidad = especialidadList.get(especialidadList.size() - 1);
        assertThat(testEspecialidad.getNombreEspecialidad()).isEqualTo(UPDATED_NOMBRE_ESPECIALIDAD);
    }

    @Test
    void patchNonExistingEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, especialidadDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteEspecialidad() {
        // Initialize the database
        especialidadRepository.save(especialidad).block();
        especialidadRepository.save(especialidad).block();
        especialidadSearchRepository.save(especialidad).block();

        int databaseSizeBeforeDelete = especialidadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the especialidad
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, especialidad.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Especialidad> especialidadList = especialidadRepository.findAll().collectList().block();
        assertThat(especialidadList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(especialidadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchEspecialidad() {
        // Initialize the database
        especialidad = especialidadRepository.save(especialidad).block();
        especialidadSearchRepository.save(especialidad).block();

        // Search the especialidad
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + especialidad.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(especialidad.getId().intValue()))
            .jsonPath("$.[*].nombreEspecialidad")
            .value(hasItem(DEFAULT_NOMBRE_ESPECIALIDAD));
    }
}
