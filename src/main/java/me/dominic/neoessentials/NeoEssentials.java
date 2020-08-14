package me.dominic.neoessentials;

import me.dominic.neoessentials.listener.ChatReceiveListener;
import me.dominic.neoessentials.listener.ChatSendListener;
import me.dominic.neoessentials.listener.Events;
import me.dominic.neoessentials.settings.Settings;
import me.dominic.neoessentials.utils.Helper;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NeoEssentials extends LabyModAddon {

    public static final String PREFIX = "§8[§2NeoEssentials§8] §7";

    private static NeoEssentials neoEssentials;
    private Helper helper;
    private Settings settings;
    private String currentDate;

    @Override
    public void onEnable() {
        setNeoEssentials(this);
        setCurrentDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        setHelper(new Helper());
        setSettings(new Settings());

        getApi().getEventManager().register(new ChatSendListener());
        getApi().getEventManager().register(new ChatReceiveListener());
        getApi().registerForgeListener(new Events());
    }

    @Override
    public void loadConfig() {
        getSettings().loadSettings();
    }

    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        getSettings().fillSettings(settings);
    }

    public static void setNeoEssentials(NeoEssentials neoEssentials) {
        NeoEssentials.neoEssentials = neoEssentials;
    }
    public static NeoEssentials getNeoEssentials() {
        return neoEssentials;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }
    public Helper getHelper() {
        return helper;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
    public Settings getSettings() {
        return settings;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
    public String getCurrentDate() {
        return currentDate;
    }
}
