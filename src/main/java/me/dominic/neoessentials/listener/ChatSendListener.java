package me.dominic.neoessentials.listener;

import me.dominic.neoessentials.utils.Helper;
import me.dominic.neoessentials.NeoEssentials;
import me.dominic.neoessentials.utils.Schedule;
import net.labymod.api.events.MessageSendEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;

public class ChatSendListener implements MessageSendEvent {

    private final String RAINBOW_COLOR_CODES = "abcdef";

    @Override
    public boolean onSend(String msg) {
        String[] args = msg.split(" ");
        if(args.length >= 1) {
            // Rainbow
            if(args[0].equalsIgnoreCase(".rainbow")) {
                if (args.length >= 2) {
                    StringJoiner joiner = new StringJoiner(" ");
                    for(int i=1; i<args.length; i++) joiner.add(args[i]);
                    String message = joiner.toString();
                    int len = message.toCharArray().length;
                    if(len <= 33) {
                        AtomicReference<String> message1 = new AtomicReference<>("");
                        message.chars().forEach(c -> {
                            message1.updateAndGet(v -> v + "&" + RAINBOW_COLOR_CODES.charAt(new Random().nextInt(RAINBOW_COLOR_CODES.length()-1))+((char)c));
                        });
                        getMC().thePlayer.sendChatMessage(message1.get());
                    } else getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX + "§cDie Nachricht darf nicht länger als 33 Zeichen sein: §7"+message.substring(0, 33)+"§4"+message.substring(33, len));
                } else getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX + "§cVerwendung: .rainbow <Nachricht>");
                return true;
            }
            // Open chatlog
            if(args[0].equalsIgnoreCase(".chatlog")) {
                if(Desktop.isDesktopSupported()) {
                    getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§aÖffne Logdatei in Texteditor.");
                    try {
                        Desktop.getDesktop().open(getHelper().getChatLogFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+
                            "§cAutomatisches Öffnen für dieses Betriebsystem nicht unterstützt. Bitte öffne die Logdatei in einem Dateimanager: §e"+
                            getHelper().getChatLogFile().getAbsolutePath());
                }
                return true;
            }
            // UUID
            if(args[0].equalsIgnoreCase(".uuid")) {
                if(args.length == 2) {
                    new Thread(() ->  {
                        String uuid = getHelper().getUUIDFromName(args[1]);
                        if(uuid != null) {
                            getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX + "§aDie UUID von §e" + args[1] + " §aist §e" + uuid + "§a.");
                        } else {
                            getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cEs gibt keinen Spieler mit diesem Namen.");
                        }
                    }).start();
                } else {
                    getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cVerwendung: .uuid <Name>");
                }
                return true;
            }
            // Color debug log
            if(args[0].equalsIgnoreCase(".colordebug")) {
                File logFile = new File(getHelper().getAddonDir()+"/colordebug-"+getNeoEssentials().getCurrentDate()+".log");
                int counter = 1;
                while(logFile.exists()) {
                    logFile = new File(getHelper().getAddonDir()+"/colordebug-"+getNeoEssentials().getCurrentDate()+"-"+counter+".log");
                    counter++;
                }

                try {
                    logFile.createNewFile();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
                    for(int i = 0; i< getHelper().getLastFormatedChatMessages().size(); i++) {
                        writer.write(getHelper().getLastFormatedChatMessages().get(i)+"\n");
                    }
                    writer.close();

                    ChatComponentText fileLink = new ChatComponentText("§e"+logFile.getName());
                    fileLink.setChatStyle(new ChatStyle()
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, logFile.getAbsolutePath()))
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§aÖffnen"))));
                    getMC().thePlayer.addChatComponentMessage(new ChatComponentText(NeoEssentials.PREFIX+"§aLogdatei erstellt: ").appendSibling(fileLink));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            // Schedules
            if(args[0].equalsIgnoreCase(".schedule")) {
                if(args.length >= 2) {
                    if(args[1].equalsIgnoreCase("create")) {
                        if(args.length >= 5) {
                            long interval = getHelper().parseTime(args[2]);
                            if(interval == -1) {
                                getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cDas Interval muss größer als Null sein.");
                            } else if(interval == -2) {
                                getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cGültige Zeiteinheiten: §es - Sekunde§8, §em - Minute§8, §eh - Stunde§8, §ed - Tag§8");
                            } else if(interval == -3) {
                                getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cUngültiges Interval. Beispiel: 10s");
                            } else {
                                StringJoiner joiner = new StringJoiner(" ");
                                for(int i=4; i<args.length; i++) {
                                    joiner.add(args[i]);
                                }
                                Schedule schedule = new Schedule(interval, Boolean.parseBoolean(args[3]), joiner.toString());
                                schedule.start();
                                getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§aNeue Aufgabe erstellt:" +
                                        "\n§eInterval: §a"+ Helper.formatTime(schedule.getInterval()) +
                                        "\n§eWiederholen: §a"+schedule.isRepeat() +
                                        "\n§eNachricht: §a"+schedule.getMessage());
                            }
                        } else {
                            getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cVerwendung: .schedule create <Verzögerung/Interval> <Wiederholen> <Nachricht/Befehl>");
                        }
                    } else if(args[1].equalsIgnoreCase("delete")) {
                        if(args.length == 3) {
                            try {
                                int scheduleNr = Integer.parseInt(args[2]);
                                if(scheduleNr > 0 && scheduleNr <= Schedule.getSchedules().size()) {
                                    Schedule.getSchedules().get(scheduleNr - 1).destroy();
                                    getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§aAufgabe §e"+scheduleNr+" §agelöscht.");
                                } else {
                                    getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                                }
                            } catch(NumberFormatException e) {
                                getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                            }
                        } else {
                            getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cVerwendung: .schedule delete <Aufgabennummer>");
                        }
                    } else if(args[1].equalsIgnoreCase("list")) {
                        if(Schedule.getSchedules().size() > 0) {
                            StringBuilder builder = new StringBuilder();
                            builder.append(NeoEssentials.PREFIX+"§aAktuelle Aufgaben:");
                            for(int i=0; i<Schedule.getSchedules().size(); i++) {
                                Schedule schedule = Schedule.getSchedules().get(i);
                                builder.append("\n§8- §a"+(i+1)+" §7| §e"+ Helper.formatTime(schedule.getInterval())+(schedule.isRepeat() ? " ∞" : "")+" "+(schedule.isRunning() ? "§aLäuft" : "§cGestoppt")+" §7>> §a"+schedule.getMessage());
                            }
                            getNeoEssentials().getApi().displayMessageInChat(builder.toString());
                        } else {
                            getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cDie Liste der Aufgaben ist leer.");
                        }
                    } else if(args[1].equalsIgnoreCase("start")) {
                        if(args.length == 3) {
                            try {
                                int scheduleNr = Integer.parseInt(args[2]);
                                if(scheduleNr > 0 && scheduleNr <= Schedule.getSchedules().size()) {
                                    if(!Schedule.getSchedules().get(scheduleNr - 1).isRunning()) {
                                        Schedule.getSchedules().get(scheduleNr - 1).start();
                                        getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§aAufgabe §e"+scheduleNr+" §agestartet.");
                                    } else {
                                        getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cAufgabe §e"+scheduleNr+" §cläuft bereits.");
                                    }
                                } else {
                                    getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                                }
                            } catch(NumberFormatException e) {
                                getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                            }
                        } else {
                            getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cVerwendung: .schedule start <Aufgabennummer>");
                        }
                    } else if(args[1].equalsIgnoreCase("stop")) {
                        if(args.length == 3) {
                            try {
                                int scheduleNr = Integer.parseInt(args[2]);
                                if(scheduleNr > 0 && scheduleNr <= Schedule.getSchedules().size()) {
                                    if(Schedule.getSchedules().get(scheduleNr - 1).isRunning()) {
                                        Schedule.getSchedules().get(scheduleNr - 1).stop();
                                        getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§aAufgabe §e"+scheduleNr+" §agestoppt.");
                                    } else {
                                        getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cAufgabe §e"+scheduleNr+" §cist bereits gestoppt.");
                                    }
                                } else {
                                    getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                                }
                            } catch(NumberFormatException e) {
                                getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                            }
                        } else {
                            getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cVerwendung: .schedule stop <Aufgabennummer>");
                        }
                    } else {
                        getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cVerwendung: .schedule <create|delete|list|start|stop> [...]");
                    }
                } else {
                    getNeoEssentials().getApi().displayMessageInChat(NeoEssentials.PREFIX+"§cVerwendung: .schedule <create|delete|list|start|stop> [...]");
                }
                return true;
            }
        }

        // auto color
        if(msg.startsWith("/")) return false;
        for(String ignoreMessage : getNeoEssentials().getSettings().getAutoColorIgnoreMessages()) {
            if(msg.startsWith(ignoreMessage)) return false;
        }

        getMC().thePlayer.sendChatMessage("&"+getNeoEssentials().getSettings().getAutoColor().getColorCode()+msg);
        return true;
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
