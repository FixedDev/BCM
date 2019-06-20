package me.fixeddev.bcm;

import me.fixeddev.bcm.basic.ArgumentStack;
import me.fixeddev.bcm.basic.Namespace;
import me.fixeddev.bcm.basic.exceptions.NoMoreArgumentsException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandContext {
    private String commandName;
    private String label;

    private ArgumentStack rawArguments;
    private List<String> parsedArguments;

    private boolean anyFlagsAllowed;
    private List<Character> expectedFlags;
    private List<Character> actualFlags;

    private Namespace namespace;

    public CommandContext(String command, String label, ArgumentStack arguments, List<Character> expectedFlags, boolean anyFlagsAllowed, Namespace namespace) {
        commandName = command;
        this.label = label;

        rawArguments = arguments;

        this.expectedFlags = expectedFlags;
        this.anyFlagsAllowed = anyFlagsAllowed;

        actualFlags = new ArrayList<>();

        parsedArguments = new ArrayList<>();

        this.namespace = namespace;


        ArgumentStack argumentsCopy = arguments.clone();

        try {
            while (argumentsCopy.hasNext()) {
                String argument = argumentsCopy.next();

                if (argument == null || argument.isEmpty()) {
                    continue;
                }

                if (argument.startsWith("-")) {
                    argument = argument.substring(1);

                    StringBuilder newArgument = new StringBuilder().append("-");

                    if (argument.length() >= 1) {
                        for (char c : argument.toCharArray()) {
                            if (expectedFlags.contains(c) || anyFlagsAllowed) {
                                actualFlags.add(c);
                                continue;
                            }
                            newArgument.append(c);
                        }
                    }

                    String newArgumentString = newArgument.toString();

                    if(!newArgumentString.isEmpty()){
                        parsedArguments.add(newArgumentString);
                    }

                    continue;
                }

                parsedArguments.add(argument);
            }
        } catch (NoMoreArgumentsException e) {
            // ignored, it just can't happen
        }
    }

    public String getCommandName() {
        return commandName;
    }

    public String getLabel() {
        return label;
    }

    public boolean isAnyFlagsAllowed() {
        return anyFlagsAllowed;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public String getArgument(int index) {
        return parsedArguments.get(index);
    }

    public List<String> getArguments() {
        return this.parsedArguments;
    }

    public int getArgumentsLength(){
        return parsedArguments.size();
    }

    public String getJoinedArgs(int startIndex) {
        return this.getJoinedArgs(startIndex, this.getArguments().size());
    }

    public String getJoinedArgs(int startIndex, int endIndex) {
        String joinedString = getArguments().subList(startIndex, endIndex).stream().collect(Collectors.joining(" "));

        return  joinedString;
    }

    public ArgumentStack getRawArguments() {
        return this.rawArguments;
    }

    public ArgumentStack getRawArgumentsWithoutFlags(){
        return new ArgumentStack(this.parsedArguments.toArray(new String[0]));
    }

    public List<Character> getExpectedFlags() {
        return new ArrayList<>(this.expectedFlags);
    }

    public List<Character> getActualFlags() {
        return new ArrayList<>(actualFlags);
    }

    public boolean getFlagValue(Character character) {
        return actualFlags.contains(character);
    }
}