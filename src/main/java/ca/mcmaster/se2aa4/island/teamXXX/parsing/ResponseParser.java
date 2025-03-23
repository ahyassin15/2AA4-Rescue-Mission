package ca.mcmaster.se2aa4.island.teamXXX.parsing;

import ca.mcmaster.se2aa4.island.teamXXX.data.SensorData;
import org.json.JSONObject;

public class ResponseParser {
    public SensorData translate(JSONObject response) {
        return new SensorData(response.getInt("cost"), response.getJSONObject("extras"));
    }
}