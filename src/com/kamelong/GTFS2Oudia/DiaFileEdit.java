package com.kamelong.GTFS2Oudia;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.Station;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
public interface DiaFileEdit {
    Station addNewStation();
    Diagram addNewDiagram();
    DiaFile getDiaFile();
}
