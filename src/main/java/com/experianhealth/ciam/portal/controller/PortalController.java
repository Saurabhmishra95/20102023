package com.experianhealth.ciam.portal.controller;



import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.forgerock.model.FRQuery;
import com.experianhealth.ciam.forgerock.model.FRQueryFilter;
import com.experianhealth.ciam.forgerock.model.OrganizationDetails;
import com.experianhealth.ciam.forgerock.service.ManagedOrganizationService;
import com.experianhealth.ciam.portal.entity.ApplicationSection;
import com.experianhealth.ciam.portal.entity.Organization;
import com.experianhealth.ciam.portal.entity.PasswordUpdateRequest;
import com.experianhealth.ciam.portal.entity.PortalConfiguration;
import com.experianhealth.ciam.portal.service.PortalService;
import com.experianhealth.ciam.portal.utility.OrganizationMapper;
import com.experianhealth.ciam.scimapi.utils.AuthorizationUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(PortalController.PORTAL_PATH)
public class PortalController {

    public static final String CONFIGURATION_PATH = "/configuration";
    public static final String UPDATEPASSWORD_PATH = "/updatepassword";
    public static final String PORTAL_PATH = "/portal";
    public static final String APPLICATION_DETAILS_PATH = "/applicationdetails";
	private static final String ORGANIZATIONS_PATH = "organizations";
	
	  @Autowired
	    private ManagedOrganizationService managedOrganizationService;

    @Autowired
    private PortalService portalService;
    
    @GetMapping(CONFIGURATION_PATH)
    public ResponseEntity<PortalConfiguration> getConfiguration() {
        return ResponseEntity.ok(portalService.getConfiguration());
    }

    @PostMapping(UPDATEPASSWORD_PATH)
    public ResponseEntity<String> updatePassword(
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @RequestBody PasswordUpdateRequest verificationRequest) {
        portalService.updatePassword(
                AuthorizationUtils.validateBearerToken(Optional.ofNullable(bearerToken)),
                verificationRequest.getCurrentPassword(),
                verificationRequest.getNewPassword()
        );
        return ResponseEntity.ok("Password updated successfully.");
    }

    @GetMapping(APPLICATION_DETAILS_PATH)
    public ResponseEntity<List<ApplicationSection>> getApplicationDetails(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken) {
        String token = AuthorizationUtils.validateBearerToken(bearerToken);
        List<ApplicationSection> responses = portalService.getApplicationDetails(token);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping(ORGANIZATIONS_PATH)
    public ResponseEntity<List<Organization>> getOrganizations(
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @RequestParam(required = false) String searchFilter,
            @RequestParam(required = false) String returnAttributes) {
        String token = AuthorizationUtils.validateBearerToken(Optional.ofNullable(bearerToken));
        List<Organization> organizations = portalService.getOrganizations(token, searchFilter, returnAttributes);
        return ResponseEntity.ok(organizations);
    }

    @GetMapping(ORGANIZATIONS_PATH + "/{id}")
    public ResponseEntity<Organization> getOrganizationById(
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @PathVariable String id,
            @RequestParam(required = false) String attributes) {
        String token = AuthorizationUtils.validateBearerToken(Optional.ofNullable(bearerToken));
        Optional<Organization> organization = portalService.getOrganizationDetailsById(token, id, attributes);
        if (!organization.isPresent()) {
            throw new CIAMNotFoundException(id, "Organization not found");
        }
        return ResponseEntity.ok(organization.get());
    }

}