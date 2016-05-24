package com.arnellconsulting.worktajm.ms.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.arnellconsulting.worktajm.ms.domain.Organization;
import com.arnellconsulting.worktajm.ms.repository.OrganizationRepository;
import com.arnellconsulting.worktajm.ms.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Organization.
 */
@RestController
@RequestMapping("/api")
public class OrganizationResource {

    private final Logger log = LoggerFactory.getLogger(OrganizationResource.class);
        
    @Inject
    private OrganizationRepository organizationRepository;
    
    /**
     * POST  /organizations : Create a new organization.
     *
     * @param organization the organization to create
     * @return the ResponseEntity with status 201 (Created) and with body the new organization, or with status 400 (Bad Request) if the organization has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/organizations",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Organization> createOrganization(@Valid @RequestBody Organization organization) throws URISyntaxException {
        log.debug("REST request to save Organization : {}", organization);
        if (organization.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("organization", "idexists", "A new organization cannot already have an ID")).body(null);
        }
        Organization result = organizationRepository.save(organization);
        return ResponseEntity.created(new URI("/api/organizations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("organization", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /organizations : Updates an existing organization.
     *
     * @param organization the organization to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated organization,
     * or with status 400 (Bad Request) if the organization is not valid,
     * or with status 500 (Internal Server Error) if the organization couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/organizations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Organization> updateOrganization(@Valid @RequestBody Organization organization) throws URISyntaxException {
        log.debug("REST request to update Organization : {}", organization);
        if (organization.getId() == null) {
            return createOrganization(organization);
        }
        Organization result = organizationRepository.save(organization);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("organization", organization.getId().toString()))
            .body(result);
    }

    /**
     * GET  /organizations : get all the organizations.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of organizations in body
     */
    @RequestMapping(value = "/organizations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Organization> getAllOrganizations() {
        log.debug("REST request to get all Organizations");
        List<Organization> organizations = organizationRepository.findAll();
        return organizations;
    }

    /**
     * GET  /organizations/:id : get the "id" organization.
     *
     * @param id the id of the organization to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the organization, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/organizations/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Organization> getOrganization(@PathVariable Long id) {
        log.debug("REST request to get Organization : {}", id);
        Organization organization = organizationRepository.findOne(id);
        return Optional.ofNullable(organization)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /organizations/:id : delete the "id" organization.
     *
     * @param id the id of the organization to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/organizations/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        log.debug("REST request to delete Organization : {}", id);
        organizationRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("organization", id.toString())).build();
    }

}
