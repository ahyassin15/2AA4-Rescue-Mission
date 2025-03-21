package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeliverFinalReport {
    
    private static final Logger logger = LogManager.getLogger(DeliverFinalReport.class);
    private final CellTracker cellTracker;
    private String finalReport;

    public DeliverFinalReport(CellTracker cellTracker) {
        this.cellTracker = cellTracker;
        createFinalReport();
    }

    private void createFinalReport() {
        logger.debug("Creating final exploration report");
        StringBuilder report = new StringBuilder();

        report.append("===== FINAL EXPLORATION REPORT =====\n\n");

       var creeks = cellTracker.getAllCreeks();
        report.append("CREEKS DISCOVERED: ").append(creeks.size()).append("\n");

        if (!creeks.isEmpty()) {

            report.append("CREEK DETAILS:\n");

            for (String creek : creeks) {
                CellTracker.Position pos = cellTracker.getCreekPosition(creek);
                report.append(" - ").append(creek).append(" at ").append(pos).append("\n");
            }

        } else {
            report.append("No creeks were discovered during this exploration.\n");
        }

        report.append("\n");

        var sites = cellTracker.getAllEmergencySites();
        report.append("EMERGENCY SITES DISCOVERED: ").append(sites.size()).append("\n");

        if (!sites.isEmpty()) {

            report.append("EMERGENCY SITE DETAILS:\n");

            for (String site : sites) {
                CellTracker.Position pos = cellTracker.getEmergencySitePosition(site);
                report.append(" - ").append(site).append(" at ").append(pos).append("\n");
            }

            report.append("\nNEAREST CREEK ANALYSIS:\n");
            
            for (String site : sites) {
                String nearestCreek = cellTracker.findNearestCreekToSite(site);
                
                if (nearestCreek != null) {
                   
                    CellTracker.Position sitePos = cellTracker.getEmergencySitePosition(site);
                    CellTracker.Position creekPos = cellTracker.getCreekPosition(nearestCreek);
                    
                    double dx = creekPos.x - sitePos.x;
                    double dy = creekPos.y - sitePos.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    report.append(" - Emergency site ").append(site)
                          .append(" → closest creek is ").append(nearestCreek)
                          .append(" (distance: ").append(String.format("%.2f", distance)).append(")\n");

                } else {
                    report.append(" - Emergency site ").append(site)
                          .append(" → no creek discovered nearby\n");
                }
            }

        } else {
            report.append("No emergency sites were discovered during this exploration.\n");
        }

        report.append("\n===== END OF EXPLORATION REPORT =====");

        finalReport = report.toString();
        logger.debug("Final exploration report created");
    }

    public String getFinalReport() {
        return finalReport;
    }
}
