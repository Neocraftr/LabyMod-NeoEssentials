package me.dominic.neoessentials.listener;

import me.dominic.neoessentials.settings.Settings;
import me.dominic.neoessentials.utils.Helper;
import me.dominic.neoessentials.NeoEssentials;
import me.dominic.neoessentials.utils.Schedule;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.util.Timer;
import java.util.TimerTask;

public class Events {

    private boolean afkMenuOpen;

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if(getSettings().getAutoBreakKey() != -1 && Keyboard.isKeyDown(getSettings().getAutoBreakKey())) {
            boolean state = !getHelper().isAutoBreakActive();
            getHelper().setAutoBreakActive(state);
            KeyBinding.setKeyBindState(getMC().gameSettings.keyBindAttack.getKeyCode(), state);
        }
        if(getSettings().getAutoUseKey() != -1 && Keyboard.isKeyDown(getSettings().getAutoUseKey())) {
            boolean state = !getHelper().isAutoUseActive();
            getHelper().setAutoUseActive(state);
            KeyBinding.setKeyBindState(getMC().gameSettings.keyBindUseItem.getKeyCode(), state);
        }
        if(getSettings().getUngrabMouseKey() != -1 && Keyboard.isKeyDown(getSettings().getUngrabMouseKey())) {
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

            if(getSettings().isAntiAfkKick()) {
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                Container cont = player.openContainer;
                if(cont instanceof ContainerChest) {
                    if(!afkMenuOpen) {
                        ContainerChest chest = ((ContainerChest) cont);
                        IInventory inv = chest.getLowerChestInventory();
                        if(inv.getName().equals("§cAfk?")) {
                            afkMenuOpen = true;
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Minecraft.getMinecraft().playerController.windowClick(chest.windowId, 0,0, 0, player);
                                }
                            }, 1000);
                        }
                    }
                } else {
                    afkMenuOpen = false;
                }
            }

            if(getHelper().isAutoBreakActive() && !(getMC().gameSettings.keyBindAttack.isKeyDown()
                    || (getSettings().isPauseOnItemRemover() && getHelper().isItemRemoverActive()))) {
                getHelper().setAutoBreakActive(false);
            }

            if(getHelper().isAutoUseActive() && !(getMC().gameSettings.keyBindUseItem.isKeyDown()
                    || (getSettings().isPauseOnItemRemover() && getHelper().isItemRemoverActive()))) {
                getHelper().setAutoUseActive(false);
            }
        }
    }

    @SubscribeEvent
    public void onJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if(getSettings().isBypassServerPermissions()) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    LabyMod.getInstance().getServerManager().getPermissionMap().forEach((permission, enabled) -> {
                        if(permission.isDefaultEnabled() && !enabled) {
                            LabyMod.getInstance().getServerManager().getPermissionMap().remove(permission);
                            NeoEssentials.getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§aBypassed server permission §e"+permission.getDisplayName()+"§a.");
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

    private Settings getSettings() {
        return NeoEssentials.getNeoEssentials().getSettings();
    }
}
