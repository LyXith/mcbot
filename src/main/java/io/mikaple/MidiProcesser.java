package io.mikaple;

import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundCommandSuggestionsPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundCommandSuggestionPacket;

import javax.sound.midi.*;
import java.io.File;
import java.util.*;


public class MidiProcesser {

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


    static {

        addMapping(0, "一丁丂七丄丅丆万丈三上下丌不与丏丐丑丒专且丕世丗丘丙业丛东丝丞丟丠両丢丣两严並丧丨丩个丫丬中丮丯丰丱串丳临丵丶丷丸丹为主丼丽举丿乀乁乂乃乄久乆乇么义乊之乌乍乎乏乐乑乒乓乔乕乖乗");
        addMapping(1, "亀亁亂亃亄亅了亇予争亊事二亍于亏亐云互亓五井亖亗亘亙亚些亜亝亞亟亠亡亢亣交亥亦产亨亩亪享京亭亮亯亰亱亲亳亴亵亶亷亸亹人亻亼亽亾亿什仁仂仃仄仅仆仇仈仉今介仌仍从仏仐仑仒仓仔仕他仗");
        addMapping(2, "伀企伂伃伄伅伆伇伈伉伊伋伌伍伎伏伐休伒伓伔伕伖众优伙会伛伜伝伞伟传伡伢伣伤伥伦伧伨伩伪伫伬伭伮伯估伱伲伳伴伵伶伷伸伹伺伻似伽伾伿佀佁佂佃佄佅但佇佈佉佊佋佌位低住佐佑佒体佔何佖佗");
        addMapping(3,"侀侁侂侃侄侅來侇侈侉侊例侌侍侎侏侐侑侒侓侔侕侖侗侘侙侚供侜依侞侟侠価侢侣侤侥侦侧侨侩侪侫侬侭侮侯侰侱侲侳侴侵侶侷侸侹侺侻侼侽侾便俀俁係促俄俅俆俇俈俉俊俋俌俍俎俏俐俑俒俓俔俕俖俗");
        addMapping(4,"倀倁倂倃倄倅倆倇倈倉倊個倌倍倎倏倐們倒倓倔倕倖倗倘候倚倛倜倝倞借倠倡倢倣値倥倦倧倨倩倪倫倬倭倮倯倰倱倲倳倴倵倶倷倸倹债倻值倽倾倿偀偁偂偃偄偅偆假偈偉偊偋偌偍偎偏偐偑偒偓偔偕偖偗");
        addMapping(5,"傀傁傂傃傄傅傆傇傈傉傊傋傌傍傎傏傐傑傒傓傔傕傖傗傘備傚傛傜傝傞傟傠傡傢傣傤傥傦傧储傩傪傫催傭傮傯傰傱傲傳傴債傶傷傸傹傺傻傼傽傾傿僀僁僂僃僄僅僆僇僈僉僊僋僌働僎像僐僑僒僓僔僕僖僗");
        addMapping(6,"儀儁儂儃億儅儆儇儈儉儊儋儌儍儎儏儐儑儒儓儔儕儖儗儘儙儚儛儜儝儞償儠儡儢儣儤儥儦儧儨儩優儫儬儭儮儯儰儱儲儳儴儵儶儷儸儹儺儻儼儽儾儿兀允兂元兄充兆兇先光兊克兌免兎兏児兑兒兓兔兕兖兗");
        addMapping(7,"冀冁冂冃冄内円冇冈冉冊冋册再冎冏冐冑冒冓冔冕冖冗冘写冚军农冝冞冟冠冡冢冣冤冥冦冧冨冩冪冫冬冭冮冯冰冱冲决冴况冶冷冸冹冺冻冼冽冾冿净凁凂凃凄凅准凇凈凉凊凋凌凍凎减凐凑凒凓凔凕凖凗");
        addMapping(8,"刀刁刂刃刄刅分切刈刉刊刋刌刍刎刏刐刑划刓刔刕刖列刘则刚创刜初刞刟删刡刢刣判別刦刧刨利刪别刬刭刮刯到刱刲刳刴刵制刷券刹刺刻刼刽刾刿剀剁剂剃剄剅剆則剈剉削剋剌前剎剏剐剑剒剓剔剕剖剗");
        addMapping(9,"劀劁劂劃劄劅劆劇劈劉劊劋劌劍劎劏劐劑劒劓劔劕劖劗劘劙劚力劜劝办功加务劢劣劤劥劦劧动助努劫劬劭劮劯劰励劲劳労劵劶劷劸効劺劻劼劽劾势勀勁勂勃勄勅勆勇勈勉勊勋勌勍勎勏勐勑勒勓勔動勖勗");
        addMapping(10,"匀匁匂匃匄包匆匇匈匉匊匋匌匍匎匏匐匑匒匓匔匕化北匘匙匚匛匜匝匞匟匠匡匢匣匤匥匦匧匨匩匪匫匬匭匮匯匰匱匲匳匴匵匶匷匸匹区医匼匽匾匿區十卂千卄卅卆升午卉半卋卌卍华协卐卑卒卓協单卖南");
        addMapping(11,"厀厁厂厃厄厅历厇厈厉厊压厌厍厎厏厐厑厒厓厔厕厖厗厘厙厚厛厜厝厞原厠厡厢厣厤厥厦厧厨厩厪厫厬厭厮厯厰厱厲厳厴厵厶厷厸厹厺去厼厽厾县叀叁参參叄叅叆叇又叉及友双反収叏叐发叒叓叔叕取受");
        addMapping(12,"吀吁吂吃各吅吆吇合吉吊吋同名后吏吐向吒吓吔吕吖吗吘吙吚君吜吝吞吟吠吡吢吣吤吥否吧吨吩吪含听吭吮启吰吱吲吳吴吵吶吷吸吹吺吻吼吽吾吿呀呁呂呃呄呅呆呇呈呉告呋呌呍呎呏呐呑呒呓呔呕呖呗");
        addMapping(13,"咀咁咂咃咄咅咆咇咈咉咊咋和咍咎咏咐咑咒咓咔咕咖咗咘咙咚咛咜咝咞咟咠咡咢咣咤咥咦咧咨咩咪咫咬咭咮咯咰咱咲咳咴咵咶咷咸咹咺咻咼咽咾咿哀品哂哃哄哅哆哇哈哉哊哋哌响哎哏哐哑哒哓哔哕哖哗");
        addMapping(14,"唀唁唂唃唄唅唆唇唈唉唊唋唌唍唎唏唐唑唒唓唔唕唖唗唘唙唚唛唜唝唞唟唠唡唢唣唤唥唦唧唨唩唪唫唬唭售唯唰唱唲唳唴唵唶唷唸唹唺唻唼唽唾唿啀啁啂啃啄啅商啇啈啉啊啋啌啍啎問啐啑啒啓啔啕啖啗");
        addMapping(15,"喀喁喂喃善喅喆喇喈喉喊喋喌喍喎喏喐喑喒喓喔喕喖喗喘喙喚喛喜喝喞喟喠喡喢喣喤喥喦喧喨喩喪喫喬喭單喯喰喱喲喳喴喵営喷喸喹喺喻喼喽喾喿嗀嗁嗂嗃嗄嗅嗆嗇嗈嗉嗊嗋嗌嗍嗎嗏嗐嗑嗒嗓嗔嗕嗖嗗");


    }



    private static void addMapping(
            int start,
            String chars
    ){

        int note = start;


        for(int i = 0; i < chars.length(); ){

            int code =
                    chars.codePointAt(i);


            noteMap.put(
                    note,
                    code
            );


            note++;


            i += Character.charCount(code);
        }
    }





    public static void play(File file, Session session)
            throws Exception {

        int transactionId = 0;

        session.send(new ServerboundChatCommandPacket("piano keyboard unicode"));


        Sequence sequence =
                MidiSystem.getSequence(file);



        // MIDI 每拍 tick 数
        int resolution =
                sequence.getResolution();



        // 获取 BPM
        double microPerQuarter =
                500000; // 默认120 BPM


        for(Track track : sequence.getTracks()){

            for(int i = 0; i < track.size(); i++){

                MidiEvent event =
                        track.get(i);


                MidiMessage msg =
                        event.getMessage();


                if(msg instanceof MetaMessage meta){

                    // Tempo事件
                    if(meta.getType() == 0x51){

                        byte[] data =
                                meta.getData();


                        microPerQuarter =
                                ((data[0]&0xff)<<16)
                                        |
                                        ((data[1]&0xff)<<8)
                                        |
                                        (data[2]&0xff);
                    }
                }
            }
        }




        List<MidiEventData> events =
                new ArrayList<>();



        for(Track track : sequence.getTracks()){


            for(int i = 0; i < track.size(); i++){


                MidiEvent event =
                        track.get(i);


                MidiMessage msg =
                        event.getMessage();



                if(msg instanceof ShortMessage sm){


                    if(sm.getCommand()
                            == ShortMessage.NOTE_ON
                            &&
                            sm.getData2()>0){


                        int note =
                                sm.getData1();

                        int channel =
                                sm.getChannel();

                        note = normalizeNote(note);



                        int unicode =
                                noteMap.getOrDefault(
                                        note,
                                        0
                                );



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



        events.sort(
                Comparator.comparingLong(
                        e -> e.tick
                )
        );



        long lastTick = 0;
        int currentChannel = -1;



        for(MidiEventData e : events){



            long deltaTick =
                    e.tick-lastTick;



            /*
             * tick -> ms
             *
             * microPerQuarter:
             * 每四分音符多少微秒
             *
             * resolution:
             * 每四分音符多少tick
             */
            long sleep =
                    (long)(
                            deltaTick
                                    *
                                    microPerQuarter
                                    /
                                    resolution
                                    /
                                    1000
                    );



            if(sleep>0){
                Thread.sleep(sleep);
            }




            if(e.channel != currentChannel){
                currentChannel = e.channel;
            }


            if(e.unicode != 0){

                String text =
                        "/// " + (char)e.unicode;

                session.send(
                        new ServerboundCommandSuggestionPacket(
                                transactionId,
                                text
                        )
                );
                transactionId ++;

            }



            lastTick =
                    e.tick;
        }

    }





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

    private static int normalizeNote(int note) {

        int min = Collections.min(noteMap.keySet());
        int max = Collections.max(noteMap.keySet());


        // 高音降八度
        while(note > max) {
            note -= 12;
        }


        // 低音升八度
        while(note < min) {
            note += 12;
        }


        return note;
    }

}