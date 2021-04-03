package me.dominic.neoessentials.listener;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import me.dominic.neoessentials.NeoEssentials;
import net.labymod.api.events.PluginMessageEvent;
import net.labymod.support.util.Debug;
import net.minecraft.network.PacketBuffer;

public class PluginMessageListener implements PluginMessageEvent {

    @Override
    public void receiveMessage(String channel, PacketBuffer packetBuffer) {
        if(NeoEssentials.getNeoEssentials().getSettings().isEmulateMysteryMod() && channel.equals("mysterymod:mm")) {
            if(packetBuffer.readableBytes() <= 0) return;
            String messageKey = readStringFromBuffer(32767, packetBuffer);

            if(packetBuffer.readableBytes() <= 0) return;
            String message = readStringFromBuffer(32767, packetBuffer);

            Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "[IN] [MYSTERYMOD] " + messageKey + ": " + message);

            if(messageKey.equals("mysterymod_user_check")) {
                sendMysteryModMessage(message);
            }
        }
    }

    private void sendMysteryModMessage(String message) {
        PacketBuffer responseBuffer = new PacketBuffer(Unpooled.buffer());
        responseBuffer.writeString(message);
        NeoEssentials.getNeoEssentials().getApi().sendPluginMessage("mysterymod:mm", responseBuffer);

        Debug.log(Debug.EnumDebugMode.PLUGINMESSAGE, "[OUT] [MYSTERYMOD] " + message);
    }

    private String readStringFromBuffer(int maxLength, PacketBuffer packetBuffer) {
        int i = this.readVarIntFromBuffer(packetBuffer);
        if (i > maxLength * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
        } else if (i < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            ByteBuf byteBuf = packetBuffer.readBytes(i);
            byte[] bytes;
            if (byteBuf.hasArray()) {
                bytes = byteBuf.array();
            } else {
                bytes = new byte[byteBuf.readableBytes()];
                byteBuf.getBytes(byteBuf.readerIndex(), bytes);
            }

            String s = new String(bytes, Charsets.UTF_8);
            if (s.length() > maxLength) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
            } else {
                return s;
            }
        }
    }

    private int readVarIntFromBuffer(PacketBuffer packetBuffer) {
        int i = 0;
        int j = 0;

        byte b0;
        do {
            b0 = packetBuffer.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while((b0 & 128) == 128);

        return i;
    }
}
