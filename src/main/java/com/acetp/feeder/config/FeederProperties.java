package com.acetp.feeder.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

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

    private boolean stopOnMaxTotalMessagesEnabled = false;

    @Min(1)
    private long maxTotalMessages = 100_000;

    private boolean branchDistributionEnabled = false;
    private Map<String, Integer> branchDistributionPercentages = new LinkedHashMap<>();

    private String cbMsgSequenceName = "ACETP.BDOMO_GRM_TRD_CB_MSGS_DB_ID_Test";
    private String clBusinessFileSequenceName = "ACETP.SEQ_CL_BUSINESS_FILE_ID";
    private final Mq mq = new Mq();

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

    public boolean isStopOnMaxTotalMessagesEnabled() {
        return stopOnMaxTotalMessagesEnabled;
    }

    public void setStopOnMaxTotalMessagesEnabled(boolean stopOnMaxTotalMessagesEnabled) {
        this.stopOnMaxTotalMessagesEnabled = stopOnMaxTotalMessagesEnabled;
    }

    public long getMaxTotalMessages() {
        return maxTotalMessages;
    }

    public void setMaxTotalMessages(long maxTotalMessages) {
        this.maxTotalMessages = maxTotalMessages;
    }

    public boolean isBranchDistributionEnabled() {
        return branchDistributionEnabled;
    }

    public void setBranchDistributionEnabled(boolean branchDistributionEnabled) {
        this.branchDistributionEnabled = branchDistributionEnabled;
    }

    public Map<String, Integer> getBranchDistributionPercentages() {
        return branchDistributionPercentages;
    }

    public void setBranchDistributionPercentages(Map<String, Integer> branchDistributionPercentages) {
        this.branchDistributionPercentages = branchDistributionPercentages;
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

    public Mq getMq() {
        return mq;
    }

    public static class Mq {
        private boolean enabled = false;
        private String queueName = "";
        private boolean purgeOnStartupEnabled = false;
        private Map<String, String> branchTemplates = new LinkedHashMap<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }

        public boolean isPurgeOnStartupEnabled() {
            return purgeOnStartupEnabled;
        }

        public void setPurgeOnStartupEnabled(boolean purgeOnStartupEnabled) {
            this.purgeOnStartupEnabled = purgeOnStartupEnabled;
        }

        public Map<String, String> getBranchTemplates() {
            return branchTemplates;
        }

        public void setBranchTemplates(Map<String, String> branchTemplates) {
            this.branchTemplates = branchTemplates;
        }
    }
}
