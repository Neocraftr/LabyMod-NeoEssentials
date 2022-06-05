package me.dominic.neoessentials.settings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.dominic.neoessentials.NeoEssentials;
import me.dominic.neoessentials.enums.EnumAutoColor;
import me.dominic.neoessentials.utils.Helper;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.*;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Settings {

    private TextElement infoText;

    private ArrayList<String> autoColorIgnoreMessages = new ArrayList<>();
    private EnumAutoColor autoColor = EnumAutoColor.DEFAULT;
    private int autoBreakKey = Keyboard.KEY_B;
    private int autoUseKey = Keyboard.KEY_U;
    private int ungrabMouseKey = Keyboard.KEY_F12;
    private int dropAllKey = Keyboard.KEY_A;
    private int freecamKey = Keyboard.KEY_V;
    private boolean logChat = true;
    private boolean bypassServerPermissions = false;
    private boolean hideAddons = false;
    private boolean antiAfkKick = false;
    private boolean pauseOnItemRemover = false;
    private boolean autoUpdateAddon = true;
    private boolean emulateMysteryMod = true;
    private boolean labymodUpdater = true;
    public boolean fixLabymodUpdater = false;
    public boolean showAnvilCost = true;

    public void loadSettings() {
        // TODO: Add graphical settings
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

        if(getConfig().has("dropAllKey"))
            dropAllKey = getConfig().get("dropAllKey").getAsInt();

        if(getConfig().has("freecamKey"))
            freecamKey = getConfig().get("freecamKey").getAsInt();

        if(getConfig().has("logChat"))
            logChat = getConfig().get("logChat").getAsBoolean();

        if(getConfig().has("bypassServerPermissons"))
            bypassServerPermissions = getConfig().get("bypassServerPermissons").getAsBoolean();

        if(getConfig().has("hideAddons"))
            hideAddons = getConfig().get("hideAddons").getAsBoolean();

        if(getConfig().has("antiAfkKick"))
            antiAfkKick = getConfig().get("antiAfkKick").getAsBoolean();

        if(getConfig().has("pauseOnItemRemover"))
            pauseOnItemRemover = getConfig().get("pauseOnItemRemover").getAsBoolean();

        if(getConfig().has("autoUpdateAddon"))
            autoUpdateAddon = getConfig().get("autoUpdateAddon").getAsBoolean();

        if(getConfig().has("emulateMysteryMod"))
            emulateMysteryMod = getConfig().get("emulateMysteryMod").getAsBoolean();

        if(getConfig().has("labymodUpdater"))
            labymodUpdater = getConfig().get("labymodUpdater").getAsBoolean();

        if(getConfig().has("fixLabymodUpdater"))
            fixLabymodUpdater = getConfig().get("fixLabymodUpdater").getAsBoolean();

        if(getConfig().has("showAnvilCost"))
            showAnvilCost = getConfig().get("showAnvilCost").getAsBoolean();

        if(logChat) getHelper().initChatLog();
    }

    public void fillSettings(List<SettingsElement> settings) {
        final BooleanElement autoUpdateAddonBtn = new BooleanElement("Addon aktualisieren", new ControlElement.IconData("labymod/textures/settings/settings/serverlistliveview.png"), enabled -> {
            autoUpdateAddon = enabled;
            updateInfoText();
            getConfig().addProperty("autoUpdateAddon", enabled);
            saveConfig();
        }, autoUpdateAddon);
        autoUpdateAddonBtn.setDescriptionText("Addon beim beenden automatisch aktualisieren");
        settings.add(autoUpdateAddonBtn);

        final BooleanElement labymodUpdaterBtn = new BooleanElement("LabyMod Updates", new ControlElement.IconData("labymod/textures/buttons/update.png"), enabled -> {
            labymodUpdater = enabled;
            getConfig().addProperty("labymodUpdater", enabled);
            saveConfig();
        }, labymodUpdater);
        labymodUpdaterBtn.setDescriptionText("Hiermit können die automatischen LabyMod Updates deaktiviert werden (Addons aus dem Store inbegriffen)");
        settings.add(labymodUpdaterBtn);

        final BooleanElement fixLabymodUpdaterBtn = new BooleanElement("LabyMod Updater MultiMC unterstützung", new ControlElement.IconData("labymod/textures/buttons/update.png"), enabled -> {
            fixLabymodUpdater = enabled;
            getConfig().addProperty("fixLabymodUpdater", enabled);
            saveConfig();
        }, fixLabymodUpdater);
        fixLabymodUpdaterBtn.setDescriptionText("Behebt einen Fehler im LabyMod Updater der dafür sorgt, dass Updates mit MultiMC fehlschlagen (Mit MacOS nicht getestet!)");
        settings.add(fixLabymodUpdaterBtn);

        final DropDownMenu<EnumAutoColor> autoColorDropdownMenu = new DropDownMenu<EnumAutoColor>("Chat Farbe", 0, 0, 0, 0)
                .fill(EnumAutoColor.values());
        final DropDownElement<EnumAutoColor> autoColorDropdown = new DropDownElement<EnumAutoColor>("Chat Farbe", autoColorDropdownMenu);
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

        final KeyElement autoBreakKeyOption = new KeyElement("Automatisch Abbauen",
                new ControlElement.IconData(Material.DIAMOND_PICKAXE), autoBreakKey, new Consumer<Integer>() {
            @Override
            public void accept(Integer key) {
                autoBreakKey = key;
                getConfig().addProperty("autoBreakKey", key);
                saveConfig();
            }
        });
        autoBreakKeyOption.setDescriptionText("Linke Maustaste feststellen");
        settings.add(autoBreakKeyOption);

        final KeyElement autoUseKeyOption = new KeyElement("Automatisch Interagieren",
                new ControlElement.IconData(Material.SHEARS), autoUseKey, new Consumer<Integer>() {
            @Override
            public void accept(Integer key) {
                autoUseKey = key;
                getConfig().addProperty("autoUseKey", key);
                saveConfig();
            }
        });
        autoUseKeyOption.setDescriptionText("Rechte maustaste feststellen");
        settings.add(autoUseKeyOption);

        final KeyElement ungrabMouseKeyOption = new KeyElement("Maus freigeben",
                new ControlElement.IconData("labymod/textures/settings/modules/clicktest.png"), ungrabMouseKey, new Consumer<Integer>() {
            @Override
            public void accept(Integer key) {
                ungrabMouseKey = key;
                getConfig().addProperty("ungrabMouseKey", key);
                saveConfig();
            }
        });
        ungrabMouseKeyOption.setDescriptionText("Mauszeiger freigeben ohne das Spiel zu pausieren");
        settings.add(ungrabMouseKeyOption);

        final KeyElement dropAllOptions = new KeyElement("Alle Items droppen",
                new ControlElement.IconData("labymod/textures/settings/default/item_gravity.png"), dropAllKey, new Consumer<Integer>() {
            @Override
            public void accept(Integer key) {
                dropAllKey = key;
                getConfig().addProperty("dropAllKey", key);
                saveConfig();
            }
        });
        dropAllOptions.setDescriptionText("Im Inventar alle Items des gewählten Types droppen (Taste gillt nur im Inventar)");
        settings.add(dropAllOptions);

        final KeyElement freecamKeyOption = new KeyElement("Freecam",
                new ControlElement.IconData("labymod/textures/settings/settings/discordallowspectating.png"), freecamKey, new Consumer<Integer>() {
            @Override
            public void accept(Integer key) {
                freecamKey = key;
                getConfig().addProperty("freecamKey", key);
                saveConfig();
            }
        });
        freecamKeyOption.setDescriptionText("Freecam aktivieren / deaktivieren");
        settings.add(freecamKeyOption);

        final BooleanElement logChatBtn = new BooleanElement("Chatverlauf speichern",
                new ControlElement.IconData("labymod/textures/settings/settings/second_chat.png"), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) {
                logChat = enabled;
                if(enabled) getHelper().initChatLog();
                getConfig().addProperty("logChat", enabled);
                saveConfig();
            }
        }, logChat);
        logChatBtn.setDescriptionText("Alles Chatnachrichten in Datei speichern");
        settings.add(logChatBtn);

        final ButtonElement openLogDirBtn = new ButtonElement("Chatverlauf Ordner", "Öffnen",
                new ControlElement.IconData("labymod/textures/settings/settings/second_chat.png"), new Runnable() {
            @Override
            public void run() {
                try {
                    Desktop.getDesktop().open(getHelper().getChatLogDir());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        openLogDirBtn.setDescriptionText("Ordner mit Chatverläufen anzeigen");
        settings.add(openLogDirBtn);

        final BooleanElement showAnvilCostBtn = new BooleanElement("Amboslevel anzeigen",
                new ControlElement.IconData(Material.EXP_BOTTLE), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) {
                showAnvilCost = enabled;
                getConfig().addProperty("showAnvilCost", enabled);
                saveConfig();
            }
        }, showAnvilCost);
        showAnvilCostBtn.setDescriptionText("Zeigt die für die nächste Reperatur im Ambos benötigten Level an");
        settings.add(showAnvilCostBtn);

        final BooleanElement bypassServerPermissionsBtn = new BooleanElement("Server Rechte umgehen",
                new ControlElement.IconData(Material.COMMAND), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) {
                bypassServerPermissions = enabled;
                getConfig().addProperty("bypassServerPermissons", enabled);
                saveConfig();
            }
        }, bypassServerPermissions);
        bypassServerPermissionsBtn.setDescriptionText("Unterbinden, dass Server bestimmte LabyMod Funktionen deaktivieren können");
        settings.add(bypassServerPermissionsBtn);

        final BooleanElement hideAddonsBtn = new BooleanElement("Installierte Mods verbergen",
                new ControlElement.IconData(Material.BARRIER), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) {
                hideAddons = enabled;
                getConfig().addProperty("hideAddons", enabled);
                saveConfig();
            }
        }, hideAddons);
        hideAddonsBtn.setDescriptionText("Installierte Mods/LabyMod Addons nicht an den Server übermitteln");
        settings.add(hideAddonsBtn);

        settings.add(new HeaderElement("GrieferGames"));

        final BooleanElement antiAfkKickBtn = new BooleanElement("Anti AFK",
                new ControlElement.IconData(Material.EMERALD), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) {
                antiAfkKick = enabled;
                getConfig().addProperty("antiAfkKick", enabled);
                saveConfig();
            }
        }, antiAfkKick);
        antiAfkKickBtn.setDescriptionText("AFk Menü automatisch bestätigen");
        settings.add(antiAfkKickBtn);

        final BooleanElement pauseOnItemRemoverBtn = new BooleanElement("Pause bei ItemRemover",
                new ControlElement.IconData(Material.WATCH), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) {
                pauseOnItemRemover = enabled;
                getConfig().addProperty("pauseOnItemRemover", enabled);
                saveConfig();
            }
        }, pauseOnItemRemover);
        pauseOnItemRemoverBtn.setDescriptionText("Automatisches Abbauen/Interagieren bei ItemRemover pausieren");
        settings.add(pauseOnItemRemoverBtn);

        final BooleanElement emulateMysteryModBtn = new BooleanElement("Als MysteryMod ausgeben",
                new ControlElement.IconData("labymod/textures/settings/settings/particlefix.png"), new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enabled) {
                emulateMysteryMod = enabled;
                getConfig().addProperty("emulateMysteryMod", enabled);
                saveConfig();
            }
        }, emulateMysteryMod);
        emulateMysteryModBtn.setDescriptionText("Gibt vor MysteryMod zu verwenden (zusätzliche MoneyDrops)");
        settings.add(emulateMysteryModBtn);

        infoText = new TextElement("");
        updateInfoText();
        settings.add(infoText);
    }

    private Helper getHelper() {
        return NeoEssentials.getNeoEssentials().getHelper();
    }

    private JsonObject getConfig() {
        return NeoEssentials.getNeoEssentials().getConfig();
    }

    private void saveConfig() {
        NeoEssentials.getNeoEssentials().saveConfig();
    }

    private void updateInfoText() {
        String text = "§7GitHub: §ahttps://github.com/Neocraftr/LabyMod-NeoEssentials/\n";
        text += "§7Version: §a"+ NeoEssentials.VERSION;
        if(NeoEssentials.getNeoEssentials().getUpdater().isUpdatePending()) {
            text += " §c(Update ausstehend. Neustart erforderlich)";
        } else if(NeoEssentials.getNeoEssentials().getUpdater().isUpdateAvailable()) {
            text += " §c(Update verfügbar)";
        }
        infoText.setText(text);
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

    public int getFreecamKey() {
        return freecamKey;
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

    public boolean isLogChat() {
        return logChat;
    }

    public boolean isAutoUpdateAddon() {
        return autoUpdateAddon;
    }

    public int getDropAllKey() {
        return dropAllKey;
    }

    public boolean isEmulateMysteryMod() {
        return emulateMysteryMod;
    }

    public boolean isLabymodUpdater() {
        return labymodUpdater;
    }

    public boolean isFixLabymodUpdater() {
        return fixLabymodUpdater;
    }

    public boolean isShowAnvilCost() {
        return showAnvilCost;
    }
}
