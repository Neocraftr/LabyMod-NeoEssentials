package me.dominic.neoessentials;

import me.dominic.neoessentials.listener.*;
import me.dominic.neoessentials.settings.Settings;
import me.dominic.neoessentials.custom.CustomIngameChatManager;
import me.dominic.neoessentials.custom.CustomLabyModAPI;
import me.dominic.neoessentials.utils.Helper;
import me.dominic.neoessentials.utils.Updater;
import net.labymod.addon.AddonLoader;
import net.labymod.api.LabyModAddon;
import net.labymod.ingamechat.IngameChatManager;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NeoEssentials extends LabyModAddon {

    public static final String PREFIX = "§8[§2NeoEssentials§8] §7",
                               COMMAND_PREFIX = ".",
                               VERSION = "1.2.0";
    
    private static NeoEssentials neoEssentials;
    private Helper helper;
    private Settings settings;
    private String currentDate;
    private Updater updater;
    private Set<ClientCommandEvent> commandListeners = new HashSet<>();

    @Override
    public void onEnable() {
        try {
            Field ingameChatManagerField = IngameChatManager.class.getDeclaredField("INSTANCE");
            Field ingameChatManagerModifiers = Field.class.getDeclaredField("modifiers");
            ingameChatManagerModifiers.setAccessible(true);
            ingameChatManagerModifiers.setInt(ingameChatManagerField, ingameChatManagerField.getModifiers() & ~Modifier.FINAL);
            ingameChatManagerField.set(null, new CustomIngameChatManager());

            Field labyModApiField = LabyMod.class.getDeclaredField("labyModAPI");
            labyModApiField.setAccessible(true);
            labyModApiField.set(LabyMod.getInstance(), new CustomLabyModAPI(LabyMod.getInstance()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        neoEssentials = this;
        currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        helper = new Helper();
        settings = new Settings();
        updater = new Updater();

        getApi().getEventManager().register(new ChatSendListener());
        getApi().getEventManager().register(new ChatReceiveListener());
        getApi().getEventManager().register(new PluginMessageListener());
        getApi().registerForgeListener(new Events());
        registerEvent(new CommandListener());
    }

    @Override
    public void loadConfig() {
        updater.setAddonJar(AddonLoader.getFiles().get(about.uuid));
        settings.loadSettings();
    }

    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        this.settings.fillSettings(settings);
    }

    public void registerEvent(ClientCommandEvent listener) {
        commandListeners.add(listener);
    }

    public static NeoEssentials getNeoEssentials() {
        return neoEssentials;
    }

    public Helper getHelper() {
        return helper;
    }

    public Settings getSettings() {
        return settings;
    }

    public Updater getUpdater() {
        return updater;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public Set<ClientCommandEvent> getCommandListeners() {
        return commandListeners;
    }
}
