import GTFS.GTFS;

public class Main {
    public static void main(String[] args){
        try{

            GTFS gtfs=new GTFS("C:\\Users\\kame\\Downloads\\20190529GTFS-dia\\gtfs-tokachi");
            System.out.println(gtfs);
            gtfs.parse2Oudia();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
