package com.example.libbook.repository.impl;

import com.example.libbook.entity.Role;
import com.example.libbook.repository.RoleRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    JdbcTemplate jdbcTemplate;

    private static final RowMapper<Role> ROLE_ROW_MAPPER = (rs, rowNum) -> new Role(
            rs.getInt("RoleId"),
            rs.getString("RoleName")
    );
    public List<Role> inra() {
        String selectSql = "SELECT * FROM [role]";
        return jdbcTemplate.query(selectSql, ROLE_ROW_MAPPER);
    }
}
