package me.fixeddev.bcm.parametric;


import java.util.Optional;

public interface IParameterData {


    /**
     * @return - The class type of this parameter
     */
    Class<?> getParameterType();

    /**
     * @return - The ParameterType of this parameter, ARGUMENT or FLAG
     */
    ParameterType getType();

    /**
     * @return - The default value of this parameter, absent if this parameter doesn't has a default value
     *           "false" if the ParameterType is FLAG
     */
    Optional<String> getDefaultValue();
}
