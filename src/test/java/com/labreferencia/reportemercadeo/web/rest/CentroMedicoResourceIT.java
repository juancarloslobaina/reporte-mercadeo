package com.labreferencia.reportemercadeo.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.labreferencia.reportemercadeo.IntegrationTest;
import com.labreferencia.reportemercadeo.domain.CentroMedico;
import com.labreferencia.reportemercadeo.repository.CentroMedicoRepository;
import com.labreferencia.reportemercadeo.service.CentroMedicoService;
import com.labreferencia.reportemercadeo.service.dto.CentroMedicoDTO;
import com.labreferencia.reportemercadeo.service.mapper.CentroMedicoMapper;
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
 * Integration tests for the {@link CentroMedicoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CentroMedicoResourceIT {

    private static final String DEFAULT_NOMBRE_CENTRO_MEDICO = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_CENTRO_MEDICO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/centro-medicos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

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
    private EntityManager em;

    @Autowired
    private MockMvc restCentroMedicoMockMvc;

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

    @BeforeEach
    public void initTest() {
        centroMedico = createEntity(em);
    }

    @Test
    @Transactional
    void createCentroMedico() throws Exception {
        int databaseSizeBeforeCreate = centroMedicoRepository.findAll().size();
        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);
        restCentroMedicoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeCreate + 1);
        CentroMedico testCentroMedico = centroMedicoList.get(centroMedicoList.size() - 1);
        assertThat(testCentroMedico.getNombreCentroMedico()).isEqualTo(DEFAULT_NOMBRE_CENTRO_MEDICO);
    }

    @Test
    @Transactional
    void createCentroMedicoWithExistingId() throws Exception {
        // Create the CentroMedico with an existing ID
        centroMedico.setId(1L);
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        int databaseSizeBeforeCreate = centroMedicoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCentroMedicoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreCentroMedicoIsRequired() throws Exception {
        int databaseSizeBeforeTest = centroMedicoRepository.findAll().size();
        // set the field null
        centroMedico.setNombreCentroMedico(null);

        // Create the CentroMedico, which fails.
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        restCentroMedicoMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            )
            .andExpect(status().isBadRequest());

        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCentroMedicos() throws Exception {
        // Initialize the database
        centroMedicoRepository.saveAndFlush(centroMedico);

        // Get all the centroMedicoList
        restCentroMedicoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(centroMedico.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombreCentroMedico").value(hasItem(DEFAULT_NOMBRE_CENTRO_MEDICO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCentroMedicosWithEagerRelationshipsIsEnabled() throws Exception {
        when(centroMedicoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCentroMedicoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(centroMedicoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCentroMedicosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(centroMedicoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCentroMedicoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(centroMedicoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCentroMedico() throws Exception {
        // Initialize the database
        centroMedicoRepository.saveAndFlush(centroMedico);

        // Get the centroMedico
        restCentroMedicoMockMvc
            .perform(get(ENTITY_API_URL_ID, centroMedico.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(centroMedico.getId().intValue()))
            .andExpect(jsonPath("$.nombreCentroMedico").value(DEFAULT_NOMBRE_CENTRO_MEDICO));
    }

    @Test
    @Transactional
    void getNonExistingCentroMedico() throws Exception {
        // Get the centroMedico
        restCentroMedicoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCentroMedico() throws Exception {
        // Initialize the database
        centroMedicoRepository.saveAndFlush(centroMedico);

        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().size();

        // Update the centroMedico
        CentroMedico updatedCentroMedico = centroMedicoRepository.findById(centroMedico.getId()).get();
        // Disconnect from session so that the updates on updatedCentroMedico are not directly saved in db
        em.detach(updatedCentroMedico);
        updatedCentroMedico.nombreCentroMedico(UPDATED_NOMBRE_CENTRO_MEDICO);
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(updatedCentroMedico);

        restCentroMedicoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, centroMedicoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            )
            .andExpect(status().isOk());

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        CentroMedico testCentroMedico = centroMedicoList.get(centroMedicoList.size() - 1);
        assertThat(testCentroMedico.getNombreCentroMedico()).isEqualTo(UPDATED_NOMBRE_CENTRO_MEDICO);
    }

    @Test
    @Transactional
    void putNonExistingCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().size();
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCentroMedicoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, centroMedicoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().size();
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCentroMedicoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().size();
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCentroMedicoMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCentroMedicoWithPatch() throws Exception {
        // Initialize the database
        centroMedicoRepository.saveAndFlush(centroMedico);

        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().size();

        // Update the centroMedico using partial update
        CentroMedico partialUpdatedCentroMedico = new CentroMedico();
        partialUpdatedCentroMedico.setId(centroMedico.getId());

        partialUpdatedCentroMedico.nombreCentroMedico(UPDATED_NOMBRE_CENTRO_MEDICO);

        restCentroMedicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCentroMedico.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCentroMedico))
            )
            .andExpect(status().isOk());

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        CentroMedico testCentroMedico = centroMedicoList.get(centroMedicoList.size() - 1);
        assertThat(testCentroMedico.getNombreCentroMedico()).isEqualTo(UPDATED_NOMBRE_CENTRO_MEDICO);
    }

    @Test
    @Transactional
    void fullUpdateCentroMedicoWithPatch() throws Exception {
        // Initialize the database
        centroMedicoRepository.saveAndFlush(centroMedico);

        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().size();

        // Update the centroMedico using partial update
        CentroMedico partialUpdatedCentroMedico = new CentroMedico();
        partialUpdatedCentroMedico.setId(centroMedico.getId());

        partialUpdatedCentroMedico.nombreCentroMedico(UPDATED_NOMBRE_CENTRO_MEDICO);

        restCentroMedicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCentroMedico.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCentroMedico))
            )
            .andExpect(status().isOk());

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
        CentroMedico testCentroMedico = centroMedicoList.get(centroMedicoList.size() - 1);
        assertThat(testCentroMedico.getNombreCentroMedico()).isEqualTo(UPDATED_NOMBRE_CENTRO_MEDICO);
    }

    @Test
    @Transactional
    void patchNonExistingCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().size();
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCentroMedicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, centroMedicoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().size();
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCentroMedicoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCentroMedico() throws Exception {
        int databaseSizeBeforeUpdate = centroMedicoRepository.findAll().size();
        centroMedico.setId(count.incrementAndGet());

        // Create the CentroMedico
        CentroMedicoDTO centroMedicoDTO = centroMedicoMapper.toDto(centroMedico);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCentroMedicoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(centroMedicoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CentroMedico in the database
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCentroMedico() throws Exception {
        // Initialize the database
        centroMedicoRepository.saveAndFlush(centroMedico);

        int databaseSizeBeforeDelete = centroMedicoRepository.findAll().size();

        // Delete the centroMedico
        restCentroMedicoMockMvc
            .perform(delete(ENTITY_API_URL_ID, centroMedico.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CentroMedico> centroMedicoList = centroMedicoRepository.findAll();
        assertThat(centroMedicoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
