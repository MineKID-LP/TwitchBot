package de.stylabs.twitchbot.commands;

import java.util.HashMap;

public class PollManager {
    private static String question;
    private static HashMap<String, Integer> votes = new HashMap<>();

    public static boolean startPoll(String question, String[] options) {
        if(!question.isEmpty() || !question.isBlank()) return false;
        PollManager.question = question;
        for(String option : options) {
            votes.put(option, 0);
        }
        return true;
    }

    public static boolean vote(String option) {
        if(votes.containsKey(option)) {
            votes.put(option, votes.get(option) + 1);
            return true;
        }
        return false;
    }

    public static boolean hasVoted(String user) {
        return votes.containsKey(user);
    }

    public static String getQuestion() {
        return question;
    }

    public static HashMap<String, Integer> getVotes() {
        return votes;
    }

    public static void endPoll() {
        question = null;
        votes.clear();
    }
}
