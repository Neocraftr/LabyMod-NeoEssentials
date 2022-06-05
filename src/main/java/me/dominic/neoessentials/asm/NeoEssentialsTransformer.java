package me.dominic.neoessentials.asm;

import net.labymod.core.asm.LabyModCoreMod;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class NeoEssentialsTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // Classes
        final String itemStackName = LabyModCoreMod.isObfuscated() ? "zx" : "net.minecraft.item.ItemStack";
        final String entityPlayerName = LabyModCoreMod.isObfuscated() ? "wn" : "net.minecraft.entity.player.EntityPlayer";

        // Methods
        final String getTooltipName = LabyModCoreMod.isObfuscated() ? "a" : "getTooltip";

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

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }
}
