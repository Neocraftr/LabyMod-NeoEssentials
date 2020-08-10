package me.dominic.neoessentials;

import net.labymod.api.events.MessageReceiveEvent;

public class ChatReceiveListener implements MessageReceiveEvent {

    @Override
    public boolean onReceive(String msgRaw, String msg) {
        Utils.logChatMessage(msg);

        Utils.lastFormatedChatMessages.add(msgRaw);
        while(Utils.lastFormatedChatMessages.size() > 20) {
            Utils.lastFormatedChatMessages.remove(0);
        }

        return false;
    }
}
