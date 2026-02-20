package com.smartgym.manager.web.rest;

import com.smartgym.manager.repository.CoachRepository;
import com.smartgym.manager.service.CoachService;
import com.smartgym.manager.service.dto.CoachDTO;
import com.smartgym.manager.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.smartgym.manager.domain.Coach}.
 */
@RestController
@RequestMapping("/api/coaches")
public class CoachResource {

    private static final Logger LOG = LoggerFactory.getLogger(CoachResource.class);

    private static final String ENTITY_NAME = "coach";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CoachService coachService;

    private final CoachRepository coachRepository;

    public CoachResource(CoachService coachService, CoachRepository coachRepository) {
        this.coachService = coachService;
        this.coachRepository = coachRepository;
    }

    /**
     * {@code POST  /coaches} : Create a new coach.
     *
     * @param coachDTO the coachDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new coachDTO, or with status {@code 400 (Bad Request)} if the coach has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CoachDTO> createCoach(@Valid @RequestBody CoachDTO coachDTO) throws URISyntaxException {
        LOG.debug("REST request to save Coach : {}", coachDTO);
        if (coachDTO.getId() != null) {
            throw new BadRequestAlertException("A new coach cannot already have an ID", ENTITY_NAME, "idexists");
        }
        coachDTO = coachService.save(coachDTO);
        return ResponseEntity.created(new URI("/api/coaches/" + coachDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, coachDTO.getId().toString()))
            .body(coachDTO);
    }

    /**
     * {@code PUT  /coaches/:id} : Updates an existing coach.
     *
     * @param id the id of the coachDTO to save.
     * @param coachDTO the coachDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated coachDTO,
     * or with status {@code 400 (Bad Request)} if the coachDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the coachDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CoachDTO> updateCoach(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CoachDTO coachDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Coach : {}, {}", id, coachDTO);
        if (coachDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, coachDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!coachRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        coachDTO = coachService.update(coachDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, coachDTO.getId().toString()))
            .body(coachDTO);
    }

    /**
     * {@code PATCH  /coaches/:id} : Partial updates given fields of an existing coach, field will ignore if it is null
     *
     * @param id the id of the coachDTO to save.
     * @param coachDTO the coachDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated coachDTO,
     * or with status {@code 400 (Bad Request)} if the coachDTO is not valid,
     * or with status {@code 404 (Not Found)} if the coachDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the coachDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CoachDTO> partialUpdateCoach(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CoachDTO coachDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Coach partially : {}, {}", id, coachDTO);
        if (coachDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, coachDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!coachRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CoachDTO> result = coachService.partialUpdate(coachDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, coachDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /coaches} : get all the coaches.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of coaches in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CoachDTO>> getAllCoaches(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Coaches");
        Page<CoachDTO> page = coachService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /coaches/:id} : get the "id" coach.
     *
     * @param id the id of the coachDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the coachDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CoachDTO> getCoach(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Coach : {}", id);
        Optional<CoachDTO> coachDTO = coachService.findOne(id);
        return ResponseUtil.wrapOrNotFound(coachDTO);
    }

    /**
     * {@code DELETE  /coaches/:id} : delete the "id" coach.
     *
     * @param id the id of the coachDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoach(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Coach : {}", id);
        coachService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
