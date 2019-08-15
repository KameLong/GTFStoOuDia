package com.kamelong.GTFS;

import java.util.HashMap;
import java.util.Map;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
public class Route {
    public String route_id;
    public String route_name;
    public String parent_route_id;
    public Map<String,Trip> trips=new HashMap<>();
    public Route(String[] list){
        route_id=list[0];
        route_name=list[3];
        parent_route_id=list[9];
    }
    public void addTrip(Trip mTrip){
        trips.put(mTrip.trip_id,mTrip);
    }
    public void addStopTime(StopTime time){
        for(Trip t :trips.values()){
            if(time.trip_id.equals(t.trip_id)){
                t.addStopTime(time);
            }
        }
    }

}
