package me.dominic.neoessentials.utils;

import net.labymod.core.ChatComponent;
import net.labymod.core.LabyModCore;
import net.labymod.ingamechat.IngameChatManager;
import net.labymod.ingamechat.renderer.MessageData;
import net.labymod.ingamechat.tools.filter.Filters.Filter;
import net.labymod.main.LabyMod;
import net.labymod.servermanager.ChatDisplayAction;
import net.minecraft.util.ResourceLocation;

public class CustomIngameChatManager extends IngameChatManager {

    @Override
    public MessageData handleSwap(ChatDisplayAction chatDisplayAction, ChatComponent chatComponent) {
        Filter filter = CustomFilterChatManager.getFilterComponent(chatComponent);
        if (chatDisplayAction == ChatDisplayAction.NORMAL && filter != null && filter.isDisplayInSecondChat()) {
            chatDisplayAction = ChatDisplayAction.SWAP;
        }

        if (filter != null && filter.isPlaySound()) {
            LabyModCore.getMinecraft().playSound(new ResourceLocation(filter.getSoundPath()), 1.0F);
        }

        if (chatDisplayAction != ChatDisplayAction.HIDE && (filter == null || !filter.isHideMessage())) {
            boolean displayInSecondChat = chatDisplayAction == ChatDisplayAction.SWAP;
            if (LabyMod.getSettings().chatPositionRight) {
                displayInSecondChat = !displayInSecondChat;
            }

            return new MessageData(displayInSecondChat, filter);
        } else {
            return null;
        }
    }
}
