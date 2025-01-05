package de.stylabs.twitchbot.ai;

import de.stylabs.twitchbot.TwitchBot;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SoundPlayer {
    public static void playSound(String path, long timestamp) {
        File file = new File(path);
        if (!file.exists()) {
            TwitchBot.getLogger().info("File not found: " + path);
            return;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            AdvancedPlayer player = new AdvancedPlayer(fis);

            player.play();
        } catch (JavaLayerException | IOException e) {
            TwitchBot.getLogger().severe(e.getMessage());
        }
    }
}