package me.dominic.neoessentials.utils;

import com.google.common.base.CharMatcher;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import me.dominic.neoessentials.NeoEssentials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.MouseHelper;
import org.lwjgl.input.Mouse;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Helper {

    private BufferedWriter chatLogWriter;
    private File addonDir, chatLogDir, chatLogFile;
    private boolean mouseUngrabbed;
    private boolean originalFocusPauseSetting;
    private MouseHelper oldMouseHelper;
    private ArrayList<String> lastFormatedChatMessages = new ArrayList<>();
    private boolean itemRemoverActive = false;
    private boolean autoBreakActive = false;
    private boolean autoUseActive = false;
    private boolean freecamActive = false;
    private EntityOtherPlayerMP freecamPlayer;

    public Helper() {
        setAddonDir(new File("neoessentials"));
        if(!getAddonDir().exists()) getAddonDir().mkdir();

        setChatLogDir(new File(getAddonDir()+"/chatlog"));
        if(!getChatLogDir().exists()) getChatLogDir().mkdir();
    }

    public void initChatLog() {
        if(getChatLogWriter() != null) return;

        try {
            setChatLogFile(new File(getChatLogDir()+"/"+getNeoEssentials().getCurrentDate()+".txt"));
            if(!getChatLogFile().exists()) {
                getChatLogFile().createNewFile();
            }

            setChatLogWriter(new BufferedWriter(new FileWriter(getChatLogFile(), true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logChatMessage(String msg) {
        if(getChatLogWriter() == null) return;
        final String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        try {
            getChatLogWriter().append("["+time+"] "+msg+"\n");
            getChatLogWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String colorize(String msg) {
        return msg.replace("&", "§");
    }

    public ArrayList<String> getNamesFromUUID(String uuid) {
        ArrayList<String> names = new ArrayList<>();
        try {
            uuid = CharMatcher.is('-').removeFrom(uuid);
            try (BufferedReader reader = Resources.asCharSource(new URL(String.format("https://api.mojang.com/user/profiles/%s/names", uuid)), StandardCharsets.UTF_8).openBufferedStream()) {
                JsonReader json = new JsonReader(reader);
                json.beginArray();

                while (json.hasNext()) {
                    json.beginObject();
                    while (json.hasNext()) {
                        String key = json.nextName();
                        if (key.equals("name")) {
                            names.add(json.nextString());
                        } else {
                            json.skipValue();
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

    public String getUUIDFromName(String name) {
        try {
            try (BufferedReader reader = Resources.asCharSource(new URL(String.format("https://api.mojang.com/users/profiles/minecraft/%s", name)), StandardCharsets.UTF_8).openBufferedStream()) {
                JsonObject json = new Gson().fromJson(reader, JsonObject.class);
                if(json == null || !json.has("id")) return null;
                String uuid = json.get("id").getAsString();
                return Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})").matcher(uuid).replaceAll("$1-$2-$3-$4-$5");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void ungrabMouse() {
        if(!Mouse.isGrabbed() || isMouseUngrabbed()) return;
        setOriginalFocusPauseSetting(getMC().gameSettings.pauseOnLostFocus);
        getMC().gameSettings.pauseOnLostFocus = false;
        setOldMouseHelper(getMC().mouseHelper);
        getOldMouseHelper().ungrabMouseCursor();
        getMC().inGameHasFocus = true;
        getMC().mouseHelper = new MouseHelper(){
            @Override
            public void mouseXYChange(){}
            @Override
            public void grabMouseCursor(){}
            @Override
            public void ungrabMouseCursor(){}
        };
        setMouseUngrabbed(true);
    }

    public void regrabMouse() {
        if(!isMouseUngrabbed()) return;
        getMC().gameSettings.pauseOnLostFocus = isOriginalFocusPauseSetting();
        getMC().mouseHelper = getOldMouseHelper();
        getMC().mouseHelper.grabMouseCursor();
        setMouseUngrabbed(false);
    }

    public long parseTime(String timeStr) {
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

    private Minecraft getMC() {
        return Minecraft.getMinecraft();
    }

    private NeoEssentials getNeoEssentials() {
        return NeoEssentials.getNeoEssentials();
    }

    private void setChatLogWriter(BufferedWriter chatLogWriter) {
        this.chatLogWriter = chatLogWriter;
    }
    private BufferedWriter getChatLogWriter() {
        return chatLogWriter;
    }

    public void setAddonDir(File addonDir) {
        this.addonDir = addonDir;
    }
    public File getAddonDir() {
        return addonDir;
    }

    public void setChatLogDir(File chatLogDir) {
        this.chatLogDir = chatLogDir;
    }
    public File getChatLogDir() {
        return chatLogDir;
    }

    public void setChatLogFile(File chatLogFile) {
        this.chatLogFile = chatLogFile;
    }
    public File getChatLogFile() {
        return chatLogFile;
    }

    public void setMouseUngrabbed(boolean mouseUngrabbed) {
        this.mouseUngrabbed = mouseUngrabbed;
    }
    public boolean isMouseUngrabbed() {
        return mouseUngrabbed;
    }

    public void setOriginalFocusPauseSetting(boolean originalFocusPauseSetting) {
        this.originalFocusPauseSetting = originalFocusPauseSetting;
    }
    public boolean isOriginalFocusPauseSetting() {
        return originalFocusPauseSetting;
    }

    public void setOldMouseHelper(MouseHelper oldMouseHelper) {
        this.oldMouseHelper = oldMouseHelper;
    }
    public MouseHelper getOldMouseHelper() {
        return oldMouseHelper;
    }

    public ArrayList<String> getLastFormatedChatMessages() {
        return lastFormatedChatMessages;
    }

    public void setItemRemoverActive(boolean itemRemoverActive) {
        this.itemRemoverActive = itemRemoverActive;
    }
    public boolean isItemRemoverActive() {
        return itemRemoverActive;
    }

    public void setAutoUseActive(boolean autoUseActive) {
        this.autoUseActive = autoUseActive;
    }
    public boolean isAutoUseActive() {
        return autoUseActive;
    }

    public void setAutoBreakActive(boolean autoBreakActive) {
        this.autoBreakActive = autoBreakActive;
    }
    public boolean isAutoBreakActive() {
        return autoBreakActive;
    }

    public void setFreecamActive(boolean freecamActive) {
        this.freecamActive = freecamActive;
    }
    public boolean isFreecamActive() {
        return freecamActive;
    }

    public void setFreecamPlayer(EntityOtherPlayerMP freecamPlayer) {
        this.freecamPlayer = freecamPlayer;
    }

    public EntityOtherPlayerMP getFreecamPlayer() {
        return freecamPlayer;
    }
}
