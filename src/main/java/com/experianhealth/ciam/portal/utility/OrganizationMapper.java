package com.experianhealth.ciam.portal.utility;

import java.util.List;

import com.experianhealth.ciam.forgerock.model.OrganizationDetails;
import com.experianhealth.ciam.portal.entity.Organization;

import java.util.ArrayList;

public class OrganizationMapper {

    public static List<Organization> mapToOrganizations(List<OrganizationDetails> organizationDetailsList) {
        List<Organization> organizations = new ArrayList<>();

        for (OrganizationDetails orgDetails : organizationDetailsList) {
            Organization organization = new Organization();
            organization.setId(orgDetails.get_id());
            organization.setName(orgDetails.getName());
            organization.setDescription(orgDetails.getDescription());
            organization.setAddress(orgDetails.getAddress());

            organizations.add(organization);
        }

        return organizations;
    }
}
