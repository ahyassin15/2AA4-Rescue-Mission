package ca.mcmaster.se2aa4.island.teamXXX.decision.find;

import ca.mcmaster.se2aa4.island.teamXXX.communication.ActionCommand;

public interface FindIslandState {
    ActionCommand stateChange(FindIsland drone);
}