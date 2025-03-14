package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;
import org.json.JSONTokener;

public class FindIsland {

    private final Decisions decisionMaker;
    private final Compass compass;

    // Store the data from the latest echo results
    private String foundFront = "NONE";
    private int rangeFront = -1;

    private String foundRight = "NONE";
    private int rangeRight = -1;

    private String foundLeft = "NONE";
    private int rangeLeft = -1;

    private boolean groundFound = false;
    private int steps = 0;
    private final int MAX_STEPS = 200; // Example safety limit to avoid infinite loops

    // Example toggle to see if we should do “front / right / left” next
    // or we can track them systematically
    private int echoStage = 0;

    public FindIsland(Decisions decisionMaker, Compass compass) {
        this.decisionMaker = decisionMaker;
        this.compass = compass;
    }

    /**
     * The Explorer calls this whenever the engine returns a JSON response
     * for the last command. Here we parse 'found' and 'range' if the last
     * command was an echo. This method is where we handle ANY new data.
     */
    public void acknowledgeResults(String jsonResponse) {
        JSONObject response = new JSONObject(new JSONTokener(jsonResponse));
        JSONObject extras = response.optJSONObject("extras");
        if (extras != null) {
            String found = extras.optString("found", "NONE");
            int range = extras.optInt("range", -1);

            // We'll store these based on echoStage.
            // For example, echoStage 0 => front, echoStage 1 => right, echoStage 2 => left
            // Then we cycle back or do logic as needed.
            if (echoStage == 0) {
                foundFront = found;
                rangeFront = range;
            } else if (echoStage == 1) {
                foundRight = found;
                rangeRight = range;
            } else if (echoStage == 2) {
                foundLeft = found;
                rangeLeft = range;
            }

            // If any direction is GROUND, we can mark it.
            if ("GROUND".equals(found)) {
                groundFound = true;
            }
        }
    }

    /**
     * Called by Explorer.takeDecision(). This method decides what command to send next.
     */
    public String nextDecision() {
        // If we already found ground in a previous step, we can choose to stop or do something else
        if (groundFound) {
            // For instance, we might just stop:
            return decisionMaker.stop();
        }

        // Some iteration limit to avoid infinite loops
        steps++;
        if (steps > MAX_STEPS) {
            // fallback if no ground found
            return decisionMaker.stop();
        }

        // We'll do a 3-step pattern: echo front, echo right, echo left -> then interpret
        // For example, we do stage 0 => echo front, stage 1 => echo right, stage 2 => echo left,
        // then stage 3 => interpret the results of all three, do a move, then repeat.

        // 0 => echo front
        if (echoStage == 0) {
            echoStage++;
            return decisionMaker.echo(compass.current());
        }
        // 1 => echo right
        else if (echoStage == 1) {
            echoStage++;
            return decisionMaker.echo(compass.right());
        }
        // 2 => echo left
        else if (echoStage == 2) {
            echoStage++;
            return decisionMaker.echo(compass.left());
        }
        // 3 => interpret the data from front/right/left, then reset stage to 0
        else {
            // interpret front/right/left
            // If any direction is GROUND, decide how to move
            if ("GROUND".equals(foundFront)) {
                groundFound = true;
                return decisionMaker.fly(); // Or heading if you prefer
            }
            if ("GROUND".equals(foundRight)) {
                groundFound = true;
                compass.updateDirection(compass.right());
                return decisionMaker.heading(compass.current());
            }
            if ("GROUND".equals(foundLeft)) {
                groundFound = true;
                compass.updateDirection(compass.left());
                return decisionMaker.heading(compass.current());
            }

            // If no ground found, compare ranges
            if (rangeFront >= rangeRight && rangeFront >= rangeLeft) {
                return decisionMaker.fly();
            } else if (rangeRight >= rangeFront && rangeRight >= rangeLeft) {
                compass.updateDirection(compass.right());
                return decisionMaker.heading(compass.current());
            } else {
                compass.updateDirection(compass.left());
                return decisionMaker.heading(compass.current());
            }
            // After deciding, reset for next cycle
            // We might do that AFTER we return.
            // So let's reset:
            // but let's do it next time after we see the results of the move
            // echoStage = 0;
        }
    }
}