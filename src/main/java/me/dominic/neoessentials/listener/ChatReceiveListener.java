package me.dominic.neoessentials.listener;

import me.dominic.neoessentials.utils.Helper;
import me.dominic.neoessentials.NeoEssentials;
import net.labymod.api.events.MessageReceiveEvent;

public class ChatReceiveListener implements MessageReceiveEvent {

    @Override
    public boolean onReceive(String msgRaw, String msg) {
        getNeoEssentials().getHelper().logChatMessage(msg);

        getHelper().getLastFormatedChatMessages().add(msgRaw);
        while(getHelper().getLastFormatedChatMessages().size() > 60) {
            getHelper().getLastFormatedChatMessages().remove(0);
        }

        return false;
    }

    private NeoEssentials getNeoEssentials() {
        return NeoEssentials.getNeoEssentials();
    }

    private Helper getHelper() {
        return NeoEssentials.getNeoEssentials().getHelper();
    }
}
