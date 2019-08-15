package com.kamelong.OuDiaEditor;

import com.kamelong.GTFS2Oudia.DiaFileEdit;
import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.Station;

import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
public class DiaFileEditor implements DiaFileEdit {
    private DiaFile diaFile;
    public DiaFileEditor(DiaFile diaFile){
        this.diaFile=diaFile;
    }
    @Override
    public DiaFile getDiaFile() {
        return diaFile;
    }

    @Override
    public Station addNewStation() {
        Station station=new Station(diaFile);
        diaFile.station.add(station);
        return station;
    }

    @Override
    public Diagram addNewDiagram() {
        Diagram dia=new Diagram(diaFile);
        diaFile.diagram.add(dia);
        return dia;
    }
    public ArrayList<Integer> getLineTime(){
        ArrayList<Integer>result=new ArrayList<>();
        result.add(0);
        int nowTime=0;
        for(int i=0;i<diaFile.getStationNum()-1;i++){
            nowTime+=getMinReqiredTime(i,i+1);
            result.add(nowTime);
        }
        return result;

    }

    public int getDiaNum(){
        return diaFile.diagram.size();
    }
    public int getTrainSize(int diaIndex,int direction){
        return diaFile.diagram.get(diaIndex).trains[direction].size();
    }
    public TrainEditor getTrainEdit(int diaIndex,int direction,int trainIndex){
        return new TrainEditor(diaFile.diagram.get(diaIndex).trains[direction].get(trainIndex));
    }

    /**
     *  駅間最小所要時間を返す。
     *  startStatioin endStationの両方に止まる列車のうち、
     *  所要時間（着時刻-発時刻)の最も短いものを秒単位で返す。
     *  ただし、駅間所要時間が90秒より短いときは90秒を返す。
     *
     *  startStation endStationは便宜上区別しているが、順不同である。
     * @param startStation
     * @param endStation
     * @return time(second)
     */
    public int getMinReqiredTime(int startStation,int endStation){
        int result=360000;
        for(int i=0;i<getDiaNum();i++){
            if(diaFile.diagram.get(i).name.equals("基準運転時分")){
                result=360000;
                for(int train=0;train<getTrainSize(i,0);train++){
                    int value=getTrainEdit(i,0,train).getRequiredTime(startStation,endStation);
                    if(value>0&&(getTrainEdit(i,0,train).getStopType(startStation)!=1||getTrainEdit(i,0,train).getStopType(endStation)!=1)){
                        value+=120;
                    }
                    if(value>0&&result>value){
                        result=value;
                    }
                }
                for(int train=0;train<getTrainSize(i,1);train++){
                    int value=this.getTrainEdit(i,1,train).getRequiredTime(startStation,endStation);
                    if(value>0&&(getTrainEdit(i,1,train).getStopType(startStation)!=1||getTrainEdit(i,1,train).getStopType(endStation)!=1)){
                        value+=120;
                    }

                    if(value>0&&result>value){
                        result=value;
                    }
                }
                if(result==360000){
                    result=120;
                }
                return result;
            }
        }
        for(int i=0;i<getDiaNum();i++){

            for(int train=0;train<getTrainSize(i,0);train++){
                int value=getTrainEdit(i,0,train).getRequiredTime(startStation,endStation);
                if(value>0&&(getTrainEdit(i,0,train).getStopType(startStation)!=1||getTrainEdit(i,0,train).getStopType(endStation)!=1)){
                    value+=120;
                }

                if(value>0&&result>value){
                    result=value;
                }
            }
            for(int train=0;train<getTrainSize(i,1);train++){
                int value=getTrainEdit(i,1,train).getRequiredTime(startStation,endStation);
                if(value>0&&(getTrainEdit(i,1,train).getStopType(startStation)!=1||getTrainEdit(i,1,train).getStopType(endStation)!=1)){
                    value+=120;
                }

                if(value>0&&result>value){
                    result=value;
                }
            }
        }
        if(result==360000){
            result=120;
        }
        if(result<90){
            result=90;
        }

        return result;
    }

}
