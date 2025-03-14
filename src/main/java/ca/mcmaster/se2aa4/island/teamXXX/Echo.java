package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class Echo {
    
    public boolean landFound = false;
    public JSONObject extras;

    public void initializeExtras(Information info) {

        if (!(info == null)){
            extras = info.getExtrasJson();
        } else {
            extras = new JSONObject();
        }
    }

    public boolean isFound() {
        
        if (extras.has("found")){
            String found = extras.getString("found");
            landFound = "GROUND".equals(found);

        } else {
            landFound = false;
        }
        
        return landFound;
    }

    public int distance() {
        
        int range;
        
        if (extras.has("range")){
            range = extras.getInt("range");
        } else {
            range = 0;
        }

        return range;
    }
}
