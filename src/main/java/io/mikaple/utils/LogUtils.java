package io.mikaple.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LogUtils {
    private static FileWriter fw;
    private static String curDate;
    public synchronized static void logMessage(String log) {
        boolean shouldRefresh = false;
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
        String formattedDate = now.format(formatter);
        if (!formattedDate.equals(curDate)) {
            curDate = formattedDate;
            shouldRefresh = true;
            try {
                if (fw != null) fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File curLog = new File("logs", formattedDate+".log");
        if (!curLog.exists()) {
            try {
                curLog.createNewFile();
                shouldRefresh = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (shouldRefresh) {
            try {
                fw = new FileWriter(curLog,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (curLog.canWrite() && fw != null) {
            write(log);
        }
    }



    private static void write(String content) {
        try {
            fw.write(content + "\n");
            fw.flush();  // 每次立即刷新
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
