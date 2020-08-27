package me.dominic.neoessentials.utils;


import net.labymod.core.ChatComponent;
import net.labymod.ingamechat.tools.filter.FilterChatManager;
import net.labymod.ingamechat.tools.filter.Filters.Filter;
import net.labymod.main.LabyMod;
import net.labymod.utils.ModColor;

import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CustomFilterChatManager extends FilterChatManager {

    public static Filter getFilterComponent(ChatComponent chatComponent) {
        if (!LabyMod.getSettings().chatFilter) {
            return null;
        } else {
            String message = ModColor.removeColor(chatComponent.getUnformattedText()).toLowerCase();
            String messageJson = ModColor.removeColor(chatComponent.getJson()).toLowerCase();
            Filter foundComponent = null;
            for (Filter filter : LabyMod.getInstance().getChatToolManager().getFilters()) {
                boolean contains = false;
                String[] wordsContains = filter.getWordsContains();

                if(wordsContains[0].startsWith("/") && wordsContains[wordsContains.length-1].endsWith("/")) {
                    // RegEx
                    StringJoiner joiner = new StringJoiner(" ");
                    for(String word : wordsContains) {
                        joiner.add(word);
                    }
                    try {
                        String regex = joiner.toString();
                        Matcher m = Pattern.compile(regex.substring(1, regex.length()-1)).matcher(message);
                        contains = m.find();
                    } catch(PatternSyntaxException e) {
                        System.out.println("[NeoEssentials] Could not compile regex for chatfilter "+filter.getFilterName()+".");
                    }
                } else {
                    // Regular words
                    for (String word : wordsContains) {
                        if (message.contains(word.toLowerCase()) || filter.isFilterTooltips() && messageJson.contains(word.toLowerCase())) {
                            contains = true;
                        }
                    }

                    if (contains) {
                        String[] wordsContainsNot = filter.getWordsContainsNot();
                        for (String word : wordsContainsNot) {
                            if (!word.isEmpty()) {
                                if (message.contains(word.toLowerCase())) {
                                    contains = false;
                                    break;
                                }

                                if (filter.isFilterTooltips() && messageJson.contains(word.toLowerCase())) {
                                    contains = false;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (contains) {
                    if (foundComponent == null) {
                        foundComponent = filter.clone();
                    }

                    if (!foundComponent.isDisplayInSecondChat() && filter.isDisplayInSecondChat()) {
                        foundComponent.setDisplayInSecondChat(true);
                    }

                    if (!foundComponent.isHideMessage() && filter.isHideMessage()) {
                        foundComponent.setHideMessage(true);
                    }

                    if (!foundComponent.isPlaySound() && filter.isPlaySound()) {
                        foundComponent.setPlaySound(true);
                        foundComponent.setSoundPath(filter.getSoundPath());
                    }

                    if (!foundComponent.isHighlightMessage() && filter.isHighlightMessage()) {
                        foundComponent.setHighlightMessage(true);
                        foundComponent.setHighlightColorR(filter.getHighlightColorR());
                        foundComponent.setHighlightColorG(filter.getHighlightColorG());
                        foundComponent.setHighlightColorB(filter.getHighlightColorB());
                    }

                    foundComponent.setRoom(filter.getRoom());
                }
            }


            return foundComponent;
        }
    }
}
