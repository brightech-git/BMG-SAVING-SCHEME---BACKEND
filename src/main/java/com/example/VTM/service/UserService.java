package com.example.VTM.service;


import com.example.VTM.model.RequestResponse.RequestResponse;
import com.example.VTM.model.User;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final JdbcTemplate firstJdbcTemplate;
    private final JdbcTemplate secondJdbcTemplate;

    @Autowired
    public UserService(@Qualifier("firstJdbcTemplate") JdbcTemplate firstJdbcTemplate, JdbcTemplate secondJdbcTemplate) {
        this.firstJdbcTemplate = firstJdbcTemplate;
        this.secondJdbcTemplate = secondJdbcTemplate;
    }

    public List<User> getUser() {
        String sql = "SELECT USERID AS id, USERNAME AS userName, ACTIVE AS status, PWD AS pwd FROM USERMASTER";
        return firstJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }


    public RequestResponse checkHealthy() {
        RequestResponse requestResponse = new RequestResponse();
        try {
            String sql = "SELECT Count(*) FROM USERMASTER";
            firstJdbcTemplate.queryForObject(sql, String.class);
            requestResponse.setMessage("Up");
        } catch (Exception e) {
            requestResponse.setMessage("Down");
        }
        return requestResponse;
    }

    public RequestResponse getCompanyName() {
        RequestResponse requestResponse = new RequestResponse();
        try {
            String sql = " SELECT TOP 1 Cname FROM Company ";
            requestResponse.setMessage(firstJdbcTemplate.queryForObject(sql, String.class));
        } catch (Exception e) {
            requestResponse.setMessage("No name in company table");
        }
        return requestResponse;
    }


    public List<Map<String, String>> getAmountWeight(Integer regNo, String groupCode) {
        // Corrected SQL query
        String sql = "SELECT SUM(AMOUNT) AS AMOUNT, SUM(WEIGHT) AS WEIGHT, GROUPCODE, REGNO " +
                "FROM SCHEMETRAN " +
                "WHERE REGNO = ? AND GROUPCODE = ? " +
                "GROUP BY GROUPCODE, REGNO";

        // Execute query with parameter binding and mapping results
        return secondJdbcTemplate.query(sql, new Object[]{regNo, groupCode}, (rs, rowNum) -> {
            Map<String, String> map = new HashMap<>();
            map.put("Amount", String.valueOf(rs.getFloat("AMOUNT")));
            map.put("Weight", String.valueOf(rs.getFloat("WEIGHT")));
            map.put("GroupCode", rs.getString("GROUPCODE"));
            map.put("RegNo", String.valueOf(rs.getInt("REGNO")));
            return map;
});
    }


}
