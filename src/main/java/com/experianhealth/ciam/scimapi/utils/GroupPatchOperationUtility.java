package com.experianhealth.ciam.scimapi.utils;

import com.experianhealth.ciam.exception.CIAMInvalidRequestException;
import com.experianhealth.ciam.forgerock.model.RefProperties;
import com.experianhealth.ciam.forgerock.model.Role;
import com.experianhealth.ciam.forgerock.model.RoleMember;
import com.experianhealth.ciam.scimapi.entity.Operation;

import javax.json.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupPatchOperationUtility {

    private static final String REMOVE = "remove";
    private final JsonArrayBuilder operations = Json.createArrayBuilder();

    public void applyOperation(Operation operation, Role role) {
        String opType = operation.getOp().toLowerCase();
        switch (opType) {
            case REMOVE:
                handleRemoveOperation(operation, role);
                break;
            default:
                handleAttributeOperation(opType, operation);
                break;
        }
    }

    private void handleRemoveOperation(Operation operation, Role role) {
        List<String> userIdsToRemove = extractUserIdsToRemove(operation);
        for (String userIdToRemove : userIdsToRemove) {
            RoleMember memberToRemove = findMemberToRemove(role, userIdToRemove);
            if (memberToRemove != null) {
                JsonObject removeOperation = buildRemoveMemberOperation(memberToRemove);
                operations.add(removeOperation);
            }
        }
    }

    RoleMember findMemberToRemove(Role role, String userIdToRemove) {
        if (userIdToRemove != null) {
            List<RoleMember> members = role.getMembers();
            if (members != null) {
                for (RoleMember member : members) {
                    if (userIdToRemove.equals(member.get_refResourceId())) {
                        return member;
                    }
                }
            }
        }
        return null;
    }

    JsonObject buildRemoveMemberOperation(RoleMember memberToRemove) {
        if (memberToRemove == null || memberToRemove.get_refProperties() == null) {
            return null;
        }

        RefProperties _refProperties = memberToRemove.get_refProperties();
        JsonObject valueObject = Json.createObjectBuilder()
                .add("_ref", memberToRemove.get_ref())
                .add("_refResourceCollection", memberToRemove.get_refResourceCollection())
                .add("_refResourceId", memberToRemove.get_refResourceId())
                .add("_refProperties", Json.createObjectBuilder()
                        .add("_id", _refProperties.get_id())
                        .add("_rev", _refProperties.get_rev()))
                .build();

        return Json.createObjectBuilder()
                .add("op", REMOVE)
                .add("path", "/members")
                .add("value", valueObject)
                .build();
    }

    private void handleAttributeOperation(String op, Operation operation) {
        String path = operation.getPath();
        Object value = operation.getValue();

        switch (path.toLowerCase()) {
            case "members":
                handleMembersOperation(op, value);
                break;
            case "displayname":
                applyAttributeOperation(op, "name", value);
            case "description":
                applyAttributeOperation(op, "description", value);
                break;
            default:
                throw new CIAMInvalidRequestException("Unsupported group attribute path: " + path);
        }
    }

    private void handleMembersOperation(String op, Object value) {
        if (value instanceof List) {
            List<Map<String, String>> members = (List<Map<String, String>>) value;
            for (Map<String, String> member : members) {
                String userId = member.get("value");
                String memberPath = "/members/-";
                if (REMOVE.equalsIgnoreCase(op)) {
                    memberPath = "/members[value=\"" + userId + "\"]";
                }
                JsonObject memberValue = Json.createObjectBuilder().add("_ref", "managed/user/" + userId).build();
                applyAttributeOperation(op, memberPath, memberValue);
            }
        } else {
            throw new CIAMInvalidRequestException("The value for 'members' is not of type List");
        }
    }

    private GroupPatchOperationUtility applyAttributeOperation(String op, String path, Object value) {
        if (value != null || REMOVE.equalsIgnoreCase(op)) {
            JsonObjectBuilder operationBuilder = Json.createObjectBuilder()
                    .add("op", op)
                    .add("path", path);
            if (value != null) {
                if (value instanceof JsonObject) {
                    operationBuilder.add("value", (JsonObject) value);
                } else {
                    operationBuilder.add("value", value.toString());
                }
            }
            operations.add(operationBuilder.build());
        }
        return this;
    }

    List<String> extractUserIdsToRemove(Operation operation) {
        List<String> userIds = new ArrayList<>();
        if (REMOVE.equalsIgnoreCase(operation.getOp()) && "members".equalsIgnoreCase(operation.getPath())) {
            List<Map<String, Object>> values = (List<Map<String, Object>>) operation.getValue();
            if (values != null && !values.isEmpty()) {
                for (Map<String, Object> value : values) {
                    userIds.add((String) value.get("value"));
                }
            }
        }
        return userIds;
    }

    public JsonPatch build() {
        return Json.createPatch(operations.build());
    }
}
