package me.fixeddev.bcm.basic;

public interface Authorizer {
    boolean isAuthorized(Namespace namespace, String permission);
}
