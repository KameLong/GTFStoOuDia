import com.kamelong.GTFS.GTFS;
import com.kamelong.GTFS2Oudia.GTFS2OuDia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
/**
 * This softwere convert com.kamelong.GTFS file to OuDia or OuDia2nd format file.
 * See http://take-okm.a.la9.jp/oudia/ about OuDia format file
 */
public class GTFStoOuDia {
    /**
     * true:Software use command line argment for input.txt and output files info.
     * false:Software use source code propary for input.txt and output files info.
     */
    public static boolean loadFromArgs=false;
    /**
     * directory path of input.txt com.kamelong.GTFS
     * com.kamelong.GTFS's csv file (such as stops.txt and route.txt ...) must be contained in this directory.
     *
     * This propaty is ignored when loadFromArgs=true.
     * Software use args[1] as GTFSdirectory path when loadFromArgs=true.
     */
    public static String GTFSdirectoryPath="C:\\Users\\kame_\\Downloads\\20190529GTFS-dia\\gtfs-tokachi";
    /**
     * Software need additional files for convert.
     *
     * rroute.txt has many route and they don't seperate down or up timetable.
     * User must pick up a few route from com.kamelong.GTFS and mark direction.
     * This file tell software which route convert to which oudia file and direction infomation.
     *
     *
     * File
     * This file must be "tsv" format. Not a "csv"
     * =================
     * first column: com.kamelong.GTFS's rotue id.
     * second column: OuDia file's id. Same id's routes are converted to one OuDia file.
     * third column: OuDia files's name. third column's value should be same when second column's value are same of each row.
     * forth column: Direction for this route. Use 0 or 1 integer. When 0, this route treat as Down("Kudari"). When 1, this route treat as Up("Kudari).
     * =================

     * A sample of additional file is src./addtionalFile.txt
     */

    /**
     * 変換のために用いる追加ファイルのパス
     * route.txtには多くのrouteが含まれており、すべてのrouteをまとめて1つのoudiaファイルに変換するのは難しいです。
     * また、routeには上り下りの概念がないため、1つのrouteをoudiaに変換すると下りのみの時刻表が生成されます。
     * これでは都合が悪いため、いくつかのrouteをまとめて1つのoudiaファイルを作成します。

     * 1つのoudiaファイルにどのrouteを入れるべきはユーザーが判断します。
     * 選ばれたrouteからソフト内で自動的に駅順を判定し、枝分かれなしの１本の路線にします。
     * ユーザーは１つ１つのrouteに方向をつける必要があります。また、複数のrouteを１つのoudiaの同じ向きに設定することもできます。
     * 必ずじも上下線で同じroute数、同じ駅順にする必要はありませんが、あまりかけ離れたrouteを選ぶと見栄えが悪い時刻表が出来上がります。
     *
     * ファイルフォーマット
     * このファイルはタブ区切りです
     * ==========================
     * １列目:GTFSのroute_id
     * ２列目:OuDiaファイルのID このIDが等しい行のrouteを１つのoudiaファイルにまとめます。
     * ３列目:OuDiaファイル名　OuDiaファイルIDが等しい行は同じ名前にするべきです。
     * ４列目:方向　0の時下り時刻表　1の時上り時刻表となります
     */
    public static String convertInfoFilePath="C:\\Users\\kame_\\ProgramingProject\\GTFStoOuDia\\input.txt";

    /**
     * User must use argument when loadFromArgs=true
     * In this time, length of args must be 3
     * @param args
     */
    public static void main(String[] args){
        GTFStoOuDia GTFStoOuDia =new GTFStoOuDia(args);
    }
    public GTFStoOuDia(String[] args){
        try{
            if(loadFromArgs){
                if(args.length<3){
                    System.out.println("Too less argment");
                    return;
                }
                GTFSdirectoryPath=args[1];
                convertInfoFilePath=args[2];
            }
            GTFS gtfs=new GTFS(GTFSdirectoryPath);
            BufferedReader inputFile = new BufferedReader(new FileReader(new File(convertInfoFilePath)));
            HashMap<String, GTFS2OuDia>converter=new HashMap<>();

            String str = inputFile.readLine();
            while (str != null) {
                String[] lines=str.split("\t");
                if(!converter.containsKey(lines[1])){
                    GTFS2OuDia c=new GTFS2OuDia(gtfs,GTFSdirectoryPath,lines[2]);
                    converter.put(lines[1],c);
                }
                converter.get(lines[1]).addRouteID(lines);
                str=inputFile.readLine();
            }
            for(GTFS2OuDia c :converter.values()){
                c.makeOudiaFile();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}
