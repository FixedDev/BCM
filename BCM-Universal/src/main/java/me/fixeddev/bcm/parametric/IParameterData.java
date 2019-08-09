package me.fixeddev.bcm.parametric;

public interface IParameterData {

    /**
     * @return - The class type of this parameter
     */
    Class<?> getParameterType();

    /**
     * @return - The ParameterType of this parameter, ARGUMENT or FLAG
     */
    ParameterType getType();
}
