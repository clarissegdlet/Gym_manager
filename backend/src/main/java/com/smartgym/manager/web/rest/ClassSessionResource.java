package com.smartgym.manager.web.rest;

import com.smartgym.manager.repository.ClassSessionRepository;
import com.smartgym.manager.service.ClassSessionService;
import com.smartgym.manager.service.dto.ClassSessionDTO;
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
 * REST controller for managing {@link com.smartgym.manager.domain.ClassSession}.
 */
@RestController
@RequestMapping("/api/class-sessions")
public class ClassSessionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClassSessionResource.class);

    private static final String ENTITY_NAME = "classSession";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClassSessionService classSessionService;

    private final ClassSessionRepository classSessionRepository;

    public ClassSessionResource(ClassSessionService classSessionService, ClassSessionRepository classSessionRepository) {
        this.classSessionService = classSessionService;
        this.classSessionRepository = classSessionRepository;
    }

    /**
     * {@code POST  /class-sessions} : Create a new classSession.
     *
     * @param classSessionDTO the classSessionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new classSessionDTO, or with status {@code 400 (Bad Request)} if the classSession has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClassSessionDTO> createClassSession(@Valid @RequestBody ClassSessionDTO classSessionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ClassSession : {}", classSessionDTO);
        if (classSessionDTO.getId() != null) {
            throw new BadRequestAlertException("A new classSession cannot already have an ID", ENTITY_NAME, "idexists");
        }
        classSessionDTO = classSessionService.save(classSessionDTO);
        return ResponseEntity.created(new URI("/api/class-sessions/" + classSessionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, classSessionDTO.getId().toString()))
            .body(classSessionDTO);
    }

    /**
     * {@code PUT  /class-sessions/:id} : Updates an existing classSession.
     *
     * @param id the id of the classSessionDTO to save.
     * @param classSessionDTO the classSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classSessionDTO,
     * or with status {@code 400 (Bad Request)} if the classSessionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the classSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClassSessionDTO> updateClassSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClassSessionDTO classSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ClassSession : {}, {}", id, classSessionDTO);
        if (classSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        classSessionDTO = classSessionService.update(classSessionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classSessionDTO.getId().toString()))
            .body(classSessionDTO);
    }

    /**
     * {@code PATCH  /class-sessions/:id} : Partial updates given fields of an existing classSession, field will ignore if it is null
     *
     * @param id the id of the classSessionDTO to save.
     * @param classSessionDTO the classSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classSessionDTO,
     * or with status {@code 400 (Bad Request)} if the classSessionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the classSessionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the classSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClassSessionDTO> partialUpdateClassSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClassSessionDTO classSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ClassSession partially : {}, {}", id, classSessionDTO);
        if (classSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClassSessionDTO> result = classSessionService.partialUpdate(classSessionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classSessionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /class-sessions} : get all the classSessions.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of classSessions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClassSessionDTO>> getAllClassSessions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ClassSessions");
        Page<ClassSessionDTO> page;
        if (eagerload) {
            page = classSessionService.findAllWithEagerRelationships(pageable);
        } else {
            page = classSessionService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /class-sessions/:id} : get the "id" classSession.
     *
     * @param id the id of the classSessionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the classSessionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClassSessionDTO> getClassSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClassSession : {}", id);
        Optional<ClassSessionDTO> classSessionDTO = classSessionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(classSessionDTO);
    }

    /**
     * {@code DELETE  /class-sessions/:id} : delete the "id" classSession.
     *
     * @param id the id of the classSessionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClassSession : {}", id);
        classSessionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
