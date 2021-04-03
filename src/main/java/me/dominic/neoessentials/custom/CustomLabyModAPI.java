package me.dominic.neoessentials.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dominic.neoessentials.NeoEssentials;
import me.dominic.neoessentials.settings.Settings;
import net.labymod.api.LabyModAPI;
import net.labymod.main.LabyMod;

public class CustomLabyModAPI extends LabyModAPI {

    public CustomLabyModAPI(LabyMod labyMod) {
        super(labyMod);
    }

    @Override
    public void sendJsonMessageToServer(String messageKey, JsonElement message) {
        JsonObject messageObj = message.getAsJsonObject();
        if(getSettings().isHideAddons() &&
                messageObj.get("version") != null &&
                messageObj.get("mods") != null &&
                messageObj.get("addons") != null) {

            messageObj.add("addons", new JsonArray());
            messageObj.add("mods", new JsonArray());
        }
        super.sendJsonMessageToServer(messageKey, messageObj);
    }

    private Settings getSettings() {
        return NeoEssentials.getNeoEssentials().getSettings();
    }
}
