package com.acetp.feeder.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
public class ClBusinessMtmInRepository {

    private final JdbcTemplate jdbcTemplate;

    public ClBusinessMtmInRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(long fileId, long msgId, long cbMsgDbId) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        jdbcTemplate.update(
                """
                INSERT INTO ACETP.CL_BUSINESS_MTM_IN_FEEDER (
                    FILE_ID,
                    MSG_ID,
                    CB_MSG_DB_ID,
                    CREATION_DATE
                ) VALUES (?, ?, ?, ?)
                """,
                fileId,
                msgId,
                cbMsgDbId,
                now
        );
    }
}
