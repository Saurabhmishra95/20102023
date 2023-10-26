package com.experianhealth.ciam.portal.utility;

import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.forgerock.model.OrganizationDetails;
import com.experianhealth.ciam.portal.entity.Organization;
import com.experianhealth.ciam.portal.entity.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrganizationMapperTest extends CIAMTestBase {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMapToOrganizations() {
        Address address1 = new Address();
        address1.setStreetAddress("123 Main St");
        address1.setState("State1");
        address1.setPostalCode("12345");
        address1.setCity("City1");
        address1.setCounty("County1");

        OrganizationDetails orgDetails1 = new OrganizationDetails();
        orgDetails1.set_id("1");
        orgDetails1.setName("Org1");
        orgDetails1.setDescription("Description1");
        orgDetails1.setAddress(address1);

        Address address2 = new Address();
        address2.setStreetAddress("456 Elm St");
        address2.setState("State2");
        address2.setPostalCode("67890");
        address2.setCity("City2");
        address2.setCounty("County2");

        OrganizationDetails orgDetails2 = new OrganizationDetails();
        orgDetails2.set_id("2");
        orgDetails2.setName("Org2");
        orgDetails2.setDescription("Description2");
        orgDetails2.setAddress(address2);

        List<OrganizationDetails> orgDetailsList = Arrays.asList(orgDetails1, orgDetails2);

        List<Organization> organizations = OrganizationMapper.mapToOrganizations(orgDetailsList);

        assertEquals(2, organizations.size());

        Organization org1 = organizations.get(0);
        assertEquals("1", org1.getId());
        assertEquals("Org1", org1.getName());
        assertEquals("Description1", org1.getDescription());
        assertEquals(address1, org1.getAddress());

        Organization org2 = organizations.get(1);
        assertEquals("2", org2.getId());
        assertEquals("Org2", org2.getName());
        assertEquals("Description2", org2.getDescription());
        assertEquals(address2, org2.getAddress());
    }
}
