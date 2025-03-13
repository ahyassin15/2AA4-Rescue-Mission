package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private int num = 0; // Counter to track the decision sequence

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}", info.toString(2));

        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");

        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();

        if (num < 40) {  // First 20 fly operations with scans, so doubled
            if (num % 2 == 0) {
                // Fly on even numbers
                decision.put("action", "fly");
            } else {
                // Scan on odd numbers
                decision.put("action", "scan");
            }
        } else if (num == 40) {
            // Change heading to South at step 21
            JSONObject parameters = new JSONObject();
            parameters.put("direction", "S");
            decision.put("action", "heading");
            decision.put("parameters", parameters);
        } else if (num > 40 && num <= 90) { // Next 25 fly operations with scans, so doubled
            if ((num - 41) % 2 == 0) {
                // Fly on even numbers after heading change
                decision.put("action", "fly");
            } else {
                // Scan on odd numbers after heading change
                decision.put("action", "scan");
            }
        } else {
            // Stop after all actions
            decision.put("action", "stop");
        }

        num++;  // Increment the counter to track which decision to make next
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n" + response.toString(2));

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
