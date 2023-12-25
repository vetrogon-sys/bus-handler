package com.example.busticketplatform.telegram;

import java.util.Set;
public final class MarkdownV2EscapeUtil {

    static final Set<Character> ESCAPE_PREDICATE = Set.of('_', '*', '[', ']', '(', ')', '~', '>', '#', '+', '-', '=', '\\', '{', '}', '.', '!');

    public static String escapeMarkdown(String message) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char ch = message.charAt(i);
            if (ESCAPE_PREDICATE.contains(ch)) {
                sb.append("\\");
            }
            sb.append(ch);
        }
        return sb.toString();
    }

}
