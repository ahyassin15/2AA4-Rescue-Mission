package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Implements the logic plus the
 * ExplorationPhase interface to allow easy phase switching once ground is found.
 */
public class FindIsland implements ExplorationPhase {

    // Object creation to help make decision and know headings
    private final Decisions decisionMaker;
    private final Compass compass;

    // The echo data for each side of the drone
    private String foundFront = "NONE";
    private int rangeFront = -1;

    private String foundRight = "NONE";
    private int rangeRight = -1;

    private String foundLeft = "NONE";
    private int rangeLeft = -1; // Not needed but keep just in case

    // boolean values that track if the ground is found and or reached
    private boolean groundFound = false;
    private boolean groundReached = false;

    // Additional counters / toggles
    private int num = 0;
    private int echoStage = 0;
    private int randomEcho = 11; // keep this over 3

    public FindIsland(Decisions decisionMaker, Compass compass) {
        this.decisionMaker = decisionMaker;
        this.compass = compass;
    }

    /**
     * Called by Explorer. Acknowledges the engine's JSON response for the last action,
     * updating echo data, checking if ground is found, etc.
     */
    @Override
    public void acknowledgeResults(String jsonResponse) {

        // Handles JSON and strips important info
        JSONObject response = new JSONObject(new JSONTokener(jsonResponse));
        JSONObject extras = response.optJSONObject("extras");
        if (extras != null) {
            String found = extras.optString("found", "NONE");
            int range = extras.optInt("range", -1);

            // Store found/range based on echoStage
            if (echoStage == 0) {
                foundFront = found;
                rangeFront = range;
            } else if (echoStage == 1) {
                foundRight = found;
                rangeRight = range;
            } else if (echoStage == 2) {
                foundLeft = found;
                rangeLeft = range;
            } else if (echoStage >= randomEcho) {
                // This covers the scenario where echoStage was bumped
                // to 11 or beyond if ground was found
                // Can fix logic for this later!! but for now it works
                foundFront = found;
                rangeFront = range;
            }

            // Increment echoStage for the next cycle
            echoStage++;
        }
    }

    /**
     * Called by Explorer.takeDecision(). This method decides what command
     * to send next, preserving your existing echo logic.
     */
    @Override
    public String nextDecision() {
        // If we already found ground, use post-ground logic:
        if (groundFound) {
            // If the forward range is 1, then ground reached and patrol Island will deal with the rest
            // return fly because its 1 block away from the island, if it flies it will actually be 0 blocks away
            // Need to return a decision so that's the logic chosen.
            if (rangeFront == 1) {
                groundReached = true;
                return decisionMaker.fly();
            } else {
                // Alternate between fly and echo
                // Echo is just for testing so don't need num
                // can replace everything in else with just return decisionMaker.fly()
                num++;
                if (num % 2 == 0) {
                    return decisionMaker.fly();
                } else {
                    return decisionMaker.echo(compass.current());
                }
            }
        }

        // The pattern: echo front, echo right, echo left, then interpret
        if (echoStage == 0) {
            return decisionMaker.echo(compass.current());
        } else if (echoStage == 1) {
            return decisionMaker.echo(compass.right());
        } else if (echoStage == 2) {
            return decisionMaker.echo(compass.left());
        } else {
            // After echoing all directions, interpret the data

            // If you are going out of range, this if statement changes your heading to stay in zone
            // If the front is close (≤2), choose left/right
            if (!groundFound && rangeFront <= 2) {
                if (rangeRight <= 2) {
                    compass.updateDirection(compass.left());
                    echoStage = -1; // reset echo stage
                    return decisionMaker.heading(compass.current());
                } else {
                    echoStage = -1;
                    compass.updateDirection(compass.right());
                    return decisionMaker.heading(compass.current());
                }
            }

            // If any direction is GROUND, decide how to move
            // Changes heading to the side that found the ground
            if ("GROUND".equals(foundFront)) {
                groundFound = true;
                echoStage = randomEcho;
                return decisionMaker.fly();
            }
            if ("GROUND".equals(foundRight)) {
                groundFound = true;
                compass.updateDirection(compass.right());
                echoStage = randomEcho;
                return decisionMaker.heading(compass.current());
            }
            if ("GROUND".equals(foundLeft)) {
                groundFound = true;
                compass.updateDirection(compass.left());
                echoStage = randomEcho;
                return decisionMaker.heading(compass.current());
            }

            // Otherwise, just fly forward if nothing special
            // Resest echo stage to cycle back to front of drone
            echoStage = -1;
            return decisionMaker.fly();
        }
    }

    /**
     * Explorer can call this to see if we should switch out of FindIsland.
     */
    public boolean isIslandReached() {
        return groundReached;
    }
}
