package ca.mcmaster.se2aa4.island.teamXXX.core;

import ca.mcmaster.se2aa4.island.teamXXX.communication.ActionCommand;
import ca.mcmaster.se2aa4.island.teamXXX.communication.DroneController;
import ca.mcmaster.se2aa4.island.teamXXX.data.ResultAggregator;
import ca.mcmaster.se2aa4.island.teamXXX.data.SensorData;
import ca.mcmaster.se2aa4.island.teamXXX.decision.CommandDecisions;
import ca.mcmaster.se2aa4.island.teamXXX.decision.find.FindIsland;
import ca.mcmaster.se2aa4.island.teamXXX.decision.patrol.PatrolIsland;
import ca.mcmaster.se2aa4.island.teamXXX.tracking.BudgetTracker;
import org.json.JSONObject;

public class RescueDrone {
    private SensorData currentInformation = new SensorData(0, new JSONObject());
    private final ResultAggregator data;
    protected BudgetTracker budgetTracker;
    private final Direction currentDirection;
    protected CommandDecisions landFinder;
    protected CommandDecisions areaScanner;
    protected boolean firstRun;
    protected boolean secondRun;
    protected DroneController droneController;
    private double initialBatteryLevel;

    public RescueDrone(Integer battery, Direction direction) {
        this.currentDirection = direction;
        int batteryInt = battery;
        this.budgetTracker = new BudgetTracker(batteryInt);
        this.firstRun = true;
        this.secondRun = true;
        this.landFinder = new FindIsland(currentDirection);
        data = new ResultAggregator();
        this.droneController = new DroneController();
        this.initialBatteryLevel = 0;
    }

    public void getInfo(SensorData info) {
        this.currentInformation = info;
        this.budgetTracker.decreaseBattery(info.getCost());
    }

    public int getBatteryLevelDrone() {
        return this.budgetTracker.getBatteryLevel();
    }

    public boolean batteryLevelWarning() {
        if ((initialBatteryLevel * 0.002) <= 40) {
            return getBatteryLevelDrone() <= 40;
        } else {
            return getBatteryLevelDrone() <= (initialBatteryLevel * 0.002);
        }
    }

    public String getClosestInlet() {
        return areaScanner.getClosestInlet();
    }

    public void initializeAreaScanner() {
        if (secondRun && landFinder.missionToLand()) {
            this.areaScanner = new PatrolIsland(landFinder.uTurnDirection(), landFinder.getCurrentDirection());
            secondRun = false;
        }
    }

    public ActionCommand makeDecision() {
        ActionCommand command;
        data.initializeExtras(currentInformation);
        if (firstRun) {
            initialBatteryLevel = getBatteryLevelDrone();
            landFinder.getInfo(currentInformation);
            command = droneController.echoTowards(currentDirection);
            firstRun = false;
        } else if (!landFinder.missionToLand()) {
            landFinder.getInfo(currentInformation);
            command = landFinder.makeDecision();
        } else if (landFinder.missionToLand()) {
            initializeAreaScanner();
            areaScanner.getInfo(currentInformation);
            command = areaScanner.makeDecision();
        } else {
            command = droneController.stop();
        }
        if (batteryLevelWarning()) {
            command = droneController.stop();
        }
        return command;
    }
}