package com.acetp.feeder.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.feeder")
@Validated
public class FeederProperties {

    @Min(1000)
    private long pollIntervalMs = 10_000;

    @Min(1)
    private int maxMessagesPerRun = 1000;

    private String cbMsgSequenceName = "ACETP.SEQ_CB_MSG_DB_ID";
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
