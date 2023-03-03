package com.labreferencia.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.labreferencia.IntegrationTest;
import com.labreferencia.domain.Centro;
import com.labreferencia.domain.Ciudad;
import com.labreferencia.domain.User;
import com.labreferencia.repository.CentroRepository;
import com.labreferencia.service.CentroService;
import com.labreferencia.service.criteria.CentroCriteria;
import com.labreferencia.service.dto.CentroDTO;
import com.labreferencia.service.mapper.CentroMapper;
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
 * Integration tests for the {@link CentroResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CentroResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/centros";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CentroRepository centroRepository;

    @Mock
    private CentroRepository centroRepositoryMock;

    @Autowired
    private CentroMapper centroMapper;

    @Mock
    private CentroService centroServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCentroMockMvc;

    private Centro centro;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Centro createEntity(EntityManager em) {
        Centro centro = new Centro().nombre(DEFAULT_NOMBRE);
        return centro;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Centro createUpdatedEntity(EntityManager em) {
        Centro centro = new Centro().nombre(UPDATED_NOMBRE);
        return centro;
    }

    @BeforeEach
    public void initTest() {
        centro = createEntity(em);
    }

    @Test
    @Transactional
    void createCentro() throws Exception {
        int databaseSizeBeforeCreate = centroRepository.findAll().size();
        // Create the Centro
        CentroDTO centroDTO = centroMapper.toDto(centro);
        restCentroMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(centroDTO)))
            .andExpect(status().isCreated());

        // Validate the Centro in the database
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeCreate + 1);
        Centro testCentro = centroList.get(centroList.size() - 1);
        assertThat(testCentro.getNombre()).isEqualTo(DEFAULT_NOMBRE);
    }

    @Test
    @Transactional
    void createCentroWithExistingId() throws Exception {
        // Create the Centro with an existing ID
        centro.setId(1L);
        CentroDTO centroDTO = centroMapper.toDto(centro);

        int databaseSizeBeforeCreate = centroRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCentroMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(centroDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Centro in the database
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = centroRepository.findAll().size();
        // set the field null
        centro.setNombre(null);

        // Create the Centro, which fails.
        CentroDTO centroDTO = centroMapper.toDto(centro);

        restCentroMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(centroDTO)))
            .andExpect(status().isBadRequest());

        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCentros() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        // Get all the centroList
        restCentroMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(centro.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCentrosWithEagerRelationshipsIsEnabled() throws Exception {
        when(centroServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCentroMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(centroServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCentrosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(centroServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCentroMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(centroRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCentro() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        // Get the centro
        restCentroMockMvc
            .perform(get(ENTITY_API_URL_ID, centro.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(centro.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE));
    }

    @Test
    @Transactional
    void getCentrosByIdFiltering() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        Long id = centro.getId();

        defaultCentroShouldBeFound("id.equals=" + id);
        defaultCentroShouldNotBeFound("id.notEquals=" + id);

        defaultCentroShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCentroShouldNotBeFound("id.greaterThan=" + id);

        defaultCentroShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCentroShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCentrosByNombreIsEqualToSomething() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        // Get all the centroList where nombre equals to DEFAULT_NOMBRE
        defaultCentroShouldBeFound("nombre.equals=" + DEFAULT_NOMBRE);

        // Get all the centroList where nombre equals to UPDATED_NOMBRE
        defaultCentroShouldNotBeFound("nombre.equals=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllCentrosByNombreIsInShouldWork() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        // Get all the centroList where nombre in DEFAULT_NOMBRE or UPDATED_NOMBRE
        defaultCentroShouldBeFound("nombre.in=" + DEFAULT_NOMBRE + "," + UPDATED_NOMBRE);

        // Get all the centroList where nombre equals to UPDATED_NOMBRE
        defaultCentroShouldNotBeFound("nombre.in=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllCentrosByNombreIsNullOrNotNull() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        // Get all the centroList where nombre is not null
        defaultCentroShouldBeFound("nombre.specified=true");

        // Get all the centroList where nombre is null
        defaultCentroShouldNotBeFound("nombre.specified=false");
    }

    @Test
    @Transactional
    void getAllCentrosByNombreContainsSomething() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        // Get all the centroList where nombre contains DEFAULT_NOMBRE
        defaultCentroShouldBeFound("nombre.contains=" + DEFAULT_NOMBRE);

        // Get all the centroList where nombre contains UPDATED_NOMBRE
        defaultCentroShouldNotBeFound("nombre.contains=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllCentrosByNombreNotContainsSomething() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        // Get all the centroList where nombre does not contain DEFAULT_NOMBRE
        defaultCentroShouldNotBeFound("nombre.doesNotContain=" + DEFAULT_NOMBRE);

        // Get all the centroList where nombre does not contain UPDATED_NOMBRE
        defaultCentroShouldBeFound("nombre.doesNotContain=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void getAllCentrosByCiudadIsEqualToSomething() throws Exception {
        Ciudad ciudad;
        if (TestUtil.findAll(em, Ciudad.class).isEmpty()) {
            centroRepository.saveAndFlush(centro);
            ciudad = CiudadResourceIT.createEntity(em);
        } else {
            ciudad = TestUtil.findAll(em, Ciudad.class).get(0);
        }
        em.persist(ciudad);
        em.flush();
        centro.setCiudad(ciudad);
        centroRepository.saveAndFlush(centro);
        Long ciudadId = ciudad.getId();

        // Get all the centroList where ciudad equals to ciudadId
        defaultCentroShouldBeFound("ciudadId.equals=" + ciudadId);

        // Get all the centroList where ciudad equals to (ciudadId + 1)
        defaultCentroShouldNotBeFound("ciudadId.equals=" + (ciudadId + 1));
    }

    @Test
    @Transactional
    void getAllCentrosByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            centroRepository.saveAndFlush(centro);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        centro.setUser(user);
        centroRepository.saveAndFlush(centro);
        Long userId = user.getId();

        // Get all the centroList where user equals to userId
        defaultCentroShouldBeFound("userId.equals=" + userId);

        // Get all the centroList where user equals to (userId + 1)
        defaultCentroShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCentroShouldBeFound(String filter) throws Exception {
        restCentroMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(centro.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)));

        // Check, that the count call also returns 1
        restCentroMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCentroShouldNotBeFound(String filter) throws Exception {
        restCentroMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCentroMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCentro() throws Exception {
        // Get the centro
        restCentroMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCentro() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        int databaseSizeBeforeUpdate = centroRepository.findAll().size();

        // Update the centro
        Centro updatedCentro = centroRepository.findById(centro.getId()).get();
        // Disconnect from session so that the updates on updatedCentro are not directly saved in db
        em.detach(updatedCentro);
        updatedCentro.nombre(UPDATED_NOMBRE);
        CentroDTO centroDTO = centroMapper.toDto(updatedCentro);

        restCentroMockMvc
            .perform(
                put(ENTITY_API_URL_ID, centroDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(centroDTO))
            )
            .andExpect(status().isOk());

        // Validate the Centro in the database
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeUpdate);
        Centro testCentro = centroList.get(centroList.size() - 1);
        assertThat(testCentro.getNombre()).isEqualTo(UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void putNonExistingCentro() throws Exception {
        int databaseSizeBeforeUpdate = centroRepository.findAll().size();
        centro.setId(count.incrementAndGet());

        // Create the Centro
        CentroDTO centroDTO = centroMapper.toDto(centro);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCentroMockMvc
            .perform(
                put(ENTITY_API_URL_ID, centroDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(centroDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Centro in the database
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCentro() throws Exception {
        int databaseSizeBeforeUpdate = centroRepository.findAll().size();
        centro.setId(count.incrementAndGet());

        // Create the Centro
        CentroDTO centroDTO = centroMapper.toDto(centro);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCentroMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(centroDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Centro in the database
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCentro() throws Exception {
        int databaseSizeBeforeUpdate = centroRepository.findAll().size();
        centro.setId(count.incrementAndGet());

        // Create the Centro
        CentroDTO centroDTO = centroMapper.toDto(centro);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCentroMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(centroDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Centro in the database
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCentroWithPatch() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        int databaseSizeBeforeUpdate = centroRepository.findAll().size();

        // Update the centro using partial update
        Centro partialUpdatedCentro = new Centro();
        partialUpdatedCentro.setId(centro.getId());

        restCentroMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCentro.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCentro))
            )
            .andExpect(status().isOk());

        // Validate the Centro in the database
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeUpdate);
        Centro testCentro = centroList.get(centroList.size() - 1);
        assertThat(testCentro.getNombre()).isEqualTo(DEFAULT_NOMBRE);
    }

    @Test
    @Transactional
    void fullUpdateCentroWithPatch() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        int databaseSizeBeforeUpdate = centroRepository.findAll().size();

        // Update the centro using partial update
        Centro partialUpdatedCentro = new Centro();
        partialUpdatedCentro.setId(centro.getId());

        partialUpdatedCentro.nombre(UPDATED_NOMBRE);

        restCentroMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCentro.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCentro))
            )
            .andExpect(status().isOk());

        // Validate the Centro in the database
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeUpdate);
        Centro testCentro = centroList.get(centroList.size() - 1);
        assertThat(testCentro.getNombre()).isEqualTo(UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    void patchNonExistingCentro() throws Exception {
        int databaseSizeBeforeUpdate = centroRepository.findAll().size();
        centro.setId(count.incrementAndGet());

        // Create the Centro
        CentroDTO centroDTO = centroMapper.toDto(centro);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCentroMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, centroDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(centroDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Centro in the database
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCentro() throws Exception {
        int databaseSizeBeforeUpdate = centroRepository.findAll().size();
        centro.setId(count.incrementAndGet());

        // Create the Centro
        CentroDTO centroDTO = centroMapper.toDto(centro);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCentroMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(centroDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Centro in the database
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCentro() throws Exception {
        int databaseSizeBeforeUpdate = centroRepository.findAll().size();
        centro.setId(count.incrementAndGet());

        // Create the Centro
        CentroDTO centroDTO = centroMapper.toDto(centro);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCentroMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(centroDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Centro in the database
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCentro() throws Exception {
        // Initialize the database
        centroRepository.saveAndFlush(centro);

        int databaseSizeBeforeDelete = centroRepository.findAll().size();

        // Delete the centro
        restCentroMockMvc
            .perform(delete(ENTITY_API_URL_ID, centro.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Centro> centroList = centroRepository.findAll();
        assertThat(centroList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
