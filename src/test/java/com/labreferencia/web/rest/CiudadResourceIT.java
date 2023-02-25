package com.labreferencia.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.labreferencia.IntegrationTest;
import com.labreferencia.domain.Ciudad;
import com.labreferencia.repository.CiudadRepository;
import com.labreferencia.repository.EntityManager;
import com.labreferencia.repository.search.CiudadSearchRepository;
import com.labreferencia.service.dto.CiudadDTO;
import com.labreferencia.service.mapper.CiudadMapper;
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
 * Integration tests for the {@link CiudadResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CiudadResourceIT {

    private static final String DEFAULT_NOMBRE_CIUDAD = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_CIUDAD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ciudads";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/ciudads";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CiudadRepository ciudadRepository;

    @Autowired
    private CiudadMapper ciudadMapper;

    @Autowired
    private CiudadSearchRepository ciudadSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Ciudad ciudad;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ciudad createEntity(EntityManager em) {
        Ciudad ciudad = new Ciudad().nombreCiudad(DEFAULT_NOMBRE_CIUDAD);
        return ciudad;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ciudad createUpdatedEntity(EntityManager em) {
        Ciudad ciudad = new Ciudad().nombreCiudad(UPDATED_NOMBRE_CIUDAD);
        return ciudad;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Ciudad.class).block();
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
        ciudadSearchRepository.deleteAll().block();
        assertThat(ciudadSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        ciudad = createEntity(em);
    }

    @Test
    void createCiudad() throws Exception {
        int databaseSizeBeforeCreate = ciudadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        // Create the Ciudad
        CiudadDTO ciudadDTO = ciudadMapper.toDto(ciudad);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ciudadDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Ciudad in the database
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Ciudad testCiudad = ciudadList.get(ciudadList.size() - 1);
        assertThat(testCiudad.getNombreCiudad()).isEqualTo(DEFAULT_NOMBRE_CIUDAD);
    }

    @Test
    void createCiudadWithExistingId() throws Exception {
        // Create the Ciudad with an existing ID
        ciudad.setId(1L);
        CiudadDTO ciudadDTO = ciudadMapper.toDto(ciudad);

        int databaseSizeBeforeCreate = ciudadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ciudadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ciudad in the database
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNombreCiudadIsRequired() throws Exception {
        int databaseSizeBeforeTest = ciudadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        // set the field null
        ciudad.setNombreCiudad(null);

        // Create the Ciudad, which fails.
        CiudadDTO ciudadDTO = ciudadMapper.toDto(ciudad);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ciudadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllCiudads() {
        // Initialize the database
        ciudadRepository.save(ciudad).block();

        // Get all the ciudadList
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
            .value(hasItem(ciudad.getId().intValue()))
            .jsonPath("$.[*].nombreCiudad")
            .value(hasItem(DEFAULT_NOMBRE_CIUDAD));
    }

    @Test
    void getCiudad() {
        // Initialize the database
        ciudadRepository.save(ciudad).block();

        // Get the ciudad
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, ciudad.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(ciudad.getId().intValue()))
            .jsonPath("$.nombreCiudad")
            .value(is(DEFAULT_NOMBRE_CIUDAD));
    }

    @Test
    void getNonExistingCiudad() {
        // Get the ciudad
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCiudad() throws Exception {
        // Initialize the database
        ciudadRepository.save(ciudad).block();

        int databaseSizeBeforeUpdate = ciudadRepository.findAll().collectList().block().size();
        ciudadSearchRepository.save(ciudad).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());

        // Update the ciudad
        Ciudad updatedCiudad = ciudadRepository.findById(ciudad.getId()).block();
        updatedCiudad.nombreCiudad(UPDATED_NOMBRE_CIUDAD);
        CiudadDTO ciudadDTO = ciudadMapper.toDto(updatedCiudad);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ciudadDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ciudadDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ciudad in the database
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeUpdate);
        Ciudad testCiudad = ciudadList.get(ciudadList.size() - 1);
        assertThat(testCiudad.getNombreCiudad()).isEqualTo(UPDATED_NOMBRE_CIUDAD);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Ciudad> ciudadSearchList = IterableUtils.toList(ciudadSearchRepository.findAll().collectList().block());
                Ciudad testCiudadSearch = ciudadSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCiudadSearch.getNombreCiudad()).isEqualTo(UPDATED_NOMBRE_CIUDAD);
            });
    }

    @Test
    void putNonExistingCiudad() throws Exception {
        int databaseSizeBeforeUpdate = ciudadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        ciudad.setId(count.incrementAndGet());

        // Create the Ciudad
        CiudadDTO ciudadDTO = ciudadMapper.toDto(ciudad);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ciudadDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ciudadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ciudad in the database
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchCiudad() throws Exception {
        int databaseSizeBeforeUpdate = ciudadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        ciudad.setId(count.incrementAndGet());

        // Create the Ciudad
        CiudadDTO ciudadDTO = ciudadMapper.toDto(ciudad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ciudadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ciudad in the database
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamCiudad() throws Exception {
        int databaseSizeBeforeUpdate = ciudadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        ciudad.setId(count.incrementAndGet());

        // Create the Ciudad
        CiudadDTO ciudadDTO = ciudadMapper.toDto(ciudad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ciudadDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ciudad in the database
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateCiudadWithPatch() throws Exception {
        // Initialize the database
        ciudadRepository.save(ciudad).block();

        int databaseSizeBeforeUpdate = ciudadRepository.findAll().collectList().block().size();

        // Update the ciudad using partial update
        Ciudad partialUpdatedCiudad = new Ciudad();
        partialUpdatedCiudad.setId(ciudad.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCiudad.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCiudad))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ciudad in the database
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeUpdate);
        Ciudad testCiudad = ciudadList.get(ciudadList.size() - 1);
        assertThat(testCiudad.getNombreCiudad()).isEqualTo(DEFAULT_NOMBRE_CIUDAD);
    }

    @Test
    void fullUpdateCiudadWithPatch() throws Exception {
        // Initialize the database
        ciudadRepository.save(ciudad).block();

        int databaseSizeBeforeUpdate = ciudadRepository.findAll().collectList().block().size();

        // Update the ciudad using partial update
        Ciudad partialUpdatedCiudad = new Ciudad();
        partialUpdatedCiudad.setId(ciudad.getId());

        partialUpdatedCiudad.nombreCiudad(UPDATED_NOMBRE_CIUDAD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCiudad.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCiudad))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ciudad in the database
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeUpdate);
        Ciudad testCiudad = ciudadList.get(ciudadList.size() - 1);
        assertThat(testCiudad.getNombreCiudad()).isEqualTo(UPDATED_NOMBRE_CIUDAD);
    }

    @Test
    void patchNonExistingCiudad() throws Exception {
        int databaseSizeBeforeUpdate = ciudadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        ciudad.setId(count.incrementAndGet());

        // Create the Ciudad
        CiudadDTO ciudadDTO = ciudadMapper.toDto(ciudad);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ciudadDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ciudadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ciudad in the database
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchCiudad() throws Exception {
        int databaseSizeBeforeUpdate = ciudadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        ciudad.setId(count.incrementAndGet());

        // Create the Ciudad
        CiudadDTO ciudadDTO = ciudadMapper.toDto(ciudad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ciudadDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ciudad in the database
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamCiudad() throws Exception {
        int databaseSizeBeforeUpdate = ciudadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        ciudad.setId(count.incrementAndGet());

        // Create the Ciudad
        CiudadDTO ciudadDTO = ciudadMapper.toDto(ciudad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ciudadDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ciudad in the database
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteCiudad() {
        // Initialize the database
        ciudadRepository.save(ciudad).block();
        ciudadRepository.save(ciudad).block();
        ciudadSearchRepository.save(ciudad).block();

        int databaseSizeBeforeDelete = ciudadRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the ciudad
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, ciudad.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Ciudad> ciudadList = ciudadRepository.findAll().collectList().block();
        assertThat(ciudadList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(ciudadSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchCiudad() {
        // Initialize the database
        ciudad = ciudadRepository.save(ciudad).block();
        ciudadSearchRepository.save(ciudad).block();

        // Search the ciudad
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + ciudad.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(ciudad.getId().intValue()))
            .jsonPath("$.[*].nombreCiudad")
            .value(hasItem(DEFAULT_NOMBRE_CIUDAD));
    }
}
