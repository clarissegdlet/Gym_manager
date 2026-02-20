package com.smartgym.manager.service;

import com.smartgym.manager.domain.CheckIn;
import com.smartgym.manager.repository.CheckInRepository;
import com.smartgym.manager.service.dto.CheckInDTO;
import com.smartgym.manager.service.mapper.CheckInMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.smartgym.manager.domain.CheckIn}.
 */
@Service
@Transactional
public class CheckInService {

    private static final Logger LOG = LoggerFactory.getLogger(CheckInService.class);

    private final CheckInRepository checkInRepository;

    private final CheckInMapper checkInMapper;

    public CheckInService(CheckInRepository checkInRepository, CheckInMapper checkInMapper) {
        this.checkInRepository = checkInRepository;
        this.checkInMapper = checkInMapper;
    }

    /**
     * Save a checkIn.
     *
     * @param checkInDTO the entity to save.
     * @return the persisted entity.
     */
    public CheckInDTO save(CheckInDTO checkInDTO) {
        LOG.debug("Request to save CheckIn : {}", checkInDTO);
        CheckIn checkIn = checkInMapper.toEntity(checkInDTO);
        checkIn = checkInRepository.save(checkIn);
        return checkInMapper.toDto(checkIn);
    }

    /**
     * Update a checkIn.
     *
     * @param checkInDTO the entity to save.
     * @return the persisted entity.
     */
    public CheckInDTO update(CheckInDTO checkInDTO) {
        LOG.debug("Request to update CheckIn : {}", checkInDTO);
        CheckIn checkIn = checkInMapper.toEntity(checkInDTO);
        checkIn = checkInRepository.save(checkIn);
        return checkInMapper.toDto(checkIn);
    }

    /**
     * Partially update a checkIn.
     *
     * @param checkInDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CheckInDTO> partialUpdate(CheckInDTO checkInDTO) {
        LOG.debug("Request to partially update CheckIn : {}", checkInDTO);

        return checkInRepository
            .findById(checkInDTO.getId())
            .map(existingCheckIn -> {
                checkInMapper.partialUpdate(existingCheckIn, checkInDTO);

                return existingCheckIn;
            })
            .map(checkInRepository::save)
            .map(checkInMapper::toDto);
    }

    /**
     * Get all the checkIns.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CheckInDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CheckIns");
        return checkInRepository.findAll(pageable).map(checkInMapper::toDto);
    }

    /**
     * Get one checkIn by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CheckInDTO> findOne(Long id) {
        LOG.debug("Request to get CheckIn : {}", id);
        return checkInRepository.findById(id).map(checkInMapper::toDto);
    }

    /**
     * Delete the checkIn by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CheckIn : {}", id);
        checkInRepository.deleteById(id);
    }
}
