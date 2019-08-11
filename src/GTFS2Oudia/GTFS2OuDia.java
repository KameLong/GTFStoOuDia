package GTFS2Oudia;

import GTFS.GTFS;
import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.Station;
import GTFS.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * GTFSをoudiaにコンバートするためのクラスです
 */
public class GTFS2OuDia {
    /**
     * using route's ID
     * index=0 :down timetable
     * index=1 :up timetable
     */
    public ArrayList<String>[]routeID=new ArrayList[2];
    public String lineName="";
    private GTFS gtfs;
    private String outputDirectoryPath="";

    /**
     * gtfs:使用GTFS1ファイル
     * outputDirectoryPath:出力するディレクトリ
     * lineName:路線名　兼　ファイル名
     */
    public GTFS2OuDia(GTFS gtfs,String outputDirectoryPath,String lineName){
        this.gtfs=gtfs;
        this.outputDirectoryPath=outputDirectoryPath;
        this.lineName=lineName;
        routeID[0]=new ArrayList<>();
        routeID[1]=new ArrayList<>();
    }
    public void addRouteID(String[] lines){
        routeID[Integer.parseInt(lines[3])].add(lines[0]);
    }
    /**
     * 与えられたrouteIDのリストを用いてoudiaファイルを生成します
     */
    public void makeOudiaFile(){
        //使用する下り列車
        ArrayList<GtfsTrain> downGtfsTrain = new ArrayList<>();
        //使用する上り列車
        ArrayList<GtfsTrain> upGtfsTrain = new ArrayList<>();
        int maxStops = 0;
        GtfsTrain maxTrain = null;
        for (String downRouteID : routeID[0]) {
            for (Trip t : gtfs.route.get(downRouteID).trips.values()) {
                GtfsTrain train = new GtfsTrain(t, 0, gtfs.stop);
                if (train.stopTimes.size() > maxStops) {
                    maxStops = train.stopTimes.size();
                    maxTrain = train;
                }
                downGtfsTrain.add(train);

            }
        }
        for (String upRouteID : routeID[1]) {
            for (Trip t : gtfs.route.get(upRouteID).trips.values()) {
                GtfsTrain train = new GtfsTrain(t, 1, gtfs.stop);
                if (train.stopTimes.size() > maxStops) {
                    maxStops = train.stopTimes.size();
                    maxTrain = train;
                }
                upGtfsTrain.add(train);
            }
        }
        System.out.println(maxStops);
        System.out.println(maxTrain);
        ArrayList<String> stationList = new ArrayList<>();
        for (String s : maxTrain.station) {
            stationList.add(s);
        }

        for (GtfsTrain train : downGtfsTrain) {
            int pos = 0;
            for (int i = 0; i < train.station.size(); i++) {
                if (!stationList.subList(pos, stationList.size()).contains(train.station.get(i))) {
                    if (i == 0) {
                        stationList.add(0, train.station.get(i));
                    } else {
                        stationList.add(pos + 1, train.station.get(i));
                    }
                    pos++;
                } else {
                    pos += stationList.subList(pos, stationList.size()).indexOf(train.station.get(i));
                }

            }
        }
        for (GtfsTrain train : upGtfsTrain) {
            int pos = 0;
            for (int i = 0; i < train.station.size(); i++) {
                if (!stationList.subList(pos, stationList.size()).contains(train.station.get(i))) {
                    if (i == 0) {
                        stationList.add(0, train.station.get(i));
                    } else {
                        stationList.add(pos + 1, train.station.get(i));
                    }
                    pos++;
                } else {
                    pos += stationList.subList(pos, stationList.size()).indexOf(train.station.get(i));
                }

            }
        }
        //stationList完成

        for (GtfsTrain train : downGtfsTrain) {
            int pos = 0;
            for (int i = 0; i < train.station.size(); i++) {
                pos += stationList.subList(pos, stationList.size()).indexOf(train.station.get(i));
                train.stationIndex.add(pos);
            }
        }
        for (GtfsTrain train : upGtfsTrain) {
            int pos = 0;
            for (int i = 0; i < train.station.size(); i++) {
                pos += stationList.subList(pos, stationList.size()).indexOf(train.station.get(i));
                train.stationIndex.add(pos);
            }
        }
        //各駅にindexを割り振ることに成功
        if (false) {
            //デバッグ用
            for (GtfsTrain train : downGtfsTrain) {
                for (int i = 0; i < stationList.size(); i++) {
                    if (train.stationIndex.contains(i)) {
                        String station = train.station.get(train.stationIndex.indexOf(i));
                        System.out.println(gtfs.stop.get(station).stop_name);
                    } else {
                        System.out.println("ㇾ");
                    }
                }
                System.out.println("eeee");
            }
            for (GtfsTrain train : upGtfsTrain) {
                for (int i = 0; i < stationList.size(); i++) {
                    if (train.stationIndex.contains(i)) {
                        String station = train.station.get(train.stationIndex.indexOf(i));
                        System.out.println(gtfs.stop.get(station).stop_name);
                    } else {
                        System.out.println("ㇾ");

                    }
                }
                System.out.println("eeee");
            }
        }

        DiaFile diaFile = new DiaFile();
        diaFile.name = lineName;
        for (int i = 0; i < stationList.size(); i++) {
            Station station = new Station(diaFile);
            station.name = gtfs.stop.get(stationList.get(i)).stop_name;
            if (i == 0) {
                station.setShowArival(1, true);
                station.setShowDepart(1, false);
            }
            if (i == stationList.size() - 1) {
                station.setShowArival(0, true);
                station.setShowDepart(0, false);
            }
            diaFile.station.add(station);
        }
        Diagram diagram = new Diagram(diaFile);
        diagram.trains[0] = new ArrayList<>();
        diagram.trains[1] = new ArrayList<>();
        diagram.name = "GTFS";
        diaFile.diagram = new ArrayList<>();
        diaFile.diagram.add(diagram);

        for (GtfsTrain train : downGtfsTrain) {
            diagram.trains[0].add(train.toOuDiaTrain(diaFile, 0));
        }
        for (GtfsTrain train : upGtfsTrain) {
            diagram.trains[1].add(train.toOuDiaTrain(diaFile, 1));
        }
        diaFile.calcMinReqiredTime();
        diagram.sortTrain(0, 0);
        diagram.sortTrain(1, diaFile.station.size() - 1);
        try {
            diaFile.saveToFile(outputDirectoryPath+"/"+lineName+".oud", false);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
