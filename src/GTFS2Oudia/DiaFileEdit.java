package GTFS2Oudia;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.Station;

public interface DiaFileEdit {
    Station addNewStation();
    Diagram addNewDiagram();
    DiaFile getDiaFile();
}
