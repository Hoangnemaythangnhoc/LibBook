package com.example.libbook;

import com.example.libbook.entity.Role;
import com.example.libbook.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties.UiService.LOGGER;

@SpringBootApplication
public class LibBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibBookApplication.class, args);

    }
    @Bean
    CommandLineRunner queryAdminRole(JdbcTemplate jdbcTemplate) {
        return args -> {

            String roleToQuery = "ADMIN";
            String selectSql = "SELECT id, name FROM role WHERE name = ?";

            try {
                // Using jdbcTemplate.query for potentially multiple results (though name should be unique)
                // or queryForObject if you expect exactly one.
                // Here, using query and checking list size is safer if uniqueness isn't strictly guaranteed
                // at the DB level or if you want to handle the "not found" case gracefully.

                List<Role> roles = jdbcTemplate.query(selectSql, new Object[]{roleToQuery},
                        (rs, rowNum) -> new Role(rs.getInt("id"), rs.getString("name"))
                );



                // Alternative: If you expect exactly one result and want an exception if not found or more than one
                /*
                try {
                    RoleQueryResult adminRole = jdbcTemplate.queryForObject(selectSql,
                        (rs, rowNum) -> new RoleQueryResult(rs.getInt("id"), rs.getString("name")),
                        roleToQuery
                    );
                    if (adminRole != null) {
                        LOGGER.info("Found role (using queryForObject): ID = {}, Name = {}", adminRole.getId(), adminRole.getName());
                    }
                } catch (EmptyResultDataAccessException e) {
                    LOGGER.warn("Role '{}' not found using queryForObject.", roleToQuery);
                }
                */


            } catch (DataAccessException e) {
//                LOGGER.error("Error querying for role '{}': {}", roleToQuery, e.getMessage(), e);
            }
        };
    }
}