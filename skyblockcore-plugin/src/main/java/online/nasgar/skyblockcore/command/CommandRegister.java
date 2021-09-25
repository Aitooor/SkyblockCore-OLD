package online.nasgar.skyblockcore.command;

import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.CommandClass;

public class CommandRegister {

    private final AnnotatedCommandTreeBuilder commandTreeBuilder;
    private final CommandManager commandManager;

    public CommandRegister(AnnotatedCommandTreeBuilder commandTreeBuilder, CommandManager commandManager) {
        this.commandTreeBuilder = commandTreeBuilder;
        this.commandManager = commandManager;
    }

    public AnnotatedCommandTreeBuilder getCommandTreeBuilder() {
        return commandTreeBuilder;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    public void registerCommandClass(CommandClass commandClass) {
        commandManager.registerCommands(commandTreeBuilder.fromClass(commandClass));
    }
}