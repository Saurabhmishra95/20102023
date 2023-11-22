package com.experianhealth.ciam.portal.utility;

import java.util.List;
import java.util.stream.Collectors;
import com.experianhealth.ciam.forgerock.model.OrganizationDetails;
import com.experianhealth.ciam.portal.entity.Organization;
import java.util.ArrayList;

public class OrganizationMapper {

    private static Organization mapDetailsToOrganization(OrganizationDetails orgDetails) {
        if (orgDetails == null) {
            return null;
        }

        Organization organization = new Organization();
        organization.setId(orgDetails.get_id());
        organization.setName(orgDetails.getName());
        organization.setDescription(orgDetails.getDescription());
        organization.setAddress(orgDetails.getAddress());
        return organization;
    }

    public static List<Organization> mapToOrganizations(List<OrganizationDetails> organizationDetailsList) {
        if (organizationDetailsList == null) {
            return new ArrayList<>();
        }

        return organizationDetailsList.stream()
                .map(OrganizationMapper::mapDetailsToOrganization)
                .collect(Collectors.toList());
    }

    public static Organization mapToOrganization(OrganizationDetails organizationDetails) {
        return mapDetailsToOrganization(organizationDetails);
    }
}
