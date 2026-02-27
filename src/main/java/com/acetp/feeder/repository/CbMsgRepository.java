package com.acetp.feeder.repository;

import com.acetp.feeder.domain.CbMsgRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
@Profile("!mqfeeder")
public class CbMsgRepository {

    private final JdbcTemplate jdbcTemplate;

    public CbMsgRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(CbMsgRecord record) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        jdbcTemplate.update(
                """
                INSERT INTO ACETP.CB_MSG (
                    CB_MSG_DB_ID,
                    BRANCH_DB_ID,
                    VERSION_NUM,
                    DIRECTION,
                    TECHNICAL_TYPE,
                    STATUS_TYPE,
                    CREATION_DATE,
                    UPDATING_DATE,
                    EZF_PROC_STATUS,
                    EZF_TRY_COUNT
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                record.cbMsgDbId(),
                record.branchDbId(),
                1,
                "IN",
                "N",
                "NEW",
                now,
                now,
                "NEW",
                0
        );
    }
}
