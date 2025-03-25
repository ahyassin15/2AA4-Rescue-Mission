package ca.mcmaster.se2aa4.island.teamXXX.reporting;

import java.util.ArrayList;

public class FinalReport {
    private final ArrayList<String> creekIds;
    private final ArrayList<Integer> creekXs;
    private final ArrayList<Integer> creekYs;
    private final String emergencyId;
    private final int emergencyX;
    private final int emergencyY;
    private final String nearestCreekId;
    private final double nearestDist;

    public FinalReport(ArrayList<String> creekIds, ArrayList<Integer> creekXs, ArrayList<Integer> creekYs, String emergencyId,
                       int emergencyX, int emergencyY, String nearestCreekId, int nearestCreekX, int nearestCreekY) {
        this.creekIds = creekIds;
        this.creekXs = creekXs;
        this.creekYs = creekYs;
        this.emergencyId = emergencyId;
        this.emergencyX = emergencyX;
        this.emergencyY = emergencyY;
        this.nearestCreekId = nearestCreekId;
        this.nearestDist = Math.sqrt(
                Math.pow(nearestCreekX - emergencyX, 2) +
                        Math.pow(nearestCreekY - emergencyY, 2)
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("### FINAL REPORT ###\n\n");
        sb.append("Creeks Identified: ").append(creekIds.size()).append("\n");
        sb.append("Creek Locations:\n");
        for (int i = 0; i < creekIds.size(); i++) {
            sb.append("# ").append(creekIds.get(i))
                    .append(" at (").append(creekXs.get(i)).append(", ")
                    .append(creekYs.get(i)).append(")\n");
        }
        sb.append("\nEmergency Site: ").append(emergencyId)
                .append(" at (").append(emergencyX).append(", ")
                .append(emergencyY).append(")\n");
        sb.append("Nearest Creek: ").append(nearestCreekId)
                .append(" (distance: ").append(String.format("%.2f", nearestDist)).append(")\n\n");
        sb.append("### END OF REPORT ###");
        return sb.toString();
    }
}