package me.dominic.neoessentials.settings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.dominic.neoessentials.NeoEssentials;
import me.dominic.neoessentials.enums.EnumAutoColor;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.*;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Settings {

    private ArrayList<String> autoColorIgnoreMessages = new ArrayList<>();
    private EnumAutoColor autoColor = EnumAutoColor.DEFAULT;
    private int autoBreakKey = Keyboard.KEY_B;
    private int autoUseKey = Keyboard.KEY_U;
    private int ungrabMouseKey = Keyboard.KEY_F12;
    private boolean bypassServerPermissions = false;
    private boolean antiAfkKick = false;

    public void loadSettings() {
        if(!getConfig().has("autoColorIgnoreMessages"))
            getConfig().add("autoColorIgnoreMessages", new Gson().toJsonTree(Collections.emptyList()));
        setAutoColorIgnoreMessages(new Gson().fromJson(getConfig().get("autoColorIgnoreMessages"), ArrayList.class));
        saveConfig();

        if(getConfig().has("autoColor")) {
            for(EnumAutoColor color : EnumAutoColor.values()) {
                if(color.name().equalsIgnoreCase(getConfig().get("autoColor").getAsString())) {
                    setAutoColor(color);
                }
            }
        }

        if(getConfig().has("autoBreakKey"))
            setAutoBreakKey(getConfig().get("autoBreakKey").getAsInt());

        if(getConfig().has("autoUseKey"))
            setAutoUseKey(getConfig().get("autoUseKey").getAsInt());

        if(getConfig().has("ungrabMouseKey"))
            setUngrabMouseKey(getConfig().get("ungrabMouseKey").getAsInt());

        if(getConfig().has("bypassServerPermissons"))
            setBypassServerPermissions(getConfig().get("bypassServerPermissons").getAsBoolean());

        if(getConfig().has("antiAfkKick"))
            setAntiAfkKick(getConfig().get("antiAfkKick").getAsBoolean());
    }

    public void fillSettings(List<SettingsElement> settings) {
        final DropDownMenu<EnumAutoColor> autoColorDropdownMenu = new DropDownMenu<EnumAutoColor>("Auto chat color", 0, 0, 0, 0)
                .fill(EnumAutoColor.values());
        final DropDownElement<EnumAutoColor> autoColorDropdown = new DropDownElement<EnumAutoColor>("Auto chat color", autoColorDropdownMenu);
        autoColorDropdownMenu.setSelected(autoColor);
        autoColorDropdown.setChangeListener(new Consumer<EnumAutoColor>() {
            @Override
            public void accept(EnumAutoColor autoColor) {
                setAutoColor(autoColor);
                getConfig().addProperty("autoColor", autoColor.name());
                saveConfig();
            }
        });
        settings.add(autoColorDropdown);

        final KeyElement autoBreakKeyOption = new KeyElement("Auto break",
                new ControlElement.IconData(Material.DIAMOND_PICKAXE), getAutoBreakKey(), new Consumer<Integer>() {
            @Override
            public void accept(Integer key) {
                setAutoBreakKey(key);
                getConfig().addProperty("autoBreakKey", key);
                saveConfig();
            }
        });
        settings.add(autoBreakKeyOption);

        final KeyElement autoUseKeyOption = new KeyElement("Auto use",
                new ControlElement.IconData(Material.SHEARS), getAutoUseKey(), new Consumer<Integer>() {
            @Override
            public void accept(Integer key) {
                setAutoUseKey(key);
                getConfig().addProperty("autoUseKey", key);
                saveConfig();
            }
        });
        settings.add(autoUseKeyOption);

        final KeyElement ungrabMouseKeyOption = new KeyElement("Ungrab mouse",
                new ControlElement.IconData("labymod/textures/settings/modules/clicktest.png"), getUngrabMouseKey(), new Consumer<Integer>() {
            @Override
            public void accept(Integer key) {
                setUngrabMouseKey(key);
                getConfig().addProperty("ungrabMouseKey", key);
                saveConfig();
            }
        });
        settings.add(ungrabMouseKeyOption);

        final BooleanElement bypassServerPermissionsBtn = new BooleanElement("Bypass server permissions",
                new ControlElement.IconData(Material.COMMAND), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean bypassPermissions) {
                setBypassServerPermissions(bypassPermissions);
                getConfig().addProperty("bypassServerPermissons", bypassPermissions);
                saveConfig();
            }
        }, isBypassServerPermissions());
        settings.add(bypassServerPermissionsBtn);

        settings.add(new HeaderElement("GrieferGames"));

        final BooleanElement antiAfkKickBtn = new BooleanElement("Anti AFK kick",
                new ControlElement.IconData(Material.EMERALD), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean antiAfkKick) {
                setAntiAfkKick(antiAfkKick);
                getConfig().addProperty("antiAfkKick", antiAfkKick);
                saveConfig();
            }
        }, isAntiAfkKick());
        settings.add(antiAfkKickBtn);
    }

    private JsonObject getConfig() {
        return NeoEssentials.getNeoEssentials().getConfig();
    }

    private void saveConfig() {
        NeoEssentials.getNeoEssentials().saveConfig();
    }

    public void setAutoColorIgnoreMessages(ArrayList<String> autoColorIgnoreMessages) {
        this.autoColorIgnoreMessages = autoColorIgnoreMessages;
    }
    public ArrayList<String> getAutoColorIgnoreMessages() {
        return autoColorIgnoreMessages;
    }

    public void setAutoColor(EnumAutoColor autoColor) {
        this.autoColor = autoColor;
    }
    public EnumAutoColor getAutoColor() {
        return autoColor;
    }

    public void setAutoBreakKey(int autoBreakKey) {
        this.autoBreakKey = autoBreakKey;
    }
    public int getAutoBreakKey() {
        return autoBreakKey;
    }

    public void setAutoUseKey(int autoUseKey) {
        this.autoUseKey = autoUseKey;
    }
    public int getAutoUseKey() {
        return autoUseKey;
    }

    public void setUngrabMouseKey(int ungrabMouseKey) {
        this.ungrabMouseKey = ungrabMouseKey;
    }
    public int getUngrabMouseKey() {
        return ungrabMouseKey;
    }

    public void setBypassServerPermissions(boolean bypassServerPermissions) {
        this.bypassServerPermissions = bypassServerPermissions;
    }
    public boolean isBypassServerPermissions() {
        return bypassServerPermissions;
    }

    public void setAntiAfkKick(boolean antiAfkKick) {
        this.antiAfkKick = antiAfkKick;
    }
    public boolean isAntiAfkKick() {
        return antiAfkKick;
    }
}
