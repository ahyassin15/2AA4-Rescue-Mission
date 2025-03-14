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
    private Decisions decisionMaker = new Decisions(); // Instance of Decisions class

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
        String result;  // Declare a variable to hold the result temporarily

        if (num < 40) {
            if (num % 2 == 0) {
                result = decisionMaker.fly();
            } else {
                result = decisionMaker.echo("E");
            }
        } else if (num == 40) {
            result = decisionMaker.heading("S");
        } else if (num > 40 && num <= 120) {
            if ((num - 41) % 2 == 0) {
                result = decisionMaker.fly();
            } else {
                result = decisionMaker.echo("S");
            }
        } else {
            result = decisionMaker.stop();
        }

        num++; // Increment the counter before returning the result
        return result; // Now return the result after incrementing
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