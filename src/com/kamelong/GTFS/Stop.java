package com.kamelong.GTFS;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
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
