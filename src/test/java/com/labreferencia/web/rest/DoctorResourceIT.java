package com.labreferencia.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.labreferencia.IntegrationTest;
import com.labreferencia.domain.Doctor;
import com.labreferencia.domain.Especialidad;
import com.labreferencia.domain.User;
import com.labreferencia.repository.DoctorRepository;
import com.labreferencia.service.DoctorService;
import com.labreferencia.service.criteria.DoctorCriteria;
import com.labreferencia.service.dto.DoctorDTO;
import com.labreferencia.service.mapper.DoctorMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DoctorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DoctorResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

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
    private EntityManager em;

    @Autowired
    private MockMvc restDoctorMockMvc;

    private Doctor doctor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Doctor createEntity(EntityManager em) {
        Doctor doctor = new Doctor()
            .nombre(DEFAULT_NOMBRE)
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
            .nombre(UPDATED_NOMBRE)
            .correoPersonal(UPDATED_CORREO_PERSONAL)
            .correoCorporativo(UPDATED_CORREO_CORPORATIVO)
            .telefonoPersonal(UPDATED_TELEFONO_PERSONAL)
            .telefonoCorporativo(UPDATED_TELEFONO_CORPORATIVO);
        return doctor;
    }

    @BeforeEach
    public void initTest() {
        doctor = createEntity(em);
    }

    @Test
    @Transactional
    void createDoctor() throws Exception {
        int databaseSizeBeforeCreate = doctorRepository.findAll().size();
        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);
        restDoctorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isCreated());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeCreate + 1);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testDoctor.getCorreoPersonal()).isEqualTo(DEFAULT_CORREO_PERSONAL);
        assertThat(testDoctor.getCorreoCorporativo()).isEqualTo(DEFAULT_CORREO_CORPORATIVO);
        assertThat(testDoctor.getTelefonoPersonal()).isEqualTo(DEFAULT_TELEFONO_PERSONAL);
        assertThat(testDoctor.getTelefonoCorporativo()).isEqualTo(DEFAULT_TELEFONO_CORPORATIVO);
    }

    @Test
    @Transactional
    void createDoctorWithExistingId() throws Exception {
        // Create the Doctor with an existing ID
        doctor.setId(1L);
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        int databaseSizeBeforeCreate = doctorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDoctorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().size();
        // set the field null
        doctor.setNombre(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        restDoctorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCorreoCorporativoIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().size();
        // set the field null
        doctor.setCorreoCorporativo(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        restDoctorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTelefonoCorporativoIsRequired() throws Exception {
        int databaseSizeBeforeTest = doctorRepository.findAll().size();
        // set the field null
        doctor.setTelefonoCorporativo(null);

        // Create the Doctor, which fails.
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        restDoctorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isBadRequest());

        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDoctors() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctor.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].correoPersonal").value(hasItem(DEFAULT_CORREO_PERSONAL)))
            .andExpect(jsonPath("$.[*].correoCorporativo").value(hasItem(DEFAULT_CORREO_CORPORATIVO)))
            .andExpect(jsonPath("$.[*].telefonoPersonal").value(hasItem(DEFAULT_TELEFONO_PERSONAL)))
            .andExpect(jsonPath("$.[*].telefonoCorporativo").value(hasItem(DEFAULT_TELEFONO_CORPORATIVO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDoctorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(doctorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDoctorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(doctorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDoctorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(doctorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDoctorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(doctorRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDoctor() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get the doctor
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL_ID, doctor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(doctor.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.correoPersonal").value(DEFAULT_CORREO_PERSONAL))
            .andExpect(jsonPath("$.correoCorporativo").value(DEFAULT_CORREO_CORPORATIVO))
            .andExpect(jsonPath("$.telefonoPersonal").value(DEFAULT_TELEFONO_PERSONAL))
            .andExpect(jsonPath("$.telefonoCorporativo").value(DEFAULT_TELEFONO_CORPORATIVO));
    }

    @Test
    @Transactional
    void getDoctorsByIdFiltering() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        Long id = doctor.getId();

        defaultDoctorShouldBeFound("id.equals=" + id);
        defaultDoctorShouldNotBeFound("id.notEquals=" + id);

        defaultDoctorShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDoctorShouldNotBeFound("id.greaterThan=" + id);

        defaultDoctorShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDoctorShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDoctorsByNombreIsEqualToSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where nombre equals to DEFAULT_NOMBRE
        defaultDoctorShouldBeFound("nombre.equals=" + DEFAULT_NOMBRE);

        // Get all the doctorList where nombre equals to UPDATED_NOMBRE
        defaultDoctorShouldNotBeFound("nombre.equals=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllDoctorsByNombreIsInShouldWork() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where nombre in DEFAULT_NOMBRE or UPDATED_NOMBRE
        defaultDoctorShouldBeFound("nombre.in=" + DEFAULT_NOMBRE + "," + UPDATED_NOMBRE);

        // Get all the doctorList where nombre equals to UPDATED_NOMBRE
        defaultDoctorShouldNotBeFound("nombre.in=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllDoctorsByNombreIsNullOrNotNull() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where nombre is not null
        defaultDoctorShouldBeFound("nombre.specified=true");

        // Get all the doctorList where nombre is null
        defaultDoctorShouldNotBeFound("nombre.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorsByNombreContainsSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where nombre contains DEFAULT_NOMBRE
        defaultDoctorShouldBeFound("nombre.contains=" + DEFAULT_NOMBRE);

        // Get all the doctorList where nombre contains UPDATED_NOMBRE
        defaultDoctorShouldNotBeFound("nombre.contains=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllDoctorsByNombreNotContainsSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where nombre does not contain DEFAULT_NOMBRE
        defaultDoctorShouldNotBeFound("nombre.doesNotContain=" + DEFAULT_NOMBRE);

        // Get all the doctorList where nombre does not contain UPDATED_NOMBRE
        defaultDoctorShouldBeFound("nombre.doesNotContain=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllDoctorsByCorreoPersonalIsEqualToSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where correoPersonal equals to DEFAULT_CORREO_PERSONAL
        defaultDoctorShouldBeFound("correoPersonal.equals=" + DEFAULT_CORREO_PERSONAL);

        // Get all the doctorList where correoPersonal equals to UPDATED_CORREO_PERSONAL
        defaultDoctorShouldNotBeFound("correoPersonal.equals=" + UPDATED_CORREO_PERSONAL);
    }

    @Test
    @Transactional
    void getAllDoctorsByCorreoPersonalIsInShouldWork() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where correoPersonal in DEFAULT_CORREO_PERSONAL or UPDATED_CORREO_PERSONAL
        defaultDoctorShouldBeFound("correoPersonal.in=" + DEFAULT_CORREO_PERSONAL + "," + UPDATED_CORREO_PERSONAL);

        // Get all the doctorList where correoPersonal equals to UPDATED_CORREO_PERSONAL
        defaultDoctorShouldNotBeFound("correoPersonal.in=" + UPDATED_CORREO_PERSONAL);
    }

    @Test
    @Transactional
    void getAllDoctorsByCorreoPersonalIsNullOrNotNull() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where correoPersonal is not null
        defaultDoctorShouldBeFound("correoPersonal.specified=true");

        // Get all the doctorList where correoPersonal is null
        defaultDoctorShouldNotBeFound("correoPersonal.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorsByCorreoPersonalContainsSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where correoPersonal contains DEFAULT_CORREO_PERSONAL
        defaultDoctorShouldBeFound("correoPersonal.contains=" + DEFAULT_CORREO_PERSONAL);

        // Get all the doctorList where correoPersonal contains UPDATED_CORREO_PERSONAL
        defaultDoctorShouldNotBeFound("correoPersonal.contains=" + UPDATED_CORREO_PERSONAL);
    }

    @Test
    @Transactional
    void getAllDoctorsByCorreoPersonalNotContainsSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where correoPersonal does not contain DEFAULT_CORREO_PERSONAL
        defaultDoctorShouldNotBeFound("correoPersonal.doesNotContain=" + DEFAULT_CORREO_PERSONAL);

        // Get all the doctorList where correoPersonal does not contain UPDATED_CORREO_PERSONAL
        defaultDoctorShouldBeFound("correoPersonal.doesNotContain=" + UPDATED_CORREO_PERSONAL);
    }

    @Test
    @Transactional
    void getAllDoctorsByCorreoCorporativoIsEqualToSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where correoCorporativo equals to DEFAULT_CORREO_CORPORATIVO
        defaultDoctorShouldBeFound("correoCorporativo.equals=" + DEFAULT_CORREO_CORPORATIVO);

        // Get all the doctorList where correoCorporativo equals to UPDATED_CORREO_CORPORATIVO
        defaultDoctorShouldNotBeFound("correoCorporativo.equals=" + UPDATED_CORREO_CORPORATIVO);
    }

    @Test
    @Transactional
    void getAllDoctorsByCorreoCorporativoIsInShouldWork() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where correoCorporativo in DEFAULT_CORREO_CORPORATIVO or UPDATED_CORREO_CORPORATIVO
        defaultDoctorShouldBeFound("correoCorporativo.in=" + DEFAULT_CORREO_CORPORATIVO + "," + UPDATED_CORREO_CORPORATIVO);

        // Get all the doctorList where correoCorporativo equals to UPDATED_CORREO_CORPORATIVO
        defaultDoctorShouldNotBeFound("correoCorporativo.in=" + UPDATED_CORREO_CORPORATIVO);
    }

    @Test
    @Transactional
    void getAllDoctorsByCorreoCorporativoIsNullOrNotNull() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where correoCorporativo is not null
        defaultDoctorShouldBeFound("correoCorporativo.specified=true");

        // Get all the doctorList where correoCorporativo is null
        defaultDoctorShouldNotBeFound("correoCorporativo.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorsByCorreoCorporativoContainsSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where correoCorporativo contains DEFAULT_CORREO_CORPORATIVO
        defaultDoctorShouldBeFound("correoCorporativo.contains=" + DEFAULT_CORREO_CORPORATIVO);

        // Get all the doctorList where correoCorporativo contains UPDATED_CORREO_CORPORATIVO
        defaultDoctorShouldNotBeFound("correoCorporativo.contains=" + UPDATED_CORREO_CORPORATIVO);
    }

    @Test
    @Transactional
    void getAllDoctorsByCorreoCorporativoNotContainsSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where correoCorporativo does not contain DEFAULT_CORREO_CORPORATIVO
        defaultDoctorShouldNotBeFound("correoCorporativo.doesNotContain=" + DEFAULT_CORREO_CORPORATIVO);

        // Get all the doctorList where correoCorporativo does not contain UPDATED_CORREO_CORPORATIVO
        defaultDoctorShouldBeFound("correoCorporativo.doesNotContain=" + UPDATED_CORREO_CORPORATIVO);
    }

    @Test
    @Transactional
    void getAllDoctorsByTelefonoPersonalIsEqualToSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where telefonoPersonal equals to DEFAULT_TELEFONO_PERSONAL
        defaultDoctorShouldBeFound("telefonoPersonal.equals=" + DEFAULT_TELEFONO_PERSONAL);

        // Get all the doctorList where telefonoPersonal equals to UPDATED_TELEFONO_PERSONAL
        defaultDoctorShouldNotBeFound("telefonoPersonal.equals=" + UPDATED_TELEFONO_PERSONAL);
    }

    @Test
    @Transactional
    void getAllDoctorsByTelefonoPersonalIsInShouldWork() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where telefonoPersonal in DEFAULT_TELEFONO_PERSONAL or UPDATED_TELEFONO_PERSONAL
        defaultDoctorShouldBeFound("telefonoPersonal.in=" + DEFAULT_TELEFONO_PERSONAL + "," + UPDATED_TELEFONO_PERSONAL);

        // Get all the doctorList where telefonoPersonal equals to UPDATED_TELEFONO_PERSONAL
        defaultDoctorShouldNotBeFound("telefonoPersonal.in=" + UPDATED_TELEFONO_PERSONAL);
    }

    @Test
    @Transactional
    void getAllDoctorsByTelefonoPersonalIsNullOrNotNull() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where telefonoPersonal is not null
        defaultDoctorShouldBeFound("telefonoPersonal.specified=true");

        // Get all the doctorList where telefonoPersonal is null
        defaultDoctorShouldNotBeFound("telefonoPersonal.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorsByTelefonoPersonalContainsSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where telefonoPersonal contains DEFAULT_TELEFONO_PERSONAL
        defaultDoctorShouldBeFound("telefonoPersonal.contains=" + DEFAULT_TELEFONO_PERSONAL);

        // Get all the doctorList where telefonoPersonal contains UPDATED_TELEFONO_PERSONAL
        defaultDoctorShouldNotBeFound("telefonoPersonal.contains=" + UPDATED_TELEFONO_PERSONAL);
    }

    @Test
    @Transactional
    void getAllDoctorsByTelefonoPersonalNotContainsSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where telefonoPersonal does not contain DEFAULT_TELEFONO_PERSONAL
        defaultDoctorShouldNotBeFound("telefonoPersonal.doesNotContain=" + DEFAULT_TELEFONO_PERSONAL);

        // Get all the doctorList where telefonoPersonal does not contain UPDATED_TELEFONO_PERSONAL
        defaultDoctorShouldBeFound("telefonoPersonal.doesNotContain=" + UPDATED_TELEFONO_PERSONAL);
    }

    @Test
    @Transactional
    void getAllDoctorsByTelefonoCorporativoIsEqualToSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where telefonoCorporativo equals to DEFAULT_TELEFONO_CORPORATIVO
        defaultDoctorShouldBeFound("telefonoCorporativo.equals=" + DEFAULT_TELEFONO_CORPORATIVO);

        // Get all the doctorList where telefonoCorporativo equals to UPDATED_TELEFONO_CORPORATIVO
        defaultDoctorShouldNotBeFound("telefonoCorporativo.equals=" + UPDATED_TELEFONO_CORPORATIVO);
    }

    @Test
    @Transactional
    void getAllDoctorsByTelefonoCorporativoIsInShouldWork() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where telefonoCorporativo in DEFAULT_TELEFONO_CORPORATIVO or UPDATED_TELEFONO_CORPORATIVO
        defaultDoctorShouldBeFound("telefonoCorporativo.in=" + DEFAULT_TELEFONO_CORPORATIVO + "," + UPDATED_TELEFONO_CORPORATIVO);

        // Get all the doctorList where telefonoCorporativo equals to UPDATED_TELEFONO_CORPORATIVO
        defaultDoctorShouldNotBeFound("telefonoCorporativo.in=" + UPDATED_TELEFONO_CORPORATIVO);
    }

    @Test
    @Transactional
    void getAllDoctorsByTelefonoCorporativoIsNullOrNotNull() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where telefonoCorporativo is not null
        defaultDoctorShouldBeFound("telefonoCorporativo.specified=true");

        // Get all the doctorList where telefonoCorporativo is null
        defaultDoctorShouldNotBeFound("telefonoCorporativo.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorsByTelefonoCorporativoContainsSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where telefonoCorporativo contains DEFAULT_TELEFONO_CORPORATIVO
        defaultDoctorShouldBeFound("telefonoCorporativo.contains=" + DEFAULT_TELEFONO_CORPORATIVO);

        // Get all the doctorList where telefonoCorporativo contains UPDATED_TELEFONO_CORPORATIVO
        defaultDoctorShouldNotBeFound("telefonoCorporativo.contains=" + UPDATED_TELEFONO_CORPORATIVO);
    }

    @Test
    @Transactional
    void getAllDoctorsByTelefonoCorporativoNotContainsSomething() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        // Get all the doctorList where telefonoCorporativo does not contain DEFAULT_TELEFONO_CORPORATIVO
        defaultDoctorShouldNotBeFound("telefonoCorporativo.doesNotContain=" + DEFAULT_TELEFONO_CORPORATIVO);

        // Get all the doctorList where telefonoCorporativo does not contain UPDATED_TELEFONO_CORPORATIVO
        defaultDoctorShouldBeFound("telefonoCorporativo.doesNotContain=" + UPDATED_TELEFONO_CORPORATIVO);
    }

    @Test
    @Transactional
    void getAllDoctorsByEspecialidadIsEqualToSomething() throws Exception {
        Especialidad especialidad;
        if (TestUtil.findAll(em, Especialidad.class).isEmpty()) {
            doctorRepository.saveAndFlush(doctor);
            especialidad = EspecialidadResourceIT.createEntity(em);
        } else {
            especialidad = TestUtil.findAll(em, Especialidad.class).get(0);
        }
        em.persist(especialidad);
        em.flush();
        doctor.setEspecialidad(especialidad);
        doctorRepository.saveAndFlush(doctor);
        Long especialidadId = especialidad.getId();

        // Get all the doctorList where especialidad equals to especialidadId
        defaultDoctorShouldBeFound("especialidadId.equals=" + especialidadId);

        // Get all the doctorList where especialidad equals to (especialidadId + 1)
        defaultDoctorShouldNotBeFound("especialidadId.equals=" + (especialidadId + 1));
    }

    @Test
    @Transactional
    void getAllDoctorsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            doctorRepository.saveAndFlush(doctor);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        doctor.setUser(user);
        doctorRepository.saveAndFlush(doctor);
        Long userId = user.getId();

        // Get all the doctorList where user equals to userId
        defaultDoctorShouldBeFound("userId.equals=" + userId);

        // Get all the doctorList where user equals to (userId + 1)
        defaultDoctorShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDoctorShouldBeFound(String filter) throws Exception {
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctor.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].correoPersonal").value(hasItem(DEFAULT_CORREO_PERSONAL)))
            .andExpect(jsonPath("$.[*].correoCorporativo").value(hasItem(DEFAULT_CORREO_CORPORATIVO)))
            .andExpect(jsonPath("$.[*].telefonoPersonal").value(hasItem(DEFAULT_TELEFONO_PERSONAL)))
            .andExpect(jsonPath("$.[*].telefonoCorporativo").value(hasItem(DEFAULT_TELEFONO_CORPORATIVO)));

        // Check, that the count call also returns 1
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDoctorShouldNotBeFound(String filter) throws Exception {
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDoctorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDoctor() throws Exception {
        // Get the doctor
        restDoctorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDoctor() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();

        // Update the doctor
        Doctor updatedDoctor = doctorRepository.findById(doctor.getId()).get();
        // Disconnect from session so that the updates on updatedDoctor are not directly saved in db
        em.detach(updatedDoctor);
        updatedDoctor
            .nombre(UPDATED_NOMBRE)
            .correoPersonal(UPDATED_CORREO_PERSONAL)
            .correoCorporativo(UPDATED_CORREO_CORPORATIVO)
            .telefonoPersonal(UPDATED_TELEFONO_PERSONAL)
            .telefonoCorporativo(UPDATED_TELEFONO_CORPORATIVO);
        DoctorDTO doctorDTO = doctorMapper.toDto(updatedDoctor);

        restDoctorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(doctorDTO))
            )
            .andExpect(status().isOk());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testDoctor.getCorreoPersonal()).isEqualTo(UPDATED_CORREO_PERSONAL);
        assertThat(testDoctor.getCorreoCorporativo()).isEqualTo(UPDATED_CORREO_CORPORATIVO);
        assertThat(testDoctor.getTelefonoPersonal()).isEqualTo(UPDATED_TELEFONO_PERSONAL);
        assertThat(testDoctor.getTelefonoCorporativo()).isEqualTo(UPDATED_TELEFONO_CORPORATIVO);
    }

    @Test
    @Transactional
    void putNonExistingDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(doctorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(doctorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(doctorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDoctorWithPatch() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();

        // Update the doctor using partial update
        Doctor partialUpdatedDoctor = new Doctor();
        partialUpdatedDoctor.setId(doctor.getId());

        partialUpdatedDoctor.telefonoCorporativo(UPDATED_TELEFONO_CORPORATIVO);

        restDoctorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDoctor))
            )
            .andExpect(status().isOk());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testDoctor.getCorreoPersonal()).isEqualTo(DEFAULT_CORREO_PERSONAL);
        assertThat(testDoctor.getCorreoCorporativo()).isEqualTo(DEFAULT_CORREO_CORPORATIVO);
        assertThat(testDoctor.getTelefonoPersonal()).isEqualTo(DEFAULT_TELEFONO_PERSONAL);
        assertThat(testDoctor.getTelefonoCorporativo()).isEqualTo(UPDATED_TELEFONO_CORPORATIVO);
    }

    @Test
    @Transactional
    void fullUpdateDoctorWithPatch() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();

        // Update the doctor using partial update
        Doctor partialUpdatedDoctor = new Doctor();
        partialUpdatedDoctor.setId(doctor.getId());

        partialUpdatedDoctor
            .nombre(UPDATED_NOMBRE)
            .correoPersonal(UPDATED_CORREO_PERSONAL)
            .correoCorporativo(UPDATED_CORREO_CORPORATIVO)
            .telefonoPersonal(UPDATED_TELEFONO_PERSONAL)
            .telefonoCorporativo(UPDATED_TELEFONO_CORPORATIVO);

        restDoctorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDoctor))
            )
            .andExpect(status().isOk());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
        Doctor testDoctor = doctorList.get(doctorList.size() - 1);
        assertThat(testDoctor.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testDoctor.getCorreoPersonal()).isEqualTo(UPDATED_CORREO_PERSONAL);
        assertThat(testDoctor.getCorreoCorporativo()).isEqualTo(UPDATED_CORREO_CORPORATIVO);
        assertThat(testDoctor.getTelefonoPersonal()).isEqualTo(UPDATED_TELEFONO_PERSONAL);
        assertThat(testDoctor.getTelefonoCorporativo()).isEqualTo(UPDATED_TELEFONO_CORPORATIVO);
    }

    @Test
    @Transactional
    void patchNonExistingDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, doctorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(doctorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(doctorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDoctor() throws Exception {
        int databaseSizeBeforeUpdate = doctorRepository.findAll().size();
        doctor.setId(count.incrementAndGet());

        // Create the Doctor
        DoctorDTO doctorDTO = doctorMapper.toDto(doctor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(doctorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Doctor in the database
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDoctor() throws Exception {
        // Initialize the database
        doctorRepository.saveAndFlush(doctor);

        int databaseSizeBeforeDelete = doctorRepository.findAll().size();

        // Delete the doctor
        restDoctorMockMvc
            .perform(delete(ENTITY_API_URL_ID, doctor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Doctor> doctorList = doctorRepository.findAll();
        assertThat(doctorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
