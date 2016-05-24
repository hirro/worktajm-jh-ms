package com.arnellconsulting.worktajm.ms.web.rest;

import com.arnellconsulting.worktajm.ms.WorktajmMsApp;
import com.arnellconsulting.worktajm.ms.domain.WorkLog;
import com.arnellconsulting.worktajm.ms.repository.WorkLogRepository;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the WorkLogResource REST controller.
 *
 * @see WorkLogResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WorktajmMsApp.class)
@WebAppConfiguration
@IntegrationTest
public class WorkLogResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_START_DATE_STR = dateTimeFormatter.format(DEFAULT_START_DATE);

    private static final ZonedDateTime DEFAULT_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_END_DATE_STR = dateTimeFormatter.format(DEFAULT_END_DATE);
    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";

    @Inject
    private WorkLogRepository workLogRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restWorkLogMockMvc;

    private WorkLog workLog;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WorkLogResource workLogResource = new WorkLogResource();
        ReflectionTestUtils.setField(workLogResource, "workLogRepository", workLogRepository);
        this.restWorkLogMockMvc = MockMvcBuilders.standaloneSetup(workLogResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        workLog = new WorkLog();
        workLog.setStartDate(DEFAULT_START_DATE);
        workLog.setEndDate(DEFAULT_END_DATE);
        workLog.setComment(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    public void createWorkLog() throws Exception {
        int databaseSizeBeforeCreate = workLogRepository.findAll().size();

        // Create the WorkLog

        restWorkLogMockMvc.perform(post("/api/work-logs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(workLog)))
                .andExpect(status().isCreated());

        // Validate the WorkLog in the database
        List<WorkLog> workLogs = workLogRepository.findAll();
        assertThat(workLogs).hasSize(databaseSizeBeforeCreate + 1);
        WorkLog testWorkLog = workLogs.get(workLogs.size() - 1);
        assertThat(testWorkLog.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testWorkLog.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testWorkLog.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    public void getAllWorkLogs() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get all the workLogs
        restWorkLogMockMvc.perform(get("/api/work-logs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(workLog.getId().intValue())))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE_STR)))
                .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE_STR)))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }

    @Test
    @Transactional
    public void getWorkLog() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);

        // Get the workLog
        restWorkLogMockMvc.perform(get("/api/work-logs/{id}", workLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(workLog.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE_STR))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE_STR))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWorkLog() throws Exception {
        // Get the workLog
        restWorkLogMockMvc.perform(get("/api/work-logs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWorkLog() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);
        int databaseSizeBeforeUpdate = workLogRepository.findAll().size();

        // Update the workLog
        WorkLog updatedWorkLog = new WorkLog();
        updatedWorkLog.setId(workLog.getId());
        updatedWorkLog.setStartDate(UPDATED_START_DATE);
        updatedWorkLog.setEndDate(UPDATED_END_DATE);
        updatedWorkLog.setComment(UPDATED_COMMENT);

        restWorkLogMockMvc.perform(put("/api/work-logs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedWorkLog)))
                .andExpect(status().isOk());

        // Validate the WorkLog in the database
        List<WorkLog> workLogs = workLogRepository.findAll();
        assertThat(workLogs).hasSize(databaseSizeBeforeUpdate);
        WorkLog testWorkLog = workLogs.get(workLogs.size() - 1);
        assertThat(testWorkLog.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testWorkLog.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testWorkLog.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void deleteWorkLog() throws Exception {
        // Initialize the database
        workLogRepository.saveAndFlush(workLog);
        int databaseSizeBeforeDelete = workLogRepository.findAll().size();

        // Get the workLog
        restWorkLogMockMvc.perform(delete("/api/work-logs/{id}", workLog.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<WorkLog> workLogs = workLogRepository.findAll();
        assertThat(workLogs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
