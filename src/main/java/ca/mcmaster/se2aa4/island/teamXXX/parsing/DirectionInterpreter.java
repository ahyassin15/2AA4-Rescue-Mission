package ca.mcmaster.se2aa4.island.teamXXX.parsing;

import ca.mcmaster.se2aa4.island.teamXXX.core.Direction;

public class DirectionInterpreter {
    private Direction currentDirection;

    public Direction translateDirection(String direction) {
        switch (direction) {
            case "E" -> currentDirection = Direction.E;
            case "W" -> currentDirection = Direction.W;
            case "N" -> currentDirection = Direction.N;
            case "S" -> currentDirection = Direction.S;
        }
        return currentDirection;
    }
}