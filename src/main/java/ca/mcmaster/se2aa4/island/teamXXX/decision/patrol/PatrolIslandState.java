package ca.mcmaster.se2aa4.island.teamXXX.decision.patrol;

import ca.mcmaster.se2aa4.island.teamXXX.communication.ActionCommand;

public interface PatrolIslandState {
    ActionCommand stateChange(PatrolIsland drone);
}