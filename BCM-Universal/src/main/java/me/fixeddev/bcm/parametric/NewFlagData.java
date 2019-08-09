package me.fixeddev.bcm.parametric;

import java.util.Optional;

public class NewFlagData implements IParameterData {

    private char flagName;

    public NewFlagData(char flagName) {
        this.flagName = flagName;
    }

    public static NewFlagData valueOf(char name){
        return new NewFlagData(name);
    }

    /**
     * @return - The name of this flag, just a letter
     */
    public char getName() {
        return flagName;
    }

    @Override
    public Class<?> getParameterType() {
        return boolean.class;
    }

    @Override
    public ParameterType getType() {
        return ParameterType.FLAG;
    }

    @Override
    public Optional<String> getDefaultValue() {
        return Optional.of("false");
    }
}
