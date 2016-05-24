package com.arnellconsulting.worktajm.ms.service;

import com.arnellconsulting.worktajm.ms.domain.Worker;
import com.arnellconsulting.worktajm.ms.repository.WorkerRepository;
import com.arnellconsulting.worktajm.ms.web.rest.dto.WorkerDTO;
import com.arnellconsulting.worktajm.ms.web.rest.mapper.WorkerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Worker.
 */
@Service
@Transactional
public class WorkerService {

    private final Logger log = LoggerFactory.getLogger(WorkerService.class);
    
    @Inject
    private WorkerRepository workerRepository;
    
    @Inject
    private WorkerMapper workerMapper;
    
    /**
     * Save a worker.
     * 
     * @param workerDTO the entity to save
     * @return the persisted entity
     */
    public WorkerDTO save(WorkerDTO workerDTO) {
        log.debug("Request to save Worker : {}", workerDTO);
        Worker worker = workerMapper.workerDTOToWorker(workerDTO);
        worker = workerRepository.save(worker);
        WorkerDTO result = workerMapper.workerToWorkerDTO(worker);
        return result;
    }

    /**
     *  Get all the workers.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Worker> findAll(Pageable pageable) {
        log.debug("Request to get all Workers");
        Page<Worker> result = workerRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one worker by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public WorkerDTO findOne(Long id) {
        log.debug("Request to get Worker : {}", id);
        Worker worker = workerRepository.findOne(id);
        WorkerDTO workerDTO = workerMapper.workerToWorkerDTO(worker);
        return workerDTO;
    }

    /**
     *  Delete the  worker by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Worker : {}", id);
        workerRepository.delete(id);
    }
}
