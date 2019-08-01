package GTFS;


import GTFS2Oudia.GtfsTrain;

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
    public void parse2Oudia(){
        String[] downRoute={"R011200001000000"};
        String[] upRoute={"R011200001010000"};
        String outpath="test.oud";

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
        ArrayList<GtfsTrain> upTrain=new ArrayList<>();
        ArrayList<GtfsTrain> downTrain=new ArrayList<>();
        ArrayList<String>stationList=new ArrayList<>();
        for(String s :maxTrain.station){
            stationList.add(s);
        }
        int decideNum=stationList.size();
        for(GtfsTrain train :downGtfsTrain){
            for(String station:train.station){
                if(!stationList.contains(station)){
                    stationList.add(station);
                }
            }
        }
        for(GtfsTrain train :upGtfsTrain){
            for(String station:train.station){
                if(!stationList.contains(station)){
                    stationList.add(station);
                }
            }
        }

        //stationListには全ての駅が含まれる
        //ある停留所に向けてどこからバスが来たか
        HashSet<Integer>[]from=new HashSet[stationList.size()];
        for(int i=0;i<from.length;i++){
            from[i]=new HashSet<Integer>();
        }
        for(GtfsTrain train :downGtfsTrain){
            int pos=0;
            for(int i=0;i<train.station.size()-1;i++){
                int startStation=stationList.subList(pos,stationList.size()).indexOf(train.station.get(i));
                if(startStation<0){
                    startStation=stationList.lastIndexOf(train.station.get(i));
                }
                int endStation=stationList.subList(startStation,stationList.size()).indexOf(train.station.get(i+1));
                if(endStation<0){
                    endStation=stationList.lastIndexOf(train.station.get(i));
                }
                from[endStation].add(startStation);
            }
        }

        ArrayList<String>newList=new ArrayList[];
        for(int i=decideNum;i<stationList.size();i++){

            for(int j=0;j<i;j++){
                if(arrow[i][j]>0){
                    for(int k=i-1;k>=0;k--){
                        if(arrow[k][i]>0){
                            //行列入れ替え

                        }
                    }

                }

            }
        }

        if(downGtfsTrain.contains(maxTrain)){
            downTrain.add(maxTrain);
            downGtfsTrain.remove(maxTrain);
        }else{
            upTrain.add(maxTrain);
            upGtfsTrain.remove(maxTrain);
        }

        for(GtfsTrain train :downGtfsTrain){
            ArrayList<Integer>fitList=new ArrayList<>();
            for(int i=0;i<stationList.size();i++){
                int fit=0;
                for(int j=0;(j<stationList.size()-i)&&(j<train.station.size());j++){
                    if(stationList.get(j+i).equals(train.station.get(j))){
                        fit++;
                    }
                }
                fitList.add(fit);
            }
        }



    }
}
