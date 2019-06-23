package me.fixeddev.bcm.parametric;

import me.fixeddev.bcm.parametric.annotation.Flag;
import me.fixeddev.bcm.parametric.annotation.Optional;
import me.fixeddev.bcm.parametric.annotation.Parameter;

import java.lang.annotation.Annotation;
import java.util.List;

class FlagData extends ParameterData {

    public FlagData(Parameter parameter, Optional optional, List<Annotation> modifiers) {
        super(boolean.class, parameter, optional, modifiers);
    }

    public FlagData(Flag parameter, Optional optional, List<Annotation> modifiers) {
        super(boolean.class, getFromFlag(parameter), optional, modifiers);
    }

    static Parameter getFromFlag(Flag flag){
        return new Parameter() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Parameter.class;
            }

            @Override
            public String value() {
                return flag.value() + "";
            }

            @Override
            public boolean isFlag() {
                return true;
            }
        };
    }
}
