package ca.mcmaster.se2aa4.island.teamXXX.data;

import ca.mcmaster.se2aa4.island.teamXXX.reporting.NearestInletFinder;
import ca.mcmaster.se2aa4.island.teamXXX.reporting.FinalReport;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResultAggregator {
    private boolean groundFound = false;
    private boolean emergencySiteFound = false;
    private final ArrayList<Integer> inletXs = new ArrayList<>();
    private final ArrayList<Integer> inletYs = new ArrayList<>();
    private int emergencyX = 0;
    private int emergencyY = 0;
    private String emergencyId;
    private JSONObject extras;
    public ArrayList<String> inletIds = new ArrayList<>();

    public void initializeExtras(SensorData info) {
        extras = (info != null) ? info.getExtrasJson() : new JSONObject();
    }

    public boolean isFound() {
        boolean landFound = false;
        if (extras.has("found")) {
            String found = extras.getString("found");
            landFound = "GROUND".equals(found);
        }
        return landFound;
    }

    public boolean groundIsFound() {
        if (extras.has("biomes")) {
            JSONArray biomesArray = extras.getJSONArray("biomes");
            if (biomesArray.isEmpty()) {
                groundFound = false;
            } else {
                for (int i = 0; i < biomesArray.length(); i++) {
                    String biome = biomesArray.getString(i);
                    if (!"OCEAN".equals(biome)) {
                        groundFound = true;
                        break;
                    }
                }
            }
        } else {
            groundFound = false;
        }
        return groundFound;
    }

    public boolean inletIsFound() {
        boolean inletFound;
        if (extras.has("creeks")) {
            JSONArray creeksArray = extras.getJSONArray("creeks");
            if (creeksArray.isEmpty()) {
                inletFound = false;
            } else {
                for (int i = 0; i < creeksArray.length(); i++) {
                    String inlet = creeksArray.getString(i);
                    inletIds.add(inlet);
                }
                inletFound = true;
            }
        } else {
            inletFound = false;
        }
        return inletFound;
    }

    public int distance() {
        return extras.optInt("range", 0);
    }

    public boolean emergencySiteIsFound() {
        if (extras.has("sites")) {
            JSONArray sitesArray = extras.getJSONArray("sites");
            if (!sitesArray.isEmpty()) {
                emergencySiteFound = true;
                emergencyId = sitesArray.getString(0);
            } else {
                emergencySiteFound = false;
            }
        } else {
            emergencySiteFound = false;
        }
        return emergencySiteFound;
    }

    public void storeCoordinates(int x, int y) {
        inletXs.add(x);
        inletYs.add(y);
    }

    public void storeCoordinatesEmergency(int x, int y) {
        emergencyX = x;
        emergencyY = y;
    }

    public FinalReport generateFinalReport() {
        int nearestIndex = inletIds.indexOf(getClosestInlet());
        int nearestX = inletXs.get(nearestIndex);
        int nearestY = inletYs.get(nearestIndex);
        return new FinalReport(inletIds, inletXs, inletYs, emergencyId, emergencyX, emergencyY,
                inletIds.get(nearestIndex), nearestX, nearestY);
    }

    public String getClosestInlet() {
        NearestInletFinder calc = new NearestInletFinder(inletXs, inletYs, inletIds, emergencyX, emergencyY);
        return calc.calculateNearestInlet();
    }
}