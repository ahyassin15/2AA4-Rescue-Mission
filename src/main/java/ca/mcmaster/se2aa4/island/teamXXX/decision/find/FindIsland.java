package ca.mcmaster.se2aa4.island.teamXXX.decision.find;

import ca.mcmaster.se2aa4.island.teamXXX.communication.ActionCommand;
import ca.mcmaster.se2aa4.island.teamXXX.communication.DroneController;
import ca.mcmaster.se2aa4.island.teamXXX.core.Direction;
import ca.mcmaster.se2aa4.island.teamXXX.data.ResultAggregator;
import ca.mcmaster.se2aa4.island.teamXXX.data.SensorData;
import ca.mcmaster.se2aa4.island.teamXXX.decision.CommandDecisions;
import org.json.JSONObject;

public class FindIsland implements CommandDecisions {
    private SensorData currentInformation = new SensorData(0, new JSONObject());
    private final ResultAggregator data;
    private Direction currentDirection;
    private String uTurnDirection;
    private FindIslandState state;
    protected int distanceToLand;
    protected boolean dontEchoRight;
    protected boolean dontEchoLeft;
    protected boolean turnedLeft;
    protected boolean turnedRight;
    private boolean missionToLand;
    protected DroneController droneController;

    public FindIsland(Direction direction) {
        this.currentDirection = direction;
        this.missionToLand = false;
        this.uTurnDirection = "left";
        this.state = new StartingState();
        this.dontEchoLeft = false;
        this.dontEchoRight = false;
        this.turnedLeft = false;
        this.turnedRight = false;
        data = new ResultAggregator();
        this.distanceToLand = 0;
        this.droneController = new DroneController();
    }

    @Override
    public void getInfo(SensorData info) {
        this.currentInformation = info;
    }

    @Override
    public String uTurnDirection() {
        return uTurnDirection;
    }

    @Override
    public boolean missionToLand() {
        return missionToLand;
    }

    @Override
    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void switchState(FindIslandState state) {
        this.state = state;
    }

    private ActionCommand turnLeftToLand() {
        turnedLeft = true;
        return droneController.turnLeft(currentDirection);
    }

    private ActionCommand turnRightToLand() {
        turnedRight = true;
        return droneController.turnRight(currentDirection);
    }

    @Override
    public String getClosestInlet() {
        return data.getClosestInlet();
    }

    @Override
    public ActionCommand makeDecision() {
        ActionCommand command;
        data.initializeExtras(currentInformation);
        command = state.stateChange(this);
        if (command.getValue().equals("heading")) {
            if (turnedRight) {
                currentDirection = currentDirection.right();
                turnedRight = false;
            } else if (turnedLeft) {
                currentDirection = currentDirection.left();
                turnedLeft = false;
            }
        }
        uTurnDirection = turnedRight ? "right" : "left";
        return command;
    }

    class StartingState implements FindIslandState {
        @Override
        public ActionCommand stateChange(FindIsland drone) {
            ActionCommand command;
            data.initializeExtras(currentInformation);
            command = droneController.echoTowards(currentDirection);
            drone.switchState(new EchoForwardState());
            return command;
        }
    }

    class EchoForwardState implements FindIslandState {
        @Override
        public ActionCommand stateChange(FindIsland drone) {
            ActionCommand command = new ActionCommand("");
            data.initializeExtras(currentInformation);
            if (data.isFound()) {
                drone.switchState(new TurnToLandState());
                drone.distanceToLand = data.distance();
                drone.distanceToLand--;
                command = droneController.fly();
            } else {
                drone.switchState(new EchoRightState());
                command = droneController.echoTowards(currentDirection.right());
            }
            return command;
        }
    }

    class EchoRightState implements FindIslandState {
        @Override
        public ActionCommand stateChange(FindIsland drone) {
            ActionCommand command = new ActionCommand("");
            data.initializeExtras(currentInformation);
            if (data.isFound()) {
                drone.switchState(new TurnToLandState());
                drone.distanceToLand = data.distance();
                drone.distanceToLand--;
                command = turnRightToLand();
            } else {
                if (data.distance() <= 2) {
                    drone.dontEchoRight = true;
                }
                drone.switchState(new EchoLeftState());
                command = droneController.echoTowards(currentDirection.left());
                if (drone.dontEchoLeft) {
                    drone.switchState(new FlyForwardState());
                    command = droneController.fly();
                }
            }
            return command;
        }
    }

    class EchoLeftState implements FindIslandState {
        @Override
        public ActionCommand stateChange(FindIsland drone) {
            ActionCommand command = new ActionCommand("");
            data.initializeExtras(currentInformation);
            if (data.isFound()) {
                drone.switchState(new TurnToLandState());
                drone.distanceToLand = data.distance();
                drone.distanceToLand--;
                command = turnLeftToLand();
            } else {
                if (data.distance() <= 2) {
                    drone.dontEchoLeft = true;
                }
                drone.switchState(new FlyForwardState());
                command = droneController.fly();
            }
            return command;
        }
    }

    class FlyForwardState implements FindIslandState {
        @Override
        public ActionCommand stateChange(FindIsland drone) {
            ActionCommand command;
            data.initializeExtras(currentInformation);
            if (drone.dontEchoRight && !drone.dontEchoLeft) {
                drone.switchState(new EchoLeftState());
                command = droneController.echoTowards(currentDirection.left());
            } else {
                drone.switchState(new EchoRightState());
                command = droneController.echoTowards(currentDirection.right());
            }
            return command;
        }
    }

    class TurnToLandState implements FindIslandState {
        @Override
        public ActionCommand stateChange(FindIsland drone) {
            ActionCommand command;
            data.initializeExtras(currentInformation);
            command = droneController.scan();
            drone.missionToLand = true;
            return command;
        }
    }
}