package com.cobnet.security;

public enum RoleRule {

    VISITOR(0),
    PRE_USER(1),
    EXTERNAL(2),
    USER(3),
    SERVER(4),
    ADMIN(5);

    private final int level;

    private RoleRule(int level) {

        this.level = level;
    }

    public int getLevel() {

        return this.level;
    }
}
