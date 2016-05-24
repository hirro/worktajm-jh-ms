package com.arnellconsulting.worktajm.ms.web.rest;

import com.arnellconsulting.worktajm.ms.WorktajmMsApp;
import com.arnellconsulting.worktajm.ms.domain.Worker;
import com.arnellconsulting.worktajm.ms.repository.WorkerRepository;
import com.arnellconsulting.worktajm.ms.service.WorkerService;
import com.arnellconsulting.worktajm.ms.web.rest.dto.WorkerDTO;
import com.arnellconsulting.worktajm.ms.web.rest.mapper.WorkerMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.arnellconsulting.worktajm.ms.domain.enumeration.Role;

/**
 * Test class for the WorkerResource REST controller.
 *
 * @see WorkerResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WorktajmMsApp.class)
@WebAppConfiguration
@IntegrationTest
public class WorkerResourceIntTest {


    private static final Long DEFAULT_WORKER_ID = 1L;
    private static final Long UPDATED_WORKER_ID = 2L;
    private static final String DEFAULT_FIRST_NAME = "AAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBB";
    private static final String DEFAULT_LAST_NAME = "AAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBB";
    private static final String DEFAULT_EMAIL = "AAAAA";
    private static final String UPDATED_EMAIL = "BBBBB";

    private static final Role DEFAULT_ROLE = Role.OWNER;
    private static final Role UPDATED_ROLE = Role.ADMIN;

    @Inject
    private WorkerRepository workerRepository;

    @Inject
    private WorkerMapper workerMapper;

    @Inject
    private WorkerService workerService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restWorkerMockMvc;

    private Worker worker;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WorkerResource workerResource = new WorkerResource();
        ReflectionTestUtils.setField(workerResource, "workerService", workerService);
        ReflectionTestUtils.setField(workerResource, "workerMapper", workerMapper);
        this.restWorkerMockMvc = MockMvcBuilders.standaloneSetup(workerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        worker = new Worker();
        worker.setWorkerId(DEFAULT_WORKER_ID);
        worker.setFirstName(DEFAULT_FIRST_NAME);
        worker.setLastName(DEFAULT_LAST_NAME);
        worker.setEmail(DEFAULT_EMAIL);
        worker.setRole(DEFAULT_ROLE);
    }

    @Test
    @Transactional
    public void createWorker() throws Exception {
        int databaseSizeBeforeCreate = workerRepository.findAll().size();

        // Create the Worker
        WorkerDTO workerDTO = workerMapper.workerToWorkerDTO(worker);

        restWorkerMockMvc.perform(post("/api/workers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(workerDTO)))
                .andExpect(status().isCreated());

        // Validate the Worker in the database
        List<Worker> workers = workerRepository.findAll();
        assertThat(workers).hasSize(databaseSizeBeforeCreate + 1);
        Worker testWorker = workers.get(workers.size() - 1);
        assertThat(testWorker.getWorkerId()).isEqualTo(DEFAULT_WORKER_ID);
        assertThat(testWorker.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testWorker.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testWorker.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testWorker.getRole()).isEqualTo(DEFAULT_ROLE);
    }

    @Test
    @Transactional
    public void getAllWorkers() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get all the workers
        restWorkerMockMvc.perform(get("/api/workers?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(worker.getId().intValue())))
                .andExpect(jsonPath("$.[*].workerId").value(hasItem(DEFAULT_WORKER_ID.intValue())))
                .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
                .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }

    @Test
    @Transactional
    public void getWorker() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);

        // Get the worker
        restWorkerMockMvc.perform(get("/api/workers/{id}", worker.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(worker.getId().intValue()))
            .andExpect(jsonPath("$.workerId").value(DEFAULT_WORKER_ID.intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWorker() throws Exception {
        // Get the worker
        restWorkerMockMvc.perform(get("/api/workers/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWorker() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);
        int databaseSizeBeforeUpdate = workerRepository.findAll().size();

        // Update the worker
        Worker updatedWorker = new Worker();
        updatedWorker.setId(worker.getId());
        updatedWorker.setWorkerId(UPDATED_WORKER_ID);
        updatedWorker.setFirstName(UPDATED_FIRST_NAME);
        updatedWorker.setLastName(UPDATED_LAST_NAME);
        updatedWorker.setEmail(UPDATED_EMAIL);
        updatedWorker.setRole(UPDATED_ROLE);
        WorkerDTO workerDTO = workerMapper.workerToWorkerDTO(updatedWorker);

        restWorkerMockMvc.perform(put("/api/workers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(workerDTO)))
                .andExpect(status().isOk());

        // Validate the Worker in the database
        List<Worker> workers = workerRepository.findAll();
        assertThat(workers).hasSize(databaseSizeBeforeUpdate);
        Worker testWorker = workers.get(workers.size() - 1);
        assertThat(testWorker.getWorkerId()).isEqualTo(UPDATED_WORKER_ID);
        assertThat(testWorker.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testWorker.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testWorker.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testWorker.getRole()).isEqualTo(UPDATED_ROLE);
    }

    @Test
    @Transactional
    public void deleteWorker() throws Exception {
        // Initialize the database
        workerRepository.saveAndFlush(worker);
        int databaseSizeBeforeDelete = workerRepository.findAll().size();

        // Get the worker
        restWorkerMockMvc.perform(delete("/api/workers/{id}", worker.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Worker> workers = workerRepository.findAll();
        assertThat(workers).hasSize(databaseSizeBeforeDelete - 1);
    }
}
