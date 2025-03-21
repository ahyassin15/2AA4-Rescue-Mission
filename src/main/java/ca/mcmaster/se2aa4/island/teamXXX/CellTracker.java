package ca.mcmaster.se2aa4.island.teamXXX;

import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//singleton class to track the coordinates of discovered creeks and emergency sites
public class CellTracker {

    //inner class to represent x,y position
    static class Position {
        double x;
        double y;

        //constructor to initialize position
        Position(double x, double y) {
            this.x = x;
            this.y = y;
        }

        //string formatted representation of the position
        @Override
        public String toString() {
            return "(" + String.format("%.2f", x) + ", " + String.format("%.2f", y) + ")";
        }
    }

    //singleton instance of CellTracker
    private static CellTracker instance;

    //maps from UID to position
    private Map<String, Position> creeks = new HashMap<>();
    private Map<String, Position> emergencySites = new HashMap<>();

    private CellTracker() {}

    public static synchronized CellTracker getInstance() {
        if (instance == null) {
            instance = new CellTracker();
        }
        return instance;
    }

    //adds creek with its UID and position
    public void addCreek(String uid, double x, double y) {
        creeks.put(uid, new Position(x, y));
    }

    //adds emergency site with its UID and position
    public void addEmergencySite(String uid, double x, double y) {
        emergencySites.put(uid, new Position(x, y));
    }

    //returns a set of all discovered creek UIDs
    public Set<String> getAllCreeks() {
        return creeks.keySet();
    }

    //returns a set of all discovered emergency site UIDs
    public Set<String> getAllEmergencySites() {
        return emergencySites.keySet();
    }

    //returns the position of a specific creek by UID
    public Position getCreekPosition(String creekId) {
        return creeks.get(creekId);
    }
    
    //returns the position of a specific emergency site by UID
    public Position getEmergencySitePosition(String siteId) {
        return emergencySites.get(siteId);
    }
    
    //checks creek's UID to see if it exists
    public boolean hasCreek(String uid) {
        return creeks.containsKey(uid);
    }

    //checks emergency site's UID to see if it exists
    public boolean hasEmergencySite(String uid) {
        return emergencySites.containsKey(uid);
    }

    //find the nearest creek to a given emergency site by UID
    public String findNearestCreekToSite(String emergencySiteId) {
        
        if (!emergencySites.containsKey(emergencySiteId)) {
            return null;
        }

        Position site = emergencySites.get(emergencySiteId);
        String nearestCreek = null;
        double minDist = Double.MAX_VALUE;

        //iterate through all creeks to find the one with the smallest distance
        for (Map.Entry<String, Position> entry : creeks.entrySet()) {
            
            Position creekPos = entry.getValue();
            
            double dx = creekPos.x - site.x;
            double dy = creekPos.y - site.y;
            double distSquared = dx*dx + dy*dy;

            //if distSquared is smaller than minDist then update distSquared as minDist
            if (distSquared < minDist) {
                minDist = distSquared;
                nearestCreek = entry.getKey();
            }
        }

        return nearestCreek;
    }

    //clears all stored creeks and emergency sites
    public void reset() {
        creeks.clear();
        emergencySites.clear();
    }

}
