package ca.mcmaster.se2aa4.island.teamXXX.decision;

import ca.mcmaster.se2aa4.island.teamXXX.communication.ActionCommand;
import ca.mcmaster.se2aa4.island.teamXXX.core.Direction;
import ca.mcmaster.se2aa4.island.teamXXX.data.SensorData;

public interface CommandDecisions {
    ActionCommand makeDecision();

    void getInfo(SensorData info);

    boolean missionToLand();

    String uTurnDirection();

    Direction getCurrentDirection();

    String getClosestInlet();
}