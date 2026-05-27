package com.splitease.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(Long memberId) {
        super("Member not found with id: " + memberId);
    }

    public MemberNotFoundException(Long memberId, Long groupId) {
        super("Member with id " + memberId + " does not belong to group " + groupId);
    }

    public MemberNotFoundException(String message) {
        super(message);
    }
}
