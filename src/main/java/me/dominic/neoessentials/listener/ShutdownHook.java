package me.dominic.neoessentials.listener;

import me.dominic.neoessentials.NeoEssentials;
import net.labymod.main.LabyMod;
import net.labymod.main.update.Updater;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShutdownHook implements Runnable {
    private String minecraftDirectory;
    private Updater updater;

    public ShutdownHook() {
        try {
            minecraftDirectory = new File(Minecraft.getMinecraft().mcDataDir, "/..").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updater = LabyMod.getInstance().getUpdater();
    }

    @Override
    public void run() {
        if(getNeoEssentials().getSettings().isLabymodUpdater()) {
            if(updater.isUpdateAvailable() || updater.isForceUpdate()) {
                executeUpdater();
            } else {
                updater.getLabyModUpdateChecker().getUpdateData().thenAccept(data -> {
                    if(data.isUpdateAvailable()) {
                        executeUpdater();
                    }
                });
            }
        } else {
            if(updater.isForceUpdate()) {
                executeUpdater();
            }
        }
    }

    public void executeUpdater() {
        if(getNeoEssentials().getSettings().isFixLabymodUpdater()) {
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            List<String> arguments = new ArrayList<>();
            arguments.add(updater.isBackupMethod() ? "java" : javaBin);
            arguments.add("-Duser.home="+minecraftDirectory);
            arguments.add("-jar");
            arguments.add(new File("LabyMod/Updater.jar").getAbsolutePath());
            arguments.add("run");
            System.out.println(arguments);
            ProcessBuilder pb = new ProcessBuilder(arguments);
            pb.environment().put("APPDATA", minecraftDirectory);
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            updater.executeUpdater();
        }
    }

    public NeoEssentials getNeoEssentials() {
        return NeoEssentials.getNeoEssentials();
    }
}
