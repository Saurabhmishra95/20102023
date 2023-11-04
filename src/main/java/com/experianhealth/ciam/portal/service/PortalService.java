package com.experianhealth.ciam.portal.service;

import java.util.List;
import com.experianhealth.ciam.portal.entity.ApplicationSection;
import com.experianhealth.ciam.portal.entity.Organization;
import com.experianhealth.ciam.portal.entity.PortalConfiguration;

public interface PortalService {

    void updatePassword(String token, String previousPassword, String newPassword);

    PortalConfiguration getConfiguration();

    List<ApplicationSection> getApplicationDetails(String token);
    
    List<Organization> getOrganizations(String token, String searchFilter, String returnAttributes);
}
