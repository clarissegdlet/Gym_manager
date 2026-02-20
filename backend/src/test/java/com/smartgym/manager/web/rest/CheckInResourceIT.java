package com.smartgym.manager.web.rest;

import static com.smartgym.manager.domain.CheckInAsserts.*;
import static com.smartgym.manager.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgym.manager.IntegrationTest;
import com.smartgym.manager.domain.CheckIn;
import com.smartgym.manager.domain.Member;
import com.smartgym.manager.repository.CheckInRepository;
import com.smartgym.manager.service.dto.CheckInDTO;
import com.smartgym.manager.service.mapper.CheckInMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link CheckInResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CheckInResourceIT {

    private static final Instant DEFAULT_CHECK_IN_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CHECK_IN_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/check-ins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCheckInMockMvc;

    private CheckIn checkIn;

    private CheckIn insertedCheckIn;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CheckIn createEntity(EntityManager em) {
        CheckIn checkIn = new CheckIn().checkInTime(DEFAULT_CHECK_IN_TIME);
        // Add required entity
        Member member;
        if (TestUtil.findAll(em, Member.class).isEmpty()) {
            member = MemberResourceIT.createEntity();
            em.persist(member);
            em.flush();
        } else {
            member = TestUtil.findAll(em, Member.class).get(0);
        }
        checkIn.setMember(member);
        return checkIn;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CheckIn createUpdatedEntity(EntityManager em) {
        CheckIn updatedCheckIn = new CheckIn().checkInTime(UPDATED_CHECK_IN_TIME);
        // Add required entity
        Member member;
        if (TestUtil.findAll(em, Member.class).isEmpty()) {
            member = MemberResourceIT.createUpdatedEntity();
            em.persist(member);
            em.flush();
        } else {
            member = TestUtil.findAll(em, Member.class).get(0);
        }
        updatedCheckIn.setMember(member);
        return updatedCheckIn;
    }

    @BeforeEach
    void initTest() {
        checkIn = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCheckIn != null) {
            checkInRepository.delete(insertedCheckIn);
            insertedCheckIn = null;
        }
    }

    @Test
    @Transactional
    void createCheckIn() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);
        var returnedCheckInDTO = om.readValue(
            restCheckInMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkInDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CheckInDTO.class
        );

        // Validate the CheckIn in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCheckIn = checkInMapper.toEntity(returnedCheckInDTO);
        assertCheckInUpdatableFieldsEquals(returnedCheckIn, getPersistedCheckIn(returnedCheckIn));

        insertedCheckIn = returnedCheckIn;
    }

    @Test
    @Transactional
    void createCheckInWithExistingId() throws Exception {
        // Create the CheckIn with an existing ID
        checkIn.setId(1L);
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCheckInMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkInDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCheckInTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        checkIn.setCheckInTime(null);

        // Create the CheckIn, which fails.
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        restCheckInMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkInDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCheckIns() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        // Get all the checkInList
        restCheckInMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checkIn.getId().intValue())))
            .andExpect(jsonPath("$.[*].checkInTime").value(hasItem(DEFAULT_CHECK_IN_TIME.toString())));
    }

    @Test
    @Transactional
    void getCheckIn() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        // Get the checkIn
        restCheckInMockMvc
            .perform(get(ENTITY_API_URL_ID, checkIn.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(checkIn.getId().intValue()))
            .andExpect(jsonPath("$.checkInTime").value(DEFAULT_CHECK_IN_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCheckIn() throws Exception {
        // Get the checkIn
        restCheckInMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCheckIn() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkIn
        CheckIn updatedCheckIn = checkInRepository.findById(checkIn.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCheckIn are not directly saved in db
        em.detach(updatedCheckIn);
        updatedCheckIn.checkInTime(UPDATED_CHECK_IN_TIME);
        CheckInDTO checkInDTO = checkInMapper.toDto(updatedCheckIn);

        restCheckInMockMvc
            .perform(
                put(ENTITY_API_URL_ID, checkInDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkInDTO))
            )
            .andExpect(status().isOk());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCheckInToMatchAllProperties(updatedCheckIn);
    }

    @Test
    @Transactional
    void putNonExistingCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(
                put(ENTITY_API_URL_ID, checkInDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkInDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(checkInDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkInDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCheckInWithPatch() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkIn using partial update
        CheckIn partialUpdatedCheckIn = new CheckIn();
        partialUpdatedCheckIn.setId(checkIn.getId());

        partialUpdatedCheckIn.checkInTime(UPDATED_CHECK_IN_TIME);

        restCheckInMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckIn.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCheckIn))
            )
            .andExpect(status().isOk());

        // Validate the CheckIn in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCheckInUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCheckIn, checkIn), getPersistedCheckIn(checkIn));
    }

    @Test
    @Transactional
    void fullUpdateCheckInWithPatch() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkIn using partial update
        CheckIn partialUpdatedCheckIn = new CheckIn();
        partialUpdatedCheckIn.setId(checkIn.getId());

        partialUpdatedCheckIn.checkInTime(UPDATED_CHECK_IN_TIME);

        restCheckInMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckIn.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCheckIn))
            )
            .andExpect(status().isOk());

        // Validate the CheckIn in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCheckInUpdatableFieldsEquals(partialUpdatedCheckIn, getPersistedCheckIn(partialUpdatedCheckIn));
    }

    @Test
    @Transactional
    void patchNonExistingCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, checkInDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(checkInDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(checkInDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCheckIn() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkIn.setId(longCount.incrementAndGet());

        // Create the CheckIn
        CheckInDTO checkInDTO = checkInMapper.toDto(checkIn);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckInMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(checkInDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CheckIn in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCheckIn() throws Exception {
        // Initialize the database
        insertedCheckIn = checkInRepository.saveAndFlush(checkIn);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the checkIn
        restCheckInMockMvc
            .perform(delete(ENTITY_API_URL_ID, checkIn.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return checkInRepository.count();
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

    protected CheckIn getPersistedCheckIn(CheckIn checkIn) {
        return checkInRepository.findById(checkIn.getId()).orElseThrow();
    }

    protected void assertPersistedCheckInToMatchAllProperties(CheckIn expectedCheckIn) {
        assertCheckInAllPropertiesEquals(expectedCheckIn, getPersistedCheckIn(expectedCheckIn));
    }

    protected void assertPersistedCheckInToMatchUpdatableProperties(CheckIn expectedCheckIn) {
        assertCheckInAllUpdatablePropertiesEquals(expectedCheckIn, getPersistedCheckIn(expectedCheckIn));
    }
}
