package ca.mcmaster.se2aa4.island.teamXXX;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Set;

public class DeliverFinalReport {
    
    private static final Logger logger = LogManager.getLogger(DeliverFinalReport.class);
    private final CellTracker cellTracker;
    private String finalReport;

    public DeliverFinalReport(CellTracker cellTracker) {
        this.cellTracker = cellTracker;
        createFinalReport();
    }

    private void createFinalReport() {
        logger.debug("Creating final report");
        StringBuilder report = new StringBuilder();

        report.append("===== FINAL EXPLORATION REPORT =====\n\n");

        Set<String> creeks = cellTracker.getAllCreeks();
        report.append("CREEKS DISCOVERED: ").append(creeks.size()).append("\n");
    }
}
