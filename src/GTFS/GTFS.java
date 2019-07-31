package GTFS;


import GTFS2Oudia.GtfsTrain;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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

        if(downGtfsTrain.contains(maxTrain)){
            downTrain.add(maxTrain);
            downGtfsTrain.remove(maxTrain);
        }else{
            upTrain.add(maxTrain);
            upGtfsTrain.remove(maxTrain);
        }


    }
}
