package com.splitease.entity;

/**
 * Defines how an expense is split among group members.
 *
 * EQUAL  - divide the total amount equally among all ACTIVE members at the time of creation.
 * CUSTOM - caller provides explicit {memberId, amount} pairs that must sum to the expense total.
 */
public enum SplitType {
    EQUAL,
    CUSTOM
}
