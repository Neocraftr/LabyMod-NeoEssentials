package me.dominic.neoessentials;

import net.labymod.api.events.MessageSendEvent;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;

public class ChatSendListener implements MessageSendEvent {
    @Override
    public boolean onSend(String msg) {
        String[] args = msg.split(" ");
        if(args.length >= 1) {
            // Rainbow
            if(args[0].equalsIgnoreCase(".rainbow")) {
                if (args.length >= 2) {
                    StringJoiner joiner = new StringJoiner(" ");
                    for(int i=1; i<args.length; i++) joiner.add(args[i]);
                    String m = joiner.toString();
                    int len = m.toCharArray().length;
                    if(len <= 33) {
                        AtomicReference<String> m1 = new AtomicReference<>("");
                        m.chars().forEach(c -> {
                            m1.updateAndGet(v -> v+"&"+Utils.rainbowColorCodes.charAt(Utils.random.nextInt(Utils.rainbowColorCodes.length()-1))+((char)c));
                        });
                        Utils.mc.thePlayer.sendChatMessage(m1.get());
                    } else Main.instance.getApi().displayMessageInChat(Main.prefix + "§cDie Nachricht darf nicht länger als 33 Zeichen sein: §7"+m.substring(0, 33)+"§4"+m.substring(33, len));
                } else Main.instance.getApi().displayMessageInChat(Main.prefix + "§cVerwendung: .rainbow <Nachricht>");
                return true;
            }
            // Auto color
            if(args[0].equalsIgnoreCase(".autocolor")) {
                if (args.length == 2) {
                    if(args[1].equalsIgnoreCase("reset")) {
                        Utils.autoColor = "";
                        Main.instance.saveSettings();
                        Main.instance.getApi().displayMessageInChat(Main.prefix + "§aChafarbe auf Standart zurückgesetzt.");
                    } else {
                        String code = args[1];
                        if(code.length() == 2 && code.startsWith("&") && Utils.colorCodes.contains(code.replace("&", ""))) {
                            Utils.autoColor = args[1];
                            Main.instance.saveSettings();
                            Main.instance.getApi().displayMessageInChat(Main.prefix + "§aChafarbe auf §7"+Utils.colorize(args[1])+args[1]+" §ageändert.");
                        } else {
                            Main.instance.getApi().displayMessageInChat(Main.prefix + "§cBitte gib einen gültigen Farbcode an.");
                        }
                    }
                } else Main.instance.getApi().displayMessageInChat(Main.prefix + "§cVerwendung: .autocolor <Farbcode>");
                return true;
            }
        }
        // Open chatlog
        if(args[0].equalsIgnoreCase(".chatlog")) {
            if(Desktop.isDesktopSupported()) {
                Main.instance.getApi().displayMessageInChat(Main.prefix+"§aÖffne Logdatei in Texteditor.");
                try {
                    Desktop.getDesktop().open(Utils.chatLogFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Main.instance.getApi().displayMessageInChat(Main.prefix+"§cAutomatisches Öffnen für dieses Betriebsystem nicht unterstützt. Bitte öffne die Logdatei in einem Dateimanager: §e"+Utils.chatLogFile.getAbsolutePath());
            }
            return true;
        }
        // Money Stats
        if(args[0].equalsIgnoreCase((".moneystats"))) {
            if(args.length == 1) {

            } else {

            }
        }
        // UUID
        if(args[0].equalsIgnoreCase(".uuid")) {
            if(args.length == 2) {
                new Thread(() ->  {
                    String uuid = Utils.getUUIDFromName(args[1]);
                    if(uuid != null) {
                        Main.instance.getApi().displayMessageInChat(Main.prefix + "§aDie UUID von §e" + args[1] + " §aist §e" + uuid + "§a.");
                    } else {
                        Main.instance.getApi().displayMessageInChat(Main.prefix+"§cEs gibt keinen Spieler mit diesem Namen.");
                    }
                }).start();
            } else {
                Main.instance.getApi().displayMessageInChat(Main.prefix+"§cVerwendung: .uuid <name>");
            }
            return true;
        }
        // Color debug log
        if(args[0].equalsIgnoreCase(".colordebug")) {
            File logFile = new File(Utils.addonDir+"/colordebug-"+Utils.date+".log");
            int counter = 1;
            while(logFile.exists()) {
                logFile = new File(Utils.addonDir+"/colordebug-"+Utils.date+"-"+counter+".log");
                counter++;
            }

            try {
                logFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
                for(int i=0; i<Utils.lastFormatedChatMessages.size(); i++) {
                    writer.write(Utils.lastFormatedChatMessages.get(i)+"\n");
                }
                writer.close();

                ChatComponentText fileLink = new ChatComponentText("§e"+logFile.getName());
                fileLink.setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, logFile.getAbsolutePath()))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§aÖffnen"))));
                Utils.mc.thePlayer.addChatComponentMessage(new ChatComponentText(Main.prefix+"§aLogdatei erstellt: ").appendSibling(fileLink));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        if(!msg.startsWith("/") && !msg.startsWith(".") && !msg.startsWith("-deletescreenshot")) {
            Utils.mc.thePlayer.sendChatMessage(Utils.autoColor+msg);
            return true;
        }

        return false;
    }
}
