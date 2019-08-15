package com.kamelong.OuDiaEditor;

import com.kamelong.GTFS2Oudia.OuDiaTrainEdit;
import com.kamelong.OuDia.StationTime;
import com.kamelong.OuDia.Train;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
public class TrainEditor implements OuDiaTrainEdit {
    private Train train;
    public TrainEditor(Train train){
        this.train=train;
    }

    @Override
    public void setStopType(int station, int value) {
        train.stationTimes.get(station).stopType=value;
    }
    @Override
    public int getStopType(int station) {
        return train.stationTimes.get(station).stopType;
    }

    @Override
    public void setAriTime(int station, int ariTime) {
        train.stationTimes.get(station).ariTime=ariTime;
    }
    public int getAriTime(int station){
        return train.stationTimes.get(station).ariTime;
    }
    public int getADTime(int station){
        if(timeExist(station,1)){
            return getAriTime(station);
        }
        return getDepTime(station);
    }

    @Override
    public void setDepTime(int station, int depTime) {
        train.stationTimes.get(station).depTime=depTime;
    }
    public int getDepTime(int station){
        return train.stationTimes.get(station).depTime;
    }
    public int getDATime(int station){
        if(timeExist(station,0)){
            return getDepTime(station);
        }
        return getAriTime(station);
    }


    @Override
    public Train getTrain() {
        return train;
    }
    public boolean timeExist(int station,int AD){
        return train.stationTimes.get(station).timeExist(AD);
    }
    public boolean timeExist(int station){
        return train.stationTimes.get(station).timeExist();
    }

    public int getPredictionTime(int station, int AD,ArrayList<Integer>lineTime) {

        if (AD == 1 && timeExist(station,1)) {
            return getAriTime(station);
        }
        if (timeExist(station)) {
            return getDATime(station);
        }
        if (getStopType(station) == StationTime.STOP_TYPE_NOVIA || getStopType(station) == StationTime.STOP_TYPE_PASS) {
            //通過時間を予測します
            int afterTime = -1;//後方の時刻あり駅の発車時間
            int beforeTime = -1;//後方の時刻あり駅の発車時間
            int afterMinTime = 0;//後方の時刻あり駅までの最小時間
            int beforeMinTime = 0;//前方の時刻あり駅までの最小時間


            //対象駅より先の駅で駅時刻が存在する駅までの最小所要時間
            for (int i = station + 1; i < train.getStationNum(); i++) {
                if (getStopType(i) == StationTime.STOP_TYPE_NOSERVICE || getStopType(i) == StationTime.STOP_TYPE_NOVIA || getStopType(i - 1) == StationTime.STOP_TYPE_NOSERVICE || getStopType(i - 1) == StationTime.STOP_TYPE_NOVIA) {
                    continue;
                }
                afterMinTime = afterMinTime + lineTime.get(i) - lineTime.get(i - 1);
                if (timeExist(i)) {
                    if(train.direction==0){
                        afterTime = getADTime(i);
                    }else{
                        afterTime = getDATime(i);
                    }
                    break;
                }
            }
            if (afterTime < 0) {
                SDlog.log("予測時間", "afterTime");
                //対象駅より先の駅で駅時刻が存在する駅がなかった
                return -1;
            }
            //対象駅より前方の駅で駅時刻が存在する駅までの最小所要時間と駅時刻
            int startStation = 0;
            for (int i = station; i > 0; i--) {
                if (getStopType(i) == StationTime.STOP_TYPE_NOSERVICE || getStopType(i) == StationTime.STOP_TYPE_NOVIA || getStopType(i - 1) == StationTime.STOP_TYPE_NOSERVICE || getStopType(i - 1) == StationTime.STOP_TYPE_NOVIA) {
                    continue;
                }
                beforeMinTime = beforeMinTime + lineTime.get(i) - lineTime.get(i - 1);
                if (timeExist(i - 1)) {
                    if(train.direction==0){
                        beforeTime = getDATime(i - 1);
                    }else{
                        beforeTime = getADTime(i - 1);
                    }

                    startStation = i - 1;
                    break;
                }
            }
            if (beforeTime < 0) {
                return -1;
            }
            return getDepTime(startStation) + (afterTime - beforeTime) * beforeMinTime / (afterMinTime + beforeMinTime);
        }
        return -1;
    }

    public int getPredictionTime(int station,ArrayList<Integer> lineTime) {
        return getPredictionTime(station, 0,lineTime);
    }
    /**
     * 日付をまたいでいる列車かどうか確認する。
     * 12時間以上さかのぼる際は日付をまたいでいると考えています。
     */
    public boolean checkDoubleDay(){
        int time = getDepTime(startStation());
        for (int i = startStation(); i < endStation(); i++) {
            if (timeExist(i)) {
                if (getDepTime(i) - time < -12 * 60 * 60 || getDepTime(i) - time > 12 * 60 * 60) {
                    SDlog.log("doubleDay");
                    return true;
                }
                time = getDepTime(i);
            }
        }
        return false;
    }

    /**
     * 始発駅を返す。
     * これ列車に時刻が存在しなければ-1を返す
     */
    public int startStation() {
        switch (train.direction) {
            case 0:
                for (int i = 0; i < train.getStationNum(); i++) {
                    if (timeExist(i)) return i;
                }
                break;
            case 1:
                for (int i = train.getStationNum() - 1; i >= 0; i--) {
                    if (timeExist(i)) return i;
                }
                break;
        }
        return -1;
    }

    /**
     * 終着駅を返す。
     * これ列車に時刻が存在しなければ-1を返す
     *
     * @return
     */
    public int endStation() {
        switch (train.direction) {
            case 0:
                for (int i =  train.getStationNum() - 1; i >= 0; i--) {
                    if (timeExist(i)) return i;
                }
                break;
            case 1:
                for (int i = 0; i <  train.getStationNum(); i++) {
                    if (timeExist(i)) return i;
                }
                break;
        }
        return -1;
    }
    public int getRequiredTime(int startStation, int endStation) {
        if (timeExist(startStation) && timeExist(endStation)) {
            if ((endStation - startStation) * (1 - train.direction * 2) > 0) {
                return getADTime(endStation) - getDATime(startStation);

            } else {
                return getADTime(startStation) - getDATime(endStation);

            }
        } else {
            return -1;
        }

    }


}
