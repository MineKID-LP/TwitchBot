package de.stylabs.twitchbot.util;

public class RandomShit {
    public static String getRandomChatColor() {
        String[] colors = {"#FF0000", "#0000FF", "#008000", "#B22222", "#FF7F50", "#9ACD32", "#FF4500", "#2E8B57", "#DAA520", "#D2691E", "#5F9EA0", "#1E90FF", "#FF69B4", "#8A2BE2", "#00FF7F"};
        return colors[(int) (Math.random() * colors.length)];
    }
}
