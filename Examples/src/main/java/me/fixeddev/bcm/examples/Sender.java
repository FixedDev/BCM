package me.fixeddev.bcm.examples;

import java.util.ArrayList;
import java.util.List;

public class Sender {
    private String senderName;
    private List<String> permissions;

    public Sender(String senderName) {
        this.senderName = senderName;
        permissions = new ArrayList<>();
    }

    public String getName() {
        return senderName;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}
