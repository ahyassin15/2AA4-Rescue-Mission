package ca.mcmaster.se2aa4.island.teamXXX;

public class BatteryLevel {
    
    private int battery;

    public BatteryLevel(int battery) {
        this.battery = battery;
    }

    public int getBatteryLevel() {
        return battery;
    }

    public void decreaseBattery(int cost) {
        battery = battery - cost;
    }

    public boolean batteryLevelLow() {
        
        // Assume that in order to stop, it requires this amount or less
        if (this.battery == 30) {
            return true;
        }

        return false;
    }
}
