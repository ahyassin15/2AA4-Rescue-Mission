package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;
import org.json.JSONTokener;

public class PatrolIsland implements ExplorationPhase {

    private final Decisions decisionMaker;
    private final Compass compass;

    // Example counters for scanning logic
    private int patrolStep = 0;

    public PatrolIsland(Decisions decisionMaker, Compass compass) {
        this.decisionMaker = decisionMaker;
        this.compass = compass;
    }

    @Override
    public void acknowledgeResults(String jsonResponse) {
        
        // parse extras if needed
        JSONObject response = new JSONObject(new JSONTokener(jsonResponse));
        JSONObject extras = response.optJSONObject("extras");
        
        if (extras != null) {
            // handle scanning data, etc.
        }
    }

    @Override
    public String nextDecision() {
        return decisionMaker.stop();
    }

}
