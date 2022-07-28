package de.neocraftr.neoessentials.listener;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.neocraftr.neoessentials.NeoEssentials;
import de.neocraftr.neoessentials.settings.Settings;
import net.labymod.api.events.ServerMessageEvent;
import net.labymod.api.permissions.Permissions.Permission;

import java.util.Iterator;
import java.util.Map.Entry;

public class ServerMessageListener implements ServerMessageEvent {

    @Override
    public void onServerMessage(String key, JsonElement message) {
        if (key.equals("PERMISSIONS") && message.isJsonObject()) {
            if(!getSettings().isBypassServerPermissions()) {
                getNeoEssentials().getLabyPermissionsListener().onServerMessage(key, message);
                return;
            }

            JsonObject permissionsObject = message.getAsJsonObject();

            Iterator<Entry<String, JsonElement>> iterator = permissionsObject.entrySet().iterator();
            while(iterator.hasNext()) {
                Entry<String, JsonElement> permissionEntry = iterator.next();
                Permission permission = Permission.getPermissionByName(permissionEntry.getKey());
                if(permission.isDefaultEnabled()) {
                    getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§aBypassed server permission §e"+permission.getDisplayName()+"§a.");
                    iterator.remove();
                }
            }

            getNeoEssentials().getLabyPermissionsListener().onServerMessage(key, permissionsObject);
        }
    }

    private Settings getSettings() {
        return NeoEssentials.getNeoEssentials().getSettings();
    }

    private NeoEssentials getNeoEssentials() {
        return NeoEssentials.getNeoEssentials();
    }
}
