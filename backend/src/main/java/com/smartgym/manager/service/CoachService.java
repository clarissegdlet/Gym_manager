package com.smartgym.manager.service;

import com.smartgym.manager.domain.Coach;
import com.smartgym.manager.repository.CoachRepository;
import com.smartgym.manager.service.dto.CoachDTO;
import com.smartgym.manager.service.mapper.CoachMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.smartgym.manager.domain.Coach}.
 */
@Service
@Transactional
public class CoachService {

    private static final Logger LOG = LoggerFactory.getLogger(CoachService.class);

    private final CoachRepository coachRepository;

    private final CoachMapper coachMapper;

    public CoachService(CoachRepository coachRepository, CoachMapper coachMapper) {
        this.coachRepository = coachRepository;
        this.coachMapper = coachMapper;
    }

    /**
     * Save a coach.
     *
     * @param coachDTO the entity to save.
     * @return the persisted entity.
     */
    public CoachDTO save(CoachDTO coachDTO) {
        LOG.debug("Request to save Coach : {}", coachDTO);
        Coach coach = coachMapper.toEntity(coachDTO);
        coach = coachRepository.save(coach);
        return coachMapper.toDto(coach);
    }

    /**
     * Update a coach.
     *
     * @param coachDTO the entity to save.
     * @return the persisted entity.
     */
    public CoachDTO update(CoachDTO coachDTO) {
        LOG.debug("Request to update Coach : {}", coachDTO);
        Coach coach = coachMapper.toEntity(coachDTO);
        coach = coachRepository.save(coach);
        return coachMapper.toDto(coach);
    }

    /**
     * Partially update a coach.
     *
     * @param coachDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CoachDTO> partialUpdate(CoachDTO coachDTO) {
        LOG.debug("Request to partially update Coach : {}", coachDTO);

        return coachRepository
            .findById(coachDTO.getId())
            .map(existingCoach -> {
                coachMapper.partialUpdate(existingCoach, coachDTO);

                return existingCoach;
            })
            .map(coachRepository::save)
            .map(coachMapper::toDto);
    }

    /**
     * Get all the coaches.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CoachDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Coaches");
        return coachRepository.findAll(pageable).map(coachMapper::toDto);
    }

    /**
     * Get one coach by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CoachDTO> findOne(Long id) {
        LOG.debug("Request to get Coach : {}", id);
        return coachRepository.findById(id).map(coachMapper::toDto);
    }

    /**
     * Delete the coach by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Coach : {}", id);
        coachRepository.deleteById(id);
    }
}
