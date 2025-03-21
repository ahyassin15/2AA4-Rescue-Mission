package ca.mcmaster.se2aa4.island.teamXXX;

import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CellTracker {

    //inner class to represent x,y position
    private static class Position {
        double x;
        double y;

        Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

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

    public void addCreek(String uid, double x, double y) {
        creeks.put(uid, new Position(x, y));
    }

    public void addEmergencySite(String uid, double x, double y) {
        emergencySites.put(uid, new Position(x, y));
    }

    public Set<String> getAllCreeks() {
        return creeks.keySet();
    }

    public Set<String> getAllEmergencySites() {
        return emergencySites.keySet();
    }

    public boolean hasCreek(String uid) {
        return creeks.containsKey(uid);
    }

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

        for (Map.Entry<String, Position> entry : creeks.entrySet()) {
            
            Position creekPos = entry.getValue();
            
            double dx = creekPos.x - site.x;
            double dy = creekPos.y - site.y;
            double distSquared = dx * dx + dy * dy;

            if (distSquared < minDist) {
                minDist = distSquared;
                nearestCreek = entry.getKey();
            }
        }

        return nearestCreek;
    }

    public void reset() {
        creeks.clear();
        emergencySites.clear();
    }

}
