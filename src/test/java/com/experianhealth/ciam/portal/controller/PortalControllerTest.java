package com.experianhealth.ciam.portal.controller;

import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.exception.CIAMUnauthorizedException;
import com.experianhealth.ciam.forgerock.model.OrganizationDetails;
import com.experianhealth.ciam.portal.entity.Organization;
import com.experianhealth.ciam.portal.entity.PasswordUpdateRequest;
import com.experianhealth.ciam.portal.service.PortalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;




public class PortalControllerTest extends CIAMTestBase {

    private static final String VALID_TOKEN = "Bearer valid_token";
    private static final String INVALID_TOKEN = "Bearer invalid_token";
    @Mock
    private PortalService portalService;

    @InjectMocks
    private PortalController portalController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testVerifyPasswordSuccess() {
        // Given
        PasswordUpdateRequest request = new PasswordUpdateRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword");

        ResponseEntity<String> response = portalController.updatePassword("Bearer token", request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password updated successfully.", response.getBody());
    }
    @Test
    void testGetOrganizationsSuccess() {
        // Given
        String searchFilter = "active=true";
        String returnAttributes = "name,id";
        List<Organization> mockOrganizations = Arrays.asList(new Organization(), new Organization());
        when(portalService.getOrganizations(any(String.class), any(String.class), any(String.class)))
                .thenReturn(mockOrganizations);

        // When
        ResponseEntity<List<Organization>> response = portalController.getOrganizations(VALID_TOKEN, searchFilter, returnAttributes);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(portalService).getOrganizations(any(String.class), any(String.class), any(String.class));
    }

    @Test
    void testGetOrganizationByIdSuccess() {
        String id = "org123";
        String attributes = "name,description";
        Organization mockOrganization = new Organization();
        mockOrganization.setId(id);
        mockOrganization.setName("Test Organization");
        when(portalService.getOrganizationDetailsById(eq(VALID_TOKEN), eq(id), eq(attributes)))
                .thenReturn(Optional.of(mockOrganization));

        ResponseEntity<Organization> response = portalController.getOrganizationById("Bearer " + VALID_TOKEN, id, attributes);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("Test Organization", response.getBody().getName());
        verify(portalService).getOrganizationDetailsById(VALID_TOKEN, id, attributes);
    }


    @Test
    void testGetOrganizationByIdNotFound() {
        String id = "orgNotFound";
        String attributes = "name,description";
        when(portalService.getOrganizationDetailsById(any(String.class), any(String.class), any(String.class)))
                .thenReturn(Optional.empty());
    
        CIAMNotFoundException thrown = assertThrows(
            CIAMNotFoundException.class,
            () -> portalController.getOrganizationById(INVALID_TOKEN, id, attributes),
            "Expected getOrganizationById to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Organization not found"));
        verify(portalService).getOrganizationDetailsById(any(String.class), any(String.class), any(String.class));
    }

   
}
