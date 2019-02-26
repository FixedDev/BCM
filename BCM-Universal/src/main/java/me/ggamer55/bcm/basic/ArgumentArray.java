package me.ggamer55.bcm.basic;

import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;

public class ArgumentArray extends ArgumentStack {
    public ArgumentArray(String[] originalArguments) {
        super(originalArguments);
    }

    protected ArgumentArray(String[] originalArguments, int position) {
        super(originalArguments, position);
    }

    public String at(int index) throws NoMoreArgumentsException {
        if (getSize() > index) {
            throw new NoMoreArgumentsException(getSize(), index);
        }

        return originalArguments[index];
    }

    public String get(int index) {
        if ((getSize() - 1) < index) {
            return null;
        }

        return originalArguments[index];
    }

    @Override
    public ArgumentArray clone() {
        return new ArgumentArray(originalArguments, getPosition());
    }

}
