package ca.mcmaster.se2aa4.island.teamXXX.core;

public enum Direction {
    N, S, E, W, NONE;

    public Direction left() {
        return switch (this) {
            case N -> W;
            case S -> E;
            case E -> N;
            case W -> S;
            default -> NONE;
        };
    }

    public Direction right() {
        return switch (this) {
            case N -> E;
            case S -> W;
            case E -> S;
            case W -> N;
            default -> NONE;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case N -> "N";
            case S -> "S";
            case E -> "E";
            case W -> "W";
            default -> "NONE";
        };
    }
}