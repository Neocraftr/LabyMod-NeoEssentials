package me.dominic.neoessentials.utils;

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
}
