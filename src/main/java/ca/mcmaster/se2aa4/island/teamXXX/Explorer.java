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
    private FindIsland findIsland;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}", info.toString(2));

        this.decisionMaker = new Decisions();
        this.compass = new Compass(info.getString("heading"));
        this.findIsland = new FindIsland(decisionMaker, compass);

        int batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", compass.current());
        logger.info("Battery level is {}", batteryLevel);
    }

    @Override
    public String takeDecision() {

        /*
        // ignore This and FindIsland
        // Simply ask findIsland for the next command
        String nextCmd = findIsland.nextDecision();
        logger.info("** nextDecision from FindIsland => {}", nextCmd);
        return nextCmd;

         */
        return decisionMaker.stop();
    }

    @Override
    public void acknowledgeResults(String s) {
        // Pass the engine's JSON response to findIsland, so it can parse found/range
        logger.info("** Acknowledging results from the Engine");
        logger.info("** Response => {}", s);
        findIsland.acknowledgeResults(s);
    }

    @Override
    public String deliverFinalReport() {
        return "No creek found or final report not implemented";
    }
}