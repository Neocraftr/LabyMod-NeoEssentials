package me.dominic.neoessentials;

import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.List;


public class Main extends LabyModAddon {

    public static Main instance;
    public static String prefix = "§8[§2NeoEssentials§8] §7";

    @Override
    public void onEnable() {
        instance = this;

        Utils.init();

        getApi().getEventManager().register(new ChatSendListener());
        getApi().getEventManager().register(new ChatReceiveListener());
        getApi().registerForgeListener(new Events());

        ClientRegistry.registerKeyBinding(Utils.autoBreakKey);
        ClientRegistry.registerKeyBinding(Utils.autoUseKey);
        ClientRegistry.registerKeyBinding(Utils.ungrabMouseKey);
    }

    @Override
    public void loadConfig() {
        if(!getConfig().has("autoColor")) {
            getConfig().addProperty("autoColor", "");
            saveConfig();
        }

        Utils.autoColor = getConfig().get("autoColor").getAsString();
    }

    public void saveSettings() {
        getConfig().addProperty("autoColor", Utils.autoColor);
        saveConfig();
    }

    @Override
    protected void fillSettings(List<SettingsElement> subSettings) {}
}
