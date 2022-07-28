package de.neocraftr.neoessentials.settings;

import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;

import java.util.ArrayList;
import java.util.List;

public class TextElement extends SettingsElement {

    private final int FONT_HEIGHT = LabyMod.getInstance().getDrawUtils().getFontRenderer().FONT_HEIGHT;
    private List<String> rows = new ArrayList<>();
    private String textAlignment;

    public TextElement(String text, String textAlignment) {
        super(text, null);
        for(String row : text.split("\n")) {
            rows.addAll(LabyMod.getInstance().getDrawUtils().listFormattedStringToWidth("§f"+row, 200));
        }
        this.textAlignment = textAlignment;
    }

    public TextElement(String text) {
        this(text, "left");
    }

    public void init() {}

    public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x, y, maxX, maxY, mouseX, mouseY);
        int absoluteY = y + 7;
        for(int i=0; i<rows.size(); i++) {
            String element = rows.get(i);
            String colorCodes = i != 0 ? getLastColors(rows.get(i - 1)) : "";

            int xOffset;
            int textWidth = LabyMod.getInstance().getDrawUtils().getFontRenderer().getStringWidth(element);
            switch(textAlignment) {
                case "center":
                    xOffset = 200/2 - textWidth/2;
                    break;
                case "right":
                    xOffset = 200 - textWidth;
                    break;
                default:
                    xOffset = 0;
                    break;
            }

            LabyMod.getInstance().getDrawUtils().drawString(colorCodes+element, x + xOffset, absoluteY + (i * FONT_HEIGHT));
        }

    }

    public int getEntryHeight() {
        return 22 + ((rows.size() - 1) * FONT_HEIGHT);
    }

    public void drawDescription(int x, int y, int screenWidth) {}

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {}

    public void keyTyped(char typedChar, int keyCode) {}

    public void mouseRelease(int mouseX, int mouseY, int mouseButton) {}

    public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {}

    public void unfocus(int mouseX, int mouseY, int mouseButton) {}

    public void setText(String text) {
        super.setDescriptionText(text);
        rows.clear();
        for(String row : text.split("\n")) {
            rows.addAll(LabyMod.getInstance().getDrawUtils().listFormattedStringToWidth("§f"+row, 200));
        }
    }

    public void setTextAlignment(String textAlignment) {
        this.textAlignment = textAlignment;
    }

    private String getLastColors(String input) {
        String result = "";
        int length = input.length();

        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == '§' && index < length - 1) {
                char c = input.charAt(index + 1);
                if("0123456789abcdef".indexOf(c) != -1) {
                    result = "§"+c+result;
                    break;
                } else if("klmno".indexOf(c) != -1) {
                    result = "§"+c+result;
                }
            }
        }

        return result;
    }
}