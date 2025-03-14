package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class Information {

    private int cost;
    
    private JSONObject extras;
 
    public Information(int cost, JSONObject extras) {
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
