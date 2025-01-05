package de.stylabs.twitchbot.util;

public class ThreadManager {
    private static final ThreadGroup threadGroup = new ThreadGroup("TwitchBot");

    public static Thread startThread(Runnable runnable) {
        Thread thread = new Thread(threadGroup, runnable);
        thread.start();
        return thread;
    }

    public static void stopThreads() {
        threadGroup.interrupt();
    }

    public static void stopThread(Thread thread) {
        thread.interrupt();
    }
}
