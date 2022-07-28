package de.neocraftr.neoessentials.enums;

public enum EnumAutoColor {
    DEFAULT(""),
    BLACK("0"),
    DARK_BLUE("1"),
    Dark_GREEN("2"),
    DARK_AQUA("3"),
    DARK_RED("4"),
    DARK_PURPLE("5"),
    GOLD("6"),
    GRAY("7"),
    DARK_GREY("8"),
    BLUE("9"),
    GREEN("a"),
    AQUA("b"),
    RED("c"),
    LIGHT_PURPLE("d"),
    YELLOW("e"),
    WHITE("f");

    private String colorCode;

    EnumAutoColor(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getColorCode() {
        return colorCode;
    }
}
