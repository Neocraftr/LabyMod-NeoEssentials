package me.dominic.neoessentials.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dominic.neoessentials.NeoEssentials;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BytecodeMethods {

    public static void onItemToolTip(ItemStack itemStack, EntityPlayer entityPlayer, List<String> tooltip, boolean showAdvancedItemTooltips) {
        if(NeoEssentials.getNeoEssentials() != null) {
            if(NeoEssentials.getNeoEssentials().getSettings().isShowAnvilCost()) {
                tooltip.add("§1Amboslevel: "+itemStack.getRepairCost());
            }
        }
    }

    public static void onSendMessageToServer(String messageKey, JsonElement message) {
        if(message.isJsonObject()) {
            JsonObject messageObj = message.getAsJsonObject();

            if(NeoEssentials.getNeoEssentials().getSettings().isHideAddons()
                    && messageObj.has("version")
                    && messageObj.has("addons")) {

                messageObj.add("addons", new JsonArray());
                if(messageObj.has("mods")) messageObj.add("mods", new JsonArray());
            }
        }
    }
}
