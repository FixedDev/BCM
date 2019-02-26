package me.ggamer55.bcm.basic;


import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.NoMoreArgumentsException;

public class ArgumentStack implements Cloneable {
    protected String[] originalArguments;

    private int size;
    private int position = -1;

    public ArgumentStack(String[] originalArguments) {
        this.originalArguments = originalArguments;

        size = originalArguments.length;
    }

    protected ArgumentStack(String[] originalArguments, int position) {
        this.originalArguments = originalArguments;

        size = originalArguments.length;

        this.position = position;
    }

    public boolean hasNext() {
        return (size - 1) > position;
    }

    public String next() throws NoMoreArgumentsException {
        if (!hasNext()) {
            throw new NoMoreArgumentsException(size, position);
        }

        position++;

        return originalArguments[position];
    }

    public String peek() throws NoMoreArgumentsException {
        int nextPosition = position + 1;

        if (!hasNext()) {
            throw new NoMoreArgumentsException(size, position);
        }

        return originalArguments[nextPosition];
    }

    public int getPosition() {
        return position;
    }

    public int getSize() {
        return size;
    }

    public int nextInt() throws NoMoreArgumentsException, ArgumentsParseException {
        String next = next();
        try {
            return Integer.parseInt(next);
        } catch (NumberFormatException e) {
            throw new ArgumentsParseException(next, int.class);
        }
    }

    public float nextFloat() throws NoMoreArgumentsException, ArgumentsParseException {
        String next = next();

        try {
            return Float.parseFloat(next);
        } catch (NumberFormatException e) {
            throw new ArgumentsParseException(next, float.class);
        }
    }

    public double nextDouble() throws NoMoreArgumentsException, ArgumentsParseException {
        String next = next();

        try {
            return Double.parseDouble(next);
        } catch (NumberFormatException e) {
            throw new ArgumentsParseException(next, double.class);
        }
    }

    public byte nextByte() throws NoMoreArgumentsException, ArgumentsParseException {
        String next = next();

        try {
            return Byte.parseByte(next);
        } catch (NumberFormatException e) {
            throw new ArgumentsParseException(next, byte.class);
        }
    }

    public boolean nextBoolean() throws NoMoreArgumentsException, ArgumentsParseException {
        String next = next();

        if (!next.equalsIgnoreCase("true") && !next.equalsIgnoreCase("false")) {
            throw new ArgumentsParseException(next, boolean.class);
        }

        return Boolean.parseBoolean(next);
    }

    public void markAsConsumed() {
        this.position = size;
    }

    @Override
    public ArgumentStack clone() {
        return new ArgumentStack(originalArguments, position);
    }
}
