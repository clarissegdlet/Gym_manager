package com.smartgym.manager.web.rest;

import static com.smartgym.manager.domain.CoachAsserts.*;
import static com.smartgym.manager.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgym.manager.IntegrationTest;
import com.smartgym.manager.domain.Coach;
import com.smartgym.manager.repository.CoachRepository;
import com.smartgym.manager.service.dto.CoachDTO;
import com.smartgym.manager.service.mapper.CoachMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CoachResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CoachResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SPECIALTY = "AAAAAAAAAA";
    private static final String UPDATED_SPECIALTY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/coaches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private CoachMapper coachMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCoachMockMvc;

    private Coach coach;

    private Coach insertedCoach;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coach createEntity() {
        return new Coach().name(DEFAULT_NAME).specialty(DEFAULT_SPECIALTY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coach createUpdatedEntity() {
        return new Coach().name(UPDATED_NAME).specialty(UPDATED_SPECIALTY);
    }

    @BeforeEach
    void initTest() {
        coach = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCoach != null) {
            coachRepository.delete(insertedCoach);
            insertedCoach = null;
        }
    }

    @Test
    @Transactional
    void createCoach() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Coach
        CoachDTO coachDTO = coachMapper.toDto(coach);
        var returnedCoachDTO = om.readValue(
            restCoachMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(coachDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CoachDTO.class
        );

        // Validate the Coach in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCoach = coachMapper.toEntity(returnedCoachDTO);
        assertCoachUpdatableFieldsEquals(returnedCoach, getPersistedCoach(returnedCoach));

        insertedCoach = returnedCoach;
    }

    @Test
    @Transactional
    void createCoachWithExistingId() throws Exception {
        // Create the Coach with an existing ID
        coach.setId(1L);
        CoachDTO coachDTO = coachMapper.toDto(coach);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCoachMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(coachDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Coach in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        coach.setName(null);

        // Create the Coach, which fails.
        CoachDTO coachDTO = coachMapper.toDto(coach);

        restCoachMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(coachDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCoaches() throws Exception {
        // Initialize the database
        insertedCoach = coachRepository.saveAndFlush(coach);

        // Get all the coachList
        restCoachMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coach.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].specialty").value(hasItem(DEFAULT_SPECIALTY)));
    }

    @Test
    @Transactional
    void getCoach() throws Exception {
        // Initialize the database
        insertedCoach = coachRepository.saveAndFlush(coach);

        // Get the coach
        restCoachMockMvc
            .perform(get(ENTITY_API_URL_ID, coach.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(coach.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.specialty").value(DEFAULT_SPECIALTY));
    }

    @Test
    @Transactional
    void getNonExistingCoach() throws Exception {
        // Get the coach
        restCoachMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCoach() throws Exception {
        // Initialize the database
        insertedCoach = coachRepository.saveAndFlush(coach);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coach
        Coach updatedCoach = coachRepository.findById(coach.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCoach are not directly saved in db
        em.detach(updatedCoach);
        updatedCoach.name(UPDATED_NAME).specialty(UPDATED_SPECIALTY);
        CoachDTO coachDTO = coachMapper.toDto(updatedCoach);

        restCoachMockMvc
            .perform(
                put(ENTITY_API_URL_ID, coachDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(coachDTO))
            )
            .andExpect(status().isOk());

        // Validate the Coach in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCoachToMatchAllProperties(updatedCoach);
    }

    @Test
    @Transactional
    void putNonExistingCoach() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coach.setId(longCount.incrementAndGet());

        // Create the Coach
        CoachDTO coachDTO = coachMapper.toDto(coach);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoachMockMvc
            .perform(
                put(ENTITY_API_URL_ID, coachDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(coachDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coach in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCoach() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coach.setId(longCount.incrementAndGet());

        // Create the Coach
        CoachDTO coachDTO = coachMapper.toDto(coach);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoachMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(coachDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coach in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCoach() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coach.setId(longCount.incrementAndGet());

        // Create the Coach
        CoachDTO coachDTO = coachMapper.toDto(coach);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoachMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(coachDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coach in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCoachWithPatch() throws Exception {
        // Initialize the database
        insertedCoach = coachRepository.saveAndFlush(coach);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coach using partial update
        Coach partialUpdatedCoach = new Coach();
        partialUpdatedCoach.setId(coach.getId());

        partialUpdatedCoach.name(UPDATED_NAME).specialty(UPDATED_SPECIALTY);

        restCoachMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoach.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCoach))
            )
            .andExpect(status().isOk());

        // Validate the Coach in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCoachUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCoach, coach), getPersistedCoach(coach));
    }

    @Test
    @Transactional
    void fullUpdateCoachWithPatch() throws Exception {
        // Initialize the database
        insertedCoach = coachRepository.saveAndFlush(coach);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coach using partial update
        Coach partialUpdatedCoach = new Coach();
        partialUpdatedCoach.setId(coach.getId());

        partialUpdatedCoach.name(UPDATED_NAME).specialty(UPDATED_SPECIALTY);

        restCoachMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoach.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCoach))
            )
            .andExpect(status().isOk());

        // Validate the Coach in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCoachUpdatableFieldsEquals(partialUpdatedCoach, getPersistedCoach(partialUpdatedCoach));
    }

    @Test
    @Transactional
    void patchNonExistingCoach() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coach.setId(longCount.incrementAndGet());

        // Create the Coach
        CoachDTO coachDTO = coachMapper.toDto(coach);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCoachMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, coachDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(coachDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coach in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCoach() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coach.setId(longCount.incrementAndGet());

        // Create the Coach
        CoachDTO coachDTO = coachMapper.toDto(coach);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoachMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(coachDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coach in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCoach() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coach.setId(longCount.incrementAndGet());

        // Create the Coach
        CoachDTO coachDTO = coachMapper.toDto(coach);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCoachMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(coachDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coach in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCoach() throws Exception {
        // Initialize the database
        insertedCoach = coachRepository.saveAndFlush(coach);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the coach
        restCoachMockMvc
            .perform(delete(ENTITY_API_URL_ID, coach.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return coachRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Coach getPersistedCoach(Coach coach) {
        return coachRepository.findById(coach.getId()).orElseThrow();
    }

    protected void assertPersistedCoachToMatchAllProperties(Coach expectedCoach) {
        assertCoachAllPropertiesEquals(expectedCoach, getPersistedCoach(expectedCoach));
    }

    protected void assertPersistedCoachToMatchUpdatableProperties(Coach expectedCoach) {
        assertCoachAllUpdatablePropertiesEquals(expectedCoach, getPersistedCoach(expectedCoach));
    }
}
