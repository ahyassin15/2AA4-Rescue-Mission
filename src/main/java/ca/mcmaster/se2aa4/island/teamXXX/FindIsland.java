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
    private int num = 0;

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
    public void acknowledgeResults(String jsonResponse1) {
        JSONObject response = new JSONObject(new JSONTokener(jsonResponse1));
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
            } else if (echoStage > 10){
                foundFront = found;
                rangeFront = range;
            }
            echoStage ++;

        }
    }

    /**
     * Called by Explorer.takeDecision(). This method decides what command to send next.
     */
    public String nextDecision() {

        if (groundFound) {
            if (rangeFront == 1){
                return decisionMaker.stop();
            } else {
                num ++;
                if (num %2 == 0) {
                    return decisionMaker.fly();
                }else{
                    return decisionMaker.echo(compass.current());
                }
            }
        }

        // We'll do a 3-step pattern: echo front, echo right, echo left -> then interpret
        // For example, we do stage 0 => echo front, stage 1 => echo right, stage 2 => echo left,
        // then stage 3 => interpret the results of all three, do a move, then repeat.

        // 0 => echo front
        if (echoStage == 0) {
            return decisionMaker.echo(compass.current());
        }
        // 1 => echo right
        else if (echoStage == 1) {
            return decisionMaker.echo(compass.right());
            }
        // 2 => echo left
        else if (echoStage == 2) {
            return decisionMaker.echo(compass.left());
        }
        else {

            /* After echoing to all directions, if the front of the drone is close to being out of range
             change directions to the most appropriate
             */
            if (!groundFound && rangeFront <= 2){
                if (rangeRight <= 2){
                    compass.updateDirection(compass.left());
                    echoStage = -1;
                    return decisionMaker.heading(compass.current());
                }
                else {
                    echoStage = -1;
                    compass.updateDirection(compass.right());
                    return decisionMaker.heading(compass.current());
                }
            }
            // interpret front/right/left
            // If any direction is GROUND, decide how to move
            if ("GROUND".equals(foundFront)) {
                groundFound = true;
                echoStage = 11;
                return decisionMaker.fly();
            }
            if ("GROUND".equals(foundRight)) {
                groundFound = true;
                compass.updateDirection(compass.right());
                echoStage = 11;
                return decisionMaker.heading(compass.current());
            }
            if ("GROUND".equals(foundLeft)) {
                groundFound = true;
                compass.updateDirection(compass.left());
                echoStage = 11;
                return decisionMaker.heading(compass.current());
            }

            echoStage = -1;
            return decisionMaker.fly();

        }

    }
}