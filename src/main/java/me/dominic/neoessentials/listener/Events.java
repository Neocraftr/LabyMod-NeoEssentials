package me.dominic.neoessentials.listener;

import me.dominic.neoessentials.utils.Helper;
import me.dominic.neoessentials.NeoEssentials;
import me.dominic.neoessentials.utils.Schedule;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.util.Timer;
import java.util.TimerTask;

public class Events {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if(Keyboard.isKeyDown(getNeoEssentials().getSettings().getAutoBreakKey())) {
            boolean state = !getMC().gameSettings.keyBindAttack.isKeyDown();
            KeyBinding.setKeyBindState(getMC().gameSettings.keyBindAttack.getKeyCode(), state);
        }
        if(Keyboard.isKeyDown(getNeoEssentials().getSettings().getAutoUseKey())) {
            boolean state = !getMC().gameSettings.keyBindUseItem.isKeyDown();
            KeyBinding.setKeyBindState(getMC().gameSettings.keyBindUseItem.getKeyCode(), state);
        }
        if(Keyboard.isKeyDown(getNeoEssentials().getSettings().getUngrabMouseKey())) {
            if(getHelper().isMouseUngrabbed()) {
                getHelper().regrabMouse();
            } else {
                getHelper().ungrabMouse();
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent e) {
        if(e.phase == TickEvent.Phase.START) {
            Schedule.updateSchedules();
            /*EntityPlayerSP player = Utils.mc.thePlayer;
            Container cont = player.openContainer;
            if(cont instanceof ContainerChest) {
                if(!Utils.afkMenuOpen) {
                    ContainerChest chest = ((ContainerChest) cont);
                    IInventory inv = chest.getLowerChestInventory();
                    if(inv.getName().equals("§cAfk?")) {
                        cont.slotClick(0, 0, 0, player);
                        System.out.println("AFK menu");
                        Utils.afkMenuOpen = true;
                    }
                }
            } else {
                Utils.afkMenuOpen = false;
            }*/
        }
    }

    @SubscribeEvent
    public void onJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if(getNeoEssentials().getSettings().isBypassServerPermissions()) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    LabyMod.getInstance().getServerManager().getPermissionMap().forEach((permission, enabled) -> {
                        if(permission.isDefaultEnabled() && !enabled) {
                            LabyMod.getInstance().getServerManager().getPermissionMap().remove(permission);
                            NeoEssentials.getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§aBypassed permission "+permission.getDisplayName()+".");
                        }
                    });
                }
            }, 1000);
        }
    }

    private Minecraft getMC() {
        return Minecraft.getMinecraft();
    }

    private NeoEssentials getNeoEssentials() {
        return NeoEssentials.getNeoEssentials();
    }

    private Helper getHelper() {
        return NeoEssentials.getNeoEssentials().getHelper();
    }
}
