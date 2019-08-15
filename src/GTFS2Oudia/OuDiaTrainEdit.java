package GTFS2Oudia;

import com.kamelong.OuDia.Train;

public interface OuDiaTrainEdit {
    void setStopType(int station,int value);
    void setAriTime(int station,int ariTime);
    void setDepTime(int station,int depTime);
    int getStopType(int station);
    Train getTrain();

}
