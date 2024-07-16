package dev.sha256.advancedwarps.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static String replace(String text, String placeholder, String replacement) {
        return text.replace(placeholder, replacement);
    }

    public static List<String> replaceAll(List<String> list, String placeholder, String replacement) {
        List<String> lore = new ArrayList<>();
        for(String line : list) {
            lore.add(replace(line, placeholder, replacement));
        }
        return lore;
    }

}
