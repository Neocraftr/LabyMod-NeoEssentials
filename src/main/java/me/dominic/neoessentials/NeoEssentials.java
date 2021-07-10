package me.dominic.neoessentials;

import me.dominic.neoessentials.listener.*;
import me.dominic.neoessentials.settings.Settings;
import me.dominic.neoessentials.custom.CustomIngameChatManager;
import me.dominic.neoessentials.custom.CustomLabyModAPI;
import me.dominic.neoessentials.utils.Helper;
import me.dominic.neoessentials.utils.Updater;
import net.labymod.addon.AddonLoader;
import net.labymod.api.EventManager;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.ServerMessageEvent;
import net.labymod.api.permissions.PermissionsListener;
import net.labymod.ingamechat.IngameChatManager;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;


public class NeoEssentials extends LabyModAddon {

    public static final String PREFIX = "§8[§2NeoEssentials§8] §7",
                               COMMAND_PREFIX = ".",
                               VERSION = "1.4.0";
    
    private static NeoEssentials neoEssentials;
    private Helper helper;
    private Settings settings;
    private String currentDate;
    private Updater updater;
    private Set<ClientCommandEvent> commandListeners = new HashSet<>();
    private ServerMessageEvent labyPermissionsListener;

    @Override
    public void onEnable() {
        neoEssentials = this;
        currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        helper = new Helper();
        settings = new Settings();
        updater = new Updater();

        getApi().getEventManager().register(new ChatSendListener());
        getApi().getEventManager().register(new ChatReceiveListener());
        getApi().getEventManager().register(new PluginMessageListener());
        getApi().getEventManager().register(new ServerMessageListener());
        getApi().getEventManager().registerShutdownHook(new ShutdownHook());
        //getApi().getEventManager().registerOnAddonDevelopmentPacket(new AddonDevelopmentListener());
        getApi().registerForgeListener(new Events());
        registerEvent(new CommandListener());

        try {
            // RegEx chatfilters
            Field ingameChatManagerField = IngameChatManager.class.getDeclaredField("INSTANCE");
            Field ingameChatManagerModifiers = Field.class.getDeclaredField("modifiers");
            ingameChatManagerModifiers.setAccessible(true);
            ingameChatManagerModifiers.setInt(ingameChatManagerField, ingameChatManagerField.getModifiers() & ~Modifier.FINAL);
            ingameChatManagerField.set(null, new CustomIngameChatManager());

            // Hide installed addons
            Field labyModApiField = LabyMod.class.getDeclaredField("labyModAPI");
            labyModApiField.setAccessible(true);
            labyModApiField.set(LabyMod.getInstance(), new CustomLabyModAPI(LabyMod.getInstance()));

            // Bypass server permissions
            Field serverMessageListenerField = EventManager.class.getDeclaredField("serverMessage");
            serverMessageListenerField.setAccessible(true);
            Set<ServerMessageEvent> serverMessageListeners = (Set<ServerMessageEvent>) serverMessageListenerField.get(getApi().getEventManager());

            Iterator<ServerMessageEvent> iterator = serverMessageListeners.iterator();
            while(iterator.hasNext()) {
                ServerMessageEvent listener = iterator.next();
                if(listener instanceof PermissionsListener) {
                    labyPermissionsListener = listener;
                    iterator.remove();
                }
            }

            // Disable default LabyMod updater
            Field shutdownHookField = EventManager.class.getDeclaredField("shutdownHook");
            shutdownHookField.setAccessible(true);
            Set<Runnable> shutdownHooks = (Set<Runnable>) shutdownHookField.get(getNeoEssentials().getApi().getEventManager());
            shutdownHooks.removeIf(shutdownHook -> shutdownHook.getClass().getName().startsWith("net.labymod.main.update.Updater"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
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

    public ServerMessageEvent getLabyPermissionsListener() {
        return labyPermissionsListener;
    }
}
