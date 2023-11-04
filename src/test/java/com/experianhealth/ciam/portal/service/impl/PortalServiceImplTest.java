package com.experianhealth.ciam.portal.service.impl;

import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.exception.CIAMPasswordException;
import com.experianhealth.ciam.forgerock.model.*;
import com.experianhealth.ciam.forgerock.service.*;
import com.experianhealth.ciam.portal.entity.ApplicationSection;
import com.experianhealth.ciam.portal.entity.Organization;
import com.experianhealth.ciam.portal.utility.ApplicationDetailsMapper;
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
    void testGetOrganizations_AllAttributes() {
        String token = "sampleToken";
        String returnAttributes = "name description";
        when(managedOrganizationService.getAllWithAttributes(token, returnAttributes))
                .thenReturn(Collections.emptyList());
        List<Organization> organizations = portalService.getOrganizations(token, null, returnAttributes);
        assertEquals(0, organizations.size());
        verify(managedOrganizationService).getAllWithAttributes(token, returnAttributes);
    }

    @Test
    void testGetOrganizations_NoFilterNoAttributes() {
        String token = "sampleToken";
        when(managedOrganizationService.getAll(token))
                .thenReturn(Collections.emptyList());
        List<Organization> organizations = portalService.getOrganizations(token, null, null);
        assertEquals(0, organizations.size());
        verify(managedOrganizationService).getAll(token);
    }

    @Test
    void testExecuteSearch() {
        String token = "sampleToken";
        String searchFilter = "sampleFilter";
        String returnAttributes = "name description";
        when(managedOrganizationService.search(eq(token), any()))
                .thenReturn(Collections.emptyList());
        List<OrganizationDetails> organizationDetails = portalService.executeSearch(token, searchFilter, returnAttributes);
        assertEquals(0, organizationDetails.size());
        verify(managedOrganizationService).search(eq(token), any());
    }


}
