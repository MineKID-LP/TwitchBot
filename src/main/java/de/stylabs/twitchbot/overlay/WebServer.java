package de.stylabs.twitchbot.overlay;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class WebServer {
    private static Tomcat tomcat;
    private static final File overlayDirectory = new File("overlays");
    public static void start() {
        unpack();
        tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector(); // Trigger default connector creation
        tomcat.addWebapp("", overlayDirectory.getAbsolutePath());
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
        tomcat.getServer().await();
    }

    public static void unpack() {
        // Already unpacked
        //if (overlayDirectory.exists()) return;
        overlayDirectory.mkdirs();

        // Unpack overlays
        // Get all files in the overlays resource directory
        // and write them to the overlays directory
        String resourcePath = Objects.requireNonNull(WebServer.class.getClassLoader().getResource("overlays")).getPath();
        try {
            Files.walk(new File(resourcePath).toPath()).forEach(path -> {
                File file = path.toFile();
                // create directories and copy files
                if (file.isDirectory()) {
                    new File(overlayDirectory, file.getName()).mkdir();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(new File(overlayDirectory, file.getName()))) {
                        fos.write(Files.readAllBytes(file.toPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerOverlay(Overlay overlay) {

    }

    public static void stop() {
        try {
            tomcat.stop();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    public static String boundAddress() {
        return "http://localhost:8080";
    }
}
