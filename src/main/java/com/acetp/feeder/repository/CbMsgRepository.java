package com.acetp.feeder.repository;

import com.acetp.feeder.domain.CbMsgRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
public class CbMsgRepository {

    private final JdbcTemplate jdbcTemplate;

    public CbMsgRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(CbMsgRecord record) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        jdbcTemplate.update(
                """
                INSERT INTO ACETP.CB_MSG_FEEDER (
                    CB_MSG_DB_ID,
                    BRANCH_DB_ID,
                    VERSION_NUM,
                    DIRECTION,
                    TECHNICAL_TYPE,
                    STATUS_TYPE,
                    CREATION_DATE,
                    UPDATING_DATE
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                record.cbMsgDbId(),
                record.branchDbId(),
                1,
                "IN",
                "N",
                "NEW",
                now,
                now
        );
    }
}
