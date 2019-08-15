package com.kamelong.GTFS2Oudia;

import com.kamelong.OuDia.Train;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
public interface OuDiaTrainEdit {
    void setStopType(int station,int value);
    void setAriTime(int station,int ariTime);
    void setDepTime(int station,int depTime);
    int getStopType(int station);
    Train getTrain();

}
