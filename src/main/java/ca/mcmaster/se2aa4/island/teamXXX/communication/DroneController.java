package ca.mcmaster.se2aa4.island.teamXXX.communication;

import ca.mcmaster.se2aa4.island.teamXXX.core.Direction;

public class DroneController {
    public ActionCommand turnLeft(Direction currentDirection) {
        return new ActionCommand("heading", currentDirection.left());
    }

    public ActionCommand turnRight(Direction currentDirection) {
        return new ActionCommand("heading", currentDirection.right());
    }

    public ActionCommand scan() {
        return new ActionCommand("scan");
    }

    public ActionCommand fly() {
        return new ActionCommand("fly");
    }

    public ActionCommand stop() {
        return new ActionCommand("stop");
    }

    public ActionCommand echoTowards(Direction currentDirection) {
        return new ActionCommand("echo", currentDirection);
    }
}