package com.experianhealth.ciam.scimapi.utils;


import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.exception.CIAMInvalidRequestException;
import com.experianhealth.ciam.forgerock.model.RefProperties;
import com.experianhealth.ciam.forgerock.model.Role;
import com.experianhealth.ciam.forgerock.model.RoleMember;
import com.experianhealth.ciam.scimapi.entity.Operation;
import org.junit.jupiter.api.Test;

import javax.json.JsonObject;
import javax.json.JsonPatch;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GroupPatchOperationUtilityTest extends CIAMTestBase {

    @Test
    public void testApplyAddOperation() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("members");
        Map<String, String> member = new HashMap<>();
        member.put("value", "123");
        operation.setValue(Arrays.asList(member));
        Role role = new Role();
        builder.applyOperation(operation, role);
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    public void testApplyInvalidOperation() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("invalid");
        operation.setPath("members");
        Role role = new Role();
        assertThrows(CIAMInvalidRequestException.class, () -> builder.applyOperation(operation, role));
    }

    @Test
    public void testApplyOperationWithInvalidPath() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("invalid");
        Role role = new Role();
        assertThrows(CIAMInvalidRequestException.class, () -> builder.applyOperation(operation, role));
    }

    @Test
    public void testApplyOperationWithInvalidValue() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("members");
        operation.setValue("invalid");
        Role role = new Role();
        assertThrows(CIAMInvalidRequestException.class, () -> builder.applyOperation(operation, role));
    }


    @Test
    public void testApplyReplaceOperation() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("replace");
        operation.setPath("displayName");
        operation.setValue("New Group Name");
        Role role = new Role();
        builder.applyOperation(operation, role);
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    public void testApplyReplaceDescriptionOperation() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("replace");
        operation.setPath("description");
        operation.setValue("Updated Group Description");
        Role role = new Role();
        builder.applyOperation(operation, role);
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    public void testApplyOperationAddMember() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("members");
        Map<String, Object> memberValue = new HashMap<>();
        memberValue.put("value", "123");
        operation.setValue(Arrays.asList(memberValue));
        Role role = new Role();
        builder.applyOperation(operation, role);
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    public void testApplyOperationRemoveMember() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("remove");
        operation.setPath("members");
        Map<String, Object> memberValue = new HashMap<>();
        memberValue.put("value", "123");
        operation.setValue(Arrays.asList(memberValue));
        Role role = new Role();
        builder.applyOperation(operation, role);
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }


    @Test
    public void testApplyOperationReplaceDisplayName() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("replace");
        operation.setPath("displayName");
        operation.setValue("New Group Name");
        Role role = new Role();
        builder.applyOperation(operation, role);
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    public void testApplyOperationReplaceDescription() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("replace");
        operation.setPath("description");
        operation.setValue("Updated Group Description");
        Role role = new Role();
        builder.applyOperation(operation, role);
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    public void testApplyOperationInvalid() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("invalid");
        operation.setPath("members");
        Role role = new Role();
        assertThrows(CIAMInvalidRequestException.class, () -> builder.applyOperation(operation, role));
    }

    @Test
    public void testExtractUserIdToRemoveValid() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("remove");
        operation.setPath("members");
        Map<String, Object> memberValue = new HashMap<>();
        memberValue.put("value", "123");
        operation.setValue(Arrays.asList(memberValue));
        List<String> userIds = builder.extractUserIdsToRemove(operation);
        assertTrue(userIds.contains("123"), "User IDs list should contain '123'");
    }

    @Test
    public void testExtractUserIdToRemoveInvalid() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Operation operation = new Operation();
        operation.setOp("remove");
        operation.setPath("invalid");
        List<String> userIds = builder.extractUserIdsToRemove(operation);
        assertTrue(userIds.isEmpty(), "User IDs list should be empty");
    }

    @Test
    public void testFindMemberToRemove() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        Role role = new Role();
        RoleMember member = new RoleMember();
        member.set_refResourceId("123");
        role.setMembers(Collections.singletonList(member));
        String userIdToRemove = "123";
        RoleMember foundMember = builder.findMemberToRemove(role, userIdToRemove);
        assertNotNull(foundMember);
    }

    @Test
    public void testBuildRemoveMemberOperation() {
        GroupPatchOperationUtility builder = new GroupPatchOperationUtility();
        RoleMember memberToRemove = new RoleMember();
        memberToRemove.set_ref("ref");
        memberToRemove.set_refResourceCollection("collection");
        memberToRemove.set_refResourceId("resourceId");
        RefProperties refProperties = new RefProperties();
        refProperties.set_id("id");
        refProperties.set_rev("rev");
        memberToRemove.set_refProperties(refProperties);
        JsonObject removeOperation = builder.buildRemoveMemberOperation(memberToRemove);
        assertNotNull(removeOperation);
    }
}
