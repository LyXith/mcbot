package io.mikaple.commands;

import io.mikaple.processer.MusicProcesser;
import io.mikaple.command.Command;
import io.mikaple.command.arguments.Argument;
import io.mikaple.command.arguments.StringArgument;
import io.mikaple.utils.ChatUtils;
import org.geysermc.mcprotocollib.network.ClientSession;

import java.io.File;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class MusicCommand {
    public static Command musicCommand = new Command().literal("music")
            .argument(new StringArgument())
            .argument(new StringArgument())
            .argument(new StringArgument())
            .executes((session, args, context) -> {
                String playerName = context.executor();
                Argument arg1 = args.getFirst();
                Argument arg2 = args.get(1);
                if (arg1 instanceof StringArgument strArg) {
                    switch (strArg.string) {
                        case "list": {
                            int page = 1;
                            if (args.get(1) instanceof StringArgument pageArg) {
                                try {
                                    page = Integer.parseInt(pageArg.string);
                                    if (page < 1) page = 1;
                                } catch (NumberFormatException ignored) {}
                            }
                            sendFileList(page, null, playerName, session);
                            break;
                        }
                        case "search": {
                            if (!(args.get(1) instanceof StringArgument kwArg) || kwArg.string.isBlank()) {
                                ChatUtils.sendMsg("<red>Usage: !!music search <keyword> [page]</red>", playerName,session);
                                try {Thread.sleep(100);} catch (InterruptedException _) {}
                                break;
                            }
                            String keyword = kwArg.string;
                            int page = 1;
                            if (args.size() > 2 && args.get(2) instanceof StringArgument pageArg) {
                                try {
                                    page = Integer.parseInt(pageArg.string);
                                    if (page < 1) page = 1;
                                } catch (NumberFormatException ignored) {}
                            }
                            sendFileList(page, keyword, playerName, session);
                            break;
                        }
                        case "play":
                            if (arg2 instanceof StringArgument strArg2) {
                                File song = new File("songs", strArg2.string);
                                if (song.exists()) {
                                    MusicProcesser.play(song, session);
                                } else {
                                    ChatUtils.sendMsg("<red>Play failed.</red>", playerName,session);
                                }
                            } else {
                                ChatUtils.sendMsg("<red>Play failed.</red>", playerName,session);
                            }
                            break;
                        case "stop":
                            MusicProcesser.stop();
                            break;
                        case "upload":
                            if (arg2 instanceof StringArgument strArg2) {
                                String url = strArg2.string;
                                CompletableFuture.supplyAsync(() -> {
                                    if (!Pattern.compile("\\.(mid|midi|nbs)$", Pattern.CASE_INSENSITIVE).matcher(url).find()) {
                                        ChatUtils.sendMsg("Can't upload this file.", playerName, session);
                                        return null;
                                    }
                                    try (var in = URI.create(url).toURL().openStream()) {
                                        String encodedFileName = Path.of(url).getFileName().toString();

                                        // 2. 使用 UTF-8 解码，得到正常的中文文件名
                                        String decodedFileName = URLDecoder.decode(encodedFileName, StandardCharsets.UTF_8);

                                        // 3. 用解码后的文件名创建路径
                                        var path = new File("songs").toPath().resolve(decodedFileName);

                                        Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                                        return path;
                                    } catch (Exception e) {
                                        ChatUtils.sendMsg("Upload failed.", playerName, session);
                                        return null;
                                    }
                                }).thenAccept(path -> {
                                    if (path != null) {
                                        ChatUtils.sendMsg("File uploaded successfully: " + path.getFileName(), playerName, session);
                                    }
                                });
                            }
                            break;
                    }
                }
            });

    /**
     * 列出 songs 目录下的文件（支持分页和搜索），通过 sendMsg 逐条发送到玩家私聊。
     */
    private static void sendFileList(int page, String keyword, String playerName, ClientSession session) {
        File dir = new File("songs");
        File[] files = dir.listFiles((f) -> f.isFile() && !f.isHidden());
        if (files == null || files.length == 0) {
            ChatUtils.sendMsg("<red>songs directory is empty or does not exist.</red>", playerName,session);
            try {Thread.sleep(100);} catch (InterruptedException _) {}
            return;
        }

        List<File> sorted = new ArrayList<>(Arrays.asList(files));
        sorted.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.getName(), b.getName()));

        List<File> filtered = sorted;
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.toLowerCase();
            filtered = new ArrayList<>();
            for (File f : sorted) {
                if (f.getName().toLowerCase().contains(kw)) {
                    filtered.add(f);
                }
            }
        }

        int totalFiles = filtered.size();
        int pageSize = 7;
        int totalPages = (totalFiles + pageSize - 1) / pageSize;
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalFiles);
        List<File> pageFiles = filtered.subList(fromIndex, toIndex);

        // 发送标题行
        if (keyword != null && !keyword.isBlank()) {
            ChatUtils.sendMsg(String.format("<gold>=== Search \"%s\" (Page %d/%d, Total %d songs) ===</gold>", keyword, page, totalPages, totalFiles), playerName, session);
        } else {
            ChatUtils.sendMsg(String.format("<gold>=== Music List (Page %d/%d, Total %d songs) ===</gold>", page, totalPages, totalFiles), playerName, session);
        }
        try {Thread.sleep(100);} catch (InterruptedException _) {}

        // 发送文件列表（每行一条）
        for (File f : pageFiles) {
            String name = f.getName();
            ChatUtils.sendMsg(
                "<click:suggest_command:!!music play \"" + name + "\"><green>\uD83C\uDFB5 " + name + "</green></click>",
                playerName,
                    session
            );
            try {Thread.sleep(200);} catch (InterruptedException _) {}
        }

        // 分页导航
        if (totalPages > 1) {
            StringBuilder nav = new StringBuilder();

            if (page > 1) {
                if (keyword != null && !keyword.isBlank()) {
                    nav.append("<click:suggest_command:!!music search ").append(keyword).append(" ").append(page - 1).append("><aqua>⬅ Prev</aqua></click> ");
                } else {
                    nav.append("<click:suggest_command:!!music list ").append(page - 1).append("><aqua>⬅ Prev</aqua></click> ");
                }
            } else {
                nav.append("<gray>⬅ Prev</gray> ");
            }

            nav.append("<white>").append(page).append("/").append(totalPages).append("</white> ");

            if (page < totalPages) {
                if (keyword != null && !keyword.isBlank()) {
                    nav.append("<click:suggest_command:!!music search ").append(keyword).append(" ").append(page + 1).append("><aqua>Next ➡</aqua></click>");
                } else {
                    nav.append("<click:suggest_command:!!music list ").append(page + 1).append("><aqua>Next ➡</aqua></click>");
                }
            } else {
                nav.append("<gray>Next ➡</gray>");
            }

            ChatUtils.sendMsg(nav.toString(), playerName,session);
            try {Thread.sleep(100);} catch (InterruptedException _) {}
        }
    }
}
