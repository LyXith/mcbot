package io.mikaple;

import io.mikaple.data.EntityData;
import io.mikaple.utils.ClientBuilder;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.network.ClientSession;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.mikaple.Settings.*;
import static io.mikaple.utils.LogUtils.*;


public class Main {

    public static ClientSession session;
    public static boolean registered = false;
    public static List<EntityData> entities = new CopyOnWriteArrayList<>();
    public static Map<UUID, GameProfile> profileMap = new ConcurrentHashMap<>();
    public static boolean mainConnected = false;
    public static int waitingTime = 3;
    static void main() {
        if (!logFolder.exists()) {
            logFolder.mkdir();
        }
        while (true) {
            if (!mainConnected) ClientBuilder.createAndRun(name,password);
            Object lock = new Object();
            synchronized (lock) {
                try {
                    lock.wait(waitingTime*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mainConnected = false;
            log.info("Bot stopped");
            logMessage("Bot stopped");
        }
    }
}
