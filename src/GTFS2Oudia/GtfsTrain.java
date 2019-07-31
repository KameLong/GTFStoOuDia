package GTFS2Oudia;

import GTFS.Stop;
import GTFS.StopTime;
import GTFS.Trip;

import java.util.ArrayList;
import java.util.Map;

public class GtfsTrain extends Trip {
    public ArrayList<Integer> stationIndex=new ArrayList<>();
    public ArrayList<String> station=new ArrayList<>();

    public GtfsTrain(String[] lines) {
        super(lines);
    }
    public GtfsTrain(Trip trip, int direction, Map<String, Stop> stops){
        this.trip_id=trip.trip_id;
        this.route_id=trip.route_id;
        this.service_id=trip.service_id;
        this.stopTimes=trip.stopTimes;

        if(direction==0){
            for(int i=0;i<stopTimes.size();i++){
                stationIndex.add(i);
                station.add(stops.get(stopTimes.get(i).stop_id).parent_station);
            }
        }else{
            for(int i=0;i<stopTimes.size();i++){
                stationIndex.add(i);
                station.add(stops.get(stopTimes.get(stopTimes.size()-i-1).stop_id).parent_station);
            }

        }


    }
}
