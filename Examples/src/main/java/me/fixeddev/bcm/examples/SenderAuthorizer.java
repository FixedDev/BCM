package me.fixeddev.bcm.examples;

import me.fixeddev.bcm.basic.Authorizer;
import me.fixeddev.bcm.basic.Namespace;

public class SenderAuthorizer implements Authorizer {
    @Override
    public boolean isAuthorized(Namespace namespace, String permission) {
        Sender sender = namespace.getObject(Sender.class, "sender");

        if(sender == null){
            return false;
        }

        return sender.hasPermission(permission);
    }
}
