package ca.mcmaster.se2aa4.island.teamXXX.tracking;

public class BudgetTracker {
    private int battery;

    public BudgetTracker(int battery) {
        this.battery = battery;
    }

    public int getBatteryLevel() {
        return battery;
    }

    public void decreaseBattery(int cost) {
        battery = battery - cost;
    }
}