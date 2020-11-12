package me.dominic.neoessentials;

import me.dominic.neoessentials.listener.*;
import me.dominic.neoessentials.reflect.Reflect;
import me.dominic.neoessentials.settings.Settings;
import me.dominic.neoessentials.custom.CustomIngameChatManager;
import me.dominic.neoessentials.custom.CustomLabyModAPI;
import me.dominic.neoessentials.utils.Helper;
import net.labymod.api.LabyModAddon;
import net.labymod.ingamechat.IngameChatManager;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NeoEssentials extends LabyModAddon {

    public static final String PREFIX = "§8[§2NeoEssentials§8] §7",
                               COMMAND_PREFIX = ".";
    
    private static NeoEssentials neoEssentials;
    private Helper helper;
    private Settings settings;
    private String currentDate;
    private Set<ClientCommandEvent> commandListeners = new HashSet<>();

    @Override
    public void onEnable() {
        Reflect.getField(IngameChatManager.class, "INSTANCE").setStatic(new CustomIngameChatManager());
        Reflect.getField(LabyMod.class, "labyModAPI").set(LabyMod.getInstance(), new CustomLabyModAPI(LabyMod.getInstance()));

        setNeoEssentials(this);
        setCurrentDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        setHelper(new Helper());
        setSettings(new Settings());

        getApi().getEventManager().register(new ChatSendListener());
        getApi().getEventManager().register(new ChatReceiveListener());
        getApi().registerForgeListener(new Events());
        registerEvent(new CommandListener());
    }

    @Override
    public void loadConfig() {
        getSettings().loadSettings();
    }

    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        getSettings().fillSettings(settings);
    }

    public void registerEvent(ClientCommandEvent listener) {
        getCommandListeners().add(listener);
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

    public void setCommandListeners(Set<ClientCommandEvent> commandListeners) {
        this.commandListeners = commandListeners;
    }
    public Set<ClientCommandEvent> getCommandListeners() {
        return commandListeners;
    }
}
