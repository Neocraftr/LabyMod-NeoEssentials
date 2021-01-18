package me.dominic.neoessentials.settings;

import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.ModColor;
import net.minecraft.client.gui.GuiButton;

public class ButtonElement extends ControlElement {

    private GuiButton button;
    private Runnable callback;

    public ButtonElement(String displayName, String buttonText, ControlElement.IconData iconData, Runnable callback) {
        super(displayName, iconData);

        this.callback = callback;

        button = new GuiButton(-2, 0, 0, 50, 20, buttonText);
    }

    @Override
    public void drawDescription(int x, int y, int screenWidth) {}

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(button.enabled && button.isMouseOver()) {
            button.playPressSound(mc.getSoundHandler());
            if(callback != null) callback.run();
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int mouseButton) {}

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {}

    @Override
    public void keyTyped(char typedChar, int keyCode) {}

    @Override
    public void unfocus(int mouseX, int mouseY, int mouseButton) {}

    @Override
    public int getEntryHeight() {
        return 23;
    }

    @Override
    public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x, y, maxX, maxY, mouseX, mouseY);

        LabyModCore.getMinecraft().setButtonXPosition(button, maxX - 50 - 2);
        LabyModCore.getMinecraft().setButtonYPosition(button, y + 1);
        LabyModCore.getMinecraft().drawButton(button, mouseX, mouseY);

        LabyMod.getInstance().getDrawUtils().drawRectangle(x - 1, y, x, maxY, ModColor.toRGB(120, 120, 120, 120));
    }

    public boolean isEnabled() {
        return button.enabled;
    }

    public void setEnabled(boolean enabled) {
        button.enabled = enabled;
    }

    public void setClickCallback(Runnable callback) {
        this.callback = callback;
    }
}
