package com.experianhealth.ciam.portal.service;

import java.util.List;

import java.util.Optional;

import com.experianhealth.ciam.portal.entity.ApplicationSection;
import com.experianhealth.ciam.portal.entity.Organization;
import com.experianhealth.ciam.portal.entity.PortalConfiguration;
import com.experianhealth.ciam.forgerock.model.*;

public interface PortalService {

    void updatePassword(String token, String previousPassword, String newPassword);

    PortalConfiguration getConfiguration();

    List<ApplicationSection> getApplicationDetails(String token);
    
    List<Organization> getOrganizations(String token, String searchFilter, String returnAttributes);
    
    Optional<Organization> getOrganizationDetailsById(String token, String id, String attributes);


}
