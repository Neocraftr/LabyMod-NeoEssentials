package me.dominic.neoessentials;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Events {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if(Utils.autoBreakKey.isPressed()) {
            boolean state = !Utils.mc.gameSettings.keyBindAttack.isKeyDown();
            KeyBinding.setKeyBindState(Utils.mc.gameSettings.keyBindAttack.getKeyCode(), state);
        }
        if(Utils.autoUseKey.isPressed()) {
            boolean state = !Utils.mc.gameSettings.keyBindUseItem.isKeyDown();
            KeyBinding.setKeyBindState(Utils.mc.gameSettings.keyBindUseItem.getKeyCode(), state);
        }
        if(Utils.ungrabMouseKey.isPressed()) {
            if(Utils.mouseUngrabbed) {
                Utils.regrabMouse();
            } else {
                Utils.ungrabMouse();
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent e) {
        if(e.phase == TickEvent.Phase.START) {
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
}
