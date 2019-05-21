package me.fixeddev.bcm;

import me.fixeddev.bcm.basic.ICommand;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAdvancedCommand implements AdvancedCommand {

    private final String[] names;
    private String usage;
    private String description;
    private String permission;
    private String permissionMessage;
    private List<ICommand> subCommands;
    private int minArguments;
    private int maxArguments;
    private boolean allowAnyFlags;
    List<Character> expectedFlags;

    public AbstractAdvancedCommand(String[] names, String usage, String description, String permission, String permissionMessage, List<ICommand> subCommands, int minArguments, int maxArguments, boolean allowAnyFlags, List<Character> expectedFlags) {
        this.names = names;
        this.usage = usage;
        this.description = description;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
        this.subCommands = subCommands;
        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
        this.allowAnyFlags = allowAnyFlags;
        this.expectedFlags = expectedFlags;
    }

    public AbstractAdvancedCommand(String[] names) {
        this(names, "Usage: /<command>", "", "", "No Permission.", new ArrayList<>(), 0, -1, false, new ArrayList<>());
    }

    @Override
    public String[] getNames() {
        return names;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public String getPermissionMessage() {
        return permissionMessage;
    }

    @Override
    public int getMinArguments() {
        return minArguments;
    }

    @Override
    public int getMaxArguments() {
        return maxArguments;
    }

    @Override
    public boolean allowAnyFlags() {
        return allowAnyFlags;
    }

    @Override
    public List<Character> getExpectedFlags() {
        return expectedFlags;
    }

    @Override
    public List<ICommand> getSubCommands() {
        return subCommands;
    }

    protected void setUsage(String usage) {
        this.usage = usage;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    protected void setPermission(String permission) {
        this.permission = permission;
    }

    protected void setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
    }

    protected void setMinArguments(int minArguments) {
        this.minArguments = minArguments;
    }

    protected void setMaxArguments(int maxArguments) {
        this.maxArguments = maxArguments;
    }

    protected void setAllowAnyFlags(boolean allowAnyFlags) {
        this.allowAnyFlags = allowAnyFlags;
    }

    protected void setExpectedFlags(List<Character> expectedFlags) {
        this.expectedFlags = expectedFlags;
    }

    @Override
    public void registerSubCommand(ICommand command) {
        this.subCommands.add(command);
    }

    @Override
    public void unregisterSubCommand(ICommand command) {
        while (subCommands.contains(command)) {
            subCommands.remove(command);
        }
    }
}
