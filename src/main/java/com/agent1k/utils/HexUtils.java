package com.agent1k.utils;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class HexUtils {

    private static final Pattern HEX_AMPERSAND = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern HEX_PLAIN     = Pattern.compile("(?<!&)#([A-Fa-f0-9]{6})");

    private HexUtils() {}

    @NotNull
    public static String colorize(@NotNull String text) {
        text = replace(HEX_AMPERSAND, text);
        text = replace(HEX_PLAIN, text);
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @NotNull
    public static String colorizeLore(@NotNull String text) {
        return colorize(text);
    }

    @NotNull
    public static List<String> colorize(@NotNull List<String> lines) {
        return lines.stream().map(HexUtils::colorize).collect(Collectors.toList());
    }

    @NotNull
    public static List<String> colorizeLore(@NotNull List<String> lines) {
        return lines.stream().map(HexUtils::colorizeLore).collect(Collectors.toList());
    }

    @NotNull
    private static String replace(@NotNull Pattern pattern, @NotNull String text) {
        Matcher matcher = pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}