package me.dominic.neoessentials.listener;

import me.dominic.neoessentials.settings.Settings;
import me.dominic.neoessentials.utils.Helper;
import me.dominic.neoessentials.NeoEssentials;
import net.labymod.api.events.MessageReceiveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatReceiveListener implements MessageReceiveEvent {

    private String itemRemoverStartMessage = "[GrieferGames] Warnung! Die auf dem Boden liegenden Items werden in 20 Sekunden entfernt!";
    private Pattern itemRemoverOverRegex = Pattern.compile("\\[GrieferGames\\] Es wurden \\d+ auf dem Boden liegende Items entfernt!");

    @Override
    public boolean onReceive(String msgRaw, String msg) {
        getNeoEssentials().getHelper().logChatMessage(msg);

        getHelper().getLastFormatedChatMessages().add(msgRaw);
        while(getHelper().getLastFormatedChatMessages().size() > 60) {
            getHelper().getLastFormatedChatMessages().remove(0);
        }

        if(getSettings().isPauseOnItemRemover()) {
            if(msg.equals(itemRemoverStartMessage)) {
                getHelper().setItemRemoverActive(true);
                if(getHelper().isAutoBreakActive()) {
                    KeyBinding.setKeyBindState(getMC().gameSettings.keyBindAttack.getKeyCode(), false);
                }
                if(getHelper().isAutoUseActive()) {
                    KeyBinding.setKeyBindState(getMC().gameSettings.keyBindUseItem.getKeyCode(), false);
                }
            }

            Matcher m = itemRemoverOverRegex.matcher(msg);
            if(m.find()) {
                getHelper().setItemRemoverActive(false);
                if(getHelper().isAutoBreakActive()) {
                    KeyBinding.setKeyBindState(getMC().gameSettings.keyBindAttack.getKeyCode(), true);
                }
                if(getHelper().isAutoUseActive()) {
                    KeyBinding.setKeyBindState(getMC().gameSettings.keyBindUseItem.getKeyCode(), true);
                }
            }
        }

        return false;
    }

    private Minecraft getMC() {
        return Minecraft.getMinecraft();
    }

    private NeoEssentials getNeoEssentials() {
        return NeoEssentials.getNeoEssentials();
    }

    private Helper getHelper() {
        return NeoEssentials.getNeoEssentials().getHelper();
    }

    private Settings getSettings() {
        return NeoEssentials.getNeoEssentials().getSettings();
    }
}
