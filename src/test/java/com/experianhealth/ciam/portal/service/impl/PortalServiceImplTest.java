package com.experianhealth.ciam.portal.service.impl;

import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.exception.CIAMPasswordException;
import com.experianhealth.ciam.forgerock.model.*;
import com.experianhealth.ciam.forgerock.service.*;
import com.experianhealth.ciam.portal.entity.ApplicationSection;
import com.experianhealth.ciam.portal.entity.Organization;
import com.experianhealth.ciam.portal.utility.ApplicationDetailsMapper;
import com.experianhealth.ciam.portal.utility.OrganizationMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PortalServiceImplTest extends CIAMTestBase {

    @Mock
    private ForgeRockAMService amService;
    @Mock
    private ManagedUserService managedUserService;
    @Mock
    private ApplicationDetailsMapper applicationDetailsMapper;
    @Mock
    private ManagedApplicationService managedApplicationService;
    @Mock
    private ManagedOrganizationService managedOrganizationService;

    @InjectMocks
    private PortalServiceImpl portalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdatePasswordSuccess() {
        String token = "fakeToken";
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";

        User userInfo = new User();
        userInfo.setUserName("username");
        userInfo.set_id("userId");

        when(amService.getUserInfo(token)).thenReturn(userInfo);
        when(amService.getAccessToken(any(), any(), any(), any())).thenReturn("fakeAccessToken");

        portalService.updatePassword(token, currentPassword, newPassword);

        
    }

    @Test
    void testUpdatePasswordInvalidCurrentPassword() {
        String token = "fakeToken";
        String currentPassword = "invalidPassword";
        String newPassword = "newPassword";

        User userInfo = new User();
        userInfo.setUserName("username");
        userInfo.set_id("userId");

        when(amService.getUserInfo(token)).thenReturn(userInfo);
        when(amService.getAccessToken(any(), any(), any(), any())).thenThrow(new RuntimeException("invalid_grant"));

        assertThrows(CIAMPasswordException.class, () -> portalService.updatePassword(token, currentPassword, newPassword));
    }

    @Test
    void testUpdatePasswordMatchingNewAndCurrent() {
        String token = "fakeToken";
        String currentPassword = "oldPassword";
        String newPassword = "oldPassword";

        User userInfo = new User();
        userInfo.setUserName("username");
        userInfo.set_id("userId");

        when(amService.getUserInfo(token)).thenReturn(userInfo);
        when(amService.getAccessToken(any(), any(), any(), any())).thenReturn("fakeAccessToken");

        assertThrows(CIAMPasswordException.class, () -> portalService.updatePassword(token, currentPassword, newPassword));
    }
    
    @Test
    void testGetApplicationDetailsSuccess() {
        String token = "sampleToken";
        User user = new User();
        user.set_id("sampleUserId");
        User detailedUser = new User();
        List<Application> effectiveApplications = Arrays.asList(new Application());
        detailedUser.setEffectiveApplications(effectiveApplications);
        ApplicationDetails mockAppDetails = new ApplicationDetails();
        mockAppDetails.set_id("mockAppDetailsId");
        mockAppDetails.setName("mockAppName");
        List<ApplicationDetails> mockAppDetailsList = Arrays.asList(mockAppDetails);
        when(amService.getUserInfo(token)).thenReturn(user);
        when(managedUserService.getById(token, user.get_id())).thenReturn(java.util.Optional.of(detailedUser));
        when(managedApplicationService.search(anyString(), any())).thenReturn(mockAppDetailsList);

        List<ApplicationSection> resultList = portalService.getApplicationDetails(token);

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals(2, resultList.size()); 
        assertEquals("myApps", resultList.get(0).getSection());
        assertEquals("availableApps", resultList.get(1).getSection());

        verify(amService, times(1)).getUserInfo(token);
        verify(managedUserService, times(1)).getById(token, user.get_id());
    }

    @Test
    void testGetApplicationDetailsNoAppsFound() {
        String token = "sampleToken";
        User user = new User();
        user.set_id("sampleUserId");
        User detailedUser = new User();
        detailedUser.setEffectiveApplications(Collections.emptyList());

        when(amService.getUserInfo(token)).thenReturn(user);
        when(managedUserService.getById(token, user.get_id())).thenReturn(java.util.Optional.of(detailedUser));
        when(managedApplicationService.search(anyString(), any())).thenReturn(Collections.emptyList());

        List<ApplicationSection> resultList = portalService.getApplicationDetails(token);

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals(2, resultList.size()); 
        assertEquals("myApps", resultList.get(0).getSection());
        assertTrue(resultList.get(0).getApps().isEmpty());
        assertEquals("availableApps", resultList.get(1).getSection());
        assertTrue(resultList.get(1).getApps().isEmpty());
    }

    @Test
    void testGetOrganizations() {
        String token = "sampleToken";
        String searchFilter = "searchQuery";
        String returnAttributes = "name,description";
        OrganizationDetails orgDetails1 = new OrganizationDetails();
        orgDetails1.setName("Org1");
        OrganizationDetails orgDetails2 = new OrganizationDetails();
        orgDetails2.setName("Org2");
        List<OrganizationDetails> organizationDetailsList = Arrays.asList(orgDetails1, orgDetails2);
        when(managedOrganizationService.search(eq(token), any(FRQuery.class))).thenReturn(organizationDetailsList);
       List<Organization> organizations = portalService.getOrganizations(token, searchFilter, returnAttributes);     
        assertNotNull(organizations);
        assertEquals(2, organizations.size());
        assertEquals("Org1", organizations.get(0).getName());
        assertEquals("Org2", organizations.get(1).getName());        
        verify(managedOrganizationService).search(eq(token), any(FRQuery.class));
    }

    @Test
    void testGetOrganizationDetailsByIdFound() {
        String token = "sampleToken";
        String orgId = "orgId123";
        String attributes = "name,description";

        OrganizationDetails orgDetails = new OrganizationDetails(); 
        orgDetails.setName("Test Organization");
        orgDetails.setDescription("A description for Test Organization");

        when(managedOrganizationService.getById(eq(token), eq(orgId), any(FRQuery.class)))
            .thenReturn(Optional.of(orgDetails)); 

        Optional<Organization> actualOrg = portalService.getOrganizationDetailsById(token, orgId, attributes);

        assertTrue(actualOrg.isPresent(), "Organization should be present");
        assertEquals(orgDetails.getName(), actualOrg.get().getName(), "Names should match");
        assertEquals(orgDetails.getDescription(), actualOrg.get().getDescription(), "Descriptions should match");

        verify(managedOrganizationService).getById(eq(token), eq(orgId), any(FRQuery.class));
    }


    @Test
    void testGetOrganizationDetailsByIdNotFound() {
        String token = "sampleToken";
        String orgId = "orgId123";
        String attributes = "name,description";

        when(managedOrganizationService.getById(eq(token), eq(orgId), any(FRQuery.class)))
            .thenReturn(Optional.empty());

        Optional<Organization> actualOrg = portalService.getOrganizationDetailsById(token, orgId, attributes);

        assertFalse(actualOrg.isPresent(), "Organization should not be present");

        verify(managedOrganizationService).getById(eq(token), eq(orgId), any(FRQuery.class));
    }


    


}
