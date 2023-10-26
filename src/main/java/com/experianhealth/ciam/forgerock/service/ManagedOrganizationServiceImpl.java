package com.experianhealth.ciam.forgerock.service;

import org.springframework.stereotype.Service;

import com.experianhealth.ciam.forgerock.model.OrganizationDetails;

@Service
public class ManagedOrganizationServiceImpl extends AbstractForgeRockIDMServiceImpl<OrganizationDetails> implements ManagedOrganizationService {
    
    private static final String ORGANIZATIONS_PATH = "/openidm/managed/organization";

    ManagedOrganizationServiceImpl() {
        super(OrganizationDetails.class);
    }

    @Override
    String getBasePath() {
        return ORGANIZATIONS_PATH;
    }

}
