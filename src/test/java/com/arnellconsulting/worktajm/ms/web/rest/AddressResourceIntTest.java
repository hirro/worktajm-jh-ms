package com.arnellconsulting.worktajm.ms.web.rest;

import com.arnellconsulting.worktajm.ms.WorktajmMsApp;
import com.arnellconsulting.worktajm.ms.domain.Address;
import com.arnellconsulting.worktajm.ms.repository.AddressRepository;

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


/**
 * Test class for the AddressResource REST controller.
 *
 * @see AddressResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WorktajmMsApp.class)
@WebAppConfiguration
@IntegrationTest
public class AddressResourceIntTest {


    private static final Long DEFAULT_ADDRESS_ID = 1L;
    private static final Long UPDATED_ADDRESS_ID = 2L;
    private static final String DEFAULT_LINE_1 = "AAAAA";
    private static final String UPDATED_LINE_1 = "BBBBB";
    private static final String DEFAULT_LINE_2 = "AAAAA";
    private static final String UPDATED_LINE_2 = "BBBBB";
    private static final String DEFAULT_COUNTRY = "AAAAA";
    private static final String UPDATED_COUNTRY = "BBBBB";

    @Inject
    private AddressRepository addressRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAddressMockMvc;

    private Address address;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AddressResource addressResource = new AddressResource();
        ReflectionTestUtils.setField(addressResource, "addressRepository", addressRepository);
        this.restAddressMockMvc = MockMvcBuilders.standaloneSetup(addressResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        address = new Address();
        address.setAddressId(DEFAULT_ADDRESS_ID);
        address.setLine1(DEFAULT_LINE_1);
        address.setLine2(DEFAULT_LINE_2);
        address.setCountry(DEFAULT_COUNTRY);
    }

    @Test
    @Transactional
    public void createAddress() throws Exception {
        int databaseSizeBeforeCreate = addressRepository.findAll().size();

        // Create the Address

        restAddressMockMvc.perform(post("/api/addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(address)))
                .andExpect(status().isCreated());

        // Validate the Address in the database
        List<Address> addresses = addressRepository.findAll();
        assertThat(addresses).hasSize(databaseSizeBeforeCreate + 1);
        Address testAddress = addresses.get(addresses.size() - 1);
        assertThat(testAddress.getAddressId()).isEqualTo(DEFAULT_ADDRESS_ID);
        assertThat(testAddress.getLine1()).isEqualTo(DEFAULT_LINE_1);
        assertThat(testAddress.getLine2()).isEqualTo(DEFAULT_LINE_2);
        assertThat(testAddress.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllAddresses() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addresses
        restAddressMockMvc.perform(get("/api/addresses?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(address.getId().intValue())))
                .andExpect(jsonPath("$.[*].addressId").value(hasItem(DEFAULT_ADDRESS_ID.intValue())))
                .andExpect(jsonPath("$.[*].line1").value(hasItem(DEFAULT_LINE_1.toString())))
                .andExpect(jsonPath("$.[*].line2").value(hasItem(DEFAULT_LINE_2.toString())))
                .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())));
    }

    @Test
    @Transactional
    public void getAddress() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get the address
        restAddressMockMvc.perform(get("/api/addresses/{id}", address.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(address.getId().intValue()))
            .andExpect(jsonPath("$.addressId").value(DEFAULT_ADDRESS_ID.intValue()))
            .andExpect(jsonPath("$.line1").value(DEFAULT_LINE_1.toString()))
            .andExpect(jsonPath("$.line2").value(DEFAULT_LINE_2.toString()))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAddress() throws Exception {
        // Get the address
        restAddressMockMvc.perform(get("/api/addresses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAddress() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);
        int databaseSizeBeforeUpdate = addressRepository.findAll().size();

        // Update the address
        Address updatedAddress = new Address();
        updatedAddress.setId(address.getId());
        updatedAddress.setAddressId(UPDATED_ADDRESS_ID);
        updatedAddress.setLine1(UPDATED_LINE_1);
        updatedAddress.setLine2(UPDATED_LINE_2);
        updatedAddress.setCountry(UPDATED_COUNTRY);

        restAddressMockMvc.perform(put("/api/addresses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAddress)))
                .andExpect(status().isOk());

        // Validate the Address in the database
        List<Address> addresses = addressRepository.findAll();
        assertThat(addresses).hasSize(databaseSizeBeforeUpdate);
        Address testAddress = addresses.get(addresses.size() - 1);
        assertThat(testAddress.getAddressId()).isEqualTo(UPDATED_ADDRESS_ID);
        assertThat(testAddress.getLine1()).isEqualTo(UPDATED_LINE_1);
        assertThat(testAddress.getLine2()).isEqualTo(UPDATED_LINE_2);
        assertThat(testAddress.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void deleteAddress() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);
        int databaseSizeBeforeDelete = addressRepository.findAll().size();

        // Get the address
        restAddressMockMvc.perform(delete("/api/addresses/{id}", address.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Address> addresses = addressRepository.findAll();
        assertThat(addresses).hasSize(databaseSizeBeforeDelete - 1);
    }
}
