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
    private boolean hideAddons = false;
    private boolean antiAfkKick = false;
    private boolean pauseOnItemRemover = false;

    public void loadSettings() {
        if(!getConfig().has("autoColorIgnoreMessages"))
            getConfig().add("autoColorIgnoreMessages", new Gson().toJsonTree(Collections.emptyList()));
        autoColorIgnoreMessages = new Gson().fromJson(getConfig().get("autoColorIgnoreMessages"), ArrayList.class);
        saveConfig();

        if(getConfig().has("autoColor")) {
            for(EnumAutoColor color : EnumAutoColor.values()) {
                if(color.name().equalsIgnoreCase(getConfig().get("autoColor").getAsString())) {
                    autoColor = color;
                }
            }
        }

        if(getConfig().has("autoBreakKey"))
            autoBreakKey = getConfig().get("autoBreakKey").getAsInt();

        if(getConfig().has("autoUseKey"))
            autoUseKey = getConfig().get("autoUseKey").getAsInt();

        if(getConfig().has("ungrabMouseKey"))
            ungrabMouseKey = getConfig().get("ungrabMouseKey").getAsInt();

        if(getConfig().has("bypassServerPermissons"))
            bypassServerPermissions = getConfig().get("bypassServerPermissons").getAsBoolean();

        if(getConfig().has("hideAddons"))
            hideAddons = getConfig().get("hideAddons").getAsBoolean();

        if(getConfig().has("antiAfkKick"))
            antiAfkKick = getConfig().get("antiAfkKick").getAsBoolean();

        if(getConfig().has("pauseOnItemRemover"))
            pauseOnItemRemover = getConfig().get("pauseOnItemRemover").getAsBoolean();
    }

    public void fillSettings(List<SettingsElement> settings) {
        final DropDownMenu<EnumAutoColor> autoColorDropdownMenu = new DropDownMenu<EnumAutoColor>("Auto chat color", 0, 0, 0, 0)
                .fill(EnumAutoColor.values());
        final DropDownElement<EnumAutoColor> autoColorDropdown = new DropDownElement<EnumAutoColor>("Auto chat color", autoColorDropdownMenu);
        autoColorDropdownMenu.setSelected(autoColor);
        autoColorDropdown.setChangeListener(new Consumer<EnumAutoColor>() {
            @Override
            public void accept(EnumAutoColor color) {
                autoColor = color;
                getConfig().addProperty("autoColor", autoColor.name());
                saveConfig();
            }
        });
        settings.add(autoColorDropdown);

        final KeyElement autoBreakKeyOption = new KeyElement("Auto break",
                new ControlElement.IconData(Material.DIAMOND_PICKAXE), autoBreakKey, new Consumer<Integer>() {
            @Override
            public void accept(Integer key) {
                autoBreakKey = key;
                getConfig().addProperty("autoBreakKey", key);
                saveConfig();
            }
        });
        settings.add(autoBreakKeyOption);

        final KeyElement autoUseKeyOption = new KeyElement("Auto use",
                new ControlElement.IconData(Material.SHEARS), autoUseKey, new Consumer<Integer>() {
            @Override
            public void accept(Integer key) {
                autoUseKey = key;
                getConfig().addProperty("autoUseKey", key);
                saveConfig();
            }
        });
        settings.add(autoUseKeyOption);

        final KeyElement ungrabMouseKeyOption = new KeyElement("Ungrab mouse",
                new ControlElement.IconData("labymod/textures/settings/modules/clicktest.png"), ungrabMouseKey, new Consumer<Integer>() {
            @Override
            public void accept(Integer key) {
                ungrabMouseKey = key;
                getConfig().addProperty("ungrabMouseKey", key);
                saveConfig();
            }
        });
        settings.add(ungrabMouseKeyOption);

        final BooleanElement bypassServerPermissionsBtn = new BooleanElement("Bypass server permissions",
                new ControlElement.IconData(Material.COMMAND), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) {
                bypassServerPermissions = enabled;
                getConfig().addProperty("bypassServerPermissons", enabled);
                saveConfig();
            }
        }, bypassServerPermissions);
        settings.add(bypassServerPermissionsBtn);

        final BooleanElement hideAddonsBtn = new BooleanElement("Hide installed addons",
                new ControlElement.IconData(Material.BARRIER), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) {
                hideAddons = enabled;
                getConfig().addProperty("hideAddons", enabled);
                saveConfig();
            }
        }, hideAddons);
        settings.add(hideAddonsBtn);

        settings.add(new HeaderElement("GrieferGames"));

        final BooleanElement antiAfkKickBtn = new BooleanElement("Anti AFK kick",
                new ControlElement.IconData(Material.EMERALD), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) {
                antiAfkKick = enabled;
                getConfig().addProperty("antiAfkKick", enabled);
                saveConfig();
            }
        }, antiAfkKick);
        settings.add(antiAfkKickBtn);

        final BooleanElement pauseOnItemRemoverBtn = new BooleanElement("Pause use/break while ItemRemover",
                new ControlElement.IconData(Material.WATCH), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) {
                pauseOnItemRemover = enabled;
                getConfig().addProperty("pauseOnItemRemover", enabled);
                saveConfig();
            }
        }, pauseOnItemRemover);
        settings.add(pauseOnItemRemoverBtn);
    }

    private JsonObject getConfig() {
        return NeoEssentials.getNeoEssentials().getConfig();
    }

    private void saveConfig() {
        NeoEssentials.getNeoEssentials().saveConfig();
    }

    public ArrayList<String> getAutoColorIgnoreMessages() {
        return autoColorIgnoreMessages;
    }

    public EnumAutoColor getAutoColor() {
        return autoColor;
    }

    public int getAutoBreakKey() {
        return autoBreakKey;
    }

    public int getAutoUseKey() {
        return autoUseKey;
    }

    public int getUngrabMouseKey() {
        return ungrabMouseKey;
    }

    public boolean isBypassServerPermissions() {
        return bypassServerPermissions;
    }

    public boolean isHideAddons() {
        return hideAddons;
    }

    public boolean isAntiAfkKick() {
        return antiAfkKick;
    }

    public boolean isPauseOnItemRemover() {
        return pauseOnItemRemover;
    }
}
