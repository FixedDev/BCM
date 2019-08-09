package me.fixeddev.bcm.parametric;

public class FlagData implements ParameterData {

    private char flagName;

    public FlagData(char flagName) {
        this.flagName = flagName;
    }

    public static FlagData valueOf(char name){
        return new FlagData(name);
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
}
