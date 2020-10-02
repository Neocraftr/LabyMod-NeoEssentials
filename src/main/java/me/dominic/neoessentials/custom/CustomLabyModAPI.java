package me.dominic.neoessentials.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dominic.neoessentials.NeoEssentials;
import me.dominic.neoessentials.settings.Settings;
import net.labymod.api.LabyModAPI;
import net.labymod.main.LabyMod;

import java.util.UUID;

public class CustomLabyModAPI extends LabyModAPI {

    public CustomLabyModAPI(LabyMod labyMod) {
        super(labyMod);
    }

    @Override
    public void sendJsonMessageToServer(String messageKey, JsonElement message) {
        System.out.println(message.toString());
        JsonObject messageObj = message.getAsJsonObject();
        if(getSettings().isHideAddons() &&
                messageObj.get("version") != null &&
                messageObj.get("mods") != null &&
                messageObj.get("addons") != null) {
            JsonArray addonsMsg = new JsonArray();
            JsonObject fakeAddon = new JsonObject();
            fakeAddon.addProperty("uuid", UUID.randomUUID().toString());
            fakeAddon.addProperty("name", "keine auskunft!");
            addonsMsg.add(fakeAddon);

            JsonArray modsMsg = new JsonArray();
            JsonObject fakeMod = new JsonObject();
            fakeMod.addProperty("hash", "sha256:addb0f5e7826c857d7376d1bd9bc33c0c544790a2eac96144a8af22b1298c940");
            fakeMod.addProperty("name", "keine auskunft!");
            modsMsg.add(fakeMod);

            messageObj.add("addons", addonsMsg);
            messageObj.add("mods", modsMsg);
        }
        super.sendJsonMessageToServer(messageKey, messageObj);
    }

    private Settings getSettings() {
        return NeoEssentials.getNeoEssentials().getSettings();
    }
}
