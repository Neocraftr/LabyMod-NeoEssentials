package me.dominic.neoessentials;

import net.labymod.api.events.MessageSendEvent;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        if(args[0].equalsIgnoreCase(".moneystats")) {
            //Utils.updateMoneyStats(10);
            //Main.instance.getApi().displayMessageInChat(String.valueOf(Utils.moneyStats));
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
                Main.instance.getApi().displayMessageInChat(Main.prefix+"§cVerwendung: .uuid <Name>");
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
        // Schedules
        if(args[0].equalsIgnoreCase(".schedule")) {
            if(args.length >= 2) {
                if(args[1].equalsIgnoreCase("create")) {
                    if(args.length >= 5) {
                        long interval = Utils.parseTime(args[2]);
                        if(interval == -1) {
                            Main.instance.getApi().displayMessageInChat(Main.prefix+"§cDas Interval muss größer als Null sein.");
                        } else if(interval == -2) {
                            Main.instance.getApi().displayMessageInChat(Main.prefix+"§cGültige Zeiteinheiten: §es - Sekunde§8, §em - Minute§8, §eh - Stunde§8, §ed - Tag§8");
                        } else if(interval == -3) {
                            Main.instance.getApi().displayMessageInChat(Main.prefix+"§cUngültiges Interval. Beispiel: 10s");
                        } else {
                            StringJoiner joiner = new StringJoiner(" ");
                            for(int i=4; i<args.length; i++) {
                                joiner.add(args[i]);
                            }
                            Schedule schedule = new Schedule(interval, Boolean.parseBoolean(args[3]), joiner.toString());
                            schedule.start();
                            Main.instance.getApi().displayMessageInChat(Main.prefix+"§aNeue Aufgabe erstellt:" +
                                    "\n§eInterval: §a"+Utils.formatTime(schedule.getInterval()) +
                                    "\n§eWiederholen: §a"+schedule.isRepeat() +
                                    "\n§eNachricht: §a"+schedule.getMessage());
                        }
                    } else {
                        Main.instance.getApi().displayMessageInChat(Main.prefix+"§cVerwendung: .schedule create <Verzögerung/Interval> <Wiederholen> <Nachricht/Befehl>");
                    }
                } else if(args[1].equalsIgnoreCase("delete")) {
                    if(args.length == 3) {
                        try {
                            int scheduleNr = Integer.parseInt(args[2]);
                            if(scheduleNr > 0 && scheduleNr <= Schedule.getSchedules().size()) {
                                Schedule.getSchedules().get(scheduleNr - 1).destroy();
                                Main.instance.getApi().displayMessageInChat(Main.prefix+"§aAufgabe §e"+scheduleNr+" §agelöscht.");
                            } else {
                                Main.instance.getApi().displayMessageInChat(Main.prefix+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                            }
                        } catch(NumberFormatException e) {
                            Main.instance.getApi().displayMessageInChat(Main.prefix+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                        }
                    } else {
                        Main.instance.getApi().displayMessageInChat(Main.prefix+"§cVerwendung: .schedule delete <Aufgabennummer>");
                    }
                } else if(args[1].equalsIgnoreCase("list")) {
                    if(Schedule.getSchedules().size() > 0) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(Main.prefix+"§aAktuelle Aufgaben:");
                        for(int i=0; i<Schedule.getSchedules().size(); i++) {
                            Schedule schedule = Schedule.getSchedules().get(i);
                            builder.append("\n§8- §a"+(i+1)+" §7| §e"+Utils.formatTime(schedule.getInterval())+(schedule.isRepeat() ? " ∞" : "")+" "+(schedule.isRunning() ? "§aLäuft" : "§cGestoppt")+" §7>> §a"+schedule.getMessage());
                        }
                        Main.instance.getApi().displayMessageInChat(builder.toString());
                    } else {
                        Main.instance.getApi().displayMessageInChat(Main.prefix+"§cDie Liste der Aufgaben ist leer.");
                    }
                } else if(args[1].equalsIgnoreCase("start")) {
                    if(args.length == 3) {
                        try {
                            int scheduleNr = Integer.parseInt(args[2]);
                            if(scheduleNr > 0 && scheduleNr <= Schedule.getSchedules().size()) {
                                if(!Schedule.getSchedules().get(scheduleNr - 1).isRunning()) {
                                    Schedule.getSchedules().get(scheduleNr - 1).start();
                                    Main.instance.getApi().displayMessageInChat(Main.prefix+"§aAufgabe §e"+scheduleNr+" §agestartet.");
                                } else {
                                    Main.instance.getApi().displayMessageInChat(Main.prefix+"§cAufgabe §e"+scheduleNr+" §cläuft bereits.");
                                }
                            } else {
                                Main.instance.getApi().displayMessageInChat(Main.prefix+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                            }
                        } catch(NumberFormatException e) {
                            Main.instance.getApi().displayMessageInChat(Main.prefix+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                        }
                    } else {
                        Main.instance.getApi().displayMessageInChat(Main.prefix+"§cVerwendung: .schedule start <Aufgabennummer>");
                    }
                } else if(args[1].equalsIgnoreCase("stop")) {
                    if(args.length == 3) {
                        try {
                            int scheduleNr = Integer.parseInt(args[2]);
                            if(scheduleNr > 0 && scheduleNr <= Schedule.getSchedules().size()) {
                                if(Schedule.getSchedules().get(scheduleNr - 1).isRunning()) {
                                    Schedule.getSchedules().get(scheduleNr - 1).stop();
                                    Main.instance.getApi().displayMessageInChat(Main.prefix+"§aAufgabe §e"+scheduleNr+" §agestoppt.");
                                } else {
                                    Main.instance.getApi().displayMessageInChat(Main.prefix+"§cAufgabe §e"+scheduleNr+" §cist bereits gestoppt.");
                                }
                            } else {
                                Main.instance.getApi().displayMessageInChat(Main.prefix+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                            }
                        } catch(NumberFormatException e) {
                            Main.instance.getApi().displayMessageInChat(Main.prefix+"§cKeine Aufgabe mit dieser Nummer gefunden.");
                        }
                    } else {
                        Main.instance.getApi().displayMessageInChat(Main.prefix+"§cVerwendung: .schedule stop <Aufgabennummer>");
                    }
                } else {
                    Main.instance.getApi().displayMessageInChat(Main.prefix+"§cVerwendung: .schedule <create|delete|list|start|stop> [...]");
                }
            } else {
                Main.instance.getApi().displayMessageInChat(Main.prefix+"§cVerwendung: .schedule <create|delete|list|start|stop> [...]");
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
