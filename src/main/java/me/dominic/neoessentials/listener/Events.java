package me.dominic.neoessentials.listener;

import me.dominic.neoessentials.settings.Settings;
import me.dominic.neoessentials.utils.Helper;
import me.dominic.neoessentials.NeoEssentials;
import me.dominic.neoessentials.utils.Schedule;
import net.labymod.core.asm.LabyModCoreMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class Events {

    private final String getSlotMethodName = LabyModCoreMod.isObfuscated() ? "c" : "getSlotAtPosition";

    private boolean afkMenuOpen;
    private boolean dropAllButtonPressed;

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if(getSettings().getAutoBreakKey() != -1 && getHelper().isKeyDown(getSettings().getAutoBreakKey())) {
            boolean state = !getHelper().isAutoBreakActive();
            getHelper().setAutoBreakActive(state);
            KeyBinding.setKeyBindState(getMC().gameSettings.keyBindAttack.getKeyCode(), state);
        }
        if(getSettings().getAutoUseKey() != -1 && getHelper().isKeyDown(getSettings().getAutoUseKey())) {
            boolean state = !getHelper().isAutoUseActive();
            getHelper().setAutoUseActive(state);
            KeyBinding.setKeyBindState(getMC().gameSettings.keyBindUseItem.getKeyCode(), state);
        }
        if(getSettings().getUngrabMouseKey() != -1 && getHelper().isKeyDown(getSettings().getUngrabMouseKey())) {
            if(getHelper().isMouseUngrabbed()) {
                getHelper().regrabMouse();
            } else {
                getHelper().ungrabMouse();
            }
        }
        if(getSettings().getFreecamKey() != -1 && getHelper().isKeyDown(getSettings().getFreecamKey())) {
            if(getHelper().isFreecamActive()) {
                getHelper().setFreecamActive(false);
                getMC().setRenderViewEntity(getMC().thePlayer);
                getMC().theWorld.removeEntity(getHelper().getFreecamPlayer());
            } else {
                getHelper().setFreecamActive(true);
                getHelper().setFreecamPlayer(new EntityOtherPlayerMP(getMC().theWorld, getMC().thePlayer.getGameProfile()));
                getHelper().getFreecamPlayer().setPositionAndRotation(getMC().thePlayer.posX, getMC().thePlayer.posY, getMC().thePlayer.posZ, getMC().thePlayer.rotationYaw, getMC().thePlayer.rotationPitch);
                getMC().theWorld.addEntityToWorld(43958, getHelper().getFreecamPlayer());
                getMC().setRenderViewEntity(getHelper().getFreecamPlayer());
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if(e.phase == TickEvent.Phase.END) {
            Schedule.updateSchedules();

            EntityPlayerSP player = getMC().thePlayer;
            if(player == null) return;
            Container cont = player.openContainer;
            if(getSettings().isAntiAfkKick()) {
                if(cont instanceof ContainerChest) {
                    if(!afkMenuOpen) {
                        ContainerChest chest = ((ContainerChest) cont);
                        IInventory inv = chest.getLowerChestInventory();
                        if(inv.getName().equals("§cAFK?")) {
                            afkMenuOpen = true;
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    getMC().playerController.windowClick(chest.windowId, 0,0, 0, player);
                                }
                            }, 1000);
                        }
                    }
                } else {
                    afkMenuOpen = false;
                }
            }
            if(getSettings().getDropAllKey() != -1) {
                if(getHelper().isKeyDown(getSettings().getDropAllKey()) && getMC().currentScreen instanceof GuiContainer) {
                    if(!dropAllButtonPressed) {
                        try {
                            dropAllButtonPressed = true;

                            GuiContainer guiContainer = (GuiContainer) getMC().currentScreen;

                            int mouseX = Mouse.getEventX() * guiContainer.width / getMC().displayWidth;
                            int mouseY = guiContainer.height - Mouse.getEventY() * guiContainer.height / getMC().displayHeight - 1;

                            Method getSlotMethod = GuiContainer.class.getDeclaredMethod(getSlotMethodName, int.class, int.class);
                            getSlotMethod.setAccessible(true);

                            Slot slot = (Slot) getSlotMethod.invoke(guiContainer, mouseX, mouseY);
                            if(slot != null && slot.getStack() != null) {
                                ItemStack stack = slot.getStack();
                                new Thread(() -> {
                                    int slotId = 0;
                                    for(Slot slot1 : cont.inventorySlots) {
                                        if(slot1.getStack() != null
                                                && Item.getIdFromItem(slot1.getStack().getItem()) == Item.getIdFromItem(stack.getItem())
                                                && slot1.getStack().getMetadata() == stack.getMetadata()) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException interruptedException) {
                                                interruptedException.printStackTrace();
                                            }

                                            getMC().playerController.windowClick(cont.windowId, slotId, 1, 4, player);
                                        }
                                        slotId++;
                                    }
                                }).start();
                            }
                        } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
                            exception.printStackTrace();
                        }
                    }
                } else {
                    dropAllButtonPressed = false;
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

            if(getHelper().isFreecamActive()) {
                EntityOtherPlayerMP cam = getHelper().getFreecamPlayer();
                cam.rotationYaw = getMC().thePlayer.rotationYaw;
                cam.rotationYawHead = getMC().thePlayer.rotationYawHead;
                cam.rotationPitch = getMC().thePlayer.rotationPitch;

                if(getHelper().isKeyDown(getMC().gameSettings.keyBindForward.getKeyCode())) {
                    cam.setPosition(cam.posX + Math.sin(-Math.toRadians(cam.rotationYaw)), cam.posY, cam.posZ + Math.cos(-Math.toRadians(cam.rotationYaw)));
                }
                if(getHelper().isKeyDown(getMC().gameSettings.keyBindBack.getKeyCode())) {
                    cam.setPosition(cam.posX - Math.sin(-Math.toRadians(cam.rotationYaw)), cam.posY, cam.posZ - Math.cos(-Math.toRadians(cam.rotationYaw)));
                }
                if(getHelper().isKeyDown(getMC().gameSettings.keyBindRight.getKeyCode())) {
                    cam.setPosition(cam.posX + Math.sin(-Math.toRadians(cam.rotationYaw + 90)), cam.posY, cam.posZ + Math.cos(-Math.toRadians(cam.rotationYaw + 90)));
                }
                if(getHelper().isKeyDown(getMC().gameSettings.keyBindLeft.getKeyCode())) {
                    cam.setPosition(cam.posX - Math.sin(-Math.toRadians(cam.rotationYaw + 90)), cam.posY, cam.posZ - Math.cos(-Math.toRadians(cam.rotationYaw + 90)));
                }
                if(getHelper().isKeyDown(getMC().gameSettings.keyBindJump.getKeyCode())) {
                    cam.posY = cam.posY + 1;
                }
                if(getHelper().isKeyDown(getMC().gameSettings.keyBindSneak.getKeyCode())) {
                    cam.posY = cam.posY - 1;
                }
            }
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
