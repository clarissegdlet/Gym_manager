package com.smartgym.manager.service;

import com.smartgym.manager.domain.ClassSession;
import com.smartgym.manager.repository.ClassSessionRepository;
import com.smartgym.manager.service.dto.ClassSessionDTO;
import com.smartgym.manager.service.mapper.ClassSessionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.smartgym.manager.domain.ClassSession}.
 */
@Service
@Transactional
public class ClassSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(ClassSessionService.class);

    private final ClassSessionRepository classSessionRepository;

    private final ClassSessionMapper classSessionMapper;

    public ClassSessionService(ClassSessionRepository classSessionRepository, ClassSessionMapper classSessionMapper) {
        this.classSessionRepository = classSessionRepository;
        this.classSessionMapper = classSessionMapper;
    }

    /**
     * Save a classSession.
     *
     * @param classSessionDTO the entity to save.
     * @return the persisted entity.
     */
    public ClassSessionDTO save(ClassSessionDTO classSessionDTO) {
        LOG.debug("Request to save ClassSession : {}", classSessionDTO);
        ClassSession classSession = classSessionMapper.toEntity(classSessionDTO);
        classSession = classSessionRepository.save(classSession);
        return classSessionMapper.toDto(classSession);
    }

    /**
     * Update a classSession.
     *
     * @param classSessionDTO the entity to save.
     * @return the persisted entity.
     */
    public ClassSessionDTO update(ClassSessionDTO classSessionDTO) {
        LOG.debug("Request to update ClassSession : {}", classSessionDTO);
        ClassSession classSession = classSessionMapper.toEntity(classSessionDTO);
        classSession = classSessionRepository.save(classSession);
        return classSessionMapper.toDto(classSession);
    }

    /**
     * Partially update a classSession.
     *
     * @param classSessionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ClassSessionDTO> partialUpdate(ClassSessionDTO classSessionDTO) {
        LOG.debug("Request to partially update ClassSession : {}", classSessionDTO);

        return classSessionRepository
            .findById(classSessionDTO.getId())
            .map(existingClassSession -> {
                classSessionMapper.partialUpdate(existingClassSession, classSessionDTO);

                return existingClassSession;
            })
            .map(classSessionRepository::save)
            .map(classSessionMapper::toDto);
    }

    /**
     * Get all the classSessions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ClassSessionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ClassSessions");
        return classSessionRepository.findAll(pageable).map(classSessionMapper::toDto);
    }

    /**
     * Get all the classSessions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ClassSessionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return classSessionRepository.findAllWithEagerRelationships(pageable).map(classSessionMapper::toDto);
    }

    /**
     * Get one classSession by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ClassSessionDTO> findOne(Long id) {
        LOG.debug("Request to get ClassSession : {}", id);
        return classSessionRepository.findOneWithEagerRelationships(id).map(classSessionMapper::toDto);
    }

    /**
     * Delete the classSession by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ClassSession : {}", id);
        classSessionRepository.deleteById(id);
    }
}
