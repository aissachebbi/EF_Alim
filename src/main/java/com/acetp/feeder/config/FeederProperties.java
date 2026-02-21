package com.acetp.feeder.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "app.feeder")
@Validated
public class FeederProperties {

    @Min(1000)
    private long pollIntervalMs = 10_000;

    @Min(1)
    private int maxMessagesPerRun = 1000;

    private boolean fixedLimit = false;
    private boolean forceSpecificBranchEnabled = false;
    private String forcedBranchCode;

    private String cbMsgSequenceName = "ACETP.BDOMO_GRM_TRD_CB_MSGS_DB_ID_Test";
    private String clBusinessFileSequenceName = "ACETP.SEQ_CL_BUSINESS_FILE_ID";

    public long getPollIntervalMs() {
        return pollIntervalMs;
    }

    public void setPollIntervalMs(long pollIntervalMs) {
        this.pollIntervalMs = pollIntervalMs;
    }

    public int getMaxMessagesPerRun() {
        return maxMessagesPerRun;
    }

    public void setMaxMessagesPerRun(int maxMessagesPerRun) {
        this.maxMessagesPerRun = maxMessagesPerRun;
    }

    public boolean isFixedLimit() {
        return fixedLimit;
    }

    public void setFixedLimit(boolean fixedLimit) {
        this.fixedLimit = fixedLimit;
    }

    public boolean isForceSpecificBranchEnabled() {
        return forceSpecificBranchEnabled;
    }

    public void setForceSpecificBranchEnabled(boolean forceSpecificBranchEnabled) {
        this.forceSpecificBranchEnabled = forceSpecificBranchEnabled;
    }

    public String getForcedBranchCode() {
        return forcedBranchCode;
    }

    public void setForcedBranchCode(String forcedBranchCode) {
        this.forcedBranchCode = forcedBranchCode;
    }

    public String getCbMsgSequenceName() {
        return cbMsgSequenceName;
    }

    public void setCbMsgSequenceName(String cbMsgSequenceName) {
        this.cbMsgSequenceName = cbMsgSequenceName;
    }

    public String getClBusinessFileSequenceName() {
        return clBusinessFileSequenceName;
    }

    public void setClBusinessFileSequenceName(String clBusinessFileSequenceName) {
        this.clBusinessFileSequenceName = clBusinessFileSequenceName;
    }
}
