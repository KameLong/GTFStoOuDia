import GTFS.GTFS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;

public class Main {
    public static void main(String[] args){
        try{

            GTFS gtfs=new GTFS("");
            System.out.println(gtfs);
            BufferedReader inputFile = new BufferedReader(new FileReader(new File( "input")));
            String str = inputFile.readLine();
            String oudiaID="";
            ArrayList<String>down=new ArrayList<>();
            ArrayList<String>up=new ArrayList<>();
            String oudiaName="";
            while (str != null) {
                String[] lines=str.split("\t");
                if(!lines[0].equals(oudiaID)){
                    if(oudiaID.length()!=0){
                        gtfs.parse2Oudia(down.toArray(new String[]{}),up.toArray(new String[]{}),oudiaName+".oud2");
                        down=new ArrayList<>();
                        up=new ArrayList<>();
                    }
                }
                if(lines[3].equals("0")){
                    down.add(lines[1]);
                }else{
                    up.add(lines[1]);
                }
                oudiaID=lines[0];
                oudiaName=lines[4];
                str=inputFile.readLine();
            }
            gtfs.parse2Oudia(down.toArray(new String[]{}),up.toArray(new String[]{}),oudiaName+".oud2");

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
