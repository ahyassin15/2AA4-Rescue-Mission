package ca.mcmaster.se2aa4.island.teamXXX.data;

import org.json.JSONObject;

public class SensorData {
    private final int cost;
    private final JSONObject extras;

    public SensorData(int cost, JSONObject extras) {
        this.cost = cost;
        this.extras = extras;
    }

    public JSONObject getExtrasJson() {
        return extras;
    }

    public int getCost() {
        return cost;
    }
}