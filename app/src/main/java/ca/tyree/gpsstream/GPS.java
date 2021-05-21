package ca.tyree.gpsstream;

import android.location.Location;

import java.util.List;

public class GPS extends MainActivity {

    private static GPS singular;
    private List<Location> myLocations;


    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations){
        this.myLocations = myLocations;
    }

    public GPS getInstance() {
        return singular;
    }

    public void onCreate(){
        //super.onCreate();
        singular = this;
    }
}
