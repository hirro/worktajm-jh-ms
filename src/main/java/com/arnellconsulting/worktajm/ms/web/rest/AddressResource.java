package com.arnellconsulting.worktajm.ms.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.arnellconsulting.worktajm.ms.domain.Address;
import com.arnellconsulting.worktajm.ms.repository.AddressRepository;
import com.arnellconsulting.worktajm.ms.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Address.
 */
@RestController
@RequestMapping("/api")
public class AddressResource {

    private final Logger log = LoggerFactory.getLogger(AddressResource.class);
        
    @Inject
    private AddressRepository addressRepository;
    
    /**
     * POST  /addresses : Create a new address.
     *
     * @param address the address to create
     * @return the ResponseEntity with status 201 (Created) and with body the new address, or with status 400 (Bad Request) if the address has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/addresses",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Address> createAddress(@RequestBody Address address) throws URISyntaxException {
        log.debug("REST request to save Address : {}", address);
        if (address.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("address", "idexists", "A new address cannot already have an ID")).body(null);
        }
        Address result = addressRepository.save(address);
        return ResponseEntity.created(new URI("/api/addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("address", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /addresses : Updates an existing address.
     *
     * @param address the address to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated address,
     * or with status 400 (Bad Request) if the address is not valid,
     * or with status 500 (Internal Server Error) if the address couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/addresses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Address> updateAddress(@RequestBody Address address) throws URISyntaxException {
        log.debug("REST request to update Address : {}", address);
        if (address.getId() == null) {
            return createAddress(address);
        }
        Address result = addressRepository.save(address);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("address", address.getId().toString()))
            .body(result);
    }

    /**
     * GET  /addresses : get all the addresses.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of addresses in body
     */
    @RequestMapping(value = "/addresses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Address> getAllAddresses() {
        log.debug("REST request to get all Addresses");
        List<Address> addresses = addressRepository.findAll();
        return addresses;
    }

    /**
     * GET  /addresses/:id : get the "id" address.
     *
     * @param id the id of the address to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the address, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/addresses/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Address> getAddress(@PathVariable Long id) {
        log.debug("REST request to get Address : {}", id);
        Address address = addressRepository.findOne(id);
        return Optional.ofNullable(address)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /addresses/:id : delete the "id" address.
     *
     * @param id the id of the address to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/addresses/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        log.debug("REST request to delete Address : {}", id);
        addressRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("address", id.toString())).build();
    }

}
