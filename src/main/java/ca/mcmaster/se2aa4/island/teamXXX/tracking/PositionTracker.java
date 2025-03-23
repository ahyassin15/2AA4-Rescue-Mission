package ca.mcmaster.se2aa4.island.teamXXX.tracking;

import ca.mcmaster.se2aa4.island.teamXXX.core.Direction;

public class PositionTracker {
    private int x;
    private int y;
    private Direction currentDirection;

    public PositionTracker(Direction direction, int x, int y) {
        this.currentDirection = direction;
        this.x = x;
        this.y = y;
    }

    public void moveForward() {
        switch (currentDirection) {
            case E:
                x += 1;
                break;
            case W:
                x -= 1;
                break;
            case N:
                y += 1;
                break;
            case S:
                y -= 1;
                break;
            default:
                break;
        }
    }

    public void turnLeft() {
        switch (currentDirection) {
            case N:
                x -= 1;
                y += 1;
                break;
            case S:
                x += 1;
                y -= 1;
                break;
            case E:
                x += 1;
                y += 1;
                break;
            case W:
                x -= 1;
                y -= 1;
                break;
            default:
                break;
        }
        currentDirection = currentDirection.left();
    }

    public void turnRight() {
        switch (currentDirection) {
            case N:
                x += 1;
                y += 1;
                break;
            case S:
                x -= 1;
                y -= 1;
                break;
            case E:
                x += 1;
                y -= 1;
                break;
            case W:
                x -= 1;
                y += 1;
                break;
            default:
                break;
        }
        currentDirection = currentDirection.right();
    }

    public int getCurrentX() {
        return x;
    }

    public int getCurrentY() {
        return y;
    }
}