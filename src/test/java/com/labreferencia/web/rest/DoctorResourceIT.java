package com.labreferencia.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.labreferencia.IntegrationTest;
import com.labreferencia.domain.Doctor;
import com.labreferencia.repository.DoctorRepository;
import com.labreferencia.repository.EntityManager;
import com.labreferencia.repository.search.DoctorSearchRepository;
import com.labreferencia.service.DoctorService;
import com.labreferencia.service.dto.DoctorDTO;
import com.labreferencia.service.mapper.DoctorMapper;
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
 * Integration tests for the {@link DoctorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DoctorResourceIT {

    private static final String DEFAULT_NOMBRE_DOCTOR = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_DOCTOR = "BBBBBBBBBB";

    private static final String DEFAULT_CORREO_PERSONAL = "nIBdGd@\\apu.62t";
    private static final String UPDATED_CORREO_PERSONAL = "I|3Z@TJ]sRq.HB?'";

    private static final String DEFAULT_CORREO_CORPORATIVO = "\"x~@X.9";
    private static final String UPDATED_CORREO_CORPORATIVO = "tQ\"R<%@.cfQ.T|Re.";

    private static final String DEFAULT_TELEFONO_PERSONAL = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONO_PERSONAL = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFONO_CORPORATIVO = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONO_CORPORATIVO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/doctors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/doctors";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorRepository doctorRepositoryMock;

    @Autowired
    private DoctorMapper doctorMapper;

    @Mock
    private DoctorService doctorServiceMock;

    @Autowired
    private DoctorSearchRepository doctorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Doctor doctor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Doctor createEntity(EntityManager em) {
        Doctor doctor = new Doctor()
            .nombreDoctor(DEFAULT_NOMBRE_DOCTOR)
            .correoPersonal(DEFAULT_CORREO_PERSONAL)
            .correoCorporativo(DEFAULT_CORREO_CORPORATIVO)
            .telefonoPersonal(DEFAULT_TELEFONO_PERSONAL)
            .telefonoCorporativo(DEFAULT_TELEFONO_CORPORATIVO);
        return doctor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Doctor createUpdatedEntity(EntityManager em) {
        Doctor doctor = new Doctor()
            .nombreDoctor(UPDATED_NOMBRE_DOCTOR)
            .correoPersonal(UPDATED_CORREO_PERSONAL)
            .correoCorporativo(UPDATED_CORREO_CORPORATIVO)
            .telefonoPersonal(UPDATED_TELEFONO_PERSONAL)
            .telefonoCorporativo(UPDATED_TELEFONO_CORPORATIVO);
        return doctor;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Doctor.class).block();
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
        doctorSearchRepository.deleteAll().block();
        assertThat(doctorSearchRepository.count().block()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        doctor = createEntity(em);
    }

    @Test
    void createDoctor() throws Exception {
        int databaseSizeBeforeCreate = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getNombreDoctor()).isEqualTo(DEFAULT_NOMBRE_DOCTOR);
        assertThat(testDoctor.getCorreoPersonal()).isEqualTo(DEFAULT_CORREO_PERSONAL);
        assertThat(testDoctor.getCorreoCorporativo()).isEqualTo(DEFAULT_CORREO_CORPORATIVO);
        assertThat(testDoctor.getTelefonoPersonal()).isEqualTo(DEFAULT_TELEFONO_PERSONAL);
        assertThat(testDoctor.getTelefonoCorporativo()).isEqualTo(DEFAULT_TELEFONO_CORPORATIVO);
    }

    @Test
    void createDoctorWithExistingId() throws Exception {
        // Create the Doctor with an existing ID
        doctor.setId(1L);
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        int databaseSizeBeforeCreate = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNombreDoctorIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        // set the field null
        doctor.setNombreDoctor(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCorreoCorporativoIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        // set the field null
        doctor.setCorreoCorporativo(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTelefonoPersonalIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        // set the field null
        doctor.setTelefonoPersonal(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllDoctors() {
        // Initialize the database
        doctorRepository.save(doctor).block();

        // Get all the doctorList
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
            .value(hasItem(doctor.getId().intValue()))
            .jsonPath("$.[*].nombreDoctor")
            .value(hasItem(DEFAULT_NOMBRE_DOCTOR))
            .jsonPath("$.[*].correoPersonal")
            .value(hasItem(DEFAULT_CORREO_PERSONAL))
            .jsonPath("$.[*].correoCorporativo")
            .value(hasItem(DEFAULT_CORREO_CORPORATIVO))
            .jsonPath("$.[*].telefonoPersonal")
            .value(hasItem(DEFAULT_TELEFONO_PERSONAL))
            .jsonPath("$.[*].telefonoCorporativo")
            .value(hasItem(DEFAULT_TELEFONO_CORPORATIVO));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDoctorsWithEagerRelationshipsIsEnabled() {
        when(doctorServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(doctorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDoctorsWithEagerRelationshipsIsNotEnabled() {
        when(doctorServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(doctorRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDoctor() {
        // Initialize the database
        doctorRepository.save(doctor).block();

        // Get the doctor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, doctor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(doctor.getId().intValue()))
            .jsonPath("$.nombreDoctor")
            .value(is(DEFAULT_NOMBRE_DOCTOR))
            .jsonPath("$.correoPersonal")
            .value(is(DEFAULT_CORREO_PERSONAL))
            .jsonPath("$.correoCorporativo")
            .value(is(DEFAULT_CORREO_CORPORATIVO))
            .jsonPath("$.telefonoPersonal")
            .value(is(DEFAULT_TELEFONO_PERSONAL))
            .jsonPath("$.telefonoCorporativo")
            .value(is(DEFAULT_TELEFONO_CORPORATIVO));
    }

    @Test
    void getNonExistingDoctor() {
        // Get the doctor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDoctor() throws Exception {
        // Initialize the database
        doctorRepository.save(doctor).block();

        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        doctorSearchRepository.save(doctor).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());

        // Update the doctor
        Doctor updatedDoctor = doctorRepository.findById(doctor.getId()).block();
        updatedDoctor
            .nombreDoctor(UPDATED_NOMBRE_DOCTOR)
            .correoPersonal(UPDATED_CORREO_PERSONAL)
            .correoCorporativo(UPDATED_CORREO_CORPORATIVO)
            .telefonoPersonal(UPDATED_TELEFONO_PERSONAL)
            .telefonoCorporativo(UPDATED_TELEFONO_CORPORATIVO);
        DoctorDTO doctorDTO = doctorMapper.toDto(updatedDoctor);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, doctorDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getNombreDoctor()).isEqualTo(UPDATED_NOMBRE_DOCTOR);
        assertThat(testDoctor.getCorreoPersonal()).isEqualTo(UPDATED_CORREO_PERSONAL);
        assertThat(testDoctor.getCorreoCorporativo()).isEqualTo(UPDATED_CORREO_CORPORATIVO);
        assertThat(testDoctor.getTelefonoPersonal()).isEqualTo(UPDATED_TELEFONO_PERSONAL);
        assertThat(testDoctor.getTelefonoCorporativo()).isEqualTo(UPDATED_TELEFONO_CORPORATIVO);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Doctor> doctorSearchList = IterableUtils.toList(doctorSearchRepository.findAll().collectList().block());
                Doctor testDoctorSearch = doctorSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testDoctorSearch.getNombreDoctor()).isEqualTo(UPDATED_NOMBRE_DOCTOR);
                assertThat(testDoctorSearch.getCorreoPersonal()).isEqualTo(UPDATED_CORREO_PERSONAL);
                assertThat(testDoctorSearch.getCorreoCorporativo()).isEqualTo(UPDATED_CORREO_CORPORATIVO);
                assertThat(testDoctorSearch.getTelefonoPersonal()).isEqualTo(UPDATED_TELEFONO_PERSONAL);
                assertThat(testDoctorSearch.getTelefonoCorporativo()).isEqualTo(UPDATED_TELEFONO_CORPORATIVO);
            });
    }

    @Test
    void putNonExistingDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, doctorDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateDoctorWithPatch() throws Exception {
        // Initialize the database
        doctorRepository.save(doctor).block();

        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();

        // Update the doctor using partial update
        Doctor partialUpdatedDoctor = new Doctor();
        partialUpdatedDoctor.setId(doctor.getId());

        partialUpdatedDoctor.telefonoCorporativo(UPDATED_TELEFONO_CORPORATIVO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDoctor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDoctor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getNombreDoctor()).isEqualTo(DEFAULT_NOMBRE_DOCTOR);
        assertThat(testDoctor.getCorreoPersonal()).isEqualTo(DEFAULT_CORREO_PERSONAL);
        assertThat(testDoctor.getCorreoCorporativo()).isEqualTo(DEFAULT_CORREO_CORPORATIVO);
        assertThat(testDoctor.getTelefonoPersonal()).isEqualTo(DEFAULT_TELEFONO_PERSONAL);
        assertThat(testDoctor.getTelefonoCorporativo()).isEqualTo(UPDATED_TELEFONO_CORPORATIVO);
    }

    @Test
    void fullUpdateDoctorWithPatch() throws Exception {
        // Initialize the database
        doctorRepository.save(doctor).block();

        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();

        // Update the doctor using partial update
        Doctor partialUpdatedDoctor = new Doctor();
        partialUpdatedDoctor.setId(doctor.getId());

        partialUpdatedDoctor
            .nombreDoctor(UPDATED_NOMBRE_DOCTOR)
            .correoPersonal(UPDATED_CORREO_PERSONAL)
            .correoCorporativo(UPDATED_CORREO_CORPORATIVO)
            .telefonoPersonal(UPDATED_TELEFONO_PERSONAL)
            .telefonoCorporativo(UPDATED_TELEFONO_CORPORATIVO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDoctor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDoctor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getNombreDoctor()).isEqualTo(UPDATED_NOMBRE_DOCTOR);
        assertThat(testDoctor.getCorreoPersonal()).isEqualTo(UPDATED_CORREO_PERSONAL);
        assertThat(testDoctor.getCorreoCorporativo()).isEqualTo(UPDATED_CORREO_CORPORATIVO);
        assertThat(testDoctor.getTelefonoPersonal()).isEqualTo(UPDATED_TELEFONO_PERSONAL);
        assertThat(testDoctor.getTelefonoCorporativo()).isEqualTo(UPDATED_TELEFONO_CORPORATIVO);
    }

    @Test
    void patchNonExistingDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, doctorDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(doctorDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteDoctor() {
        // Initialize the database
        doctorRepository.save(doctor).block();
        doctorRepository.save(doctor).block();
        doctorSearchRepository.save(doctor).block();

        int databaseSizeBeforeDelete = doctorRepository.findAll().collectList().block().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the doctor
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, doctor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Doctor> doctorList = doctorRepository.findAll().collectList().block();
        assertThat(doctorList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchDoctor() {
        // Initialize the database
        doctor = doctorRepository.save(doctor).block();
        doctorSearchRepository.save(doctor).block();

        // Search the doctor
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + doctor.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(doctor.getId().intValue()))
            .jsonPath("$.[*].nombreDoctor")
            .value(hasItem(DEFAULT_NOMBRE_DOCTOR))
            .jsonPath("$.[*].correoPersonal")
            .value(hasItem(DEFAULT_CORREO_PERSONAL))
            .jsonPath("$.[*].correoCorporativo")
            .value(hasItem(DEFAULT_CORREO_CORPORATIVO))
            .jsonPath("$.[*].telefonoPersonal")
            .value(hasItem(DEFAULT_TELEFONO_PERSONAL))
            .jsonPath("$.[*].telefonoCorporativo")
            .value(hasItem(DEFAULT_TELEFONO_CORPORATIVO));
    }
}
