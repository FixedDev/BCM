package me.ggamer55.bcm.basic;

public interface Authorizer {
    boolean isAuthorized(Namespace namespace, String permission);
}
