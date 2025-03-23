package ca.mcmaster.se2aa4.island.teamXXX;

import ca.mcmaster.se2aa4.island.teamXXX.communication.ActionCommand;
import ca.mcmaster.se2aa4.island.teamXXX.core.Direction;
import ca.mcmaster.se2aa4.island.teamXXX.core.RescueDrone;
import ca.mcmaster.se2aa4.island.teamXXX.data.SensorData;
import ca.mcmaster.se2aa4.island.teamXXX.parsing.DirectionInterpreter;
import ca.mcmaster.se2aa4.island.teamXXX.parsing.ResponseParser;
import eu.ace_design.island.bot.IExplorerRaid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.StringReader;

public class Explorer implements IExplorerRaid {
    private final ResponseParser translator = new ResponseParser();
    private final Logger logger = LogManager.getLogger();
    private RescueDrone rescueDrone;
    private final DirectionInterpreter directionInterpreter = new DirectionInterpreter();
    public JSONObject extras;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n " + info.toString(2));
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing " + direction);
        logger.info("Battery level is " + batteryLevel);
        Direction currentDirection = directionInterpreter.translateDirection(direction);
        rescueDrone = new RescueDrone(batteryLevel, currentDirection);
        logger.info("finished initializing");
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        SensorData info = translator.translate(response);
        logger.info("** Response received:\n" + response.toString(2));
        int cost = response.getInt("cost");
        logger.info("The cost of the action was " + cost);
        String status = response.getString("status");
        logger.info("The status of the drone is " + status);
        JSONObject extraInfo = response.getJSONObject("extras");
        extras = extraInfo;
        logger.info("Additional information received: " + extraInfo);
        rescueDrone.getInfo(info);
    }

    @Override
    public String takeDecision() {
        JSONObject decision;
        ActionCommand command = rescueDrone.makeDecision();
        decision = command.commandTranslator();
        logger.info("Battery: " + rescueDrone.getBatteryLevelDrone());
        logger.info(decision.toString());
        return decision.toString();
    }

    @Override
    public String deliverFinalReport() {
        logger.info("The closest creek is " + rescueDrone.getClosestInlet());
        return rescueDrone.getClosestInlet();
    }
}