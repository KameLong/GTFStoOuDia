package com.kamelong.OuDia;

import com.kamelong.tool.SDlog;

import java.io.PrintWriter;
import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * 列車１つを表します
 */
public class Train implements Cloneable{
    public static long count2=0;
    public static final int DEPART = 0;
    public static final int ARRIVE = 1;
    //下り
    public static final int BOUND_OUT=0;
    //上り
    public static final int BOUND_IN=1;

    public DiaFile diaFile;
    /**
     この列車の列車方向を示します。

     コンストラクタで決まります。
     */
    public int direction = BOUND_OUT;


    /**
     * 列車種別のindex
     */
    public int type = 0;
    /**
     * 列車番号
     */
    public String number = "";
    /**
     * 列車名
     */
    public String name = "";
    /**
     * 列車号数
     */
    public String count = "";
    /**
     * 備考
     */
    public String remark = "";

    /**
     この列車の各駅の時刻。
     要素数は、『駅』(DiaFile.stations) の数に等しくなります。
     添え字は『駅index』です。
     初期状態では、要素数は 0 となります。
     */
    public ArrayList<StationTime> stationTimes=new ArrayList<>();

    /**
     * デフォルトコンストラクタ
     * @param diaFile この列車が含まれるDiaFile
     * @param direction　進行方向　上り:1,下り:0
     */
    public Train(DiaFile diaFile, int direction) {
        this.diaFile = diaFile;
        this.direction = direction;
        this.stationTimes=new ArrayList<>();
        for(int i=0;i<diaFile.getStationNum();i++){
            stationTimes.add(new StationTime(this));
        }
    }

    /**
     * OuDiaファイルの１行を読み込みます
     */
    void setValue(String title,String value){
        switch (title) {
            case "Syubetsu":
                type = Integer.parseInt(value);
                break;
            case "Ressyabangou":
                number = value;
                break;
            case "Ressyamei":
                name = value;
                break;
            case "Gousuu":
                count = value;
                break;
            case "EkiJikoku":
                setOuDiaTime(value.split(",", -1));
                break;
            case "RessyaTrack":
                setOuDiaTrack(value.split(",", -1));
                break;
            case "Bikou":
                remark = value;
                break;
        }
        if(title.startsWith("Operation")){
            if(title.contains(".")){
                title=title.substring(9);
                String[] stations=title.split("\\.",-1);
                int index=getStationIndex(Integer.parseInt(stations[0].substring(0,stations[0].length()-1)));
                ArrayList<StationTimeOperation> operationList;
                if(stations[0].substring(stations[0].length()-1).equals("B")){
                    operationList=stationTimes.get(index).beforeOperations;
                }else{
                    operationList=stationTimes.get(index).afterOperations;
                }
                for(int i=1;i<stations.length;i++){
                    int index2=Integer.parseInt(stations[i].substring(0,stations[i].length()-1));
                    if(stations[i].substring(stations[i].length()-1).equals("B")){
                        operationList=operationList.get(index2).beforeOperation;
                    }else{
                        operationList=operationList.get(index2).afterOperation;
                    }
                }
                for(String s :value.split(",",-1)){
                    operationList.add(new StationTimeOperation(s));
                }


            }else{
                int index=getStationIndex(Integer.parseInt(title.substring(9,title.length()-1)));
                if(title.substring(title.length()-1).equals("B")){
                    for(String s:value.split(",")){
                        stationTimes.get(index).beforeOperations.add(new StationTimeOperation(s));
                    }
                }else{
                    for(String s:value.split(",")){
                        stationTimes.get(index).afterOperations.add(new StationTimeOperation(s));
                    }
                }

            }
        }
    }

    /**
     * Ekijikoku行の読み込みを行う
     * @param value
     */
    private void setOuDiaTime(String[] value) {
        stationTimes=new ArrayList<>();
        for(int i=0;i<diaFile.getStationNum();i++){
            stationTimes.add(new StationTime(this));
        }
        for (int i = 0; i < value.length && i < diaFile.getStationNum(); i++) {
            stationTimes.get(getStationIndex(i)).setStationTime(value[i]);
        }

    }

    /**
     * OuDia2ndの番線行の読み込みを行う。
     * @param value
     */
    private void setOuDiaTrack(String[] value) {
        for (int i = 0; i < value.length && i < stationTimes.size(); i++) {
            stationTimes.get(getStationIndex(i)).setTrack(value[i]);
        }
    }

    /**
     * OuDiaSecond形式で保存します
     * @param out
     */
    void saveToFile(PrintWriter out){
        out.println("Ressya.");
        if (direction == 0) {
            out.println("Houkou=Kudari");
        } else {
            out.println("Houkou=Nobori");
        }
        out.println("Syubetsu=" + type );
        if (number.length() > 0) {
            out.println("Ressyabangou=" + number );
        }
        if (name.length() > 0) {
            out.println("Ressyamei=" + name );
        }
        if (count.length() > 0) {
            out.println("Gousuu=" + count );
        }
        out.println("EkiJikoku=" + getEkijikokuOudia(true));
        if (remark.length() > 0) {
            out.println("Bikou=" + remark );
        }
        for(int i=0;i<stationTimes.size();i++){
            int index=getStationIndex(i);
            saveOperationToFile(out,stationTimes.get(index).beforeOperations,"Operation"+i+"B");
            saveOperationToFile(out,stationTimes.get(index).afterOperations,"Operation"+i+"A");
        }
        out.println(".");
    }
    private void saveOperationToFile(PrintWriter out,ArrayList<StationTimeOperation>target,String title){
        if(target.size()==0)return;
        String result=title+"=";
        for(int i=0;i<target.size();i++) {
            result+=target.get(i).getOuDiaString()+",";
        }
        out.println(result.substring(0,result.length()-1));

        for(int i=0;i<target.size();i++){
            if(target.get(i).beforeOperation.size()!=0){
                saveOperationToFile(out,target.get(i).beforeOperation,title+"."+i+"B");
            }
            if(target.get(i).afterOperation.size()!=0){
                saveOperationToFile(out,target.get(i).afterOperation,title+"."+i+"A");
            }
        }

    }
    void saveToOuDiaFile(PrintWriter out){
        out.println("Ressya.");
        if (direction == 0) {
            out.println("Houkou=Kudari");
        } else {
            out.println("Houkou=Nobori");
        }
        out.println("Syubetsu=" + type );
        if (number.length() > 0) {
            out.println("Ressyabangou=" + number );
        }
        if (name.length() > 0) {
            out.println("Ressyamei=" + name );
        }
        if (count.length() > 0) {
            out.println("Gousuu=" + count );
        }
        out.println("EkiJikoku=" + getEkijikokuOudia(false));
        if (remark.length() > 0) {
            out.println("Bikou=" + remark );
        }
        out.println(".");

    }

    /**
     * OuDia形式の駅時刻行を作成します。
     * @param secondFrag trueの時oudia2nd形式に対応します。
     * @return
     */
    private String getEkijikokuOudia(boolean secondFrag) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < stationTimes.size(); i++) {
            int station = getStationIndex(i);
            result.append(stationTimes.get(station).getOuDiaString(secondFrag));
            result.append(",");
        }
        return result.toString();
    }


    /**
     * 上り下りの時刻表駅順から、路線駅順を返します。
     * 下りの時は時刻表駅順は路線駅順と同じ
     * 上りの時は時刻表駅順は路線駅順の逆になります。
     */
    public int getStationIndex(int index){
        if(direction==0){
            return index;
        }else{
            return stationTimes.size()-index-1;
        }
    }

    @Override
    public Train clone(){
        try {
            Train result = (Train) super.clone();
            result.stationTimes = new ArrayList<>();
            for (StationTime time : stationTimes) {
                result.stationTimes.add(time.clone());
            }
            return result;
        }catch (CloneNotSupportedException e){
            SDlog.log(e);
            return new Train(diaFile,direction);
        }
    }

    /**
     * 駅数を返します
     */
    public int getStationNum(){
        return stationTimes.size();
    }
    public boolean isnull() {
        for (int i = 0; i <diaFile.getStationNum(); i++) {
            if (stationTimes.get(i).stopType!=0) return false;
        }
        return true;
    }


}
