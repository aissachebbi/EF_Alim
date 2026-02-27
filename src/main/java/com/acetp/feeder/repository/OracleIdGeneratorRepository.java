package com.acetp.feeder.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!mqfeeder & !mqpurge")
public class OracleIdGeneratorRepository implements IdGeneratorRepository {

    private final JdbcTemplate jdbcTemplate;

    public OracleIdGeneratorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long nextValue(String sequenceName) {
        String sql = "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";
        Long value = jdbcTemplate.queryForObject(sql, Long.class);
        if (value == null) {
            throw new IllegalStateException("Impossible de récupérer NEXTVAL pour la séquence " + sequenceName);
        }
        return value;
    }
}
