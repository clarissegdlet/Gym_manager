package com.smartgym.manager.web.rest;

import static com.smartgym.manager.domain.MembershipAsserts.*;
import static com.smartgym.manager.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgym.manager.IntegrationTest;
import com.smartgym.manager.domain.Member;
import com.smartgym.manager.domain.Membership;
import com.smartgym.manager.domain.enumeration.MembershipStatus;
import com.smartgym.manager.repository.MembershipRepository;
import com.smartgym.manager.service.dto.MembershipDTO;
import com.smartgym.manager.service.mapper.MembershipMapper;
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
 * Integration tests for the {@link MembershipResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MembershipResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final MembershipStatus DEFAULT_STATUS = MembershipStatus.ACTIVE;
    private static final MembershipStatus UPDATED_STATUS = MembershipStatus.SUSPENDED;

    private static final String ENTITY_API_URL = "/api/memberships";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipMapper membershipMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMembershipMockMvc;

    private Membership membership;

    private Membership insertedMembership;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Membership createEntity(EntityManager em) {
        Membership membership = new Membership()
            .type(DEFAULT_TYPE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .status(DEFAULT_STATUS);
        // Add required entity
        Member member;
        if (TestUtil.findAll(em, Member.class).isEmpty()) {
            member = MemberResourceIT.createEntity();
            em.persist(member);
            em.flush();
        } else {
            member = TestUtil.findAll(em, Member.class).get(0);
        }
        membership.setMember(member);
        return membership;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Membership createUpdatedEntity(EntityManager em) {
        Membership updatedMembership = new Membership()
            .type(UPDATED_TYPE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS);
        // Add required entity
        Member member;
        if (TestUtil.findAll(em, Member.class).isEmpty()) {
            member = MemberResourceIT.createUpdatedEntity();
            em.persist(member);
            em.flush();
        } else {
            member = TestUtil.findAll(em, Member.class).get(0);
        }
        updatedMembership.setMember(member);
        return updatedMembership;
    }

    @BeforeEach
    void initTest() {
        membership = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMembership != null) {
            membershipRepository.delete(insertedMembership);
            insertedMembership = null;
        }
    }

    @Test
    @Transactional
    void createMembership() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Membership
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);
        var returnedMembershipDTO = om.readValue(
            restMembershipMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(membershipDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MembershipDTO.class
        );

        // Validate the Membership in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMembership = membershipMapper.toEntity(returnedMembershipDTO);
        assertMembershipUpdatableFieldsEquals(returnedMembership, getPersistedMembership(returnedMembership));

        insertedMembership = returnedMembership;
    }

    @Test
    @Transactional
    void createMembershipWithExistingId() throws Exception {
        // Create the Membership with an existing ID
        membership.setId(1L);
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMembershipMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(membershipDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Membership in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        membership.setType(null);

        // Create the Membership, which fails.
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);

        restMembershipMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(membershipDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        membership.setStartDate(null);

        // Create the Membership, which fails.
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);

        restMembershipMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(membershipDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        membership.setEndDate(null);

        // Create the Membership, which fails.
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);

        restMembershipMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(membershipDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        membership.setStatus(null);

        // Create the Membership, which fails.
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);

        restMembershipMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(membershipDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMemberships() throws Exception {
        // Initialize the database
        insertedMembership = membershipRepository.saveAndFlush(membership);

        // Get all the membershipList
        restMembershipMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(membership.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getMembership() throws Exception {
        // Initialize the database
        insertedMembership = membershipRepository.saveAndFlush(membership);

        // Get the membership
        restMembershipMockMvc
            .perform(get(ENTITY_API_URL_ID, membership.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(membership.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMembership() throws Exception {
        // Get the membership
        restMembershipMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMembership() throws Exception {
        // Initialize the database
        insertedMembership = membershipRepository.saveAndFlush(membership);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the membership
        Membership updatedMembership = membershipRepository.findById(membership.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMembership are not directly saved in db
        em.detach(updatedMembership);
        updatedMembership.type(UPDATED_TYPE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);
        MembershipDTO membershipDTO = membershipMapper.toDto(updatedMembership);

        restMembershipMockMvc
            .perform(
                put(ENTITY_API_URL_ID, membershipDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(membershipDTO))
            )
            .andExpect(status().isOk());

        // Validate the Membership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMembershipToMatchAllProperties(updatedMembership);
    }

    @Test
    @Transactional
    void putNonExistingMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        membership.setId(longCount.incrementAndGet());

        // Create the Membership
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMembershipMockMvc
            .perform(
                put(ENTITY_API_URL_ID, membershipDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(membershipDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Membership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        membership.setId(longCount.incrementAndGet());

        // Create the Membership
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMembershipMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(membershipDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Membership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        membership.setId(longCount.incrementAndGet());

        // Create the Membership
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMembershipMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(membershipDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Membership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMembershipWithPatch() throws Exception {
        // Initialize the database
        insertedMembership = membershipRepository.saveAndFlush(membership);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the membership using partial update
        Membership partialUpdatedMembership = new Membership();
        partialUpdatedMembership.setId(membership.getId());

        partialUpdatedMembership.type(UPDATED_TYPE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restMembershipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMembership.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMembership))
            )
            .andExpect(status().isOk());

        // Validate the Membership in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMembershipUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMembership, membership),
            getPersistedMembership(membership)
        );
    }

    @Test
    @Transactional
    void fullUpdateMembershipWithPatch() throws Exception {
        // Initialize the database
        insertedMembership = membershipRepository.saveAndFlush(membership);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the membership using partial update
        Membership partialUpdatedMembership = new Membership();
        partialUpdatedMembership.setId(membership.getId());

        partialUpdatedMembership.type(UPDATED_TYPE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE).status(UPDATED_STATUS);

        restMembershipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMembership.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMembership))
            )
            .andExpect(status().isOk());

        // Validate the Membership in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMembershipUpdatableFieldsEquals(partialUpdatedMembership, getPersistedMembership(partialUpdatedMembership));
    }

    @Test
    @Transactional
    void patchNonExistingMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        membership.setId(longCount.incrementAndGet());

        // Create the Membership
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMembershipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, membershipDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(membershipDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Membership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        membership.setId(longCount.incrementAndGet());

        // Create the Membership
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMembershipMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(membershipDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Membership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMembership() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        membership.setId(longCount.incrementAndGet());

        // Create the Membership
        MembershipDTO membershipDTO = membershipMapper.toDto(membership);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMembershipMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(membershipDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Membership in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMembership() throws Exception {
        // Initialize the database
        insertedMembership = membershipRepository.saveAndFlush(membership);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the membership
        restMembershipMockMvc
            .perform(delete(ENTITY_API_URL_ID, membership.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return membershipRepository.count();
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

    protected Membership getPersistedMembership(Membership membership) {
        return membershipRepository.findById(membership.getId()).orElseThrow();
    }

    protected void assertPersistedMembershipToMatchAllProperties(Membership expectedMembership) {
        assertMembershipAllPropertiesEquals(expectedMembership, getPersistedMembership(expectedMembership));
    }

    protected void assertPersistedMembershipToMatchUpdatableProperties(Membership expectedMembership) {
        assertMembershipAllUpdatablePropertiesEquals(expectedMembership, getPersistedMembership(expectedMembership));
    }
}
