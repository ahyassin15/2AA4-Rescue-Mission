package ca.mcmaster.se2aa4.island.teamXXX;

public class Compass {
    private String currentDirection;

    public Compass(String initialDirection) {
        this.currentDirection = initialDirection;
    }

    public String current() {
        return currentDirection;
    }

    public void updateDirection(String newDirection) {
        this.currentDirection = newDirection;
    }

    public String left() {
        switch (currentDirection) {
            case "N": return "W";
            case "E": return "N";
            case "S": return "E";
            case "W": return "S";
            default:  return currentDirection;
        }
    }

    public String right() {
        switch (currentDirection) {
            case "N": return "E";
            case "E": return "S";
            case "S": return "W";
            case "W": return "N";
            default:  return currentDirection;
        }
    }
}