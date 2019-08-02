package GTFS;


import GTFS2Oudia.GtfsTrain;
import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.Station;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GTFS {
    public String filePath;
    Map<String ,Route> route=new HashMap<>();
    Map<String ,Stop> stop=new HashMap<>();
    public GTFS(String path) throws FileNotFoundException {
        filePath = path;


        //ファイル存在チェック

        if (!new File(filePath + "/routes.txt").exists()) {
            throw new FileNotFoundException("routes.txt");
        }
        if (!new File(filePath + "/stops.txt").exists()) {
            throw new FileNotFoundException("stops.txt");
        }
        if (!new File(filePath + "/trips.txt").exists()) {
            throw new FileNotFoundException("trips.txt");
        }
        if (!new File(filePath + "/stop_times.txt").exists()) {
            throw new FileNotFoundException("stop_times.txt");
        }

        //ファイル読み込み開始

        try {
            BufferedReader routeFile = new BufferedReader(new FileReader(new File(filePath + "/routes.txt")));
            String str = routeFile.readLine();
            str = routeFile.readLine();
            while (str != null) {
                Route mroute = new Route(str.split(","));
                route.put(mroute.route_id, mroute);
                str = routeFile.readLine();
            }
            BufferedReader stopsFile = new BufferedReader(new FileReader(new File(filePath + "/stops.txt")));
            str = stopsFile.readLine();
            str = stopsFile.readLine();
            while (str != null) {
                Stop mstop = new Stop(str.split(","));
                stop.put(mstop.stop_id,mstop);
                str = stopsFile.readLine();
            }
            BufferedReader tripsFile = new BufferedReader(new FileReader(new File(filePath + "/trips.txt")));
            str = tripsFile.readLine();
            str = tripsFile.readLine();
            while (str != null) {
                Trip mTrip = new Trip(str.split(","));
                route.get(mTrip.route_id).addTrip(mTrip);
                str = tripsFile.readLine();
            }
            BufferedReader stopTimesFile = new BufferedReader(new FileReader(new File(filePath + "/stop_times.txt")));
            str = stopTimesFile.readLine();
            str = stopTimesFile.readLine();
            while (str != null) {
                StopTime mStopTime = new StopTime(str.split(","));
                for(Route r : route.values()){
                    r.addStopTime(mStopTime);
                }
                str = stopTimesFile.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void parse2Oudia(String[] downRoute,String[] upRoute,String outpath){

        //使用する下り列車
        ArrayList<GtfsTrain> downGtfsTrain =new ArrayList<>();
        //使用する上り列車
        ArrayList<GtfsTrain> upGtfsTrain =new ArrayList<>();
        int maxStops=0;
        GtfsTrain maxTrain=null;
        for(String downRouteID:downRoute){
            for(Trip t: route.get(downRouteID).trips.values()){
                GtfsTrain train=new GtfsTrain(t,0,stop);
                if(train.stopTimes.size()>maxStops){
                    maxStops=train.stopTimes.size();
                    maxTrain=train;
                }
                downGtfsTrain.add(train);

            }
        }
        for(String upRouteID:upRoute){
            for(Trip t: route.get(upRouteID).trips.values()){
                GtfsTrain train=new GtfsTrain(t,1,stop);
                if(train.stopTimes.size()>maxStops){
                    maxStops=train.stopTimes.size();
                    maxTrain=train;
                }
                upGtfsTrain.add(train);
            }
        }
        System.out.println(maxStops);
        System.out.println(maxTrain);
        ArrayList<String>stationList=new ArrayList<>();
        for(String s :maxTrain.station){
            stationList.add(s);
        }

        for(GtfsTrain train :downGtfsTrain){
            int pos=0;
            for(int i=0;i<train.station.size();i++){
                if(!stationList.subList(pos,stationList.size()).contains(train.station.get(i))){
                    if(i==0){
                        stationList.add(0,train.station.get(i));
                    }else{
                        stationList.add(pos+1,train.station.get(i));
                    }
                    pos++;
                }else{
                    pos+=stationList.subList(pos,stationList.size()).indexOf(train.station.get(i));
                }

            }
        }
        for(GtfsTrain train :upGtfsTrain){
            int pos=0;
            for(int i=0;i<train.station.size();i++){
                if(!stationList.subList(pos,stationList.size()).contains(train.station.get(i))){
                    if(i==0){
                        stationList.add(0,train.station.get(i));
                    }else{
                        stationList.add(pos+1,train.station.get(i));
                    }
                    pos++;
                }else{
                    pos+=stationList.subList(pos,stationList.size()).indexOf(train.station.get(i));
                }

            }
        }
        //stationList完成

        for(GtfsTrain train :downGtfsTrain){
            int pos=0;
            for(int i=0;i<train.station.size();i++){
                pos+=stationList.subList(pos,stationList.size()).indexOf(train.station.get(i));
                train.stationIndex.add(pos);
            }
        }
        for(GtfsTrain train :upGtfsTrain){
            int pos=0;
            for(int i=0;i<train.station.size();i++){
                pos+=stationList.subList(pos,stationList.size()).indexOf(train.station.get(i));
                train.stationIndex.add(pos);
            }
        }
        //各駅にindexを割り振ることに成功
        if(false) {
            //デバッグ用
            for (GtfsTrain train : downGtfsTrain) {
                for (int i = 0; i < stationList.size(); i++) {
                    if (train.stationIndex.contains(i)) {
                        String station = train.station.get(train.stationIndex.indexOf(i));
                        System.out.println(stop.get(station).stop_name);
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
                        System.out.println(stop.get(station).stop_name);
                    } else {
                        System.out.println("ㇾ");

                    }
                }
                System.out.println("eeee");
            }
        }

        DiaFile diaFile=new DiaFile();
        diaFile.name=route.get(downRoute[0]).route_name;
        for(int i=0;i<stationList.size();i++){
            Station station=new Station(diaFile);
            station.name=stop.get(stationList.get(i)).stop_name;
            if(i==0){
                station.setShowArival(1,true);
                station.setShowDepart(1,false);
            }
            if(i==stationList.size()-1){
                station.setShowArival(0,true);
                station.setShowDepart(0,false);
            }
            diaFile.station.add(station);
        }
        Diagram diagram=new Diagram(diaFile);
        diagram.trains[0]=new ArrayList<>();
        diagram.trains[1]=new ArrayList<>();
        diagram.name="GTFS";
        diaFile.diagram=new ArrayList<>();
        diaFile.diagram.add(diagram);

        for(GtfsTrain train : downGtfsTrain){
            diagram.trains[0].add(train.toOuDiaTrain(diaFile,0));
        }
        for(GtfsTrain train : upGtfsTrain){
            diagram.trains[1].add(train.toOuDiaTrain(diaFile,1));
        }
        diaFile.calcMinReqiredTime();
        diagram.sortTrain(0,0);
        diagram.sortTrain(1,diaFile.station.size()-1);
        try {
            diaFile.saveToFile(outpath, false);
        }catch (Exception e){
            e.printStackTrace();
        }




    }
}
