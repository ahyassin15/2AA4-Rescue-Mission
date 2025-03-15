package ca.mcmaster.se2aa4.island.teamXXX;

public interface ExplorationPhase {
    /**
     * Called by Explorer.takeDecision() to get the next action.
     */
    String nextDecision();

    /**
     * Called by Explorer.acknowledgeResults(...) to handle the engine response
     * for the last command.
     */
    void acknowledgeResults(String jsonResponse);
}
