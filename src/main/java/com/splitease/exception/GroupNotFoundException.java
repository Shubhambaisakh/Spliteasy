package com.splitease.exception;

public class GroupNotFoundException extends RuntimeException {

    public GroupNotFoundException(Long groupId) {
        super("Group not found with id: " + groupId);
    }

    public GroupNotFoundException(String message) {
        super(message);
    }
}
