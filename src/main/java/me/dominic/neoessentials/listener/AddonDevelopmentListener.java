package me.dominic.neoessentials.listener;

import net.labymod.labyconnect.packets.PacketAddonDevelopment;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class AddonDevelopmentListener implements Consumer<PacketAddonDevelopment> {
    // Currently not in use

    @Override
    public void accept(PacketAddonDevelopment packet) {
        String key = packet.getKey();
        String senderUuid = packet.getSender().toString();

        byte[] rawData = packet.getData();
        if (rawData == null || rawData.length == 0) return;

        try {
            StringBuilder outStr = new StringBuilder();
            if (rawData[0] == 31 && rawData[1] == -117) {
                GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(rawData));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));

                String line;
                while((line = bufferedReader.readLine()) != null) {
                    outStr.append(line);
                }
            } else {
                outStr.append(Arrays.toString(rawData));
            }
            String data = outStr.toString();

            System.out.println("Addon message from "+senderUuid+": "+key+" - "+data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAddonDevelopmentPacket(UUID[] players, String key, String data) {
        if(players == null) players = new UUID[0]; // all friends

        byte[] str = data.getBytes(StandardCharsets.UTF_8);
        if (str.length == 0) return;
        try {
            ByteArrayOutputStream obj = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(obj);
            gzip.write(str);
            gzip.flush();
            gzip.close();
            byte[] sendData = obj.toByteArray();

            UUID sender = LabyMod.getInstance().getPlayerUUID();
            LabyMod.getInstance().getLabyModAPI().sendAddonDevelopmentPacket(new PacketAddonDevelopment(sender, players, key, sendData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
