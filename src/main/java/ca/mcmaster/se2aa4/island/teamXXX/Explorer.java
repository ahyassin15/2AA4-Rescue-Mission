package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();

    private Compass compass;
    private Decisions decisionMaker;

    // We store the "current" phase of our exploration logic here:
    private ExplorationPhase currentPhase;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");

        // Parse the JSON init data
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}", info.toString(2));

        this.decisionMaker = new Decisions();
        this.compass = new Compass(info.getString("heading"));

        int batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", compass.current());
        logger.info("Battery level is {}", batteryLevel);

        // Start in "FindIsland" mode:
        this.currentPhase = new FindIsland(decisionMaker, compass);
    }

    @Override
    public String takeDecision() {
        // Delegate to the current phase
        String action = currentPhase.nextDecision();
        logger.info("** nextDecision => {}", action);
        return action;
    }

    @Override
    public void acknowledgeResults(String s) {
        // prints returned values from take Decision helping in testing
        // also dissects the returned value to translate the JSON objects
        logger.info("** Acknowledging results => {}", s);

        currentPhase.acknowledgeResults(s);

        // If we are still in FindIsland, check if we've reached the ground
        if (currentPhase instanceof FindIsland) {
            FindIsland fi = (FindIsland) currentPhase;
            if (fi.isIslandReached()) {
                logger.info("** Switching from FindIsland to PatrolIsland!");
                currentPhase = new PatrolIsland(decisionMaker, compass);
            }
        }
    }


    @Override
    public String deliverFinalReport() {
        return "No creek found or final report not implemented";
    }
}
