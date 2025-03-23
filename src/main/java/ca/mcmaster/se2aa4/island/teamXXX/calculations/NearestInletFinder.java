package ca.mcmaster.se2aa4.island.teamXXX.calculations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NearestInletFinder {
    private final List<String> inletIds;
    private final List<Integer> inletXs;
    private final List<Integer> inletYs;
    private final int emergencyX;
    private final int emergencyY;
    private int closestIndex = -1;
    private double minDistance = Double.MAX_VALUE;

    public NearestInletFinder(List<Integer> inletXs, List<Integer> inletYs, List<String> inletIds, int emergencyX, int emergencyY) {
        this.inletXs = inletXs;
        this.inletYs = inletYs;
        this.inletIds = inletIds;
        this.emergencyX = emergencyX;
        this.emergencyY = emergencyY;
    }

    public String calculateNearestInlet() {
        Set<String> visitedCoordinates = new HashSet<>();
        double distance = 0;
        for (int i = 0; i < inletXs.size(); i++) {
            int x = inletXs.get(i);
            int y = inletYs.get(i);
            String coordinate = x + "," + y;
            if (!visitedCoordinates.contains(coordinate)) {
                visitedCoordinates.add(coordinate);
                distance = Math.sqrt(Math.pow(x - emergencyX, 2) + Math.pow(y - emergencyY, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestIndex = i;
                }
            }
        }
        return (closestIndex != -1) ? inletIds.get(closestIndex) : "No creek found";
    }
}