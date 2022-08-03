package com.labreferencia.reportemercadeo.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.labreferencia.reportemercadeo.IntegrationTest;
import com.labreferencia.reportemercadeo.domain.Especialidad;
import com.labreferencia.reportemercadeo.repository.EspecialidadRepository;
import com.labreferencia.reportemercadeo.service.dto.EspecialidadDTO;
import com.labreferencia.reportemercadeo.service.mapper.EspecialidadMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EspecialidadResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EspecialidadResourceIT {

    private static final String DEFAULT_NOMBRE_ESPECIALIDAD = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_ESPECIALIDAD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/especialidads";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private EspecialidadMapper especialidadMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEspecialidadMockMvc;

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

    @BeforeEach
    public void initTest() {
        especialidad = createEntity(em);
    }

    @Test
    @Transactional
    void createEspecialidad() throws Exception {
        int databaseSizeBeforeCreate = especialidadRepository.findAll().size();
        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);
        restEspecialidadMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeCreate + 1);
        Especialidad testEspecialidad = especialidadList.get(especialidadList.size() - 1);
        assertThat(testEspecialidad.getNombreEspecialidad()).isEqualTo(DEFAULT_NOMBRE_ESPECIALIDAD);
    }

    @Test
    @Transactional
    void createEspecialidadWithExistingId() throws Exception {
        // Create the Especialidad with an existing ID
        especialidad.setId(1L);
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        int databaseSizeBeforeCreate = especialidadRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEspecialidadMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreEspecialidadIsRequired() throws Exception {
        int databaseSizeBeforeTest = especialidadRepository.findAll().size();
        // set the field null
        especialidad.setNombreEspecialidad(null);

        // Create the Especialidad, which fails.
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        restEspecialidadMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            )
            .andExpect(status().isBadRequest());

        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEspecialidads() throws Exception {
        // Initialize the database
        especialidadRepository.saveAndFlush(especialidad);

        // Get all the especialidadList
        restEspecialidadMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(especialidad.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombreEspecialidad").value(hasItem(DEFAULT_NOMBRE_ESPECIALIDAD)));
    }

    @Test
    @Transactional
    void getEspecialidad() throws Exception {
        // Initialize the database
        especialidadRepository.saveAndFlush(especialidad);

        // Get the especialidad
        restEspecialidadMockMvc
            .perform(get(ENTITY_API_URL_ID, especialidad.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(especialidad.getId().intValue()))
            .andExpect(jsonPath("$.nombreEspecialidad").value(DEFAULT_NOMBRE_ESPECIALIDAD));
    }

    @Test
    @Transactional
    void getNonExistingEspecialidad() throws Exception {
        // Get the especialidad
        restEspecialidadMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEspecialidad() throws Exception {
        // Initialize the database
        especialidadRepository.saveAndFlush(especialidad);

        int databaseSizeBeforeUpdate = especialidadRepository.findAll().size();

        // Update the especialidad
        Especialidad updatedEspecialidad = especialidadRepository.findById(especialidad.getId()).get();
        // Disconnect from session so that the updates on updatedEspecialidad are not directly saved in db
        em.detach(updatedEspecialidad);
        updatedEspecialidad.nombreEspecialidad(UPDATED_NOMBRE_ESPECIALIDAD);
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(updatedEspecialidad);

        restEspecialidadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, especialidadDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            )
            .andExpect(status().isOk());

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        Especialidad testEspecialidad = especialidadList.get(especialidadList.size() - 1);
        assertThat(testEspecialidad.getNombreEspecialidad()).isEqualTo(UPDATED_NOMBRE_ESPECIALIDAD);
    }

    @Test
    @Transactional
    void putNonExistingEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().size();
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEspecialidadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, especialidadDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().size();
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEspecialidadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().size();
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEspecialidadMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEspecialidadWithPatch() throws Exception {
        // Initialize the database
        especialidadRepository.saveAndFlush(especialidad);

        int databaseSizeBeforeUpdate = especialidadRepository.findAll().size();

        // Update the especialidad using partial update
        Especialidad partialUpdatedEspecialidad = new Especialidad();
        partialUpdatedEspecialidad.setId(especialidad.getId());

        restEspecialidadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEspecialidad.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEspecialidad))
            )
            .andExpect(status().isOk());

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        Especialidad testEspecialidad = especialidadList.get(especialidadList.size() - 1);
        assertThat(testEspecialidad.getNombreEspecialidad()).isEqualTo(DEFAULT_NOMBRE_ESPECIALIDAD);
    }

    @Test
    @Transactional
    void fullUpdateEspecialidadWithPatch() throws Exception {
        // Initialize the database
        especialidadRepository.saveAndFlush(especialidad);

        int databaseSizeBeforeUpdate = especialidadRepository.findAll().size();

        // Update the especialidad using partial update
        Especialidad partialUpdatedEspecialidad = new Especialidad();
        partialUpdatedEspecialidad.setId(especialidad.getId());

        partialUpdatedEspecialidad.nombreEspecialidad(UPDATED_NOMBRE_ESPECIALIDAD);

        restEspecialidadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEspecialidad.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEspecialidad))
            )
            .andExpect(status().isOk());

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
        Especialidad testEspecialidad = especialidadList.get(especialidadList.size() - 1);
        assertThat(testEspecialidad.getNombreEspecialidad()).isEqualTo(UPDATED_NOMBRE_ESPECIALIDAD);
    }

    @Test
    @Transactional
    void patchNonExistingEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().size();
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEspecialidadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, especialidadDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().size();
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEspecialidadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEspecialidad() throws Exception {
        int databaseSizeBeforeUpdate = especialidadRepository.findAll().size();
        especialidad.setId(count.incrementAndGet());

        // Create the Especialidad
        EspecialidadDTO especialidadDTO = especialidadMapper.toDto(especialidad);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEspecialidadMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(especialidadDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Especialidad in the database
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEspecialidad() throws Exception {
        // Initialize the database
        especialidadRepository.saveAndFlush(especialidad);

        int databaseSizeBeforeDelete = especialidadRepository.findAll().size();

        // Delete the especialidad
        restEspecialidadMockMvc
            .perform(delete(ENTITY_API_URL_ID, especialidad.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Especialidad> especialidadList = especialidadRepository.findAll();
        assertThat(especialidadList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
