/*package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import eu.ace_design.island.game.actions.Echo;
 
public class DroneHandler {

    private Information currentInformation = new Information(0, new JSONObject());
    private Echo echo;

    private BatteryLevel batteryLevel; 

    private Compass currentDirection;
    private Compass temporaryDirection;

    private boolean echoAll;
    private boolean echoForward;
    private boolean echoRight;
    private boolean echoLeft;

    private final Logger logger = LogManager.getLogger();

    public DroneHandler(Integer battery, Compass direction) {
        this.currentDirection = direction;
        logger.info("set direction");

        this.echoAll = false; 
        this.echoForward = false;
        this.echoLeft = false;
        this.echoRight = false; 
    
        echo = new Echo(null); //potentially get rid of null later
        logger.info("created echo");

    }

    public void getInfo(Information info) {
        this.currentInformation = info;
        this.batteryLevel.decreaseBattery(info.getCost()); 
    }
    
    public int getBatteryLevelDrone() {
        return this.batteryLevel.getBatteryLevel();
    }

    public JSONObject echoLeft(Compass direction) {
        JSONObject decision = new JSONObject();
        String left = direction.left().toString();
        JSONObject leftJ = new JSONObject().put("direction", left);
        decision.put("action", "echo").put("parameters", leftJ);
        return decision;
    }

    public JSONObject echoRight(Compass direction) {
        JSONObject decision = new JSONObject();
        String right = direction.right().toString();
        JSONObject rightJ = new JSONObject().put("direction", right);
        decision.put("action", "echo").put("parameters", rightJ);
        return decision;
    }

    public JSONObject echoTowards(Compass direction) {
        JSONObject decision = new JSONObject();
        String forward = direction.toString();
        JSONObject forwardJ = new JSONObject().put("direction", forward);
        decision.put("action", "echo").put("parameters", forwardJ);
        return decision;
    }

    public JSONObject echoInAllDirections() {
 
        currentDirection = Compass.E;

        JSONObject decision = new JSONObject();

        if (!echoForward) {
            decision = echoTowards(currentDirection);
            this.echoForward = true;
            this.temporaryDirection = this.currentDirection;
        } else if (!echoRight) {
            decision = echoRight(currentDirection);
            this.echoRight = true;
            this.temporaryDirection = this.currentDirection.right();
        } else if (!echoLeft) {
            decision = echoLeft(currentDirection);
            this.echoLeft = true;
            this.temporaryDirection = this.currentDirection.left();
        }
 
        if (echoForward && echoRight && echoLeft) {
            echoAll = true;
        } 
 
        return decision;
    }
} */