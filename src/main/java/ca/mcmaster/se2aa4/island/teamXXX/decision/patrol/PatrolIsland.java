package ca.mcmaster.se2aa4.island.teamXXX.decision.patrol;

import ca.mcmaster.se2aa4.island.teamXXX.communication.ActionCommand;
import ca.mcmaster.se2aa4.island.teamXXX.communication.DroneController;
import ca.mcmaster.se2aa4.island.teamXXX.core.Direction;
import ca.mcmaster.se2aa4.island.teamXXX.data.ResultAggregator;
import ca.mcmaster.se2aa4.island.teamXXX.data.SensorData;
import ca.mcmaster.se2aa4.island.teamXXX.decision.CommandDecisions;
import ca.mcmaster.se2aa4.island.teamXXX.tracking.PositionTracker;
import org.json.JSONObject;

public class PatrolIsland implements CommandDecisions {
    private SensorData currentInformation = new SensorData(0, new JSONObject());
    private final ResultAggregator data;
    private Direction currentDirection;
    private final PositionTracker map;
    private PatrolIslandState state;
    protected String uTurnDirection;
    protected int outOfRangeCounter;
    protected int islandHalvesExplored;
    protected int uTurns;
    protected int originalX;
    protected int originalY;
    protected int range;
    protected int distanceToOOB;
    protected boolean firstRun;
    protected boolean checkedForSite;
    protected boolean turnedRight;
    protected boolean turnedLeft;
    protected DroneController droneController;

    public PatrolIsland(String uTurnDirection, Direction direction) {
        this.state = new ScanState();
        data = new ResultAggregator();
        this.uTurnDirection = uTurnDirection;
        this.currentDirection = direction;
        this.map = new PositionTracker(currentDirection, 0, 0);
        this.originalX = 0;
        this.originalY = 0;
        this.uTurns = 0;
        this.outOfRangeCounter = 0;
        this.islandHalvesExplored = 0;
        this.range = 0;
        this.distanceToOOB = 0;
        this.turnedLeft = false;
        this.turnedRight = false;
        this.firstRun = true;
        this.droneController = new DroneController();
    }

    @Override
    public void getInfo(SensorData info) {
        this.currentInformation = info;
    }

    @Override
    public String generateFinalReport() {
        return data.generateFinalReport().toString();
    }

    @Override
    public String getClosestInlet() {
        return data.getClosestInlet();
    }

    @Override
    public String uTurnDirection() {
        return uTurnDirection;
    }

    @Override
    public boolean missionToLand() {
        return false;
    }

    @Override
    public Direction getCurrentDirection() {
        return currentDirection;
    }

    @Override
    public ActionCommand makeDecision() {
        ActionCommand command;
        data.initializeExtras(currentInformation);
        int inletX;
        int inletY;
        int emergencySiteX;
        int emergencySiteY;
        if (checkedForSite) {
            if (data.inletIsFound()) {
                inletX = map.getCurrentX();
                inletY = map.getCurrentY();
                data.storeCoordinates(inletX, inletY);
            }
            if (data.emergencySiteIsFound()) {
                emergencySiteX = map.getCurrentX();
                emergencySiteY = map.getCurrentY();
                data.storeCoordinatesEmergency(emergencySiteX, emergencySiteY);
            }
            checkedForSite = false;
        }
        command = state.stateChange(this);
        if (command.getValue().equals("fly")) {
            map.moveForward();
        } else if (command.getValue().equals("heading")) {
            if (turnedRight) {
                currentDirection = currentDirection.right();
                map.turnRight();
                turnedRight = false;
            } else if (turnedLeft) {
                currentDirection = currentDirection.left();
                map.turnLeft();
                turnedLeft = false;
            }
        }
        if (command.getValue().equals("scan") && firstRun) {
            originalX = map.getCurrentX();
            originalY = map.getCurrentY();
            firstRun = false;
        }
        if (islandHalvesExplored > 1 && originalX == map.getCurrentX() && originalY == map.getCurrentY()) {
            command = droneController.stop();
        }
        return command;
    }

    class BigUTurnState implements PatrolIslandState {
        @Override
        public ActionCommand stateChange(PatrolIsland drone) {
            ActionCommand command = new ActionCommand("");
            data.initializeExtras(currentInformation);
            if (drone.uTurnDirection.equals("right")) {
                if (drone.uTurns == 0) {
                    command = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                    drone.islandHalvesExplored++;
                } else if (drone.uTurns == 1) {
                    command = droneController.fly();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 2) {
                    command = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 3) {
                    command = drone.scanGridSearch();
                    drone.switchState(new ScanState());
                    drone.checkedForSite = true;
                    drone.uTurns = 0;
                    drone.uTurnDirection = "right";
                    drone.islandHalvesExplored++;
                }
            } else if (drone.uTurnDirection.equals("left")) {
                if (drone.uTurns == 0) {
                    command = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                    drone.islandHalvesExplored++;
                } else if (drone.uTurns == 1) {
                    command = droneController.fly();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 2) {
                    command = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new BigUTurnState());
                } else if (drone.uTurns == 3) {
                    command = drone.scanGridSearch();
                    drone.checkedForSite = true;
                    drone.switchState(new ScanState());
                    drone.uTurns = 0;
                    drone.uTurnDirection = "left";
                }
            }
            return command;
        }
    }

    class EchoState implements PatrolIslandState {
        @Override
        public ActionCommand stateChange(PatrolIsland drone) {
            ActionCommand command = new ActionCommand("");
            data.initializeExtras(currentInformation);
            if (data.isFound()) {
                drone.switchState(new FlyState());
                command = droneController.fly();
                int dist = data.distance();
                dist = dist - 1;
                drone.range = dist;
                drone.outOfRangeCounter = 0;
            } else {
                command = droneController.echoTowards(currentDirection);
                drone.outOfRangeCounter++;
                drone.switchState(new SecondEchoState());
            }
            return command;
        }
    }

    class FlyState implements PatrolIslandState {
        @Override
        public ActionCommand stateChange(PatrolIsland drone) {
            ActionCommand command;
            data.initializeExtras(currentInformation);
            if (drone.range > 0) {
                drone.switchState(new FlyState());
                command = droneController.fly();
                drone.range--;
            } else {
                drone.switchState(new ScanState());
                command = drone.scanGridSearch();
                drone.checkedForSite = true;
            }
            return command;
        }
    }

    class FlyToEndState implements PatrolIslandState {
        @Override
        public ActionCommand stateChange(PatrolIsland drone) {
            ActionCommand command = new ActionCommand("");
            data.initializeExtras(currentInformation);
            if (drone.distanceToOOB == 0) {
                drone.distanceToOOB = data.distance();
                command = droneController.fly();
                drone.switchState(new FlyToEndState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB > 7) {
                command = droneController.fly();
                drone.switchState(new FlyToEndState());
                drone.distanceToOOB--;
            } else {
                command = droneController.fly();
                drone.switchState(new BigUTurnState());
                drone.distanceToOOB = 0;
            }
            if (drone.distanceToOOB < 2) {
                command = droneController.echoTowards(currentDirection);
                drone.switchState(new BigUTurnState());
                drone.distanceToOOB = 0;
            }
            return command;
        }
    }

    class FlyToUTurnState implements PatrolIslandState {
        @Override
        public ActionCommand stateChange(PatrolIsland drone) {
            ActionCommand command = new ActionCommand("");
            data.initializeExtras(currentInformation);
            if (drone.distanceToOOB == 0) {
                drone.distanceToOOB = data.distance();
                command = droneController.fly();
                drone.switchState(new FlyToUTurnState());
                drone.distanceToOOB--;
            } else if (drone.distanceToOOB > 7) {
                command = droneController.fly();
                drone.switchState(new FlyToUTurnState());
                drone.distanceToOOB--;
            } else {
                command = droneController.fly();
                drone.switchState(new UTurnState());
                drone.distanceToOOB = 0;
            }
            if (drone.distanceToOOB < 2) {
                command = droneController.echoTowards(currentDirection);
                drone.switchState(new UTurnState());
                drone.distanceToOOB = 0;
            }
            return command;
        }
    }

    class ScanState implements PatrolIslandState {
        private final ResultAggregator localData = new ResultAggregator();

        @Override
        public ActionCommand stateChange(PatrolIsland drone) {
            ActionCommand command = new ActionCommand("");
            localData.initializeExtras(currentInformation);
            if (localData.groundIsFound()) {
                drone.switchState(new FlyState());
                command = droneController.fly();
                drone.outOfRangeCounter = 0;
            } else {
                drone.switchState(new EchoState());
                command = droneController.echoTowards(currentDirection);
            }
            return command;
        }
    }

    class SecondEchoState implements PatrolIslandState {
        @Override
        public ActionCommand stateChange(PatrolIsland drone) {
            ActionCommand command;
            data.initializeExtras(currentInformation);
            if (drone.outOfRangeCounter == 2) {
                drone.switchState(new FlyToEndState());
                command = droneController.echoTowards(currentDirection);
            } else {
                drone.switchState(new FlyToUTurnState());
                command = droneController.echoTowards(currentDirection);
            }
            return command;
        }
    }

    class UTurnState implements PatrolIslandState {
        @Override
        public ActionCommand stateChange(PatrolIsland drone) {
            ActionCommand command = new ActionCommand("");
            data.initializeExtras(currentInformation);
            if (drone.uTurnDirection.equals("left")) {
                if (drone.uTurns == 0) {
                    command = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 1) {
                    command = drone.turnLeftGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 2) {
                    command = droneController.echoTowards(currentDirection);
                    drone.uTurns = 0;
                    drone.uTurnDirection = "right";
                    drone.switchState(new EchoState());
                }
            } else if (drone.uTurnDirection.equals("right")) {
                if (drone.uTurns == 0) {
                    command = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 1) {
                    command = drone.turnRightGridSearch();
                    drone.uTurns++;
                    drone.switchState(new UTurnState());
                } else if (drone.uTurns == 2) {
                    command = droneController.echoTowards(currentDirection);
                    drone.uTurns = 0;
                    drone.uTurnDirection = "left";
                    drone.switchState(new EchoState());
                }
            }
            return command;
        }
    }

    private ActionCommand turnLeftGridSearch() {
        turnedLeft = true;
        return droneController.turnLeft(currentDirection);
    }

    private ActionCommand turnRightGridSearch() {
        turnedRight = true;
        return droneController.turnRight(currentDirection);
    }

    private ActionCommand scanGridSearch() {
        checkedForSite = true;
        return droneController.scan();
    }

    private void switchState(PatrolIslandState newState) {
        this.state = newState;
    }
}