package com.example.VTM.service.utils;

import com.example.VTM.entity.SupportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class SupportRequestRepository {

    private final JdbcTemplate jdbcTemplate;

    public SupportRequestRepository(@Qualifier("firstJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(SupportRequest request) {
        String sql = "INSERT INTO support_request (enquiry_type, subject, description) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.getEnquiryType());
            ps.setString(2, request.getSubject());
            ps.setString(3, request.getDescription());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
