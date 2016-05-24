package com.arnellconsulting.worktajm.ms.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.arnellconsulting.worktajm.ms.domain.Worker;
import com.arnellconsulting.worktajm.ms.service.WorkerService;
import com.arnellconsulting.worktajm.ms.web.rest.util.HeaderUtil;
import com.arnellconsulting.worktajm.ms.web.rest.util.PaginationUtil;
import com.arnellconsulting.worktajm.ms.web.rest.dto.WorkerDTO;
import com.arnellconsulting.worktajm.ms.web.rest.mapper.WorkerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Worker.
 */
@RestController
@RequestMapping("/api")
public class WorkerResource {

    private final Logger log = LoggerFactory.getLogger(WorkerResource.class);
        
    @Inject
    private WorkerService workerService;
    
    @Inject
    private WorkerMapper workerMapper;
    
    /**
     * POST  /workers : Create a new worker.
     *
     * @param workerDTO the workerDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new workerDTO, or with status 400 (Bad Request) if the worker has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/workers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<WorkerDTO> createWorker(@RequestBody WorkerDTO workerDTO) throws URISyntaxException {
        log.debug("REST request to save Worker : {}", workerDTO);
        if (workerDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("worker", "idexists", "A new worker cannot already have an ID")).body(null);
        }
        WorkerDTO result = workerService.save(workerDTO);
        return ResponseEntity.created(new URI("/api/workers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("worker", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /workers : Updates an existing worker.
     *
     * @param workerDTO the workerDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated workerDTO,
     * or with status 400 (Bad Request) if the workerDTO is not valid,
     * or with status 500 (Internal Server Error) if the workerDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/workers",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<WorkerDTO> updateWorker(@RequestBody WorkerDTO workerDTO) throws URISyntaxException {
        log.debug("REST request to update Worker : {}", workerDTO);
        if (workerDTO.getId() == null) {
            return createWorker(workerDTO);
        }
        WorkerDTO result = workerService.save(workerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("worker", workerDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /workers : get all the workers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of workers in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/workers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<WorkerDTO>> getAllWorkers(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Workers");
        Page<Worker> page = workerService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/workers");
        return new ResponseEntity<>(workerMapper.workersToWorkerDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /workers/:id : get the "id" worker.
     *
     * @param id the id of the workerDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the workerDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/workers/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<WorkerDTO> getWorker(@PathVariable Long id) {
        log.debug("REST request to get Worker : {}", id);
        WorkerDTO workerDTO = workerService.findOne(id);
        return Optional.ofNullable(workerDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /workers/:id : delete the "id" worker.
     *
     * @param id the id of the workerDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/workers/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        log.debug("REST request to delete Worker : {}", id);
        workerService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("worker", id.toString())).build();
    }

}
