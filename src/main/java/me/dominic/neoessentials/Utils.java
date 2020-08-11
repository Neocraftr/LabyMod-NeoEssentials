package me.dominic.neoessentials;

import com.google.common.base.CharMatcher;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MouseHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Utils {
    public static Minecraft mc;
    public static Gson gson;
    public static Random random;
    public static BufferedWriter chatLogWriter;
    public static String date;
    public static String autoColor = "";
    public static String rainbowColorCodes = "abcdef";
    public static String colorCodes = "0123456789abcdef";
    public static File addonDir, chatLogDir, chatLogFile;
    public static KeyBinding autoBreakKey, autoUseKey, ungrabMouseKey;
    public static boolean mouseUngrabbed;
    public static boolean originalFocusPauseSetting;
    public static boolean afkMenuOpen;
    public static double moneyStats;
    public static MouseHelper oldMouseHelper;
    public static ArrayList<String> lastFormatedChatMessages = new ArrayList<>();

    public static String colorize(String msg) {
        return msg.replace("&", "§");
    }

    public static void init() {
        mc = Minecraft.getMinecraft();
        gson = new Gson();
        random = new Random();
        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        autoBreakKey = new KeyBinding("Auto break", Keyboard.KEY_B, "NeoEssentials");
        autoUseKey = new KeyBinding("Auto use", Keyboard.KEY_U, "NeoEssentials");
        ungrabMouseKey = new KeyBinding("Ungrab mouse", Keyboard.KEY_F12, "NeoEssentials");

        addonDir = new File("neoessentials");
        if(!addonDir.exists()) addonDir.mkdir();

        chatLogDir = new File(addonDir+"/chatlog");
        if(!chatLogDir.exists()) chatLogDir.mkdir();

        chatLogFile = new File(chatLogDir+"/"+date+".txt");
        if(!chatLogFile.exists()) {
            try {
                chatLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            chatLogWriter = new BufferedWriter(new FileWriter(chatLogFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logChatMessage(String msg) {
        final String time = new SimpleDateFormat("mm:HH:ss").format(new Date());
        try {
            chatLogWriter.append("["+time+"] "+msg+"\n");
            chatLogWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateMoneyStats(int value) {
        moneyStats += value;
        Main.instance.getConfig().getAsJsonObject("moneystats").addProperty(date, moneyStats);
        Main.instance.saveConfig();
    }

    public static ArrayList<String> getNamesFromUUID(String uuid) {
        ArrayList<String> names = new ArrayList<>();
        try {
            uuid = CharMatcher.is('-').removeFrom(uuid);
            try (BufferedReader reader = Resources.asCharSource(new URL(String.format("https://api.mojang.com/user/profiles/%s/names", uuid)), StandardCharsets.UTF_8).openBufferedStream()) {
                JsonReader json = new JsonReader(reader);
                json.beginArray();

                String name = null;
                long when = 0;

                while (json.hasNext()) {
                    json.beginObject();
                    while (json.hasNext()) {
                        String key = json.nextName();
                        switch (key) {
                            case "name":
                                names.add(json.nextString());
                                break;
                            default:
                                json.skipValue();
                                break;
                        }
                    }
                    json.endObject();
                }

                json.endArray();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(names);
        return names;
    }

    public static String getUUIDFromName(String name) {
        try {
            try (BufferedReader reader = Resources.asCharSource(new URL(String.format("https://api.mojang.com/users/profiles/minecraft/%s", name)), StandardCharsets.UTF_8).openBufferedStream()) {
                System.out.println(reader);
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                if(json == null || !json.has("id")) return null;
                String uuid = json.get("id").getAsString();
                return Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})").matcher(uuid).replaceAll("$1-$2-$3-$4-$5");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void ungrabMouse() {
        if(!Mouse.isGrabbed() || mouseUngrabbed) return;
        originalFocusPauseSetting = mc.gameSettings.pauseOnLostFocus;
        mc.gameSettings.pauseOnLostFocus = false;
        oldMouseHelper = mc.mouseHelper;
        oldMouseHelper.ungrabMouseCursor();
        mc.inGameHasFocus = true;
        mc.mouseHelper = new MouseHelper(){
            @Override
            public void mouseXYChange(){}
            @Override
            public void grabMouseCursor(){}
            @Override
            public void ungrabMouseCursor(){}
        };
        mouseUngrabbed = true;
    }

    public static void regrabMouse() {
        if(!mouseUngrabbed) return;
        mc.gameSettings.pauseOnLostFocus = originalFocusPauseSetting;
        mc.mouseHelper = oldMouseHelper;
        mc.mouseHelper.grabMouseCursor();
        mouseUngrabbed = false;
    }

    public static long parseTime(String timeStr) {
        try {
            String format = timeStr.substring(timeStr.length() - 1);
            long duration = Long.parseLong(timeStr.substring(0, timeStr.length() - 1));
            if(duration < 0) return -1; // lower or equal 0
            switch (format) {
                case "s":
                    duration = duration * 1000;
                    break;
                case "m":
                    duration = duration * 1000 * 60;
                    break;
                case "h":
                    duration = duration * 1000 * 60 * 60;
                    break;
                case "d":
                    duration = duration * 1000 * 60 * 60 * 24;
                    break;
                default:
                    return -2; // unknown format
            }
            return duration;
        } catch (NumberFormatException e) {
            return -3; // no number
        }
    }

    public static String formatTime(long time) {
        long seconds = time / 1000;
        if(seconds < 60) {
            // seconds
            return String.format("%ds",
                    TimeUnit.MILLISECONDS.toSeconds(time));
        } else if(seconds < 3600) {
            // minutes
            return String.format("%dmin %ds",
                    TimeUnit.MILLISECONDS.toMinutes(time),
                    TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1));
        } else if(seconds < 86400) {
            // hours
            return String.format("%dh %dmin",
                    TimeUnit.MILLISECONDS.toHours(time),
                    TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1));
        } else {
            // days
            return String.format("%dd %dh",
                    TimeUnit.MILLISECONDS.toDays(time),
                    TimeUnit.MILLISECONDS.toHours(time) % TimeUnit.DAYS.toHours(1));
        }
    }
}
