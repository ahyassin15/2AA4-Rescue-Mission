package ca.mcmaster.se2aa4.island.teamXXX.communication;

import ca.mcmaster.se2aa4.island.teamXXX.core.Direction;
import org.json.JSONObject;

public class ActionCommand {
    private final String value;
    private Direction currentDirection = Direction.NONE;

    public ActionCommand(String value) {
        this.value = value;
    }

    public ActionCommand(String value, Direction currentDirection) {
        this.value = value;
        this.currentDirection = currentDirection;
    }

    public String getValue() {
        return this.value;
    }

    public Direction getDirection() {
        return this.currentDirection;
    }

    public JSONObject commandTranslator() {
        JSONObject decision = new JSONObject();
        String key = "action";
        if (currentDirection.toString().equals("NONE")) {
            decision.put(key, value);
        } else {
            decision.put(key, value);
            decision.put("parameters", (new JSONObject()).put("direction", currentDirection.toString()));
        }
        return decision;
    }
}