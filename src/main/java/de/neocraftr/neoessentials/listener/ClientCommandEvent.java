package de.neocraftr.neoessentials.listener;

public interface ClientCommandEvent {
    boolean onCommand(String cmd, String[] args);
}
