package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    boolean fly = true;
    int num = 0;
    private int range = -1;
    private JSONObject extras = new JSONObject();

    private final Logger logger = LogManager.getLogger();

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));

        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");

        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();

        if (num < 51) {
            decision.put("action", fly ? "scan" : "fly");
            fly = !fly;
        } 
        else if (num >= 51 && num <= 54) {
            String direction = switch (num) {
                case 51 -> "N";
                case 52 -> "E";
                case 53 -> "S";
                case 54 -> "W";
                default -> "N";
            };
            decision.put("action", "echo").put("parameters", new JSONObject().put("direction", direction));
        } 
        else if (num == 55) {
            range = getRange();
            logger.info("Range detected: {}", range);

            if (range > 0) {
                decision.put("action", "heading").put("parameters", new JSONObject().put("direction", "S"));
            } else {
                decision.put("action", "stop");
            }
        } 
        else if (num > 55 && num <= (55 + 2 * range)) {
            decision.put("action", (num - 55) % 2 == 0 ? "fly" : "scan");
        } 
        else {
            decision.put("action", "stop");
        }

        num++;
        return decision.toString();
    }

    public int getRange() {
        return extras.has("range") ? extras.getInt("range") : -1;
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n"+response.toString(2));
        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        logger.info("Additional information received: {}", extraInfo);
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }

}
