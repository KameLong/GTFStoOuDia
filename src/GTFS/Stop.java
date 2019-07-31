package GTFS;

public class Stop {
    public String stop_id;
    public String stop_name;
    public String parent_station;
    public Stop(String[] line){
        stop_id=line[0];
        stop_name=line[1];
        if(line.length>6){
            parent_station=line[6];
        }else{
            parent_station=stop_id;
        }

    }
}
