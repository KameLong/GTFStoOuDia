package com.kamelong.OuDiaEditor;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.Train;

import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
public class DiagramSorter {
    /**
     * 時刻表を並び替える。
     * 並び替えに関しては、基準駅の通過時刻をもとに並び替えた後
     * @param direction 並び替え対象方向
     * @param stationNumber 並び替え基準駅
     */
    public static void sortTrain(Diagram diagram, int direction, int stationNumber) {
        DiaFile diaFile=diagram.diaFile;

        TrainEditor[] trainList=new TrainEditor[diagram.trains[direction].size()];
        for(int i=0;i<trainList.length;i++){
            trainList[i]=new TrainEditor(diagram.trains[direction].get(i));

        }
        ArrayList<Integer>lineTime=new DiaFileEditor(diagram.diaFile).getLineTime();

        //ソートする前の順番を格納したクラス
        ArrayList<Integer> sortBefore=new ArrayList<>();
        //ソートした後の順番を格納したクラス
        ArrayList<Integer> sortAfter=new ArrayList<>();

        for(int i=0;i<trainList.length;i++){
            sortBefore.add(i);
        }

        for(int i=0;i<sortBefore.size();i++) {
            if (trainList[sortBefore.get(i)].getPredictionTime(stationNumber,lineTime)>0&&!trainList[sortBefore.get(i)].checkDoubleDay()) {
                //今からsortAfterに追加する列車の基準駅の時間
                int baseTime=trainList[sortBefore.get(i)].getPredictionTime(stationNumber,lineTime);
                int j;
                for(j=sortAfter.size();j>0;j--) {
                    if(trainList[sortAfter.get(j-1)].getPredictionTime(stationNumber,lineTime)<baseTime){
                        break;
                    }
                }
                sortAfter.add(j,sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }
        }       //この時点で基準駅に予測時間を設定できるものはソートされている
        if(direction==0) {
            //ここからは基準駅を通らない列車についてソートを行う
            //基準駅より後方で運行する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber; station > 0; station--) {
                if(diaFile.station.get(station-1).getBorder()){
                    searchStation:
                    for(int i=station;i>0;i--){
                        //境界線がある駅の次の駅が分岐駅である可能性を探る
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)){
                            addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{i-1,station},lineTime);
                            for(int j=i;j<station;j++){
                                addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{j},lineTime);
                            }
                            station=i;
                            continue baseStation;
                        }
                    }
                    for(int i=station;i<diaFile.getStationNum();i++){
                        //境界線がある駅が分岐駅である可能性を探る
                        if(diaFile.station.get(station-1).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{station-1,i},lineTime);
                            for(int j=i;j<station;j++){
                                addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{j},lineTime);
                            }
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{station-1},lineTime);
            }
//            基準駅より後方から出発する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber+1; station < diaFile.getStationNum(); station++) {
                if(diaFile.station.get(station-1).getBorder()){
                    for(int i=station;i>0;i--){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station,i-1},lineTime);
                            continue baseStation;
                        }
                    }
                }
                if(diaFile.station.get(station).getBorder()){
                    for(int i=station+1;i<diaFile.getStationNum();i++){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{i,station},lineTime);
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station},lineTime);

            }
        }else{
            //ここからは基準駅を通らない列車についてソートを行う
            //基準駅より前方で運行する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber; station > 0; station--) {
                if(diaFile.station.get(station-1).getBorder()){
                    for(int i=station;i>0;i--){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{i-1,station},lineTime);
                            continue baseStation;
                        }
                    }
                }
                if(diaFile.station.get(station).getBorder()){
                    for(int i=station+1;i<diaFile.getStationNum();i++){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station,i},lineTime);
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station},lineTime);
            }


            //基準駅より後方から出発する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber+1; station <diaFile. getStationNum(); station++) {
                if(diaFile.station.get(station-1).getBorder()) {
                    for (int i = station; i > 0; i--) {
                        if (diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)) {
                            addTrainInSort1(sortBefore, sortAfter, trainList, new int[]{station, i - 1},lineTime);
                            continue baseStation;
                        }
                    }
                }
                if(diaFile.station.get(station).getBorder()){
                    for(int i=station+1;i<diaFile.getStationNum();i++){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{i,station},lineTime);
                            continue baseStation;
                        }
                    }

                }
                addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{station},lineTime);
            }

        }

        for(int i=0;i<sortBefore.size();i++) {
            if (trainList[sortBefore.get(i)].getPredictionTime(stationNumber,lineTime)>0) {
                //今からsortAfterに追加する列車の基準駅の時間
                int baseTime=trainList[sortBefore.get(i)].getPredictionTime(stationNumber,lineTime);
                int j;
                for(j=sortAfter.size();j>0;j--) {
                    if(trainList[sortAfter.get(j-1)].getPredictionTime(stationNumber,lineTime)>0&&trainList[sortAfter.get(j-1)].getPredictionTime(stationNumber,lineTime)<baseTime){
                        break;
                    }
                }
                sortAfter.add(j,sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }
        }

        sortAfter.addAll(sortBefore);
        ArrayList<Train> trainAfter=new ArrayList<>();
        for(int i=0;i<sortAfter.size();i++){
            trainAfter.add(trainList[sortAfter.get(i)].getTrain());
        }
        diagram.trains[direction]=trainAfter;


    }

    private static void addTrainInSort1(ArrayList<Integer> sortBefore, ArrayList<Integer> sortAfter, TrainEditor[] trains, int station[],ArrayList<Integer>lineTime){
        for (int i = sortBefore.size(); i >0; i--) {
            int baseTime = trains[sortBefore.get(i-1)].getAriTime(station[0]);
            if (baseTime < 0||trains[sortBefore.get(i-1)].checkDoubleDay()) {
                continue;
            }
            int j =0;
            boolean frag = false;

            for (j = 0; j < sortAfter.size(); j++) {

                int sortTime;
                if(station.length==2) {
                    sortTime = Math.max(trains[sortAfter.get(j)].getPredictionTime(station[0],lineTime), trains[sortAfter.get(j)].getPredictionTime(station[1],lineTime));
                }else{
                    sortTime =trains[sortAfter.get(j)].getPredictionTime(station[0],lineTime);
                }
                if (sortTime < 0) {
                    continue;
                }
                frag = true;
                if (sortTime >= baseTime) {
                    break;
                }
            }
            if (frag) {
                sortAfter.add(j, sortBefore.get(i - 1));
                sortBefore.remove(i-1);
            }
        }
    }
    private static void  addTrainInSort2(ArrayList<Integer> sortBefore, ArrayList<Integer> sortAfter, TrainEditor[] trains, int[] station,ArrayList<Integer>lineTime){
        for (int i = 0; i < sortBefore.size(); i++) {
            int baseTime = trains[sortBefore.get(i)].getDepTime(station[0]);
            if (baseTime < 0||trains[sortBefore.get(i)].checkDoubleDay()) {
                continue;
            }
            int j = 0;
            boolean frag = false;

            for (j = sortAfter.size(); j > 0; j--) {
                int sortTime;
                if(station.length==2){
                    if(trains[sortAfter.get(j - 1)].getPredictionTime(station[0],Train.ARRIVE,lineTime)>0&&trains[sortAfter.get(j - 1)].getPredictionTime(station[1],Train.ARRIVE,lineTime)>0) {
                        sortTime = Math.min(
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[0], Train.ARRIVE,lineTime),
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[1], Train.ARRIVE,lineTime));
                    }else{
                        sortTime = Math.max(
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[0], Train.ARRIVE,lineTime),
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[1], Train.ARRIVE,lineTime));

                    }
                }else{
                    sortTime = trains[sortAfter.get(j - 1)].getPredictionTime(station[0],Train.ARRIVE,lineTime);
                }
                if (sortTime < 0) {
                    continue;
                }
                frag = true;
                if (sortTime <= baseTime) {
                    break;
                }
            }
            if (frag) {
                sortAfter.add(j, sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }

        }

    }


}
