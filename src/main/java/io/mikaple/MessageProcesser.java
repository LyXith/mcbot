package io.mikaple;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageProcesser {

    private static final Map<Integer, String> ANSI_COLORS = new HashMap<>();
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String ITALIC = "\u001B[3m";
    private static final String UNDERLINE = "\u001B[4m";
    private static final String STRIKETHROUGH = "\u001B[9m";

    static {
        // NamedTextColor 的 RGB 值映射到最接近的 ANSI 前景色
        put(NamedTextColor.BLACK, "\u001B[30m");
        put(NamedTextColor.DARK_BLUE, "\u001B[34m");
        put(NamedTextColor.DARK_GREEN, "\u001B[32m");
        put(NamedTextColor.DARK_AQUA, "\u001B[36m");
        put(NamedTextColor.DARK_RED, "\u001B[31m");
        put(NamedTextColor.DARK_PURPLE, "\u001B[35m");
        put(NamedTextColor.GOLD, "\u001B[33m");
        put(NamedTextColor.GRAY, "\u001B[37m");
        put(NamedTextColor.DARK_GRAY, "\u001B[90m");
        put(NamedTextColor.BLUE, "\u001B[94m");
        put(NamedTextColor.GREEN, "\u001B[92m");
        put(NamedTextColor.AQUA, "\u001B[96m");
        put(NamedTextColor.RED, "\u001B[91m");
        put(NamedTextColor.LIGHT_PURPLE, "\u001B[95m");
        put(NamedTextColor.YELLOW, "\u001B[93m");
        put(NamedTextColor.WHITE, "\u001B[97m");
    }

    private static void put(NamedTextColor color, String ansi) {
        ANSI_COLORS.put(color.value(), ansi);
    }

    /**
     * 把 Component 转成带 ANSI 颜色码的字符串，直接 System.out.println 或塞进 slf4j 的 {} 里即可。
     */
    public static String toAnsi(Component component) {
        StringBuilder sb = new StringBuilder();
        render(component, sb, null);
        sb.append(RESET);
        return sb.toString();
    }

    /**
     * 把 Component 转成不带任何颜色/格式的纯文本，方便做关键字匹配、日志入库等场景。
     */
    public static String toPlainText(Component component) {
        StringBuilder sb = new StringBuilder();
        collectPlain(component, sb);
        return sb.toString();
    }

    private static void render(Component component, StringBuilder sb, TextColor inherited) {
        Style style = component.style();
        TextColor color = style.color() != null ? style.color() : inherited;
        StringBuilder prefix = new StringBuilder();
        if (color != null) {
            String ansi = ANSI_COLORS.get(color.value());
            if (ansi != null) prefix.append(ansi);
        }
        if (style.hasDecoration(TextDecoration.BOLD)) prefix.append(BOLD);
        if (style.hasDecoration(TextDecoration.ITALIC)) prefix.append(ITALIC);
        if (style.hasDecoration(TextDecoration.UNDERLINED)) prefix.append(UNDERLINE);
        if (style.hasDecoration(TextDecoration.STRIKETHROUGH)) prefix.append(STRIKETHROUGH);

        if (component instanceof TranslatableComponent) {
            // 特殊解析: key="%s" 的翻译组件,按占位符顺序把 arguments 递归渲染进去
            TranslatableComponent tc = (TranslatableComponent) component;
            String key = tc.key();
            List<TranslationArgument> args = tc.arguments();

            int argIndex = 0;
            int i = 0;
            while (i < key.length()) {
                if (key.startsWith("%s", i) && argIndex < args.size()) {
                    // 参数部分:递归渲染，让参数自身的颜色/悬浮样式生效
                    render(args.get(argIndex).asComponent(), sb, color);
                    argIndex++;
                    i += 2;
                } else {
                    // 普通字符部分：套用当前 prefix
                    if (prefix.length() > 0) sb.append(prefix);
                    sb.append(key.charAt(i));
                    if (prefix.length() > 0) sb.append(RESET);
                    i++;
                }
            }
        } else {
            String text = extractText(component);
            if (!text.isEmpty()) {
                sb.append(prefix).append(text).append(prefix.length() > 0 ? RESET : "");
            }
        }

        for (Component child : component.children()) {
            render(child, sb, color);
        }
    }

    private static String extractText(Component component) {
        if (component instanceof net.kyori.adventure.text.TextComponent text) {
            return text.content();
        } else if (component instanceof TranslatableComponent trans) {
            // 服务器自定义的 sys.lang.xxx 这类 key 通常没有客户端语言文件能翻译，
            // 优先用 fallback，没有的话就把 key 打出来，方便你自己排查是哪个字段
            if (trans.fallback() != null) {
                return trans.fallback();
            }
            return "[" + trans.key() + "]";
        }
        return "";
    }

    private static void collectPlain(Component component, StringBuilder sb) {
        if (component instanceof TranslatableComponent) {
            // 特殊解析:TranslatableComponent 的 key 当作格式串,
            // arguments 里的每个参数递归转纯文本后依次替换 %s
            TranslatableComponent tc = (TranslatableComponent) component;
            String key = tc.key();
            List<TranslationArgument> args = tc.arguments();

            StringBuilder result = new StringBuilder();
            int argIndex = 0;
            int i = 0;
            while (i < key.length()) {
                if (key.startsWith("%s", i) && argIndex < args.size()) {
                    StringBuilder argSb = new StringBuilder();
                    collectPlain(args.get(argIndex).asComponent(), argSb);
                    result.append(argSb);
                    argIndex++;
                    i += 2;
                } else {
                    result.append(key.charAt(i));
                    i++;
                }
            }
            sb.append(result);
        } else {
            sb.append(extractText(component));
        }

        // 无论哪种情况,子组件都要继续遍历(TranslatableComponent 一般没有 children,
        // 但保留这行以兼容有 children 的情况)
        for (Component child : component.children()) {
            collectPlain(child, sb);
        }
    }
}