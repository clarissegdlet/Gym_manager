package com.smartgym.manager.web.rest;

import static com.smartgym.manager.domain.ClassSessionAsserts.*;
import static com.smartgym.manager.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgym.manager.IntegrationTest;
import com.smartgym.manager.domain.ClassSession;
import com.smartgym.manager.domain.enumeration.ClassStatus;
import com.smartgym.manager.repository.ClassSessionRepository;
import com.smartgym.manager.service.ClassSessionService;
import com.smartgym.manager.service.dto.ClassSessionDTO;
import com.smartgym.manager.service.mapper.ClassSessionMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClassSessionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ClassSessionResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_CAPACITY = 1;
    private static final Integer UPDATED_CAPACITY = 2;

    private static final ClassStatus DEFAULT_STATUS = ClassStatus.PLANNED;
    private static final ClassStatus UPDATED_STATUS = ClassStatus.COMPLETED;

    private static final String ENTITY_API_URL = "/api/class-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClassSessionRepository classSessionRepository;

    @Mock
    private ClassSessionRepository classSessionRepositoryMock;

    @Autowired
    private ClassSessionMapper classSessionMapper;

    @Mock
    private ClassSessionService classSessionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClassSessionMockMvc;

    private ClassSession classSession;

    private ClassSession insertedClassSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassSession createEntity() {
        return new ClassSession().title(DEFAULT_TITLE).dateTime(DEFAULT_DATE_TIME).capacity(DEFAULT_CAPACITY).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassSession createUpdatedEntity() {
        return new ClassSession().title(UPDATED_TITLE).dateTime(UPDATED_DATE_TIME).capacity(UPDATED_CAPACITY).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        classSession = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClassSession != null) {
            classSessionRepository.delete(insertedClassSession);
            insertedClassSession = null;
        }
    }

    @Test
    @Transactional
    void createClassSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ClassSession
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);
        var returnedClassSessionDTO = om.readValue(
            restClassSessionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classSessionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ClassSessionDTO.class
        );

        // Validate the ClassSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClassSession = classSessionMapper.toEntity(returnedClassSessionDTO);
        assertClassSessionUpdatableFieldsEquals(returnedClassSession, getPersistedClassSession(returnedClassSession));

        insertedClassSession = returnedClassSession;
    }

    @Test
    @Transactional
    void createClassSessionWithExistingId() throws Exception {
        // Create the ClassSession with an existing ID
        classSession.setId(1L);
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClassSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classSessionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classSession.setTitle(null);

        // Create the ClassSession, which fails.
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);

        restClassSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classSession.setDateTime(null);

        // Create the ClassSession, which fails.
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);

        restClassSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCapacityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classSession.setCapacity(null);

        // Create the ClassSession, which fails.
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);

        restClassSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        classSession.setStatus(null);

        // Create the ClassSession, which fails.
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);

        restClassSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classSessionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClassSessions() throws Exception {
        // Initialize the database
        insertedClassSession = classSessionRepository.saveAndFlush(classSession);

        // Get all the classSessionList
        restClassSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].dateTime").value(hasItem(DEFAULT_DATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].capacity").value(hasItem(DEFAULT_CAPACITY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClassSessionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(classSessionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClassSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(classSessionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClassSessionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(classSessionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClassSessionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(classSessionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getClassSession() throws Exception {
        // Initialize the database
        insertedClassSession = classSessionRepository.saveAndFlush(classSession);

        // Get the classSession
        restClassSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, classSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(classSession.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.dateTime").value(DEFAULT_DATE_TIME.toString()))
            .andExpect(jsonPath("$.capacity").value(DEFAULT_CAPACITY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingClassSession() throws Exception {
        // Get the classSession
        restClassSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClassSession() throws Exception {
        // Initialize the database
        insertedClassSession = classSessionRepository.saveAndFlush(classSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classSession
        ClassSession updatedClassSession = classSessionRepository.findById(classSession.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClassSession are not directly saved in db
        em.detach(updatedClassSession);
        updatedClassSession.title(UPDATED_TITLE).dateTime(UPDATED_DATE_TIME).capacity(UPDATED_CAPACITY).status(UPDATED_STATUS);
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(updatedClassSession);

        restClassSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, classSessionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(classSessionDTO))
            )
            .andExpect(status().isOk());

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClassSessionToMatchAllProperties(updatedClassSession);
    }

    @Test
    @Transactional
    void putNonExistingClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // Create the ClassSession
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, classSessionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(classSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // Create the ClassSession
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(classSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // Create the ClassSession
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassSessionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(classSessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClassSessionWithPatch() throws Exception {
        // Initialize the database
        insertedClassSession = classSessionRepository.saveAndFlush(classSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classSession using partial update
        ClassSession partialUpdatedClassSession = new ClassSession();
        partialUpdatedClassSession.setId(classSession.getId());

        partialUpdatedClassSession.title(UPDATED_TITLE).dateTime(UPDATED_DATE_TIME);

        restClassSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClassSession))
            )
            .andExpect(status().isOk());

        // Validate the ClassSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClassSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedClassSession, classSession),
            getPersistedClassSession(classSession)
        );
    }

    @Test
    @Transactional
    void fullUpdateClassSessionWithPatch() throws Exception {
        // Initialize the database
        insertedClassSession = classSessionRepository.saveAndFlush(classSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the classSession using partial update
        ClassSession partialUpdatedClassSession = new ClassSession();
        partialUpdatedClassSession.setId(classSession.getId());

        partialUpdatedClassSession.title(UPDATED_TITLE).dateTime(UPDATED_DATE_TIME).capacity(UPDATED_CAPACITY).status(UPDATED_STATUS);

        restClassSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedClassSession))
            )
            .andExpect(status().isOk());

        // Validate the ClassSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClassSessionUpdatableFieldsEquals(partialUpdatedClassSession, getPersistedClassSession(partialUpdatedClassSession));
    }

    @Test
    @Transactional
    void patchNonExistingClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // Create the ClassSession
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, classSessionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(classSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // Create the ClassSession
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(classSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClassSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        classSession.setId(longCount.incrementAndGet());

        // Create the ClassSession
        ClassSessionDTO classSessionDTO = classSessionMapper.toDto(classSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassSessionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(classSessionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClassSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClassSession() throws Exception {
        // Initialize the database
        insertedClassSession = classSessionRepository.saveAndFlush(classSession);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the classSession
        restClassSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, classSession.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return classSessionRepository.count();
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

    protected ClassSession getPersistedClassSession(ClassSession classSession) {
        return classSessionRepository.findById(classSession.getId()).orElseThrow();
    }

    protected void assertPersistedClassSessionToMatchAllProperties(ClassSession expectedClassSession) {
        assertClassSessionAllPropertiesEquals(expectedClassSession, getPersistedClassSession(expectedClassSession));
    }

    protected void assertPersistedClassSessionToMatchUpdatableProperties(ClassSession expectedClassSession) {
        assertClassSessionAllUpdatablePropertiesEquals(expectedClassSession, getPersistedClassSession(expectedClassSession));
    }
}
