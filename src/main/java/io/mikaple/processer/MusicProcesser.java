package io.mikaple.processer;

import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundCommandSuggestionPacket;

import javax.sound.midi.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class MusicProcesser {
    private static Thread playThread;
    private static volatile boolean playing = false;
    private static final int PITCH_MIN = 21;
    private static final int PITCH_MAX = 108;

    /** 每个乐器在 noteMap 中占 noteCount 个键（88 = MIDI 21..108 共 88 个半音） */
    private static final int NOTE_COUNT = PITCH_MAX - PITCH_MIN + 1; // 88

    private static final String[] instruments = {
            "harp",              // channel 0
            "bass",              // channel 1
            "basedrum",          // channel 2
            "snare",             // channel 3
            "hat",               // channel 4
            "guitar",            // channel 5
            "flute",             // channel 6
            "bell",              // channel 7
            "chime",             // channel 8
            "xylophone",         // channel 9
            "iron_xylophone",    // channel 10
            "cow_bell",          // channel 11
            "didgeridoo",        // channel 12
            "bit",               // channel 13
            "banjo",             // channel 14
            "pling"              // channel 15
    };


    private static final Map<Integer, Integer> noteMap =
            new HashMap<>();

    // NBS 的 key(0-87, 钢琴 A0=0) 换算成"MIDI 等价音符"需要加的偏移量
    // MIDI 中 A0 = 21，所以 midiEquivalent = key + NBS_KEY_TO_MIDI_OFFSET
    private static final int NBS_KEY_TO_MIDI_OFFSET = 21;


    static {
        int offset = 0;

        offset = addMapping(offset, "一丁丂七丄丅丆万丈三上下丌不与丏丐丑丒专且丕世丗丘丙业丛东丝丞丟丠両丢丣两严並丧丨丩个丫丬中丮丯丰丱串丳临丵丶丷丸丹为主丼丽举丿乀乁乂乃乄久乆乇么义乊之乌乍乎乏乐乑乒乓乔乕乖乗");
        offset = addMapping(offset, "亀亁亂亃亄亅了亇予争亊事二亍于亏亐云互亓五井亖亗亘亙亚些亜亝亞亟亠亡亢亣交亥亦产亨亩亪享京亭亮亯亰亱亲亳亴亵亶亷亸亹人亻亼亽亾亿什仁仂仃仄仅仆仇仈仉今介仌仍从仏仐仑仒仓仔仕他仗");
        offset = addMapping(offset, "伀企伂伃伄伅伆伇伈伉伊伋伌伍伎伏伐休伒伓伔伕伖众优伙会伛伜伝伞伟传伡伢伣伤伥伦伧伨伩伪伫伬伭伮伯估伱伲伳伴伵伶伷伸伹伺伻似伽伾伿佀佁佂佃佄佅但佇佈佉佊佋佌位低住佐佑佒体佔何佖佗");
        offset = addMapping(offset,"侀侁侂侃侄侅來侇侈侉侊例侌侍侎侏侐侑侒侓侔侕侖侗侘侙侚供侜依侞侟侠価侢侣侤侥侦侧侨侩侪侫侬侭侮侯侰侱侲侳侴侵侶侷侸侹侺侻侼侽侾便俀俁係促俄俅俆俇俈俉俊俋俌俍俎俏俐俑俒俓俔俕俖俗");
        offset = addMapping(offset,"倀倁倂倃倄倅倆倇倈倉倊個倌倍倎倏倐們倒倓倔倕倖倗倘候倚倛倜倝倞借倠倡倢倣値倥倦倧倨倩倪倫倬倭倮倯倰倱倲倳倴倵倶倷倸倹债倻值倽倾倿偀偁偂偃偄偅偆假偈偉偊偋偌偍偎偏偐偑偒偓偔偕偖偗");
        offset = addMapping(offset,"傀傁傂傃傄傅傆傇傈傉傊傋傌傍傎傏傐傑傒傓傔傕傖傗傘備傚傛傜傝傞傟傠傡傢傣傤傥傦傧储傩傪傫催傭傮傯傰傱傲傳傴債傶傷傸傹傺傻傼傽傾傿僀僁僂僃僄僅僆僇僈僉僊僋僌働僎像僐僑僒僓僔僕僖僗");
        offset = addMapping(offset,"儀儁儂儃億儅儆儇儈儉儊儋儌儍儎儏儐儑儒儓儔儕儖儗儘儙儚儛儜儝儞償儠儡儢儣儤儥儦儧儨儩優儫儬儭儮儯儰儱儲儳儴儵儶儷儸儹儺儻儼儽儾儿兀允兂元兄充兆兇先光兊克兌免兎兏児兑兒兓兔兕兖兗");
        offset = addMapping(offset,"冀冁冂冃冄内円冇冈冉冊冋册再冎冏冐冑冒冓冔冕冖冗冘写冚军农冝冞冟冠冡冢冣冤冥冦冧冨冩冪冫冬冭冮冯冰冱冲决冴况冶冷冸冹冺冻冼冽冾冿净凁凂凃凄凅准凇凈凉凊凋凌凍凎减凐凑凒凓凔凕凖凗");
        offset = addMapping(offset,"刀刁刂刃刄刅分切刈刉刊刋刌刍刎刏刐刑划刓刔刕刖列刘则刚创刜初刞刟删刡刢刣判別刦刧刨利刪别刬刭刮刯到刱刲刳刴刵制刷券刹刺刻刼刽刾刿剀剁剂剃剄剅剆則剈剉削剋剌前剎剏剐剑剒剓剔剕剖剗");
        offset = addMapping(offset,"劀劁劂劃劄劅劆劇劈劉劊劋劌劍劎劏劐劑劒劓劔劕劖劗劘劙劚力劜劝办功加务劢劣劤劥劦劧动助努劫劬劭劮劯劰励劲劳労劵劶劷劸効劺劻劼劽劾势勀勁勂勃勄勅勆勇勈勉勊勋勌勍勎勏勐勑勒勓勔動勖勗");
        offset = addMapping(offset,"匀匁匂匃匄包匆匇匈匉匊匋匌匍匎匏匐匑匒匓匔匕化北匘匙匚匛匜匝匞匟匠匡匢匣匤匥匦匧匨匩匪匫匬匭匮匯匰匱匲匳匴匵匶匷匸匹区医匼匽匾匿區十卂千卄卅卆升午卉半卋卌卍华协卐卑卒卓協单卖南");
        offset = addMapping(offset,"厀厁厂厃厄厅历厇厈厉厊压厌厍厎厏厐厑厒厓厔厕厖厗厘厙厚厛厜厝厞原厠厡厢厣厤厥厦厧厨厩厪厫厬厭厮厯厰厱厲厳厴厵厶厷厸厹厺去厼厽厾县叀叁参參叄叅叆叇又叉及友双反収叏叐发叒叓叔叕取受");
        offset = addMapping(offset,"吀吁吂吃各吅吆吇合吉吊吋同名后吏吐向吒吓吔吕吖吗吘吙吚君吜吝吞吟吠吡吢吣吤吥否吧吨吩吪含听吭吮启吰吱吲吳吴吵吶吷吸吹吺吻吼吽吾吿呀呁呂呃呄呅呆呇呈呉告呋呌呍呎呏呐呑呒呓呔呕呖呗");
        offset = addMapping(offset,"咀咁咂咃咄咅咆咇咈咉咊咋和咍咎咏咐咑咒咓咔咕咖咗咘咙咚咛咜咝咞咟咠咡咢咣咤咥咦咧咨咩咪咫咬咭咮咯咰咱咲咳咴咵咶咷咸咹咺咻咼咽咾咿哀品哂哃哄哅哆哇哈哉哊哋哌响哎哏哐哑哒哓哔哕哖哗");
        offset = addMapping(offset,"唀唁唂唃唄唅唆唇唈唉唊唋唌唍唎唏唐唑唒唓唔唕唖唗唘唙唚唛唜唝唞唟唠唡唢唣唤唥唦唧唨唩唪唫唬唭售唯唰唱唲唳唴唵唶唷唸唹唺唻唼唽唾唿啀啁啂啃啄啅商啇啈啉啊啋啌啍啎問啐啑啒啓啔啕啖啗");
        addMapping(offset,"喀喁喂喃善喅喆喇喈喉喊喋喌喍喎喏喐喑喒喓喔喕喖喗喘喙喚喛喜喝喞喟喠喡喢喣喤喥喦喧喨喩喪喫喬喭單喯喰喱喲喳喴喵営喷喸喹喺喻喼喽喾喿嗀嗁嗂嗃嗄嗅嗆嗇嗈嗉嗊嗋嗌嗍嗎嗏嗐嗑嗒嗓嗔嗕嗖嗗");


    }


    private static int addMapping(int start, String chars) {
        int note = start;
        for (int i = 0; i < chars.length(); ) {
            int code = chars.codePointAt(i);
            noteMap.put(note, code);
            note++;
            i += Character.charCount(code);
        }
        return note;
    }


    // ========================= 公共入口 =========================

    public static void play(File file, Session session) {

        stop(); // 防止重复播放

        playing = true;

        playThread = new Thread(() -> {

            try {

                String name = file.getName().toLowerCase(Locale.ROOT);

                if (name.endsWith(".nbs")) {
                    playInternalNbs(file, session);
                } else {
                    playInternal(file, session);
                }

            } catch (InterruptedException e) {
                // 正常停止，不打印

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                playing = false;
            }

        });

        playThread.start();
    }


    // ========================= 共用播放循环 =========================

    /**
     * 播放一组已经按 tick 排好序的事件。
     * @param events 事件列表（要求已按 tick 升序排序）
     * @param msPerTick 每个 tick 对应多少毫秒
     */
    private static void playEvents(
            List<MidiEventData> events,
            double msPerTick,
            Session session
    ) throws Exception {

        int transactionId = 0;

        session.send(new ServerboundChatCommandPacket("piano keyboard unicode"));

        long lastTick = 0;

        for (MidiEventData e : events) {

            if (!playing) {
                return;
            }

            long deltaTick = e.tick - lastTick;

            long sleep = (long) (deltaTick * msPerTick);

            if (sleep > 0) {

                long remain = sleep;

                while (remain > 0 && playing) {

                    long start = System.currentTimeMillis();

                    Thread.sleep(Math.min(remain, 100));

                    remain -= System.currentTimeMillis() - start;
                }
            }

            if (!playing) {
                return;
            }

            if (e.unicode != 0) {

                String text = "/// " + (char) e.unicode;

                session.send(
                        new ServerboundCommandSuggestionPacket(
                                transactionId,
                                text
                        )
                );
                transactionId++;
            }

            lastTick = e.tick;
        }
    }


    // ========================= MIDI 解析 =========================

    public static void playInternal(File file, Session session)
            throws Exception {

        Sequence sequence =
                MidiSystem.getSequence(file);

        // MIDI 每拍 tick 数
        int resolution =
                sequence.getResolution();

        // 获取 BPM（只取第一个 tempo 事件，不处理变速）
        double microPerQuarter =
                500000; // 默认120 BPM

        for (Track track : sequence.getTracks()) {

            for (int i = 0; i < track.size(); i++) {

                MidiEvent event = track.get(i);
                MidiMessage msg = event.getMessage();

                if (msg instanceof MetaMessage meta) {

                    // Tempo事件
                    if (meta.getType() == 0x51) {

                        byte[] data = meta.getData();

                        microPerQuarter =
                                ((data[0] & 0xff) << 16)
                                        | ((data[1] & 0xff) << 8)
                                        | (data[2] & 0xff);
                    }
                }
            }
        }

        List<MidiEventData> events = new ArrayList<>();

        // 每个 MIDI channel 的当前 Program (0-127)，默认 0
        int[] channelProgram = new int[16];

        for (Track track : sequence.getTracks()) {

            for (int i = 0; i < track.size(); i++) {

                MidiEvent event = track.get(i);
                MidiMessage msg = event.getMessage();

                if (msg instanceof ShortMessage sm) {

                    if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
                        // 记录该 channel 当前使用的乐器
                        channelProgram[sm.getChannel()] = sm.getData1();
                    } else if (sm.getCommand() == ShortMessage.NOTE_ON
                            && sm.getData2() > 0) {

                        int note = sm.getData1();
                        int channel = sm.getChannel();

                        int instrument = programToInstrument(channelProgram[channel]);
                        int unicode = getUnicodeForMidi(note, instrument);
                        if (unicode != 0) {
                            events.add(
                                    new MidiEventData(
                                            event.getTick(),
                                            unicode,
                                            channel
                                    )
                            );
                        }
                    }
                }
            }
        }

        events.sort(Comparator.comparingLong(e -> e.tick));

        /*
         * tick -> ms
         * microPerQuarter: 每四分音符多少微秒
         * resolution: 每四分音符多少tick
         */
        double msPerTick = microPerQuarter / resolution / 1000.0;

        playEvents(events, msPerTick, session);
    }


    // ========================= NBS 解析 =========================

    /**
     * 播放 Note Block Studio (.nbs) 文件。
     * NBS 二进制格式说明（小端序）：
     *  Header:
     *    short  length (0 表示新版本, 紧接着读 version/vanillaInstrumentCount)
     *    [若上面为0] byte version
     *    [若上面为0] byte 默认乐器数
     *    [version>=3] short 真正的歌曲长度(tick)
     *    short  层数
     *    string 曲名 / 作者 / 原作者 / 描述  (每个string前面有个int32长度)
     *    short  tempo (除以100 = 每秒tick数, 例如 2000 -> 20 TPS)
     *    byte   自动保存开关
     *    byte   自动保存间隔
     *    byte   拍号
     *    int    耗时分钟数 / 左键数 / 右键数 / 加方块数 / 删方块数
     *    string 原始文件名(如导入的midi文件名)
     *    [version>=4] byte loop / byte 最大循环次数 / short 循环起点
     *  Notes:
     *    循环: short tickJump (0结束) -> tick累加
     *      循环: short layerJump (0结束) -> layer累加
     *        byte instrument
     *        byte key (0-87, 钢琴 A0=0)
     *        [version>=4] byte velocity, byte panning, short pitch
     */
    public static void playInternalNbs(File file, Session session)
            throws Exception {

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {

            NbsReader reader = new NbsReader(in);

            int firstShort = reader.readShortLE();

            int version = 0;

            int songLength;

            if (firstShort == 0) {

                version = reader.readUnsignedByte();

                reader.readUnsignedByte(); // 默认乐器数, 用不到

                if (version >= 3) {
                    songLength = reader.readShortLE();
                } else {
                    songLength = 0;
                }

            } else {
                // 极老的格式(version 0), 这个short本身就是长度
                songLength = firstShort;
            }

            int layerCount = reader.readShortLE();

            reader.readString(); // 曲名
            reader.readString(); // 作者
            reader.readString(); // 原作者
            reader.readString(); // 描述

            int tempoRaw = reader.readShortLE();

            double ticksPerSecond = tempoRaw / 100.0;

            if (ticksPerSecond <= 0) {
                ticksPerSecond = 10; // 兜底, 避免除0
            }

            reader.readUnsignedByte(); // 自动保存开关
            reader.readUnsignedByte(); // 自动保存间隔
            reader.readUnsignedByte(); // 拍号

            reader.readIntLE(); // 耗时分钟数
            reader.readIntLE(); // 左键数
            reader.readIntLE(); // 右键数
            reader.readIntLE(); // 加方块数
            reader.readIntLE(); // 删方块数

            reader.readString(); // 原始文件名

            if (version >= 4) {
                reader.readUnsignedByte(); // loop 开关
                reader.readUnsignedByte(); // 最大循环次数
                reader.readShortLE();      // 循环起点
            }

            List<MidiEventData> events = new ArrayList<>();

            long tick = -1;

            while (true) {

                int tickJump = reader.readShortLE();

                if (tickJump == 0) {
                    break;
                }

                tick += tickJump;

                int layer = -1;

                while (true) {

                    int layerJump = reader.readShortLE();

                    if (layerJump == 0) {
                        break;
                    }

                    layer += layerJump;

                    int instrument = reader.readUnsignedByte();

                    int key = reader.readUnsignedByte();

                    if (version >= 4) {
                        reader.readUnsignedByte();  // velocity
                        reader.readUnsignedByte();  // panning
                        reader.readShortLE();       // fine pitch
                    }

                    // 关键修复: 用 instrument 选择乐器段, key(0-87) 直接作为段内偏移
                    int unicode = getUnicodeForNbs(instrument, key);

                    if (unicode != 0) {
                        events.add(
                                new MidiEventData(
                                        tick,
                                        unicode,
                                        instrument
                                )
                        );
                    }
                }
            }

            // 层信息/自定义乐器部分不影响播放, 无需继续读取

            events.sort(Comparator.comparingLong(e -> e.tick));

            double msPerTick = 1000.0 / ticksPerSecond;

            playEvents(events, msPerTick, session);
        }
    }


    // ========================= 核心查询方法 =========================

    /**
     * 根据 MIDI note 和 channel(乐器) 获取对应的 unicode。
     * 先确保 note 在 [PITCH_MIN, PITCH_MAX] 范围内（通过12度循环），
     * 然后定位到 channel 对应的乐器段。
     */
    private static int getUnicodeForMidi(int note, int channel) {
        // 1. 将 MIDI note 纳入 PITCH_MIN..PITCH_MAX 范围（12度循环）
        note = clampOctave(note);

        // 2. 计算该乐器段内的偏移: note - PITCH_MIN 为 0..87
        int offset = note - PITCH_MIN; // 0..87

        // 3. 计算 noteMap 中的实际 key
        int noteKey = channel * NOTE_COUNT + offset;

        return noteMap.getOrDefault(noteKey, 0);
    }

    /**
     * 根据 NBS instrument 和 key(0-87) 获取对应的 unicode。
     * instrument(0-15) 选择乐器段, key(0-87) 直接作为段内偏移。
     */
    private static int getUnicodeForNbs(int instrument, int key) {
        // NBS key 0-87 对应 MIDI A0=21 到 C8=108
        // 直接映射到乐器段内的偏移
        int offset = key; // 0..87

        // 验证 key 在有效范围内
        if (offset < 0 || offset >= NOTE_COUNT) {
            return 0;
        }

        // 计算 noteMap 中的实际 key
        int noteKey = instrument * NOTE_COUNT + offset;

        return noteMap.getOrDefault(noteKey, 0);
    }

    private static int programToInstrument(int program) {
        return switch (program) {
            // Piano
            case 0, 1, 2, 3, 4, 5, 6, 7 ->
                    0; // harp

            // Chromatic Percussion
            case 8, 9, 10, 11, 12, 13, 14, 15 ->
                    9; // xylophone

            // Organ
            case 16, 17, 18, 19, 20, 21, 22, 23 ->
                    0; // harp

            // Guitar
            case 24, 25, 26, 27, 28, 29, 30, 31 ->
                    5; // guitar

            // Bass
            case 32, 33, 34, 35, 36, 37, 38, 39 ->
                    1; // bass

            // Strings
            case 40, 41, 42, 43, 44, 45, 46, 47 ->
                    0; // harp

            // Ensemble
            case 48, 49, 50, 51, 52, 53, 54, 55 ->
                    0; // harp

            // Brass
            case 56, 57, 58, 59, 60, 61, 62, 63 ->
                    12; // didgeridoo

            // Reed
            case 64, 65, 66, 67, 68, 69, 70, 71 ->
                    6; // flute

            // Pipe
            case 72, 73, 74, 75, 76, 77, 78, 79 ->
                    6; // flute

            // Synth Lead
            case 80, 81, 82, 83, 84, 85, 86, 87 ->
                    15; // pling

            // Synth Pad
            case 88, 89, 90, 91, 92, 93, 94, 95 ->
                    8; // chime

            // Synth Effects
            case 96, 97, 98, 99, 100, 101, 102, 103 ->
                    13; // bit

            // Ethnic
            case 104, 105, 106, 107, 108, 109, 110, 111 ->
                    14; // banjo

            // Percussion
            case 112, 113, 114, 115, 116, 117, 118, 119 ->
                    11; // cow_bell

            // Sound Effects
            default ->
                    15; // pling
        };
    }

    private static int clampOctave(int note) {
        while (note > PITCH_MAX) note -= 12;
        while (note < PITCH_MIN) note += 12;
        return note;
    }


    // ========================= NBS 读取工具 =========================

    /**
     * NBS 文件用到的基础二进制读取工具（全部小端序）。
     */
    private static class NbsReader {

        private final DataInputStream in;

        NbsReader(DataInputStream in) {
            this.in = in;
        }

        int readUnsignedByte() throws IOException {
            int b = in.read();
            if (b < 0) {
                throw new EOFException("NBS文件读取到末尾");
            }
            return b;
        }

        int readShortLE() throws IOException {
            int b1 = readUnsignedByte();
            int b2 = readUnsignedByte();
            return (b1 & 0xff) | ((b2 & 0xff) << 8);
        }

        int readIntLE() throws IOException {
            int b1 = readUnsignedByte();
            int b2 = readUnsignedByte();
            int b3 = readUnsignedByte();
            int b4 = readUnsignedByte();
            return (b1 & 0xff)
                    | ((b2 & 0xff) << 8)
                    | ((b3 & 0xff) << 16)
                    | ((b4 & 0xff) << 24);
        }

        String readString() throws IOException {
            int len = readIntLE();
            if (len <= 0) {
                return "";
            }
            byte[] data = new byte[len];
            in.readFully(data);
            return new String(data, StandardCharsets.UTF_8);
        }
    }


    // ========================= 数据类 =========================

    private static class MidiEventData {

        long tick;

        int unicode;

        int channel;


        MidiEventData(
                long tick,
                int unicode,
                int channel
        ){
            this.tick = tick;
            this.unicode = unicode;
            this.channel = channel;
        }
    }

    public static synchronized void stop(){

        playing = false;

        if(playThread != null){

            playThread.interrupt();

            playThread = null;
        }
    }

}
