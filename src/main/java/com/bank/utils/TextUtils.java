package com.bank.utils;

public class TextUtils {
    /**
     * Capitalize a word or sentence
     * @param str The String
     * @return String
     */
    public static String capitalize(String str) {
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
