package com.example.VTM.service.utils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class CustomQueryUtils {

    @Transactional(readOnly = true)
    public List<Map<String,Object>> customQueryBuilderForListOfObject(String query, JdbcTemplate jdbcObject){
        return jdbcObject.queryForList(query);
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> customQueryBuilderForListOfObject(String query, JdbcTemplate jdbcObject,String... arr){
        for(String parameters:arr){
            query=query.replaceFirst("\\?","'"+parameters+"'");
        }
        return jdbcObject.queryForList(query);
    }

    @Transactional(readOnly = true)
    public String customeQueryBuilderforString(String query,JdbcTemplate jdbcTemplate){
        return jdbcTemplate.queryForObject(query,String.class);
    }

    @Transactional(readOnly = true)
    public String customeQueryBuilderforString(String query,JdbcTemplate jdbcObject,String... arr){
        for(String parameters:arr){
            query=query.replaceFirst("\\?","'"+parameters+"'");
        }
        return jdbcObject.queryForObject(query,String.class);
    }

}
