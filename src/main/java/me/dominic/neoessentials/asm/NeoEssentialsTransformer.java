package me.dominic.neoessentials.asm;

import net.labymod.core.asm.LabyModCoreMod;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.Optional;

public class NeoEssentialsTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // Classes
        final String itemStackName = LabyModCoreMod.isObfuscated() ? "zx" : "net.minecraft.item.ItemStack";
        final String entityPlayerName = LabyModCoreMod.isObfuscated() ? "wn" : "net.minecraft.entity.player.EntityPlayer";
        final String labyModApiName = "net.labymod.api.LabyModAPI";

        // Methods
        final String getTooltipName = LabyModCoreMod.isObfuscated() ? "a" : "getTooltip";
        final String sendJsonMessageToServerName = "sendJsonMessageToServer";

        // Method descriptors
        final String getTooltipDesc = LabyModCoreMod.isObfuscated() ? "(Lwn;Z)Ljava/util/List;" : "(Lnet/minecraft/entity/player/EntityPlayer;Z)Ljava/util/List;";


        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(basicClass);
        reader.accept(node, 0);

        if(name.equals(itemStackName) || transformedName.equals(itemStackName)) {
            node.methods.stream()
                    .filter(methodNode -> methodNode.name.equals(getTooltipName) && methodNode.desc.equals(getTooltipDesc))
                    .findFirst()
                    .ifPresent(methodNode -> {
                        InsnList list = new InsnList();
                        String onTooltipDesc = "(L"+itemStackName.replace(".", "/")+";L"+entityPlayerName.replace(".", "/")+";Ljava/util/List;Z)V";
                        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        list.add(new VarInsnNode(Opcodes.ALOAD, 3));
                        list.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/dominic/neoessentials/utils/BytecodeMethods", "onItemToolTip", onTooltipDesc, false));
                        methodNode.instructions.insert(methodNode.instructions.getLast().getPrevious().getPrevious(), list);
                    });
        }

        if(name.equals(labyModApiName)) {
            node.methods.stream()
                    .filter(methodNode -> methodNode.name.equals(sendJsonMessageToServerName))
                    .findFirst()
                    .ifPresent(methodNode -> {
                        Optional<AbstractInsnNode> nextNode = Arrays.stream(methodNode.instructions.toArray())
                                .filter(abstractInsnNode -> abstractInsnNode.getOpcode() == Opcodes.ALOAD)
                                .findFirst();
                        if(!nextNode.isPresent()) return;

                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "me/dominic/neoessentials/utils/BytecodeMethods", "onSendMessageToServer", "(Ljava/lang/String;Lcom/google/gson/JsonElement;)V", false));
                        methodNode.instructions.insert(nextNode.get(), list);
                    });
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }
}
