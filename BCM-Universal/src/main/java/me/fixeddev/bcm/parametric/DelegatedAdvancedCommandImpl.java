package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.AbstractAdvancedCommand;
import me.fixeddev.bcm.AdvancedCommand;
import me.fixeddev.bcm.basic.ICommand;

import java.util.List;

public class DelegatedAdvancedCommandImpl extends AbstractAdvancedCommand implements me.fixeddev.bcm.DelegatedAdvancedCommand {
    private AdvancedCommand delegate;

    public DelegatedAdvancedCommandImpl(String[] names, String usage, String description, String permission, String permissionMessage, List<ICommand> subCommands, int minArguments, int maxArguments, boolean allowAnyFlags, List<Character> expectedFlags) {
        super(names, usage, description, permission, permissionMessage, subCommands, minArguments, maxArguments, allowAnyFlags, expectedFlags);
    }

    public DelegatedAdvancedCommandImpl(String[] names) {
        super(names);
    }

    @Override
    public AdvancedCommand getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(AdvancedCommand delegate) {
        this.delegate = delegate;
    }
}
