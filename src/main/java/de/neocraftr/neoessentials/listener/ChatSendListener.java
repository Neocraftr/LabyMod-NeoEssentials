package de.neocraftr.neoessentials.listener;

import de.neocraftr.neoessentials.enums.EnumAutoColor;
import de.neocraftr.neoessentials.settings.Settings;
import de.neocraftr.neoessentials.NeoEssentials;
import net.labymod.api.events.MessageSendEvent;
import net.minecraft.client.Minecraft;
import java.util.*;
import java.util.List;

public class ChatSendListener implements MessageSendEvent {

    @Override
    public boolean onSend(String msg) {
        if(msg.toLowerCase().startsWith(NeoEssentials.COMMAND_PREFIX)) {
            List<String> msg_split = new ArrayList<>(Arrays.asList(msg.split(" ")));
            String cmd = msg_split.get(0).replaceFirst(NeoEssentials.COMMAND_PREFIX, "");
            msg_split.remove(0);
            String[] args = msg_split.toArray(new String[0]);

            boolean handled = false;
            for(ClientCommandEvent listener : getNeoEssentials().getCommandListeners()) {
                if(listener.onCommand(cmd, args)) {
                    handled = true;
                }
            }

            if(handled) return true;
        }

        // auto color
        if(getSettings().getAutoColor() == EnumAutoColor.DEFAULT) return false;
        if(msg.startsWith("/")) return false;
        for(String ignoreMessage : getSettings().getAutoColorIgnoreMessages()) {
            if(msg.startsWith(ignoreMessage)) return false;
        }

        getMC().thePlayer.sendChatMessage("&"+getSettings().getAutoColor().getColorCode()+msg);
        return true;
    }

    private Minecraft getMC() {
        return Minecraft.getMinecraft();
    }

    private NeoEssentials getNeoEssentials() {
        return NeoEssentials.getNeoEssentials();
    }

    private Settings getSettings() {
        return NeoEssentials.getNeoEssentials().getSettings();
    }
}
