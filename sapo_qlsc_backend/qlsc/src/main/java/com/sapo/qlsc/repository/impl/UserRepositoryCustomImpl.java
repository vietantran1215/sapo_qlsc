package com.sapo.qlsc.repository.impl;

import com.sapo.qlsc.entity.User;
import com.sapo.qlsc.repository.UserRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getTotalMaintenanceCardByRepairman(int page, int size, String key) {
        String sql = "SELECT u.id as user,(SELECT COUNT(*) FROM maintenance_cards WHERE repairman_id = u.id and work_status != 2) AS numberMaintenanceCards \n" +
                "FROM users u LEFT JOIN maintenance_cards mc ON mc.repairman_id = u.id " +
                "where u.role = 2 \n" +
                "and u.status = 1 \n" +
                "and (u.code like :key \n" +
                "or  u.email like :key \n" +
                "or  u.full_name like :key \n" +
                "or  u.phone_number like :key ) \n" +
                "group by u.id " +
                "order by numberMaintenanceCards "+
                "limit :size offset :page";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("size", size)
                .addValue("page", page)
                .addValue("key", key);
        return jdbcTemplate.queryForList(sql, sqlParameterSource);
    }

    @Override
    public int countTotalElements(String key) {
        String sql = "SELECT count(*) as total FROM users where users.status = 1 and users.role = 2 " +
                "and (users.code like :key \n" +
                "or  users.email like :key \n" +
                "or  users.full_name like :key \n" +
                "or  users.phone_number like :key ) \n";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("key", key);
        return jdbcTemplate.queryForObject(sql, sqlParameterSource, (rs, rowNum) -> {
            return rs.getInt("total");
        });

    }

    @Override
    public Map<String, Object> getListUser(int page, int size, String key) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getTotalMaintenanceCardUser(int page, int size, String sortBy, String descending, String search) {

        String sql = "select users.id, users.created_date,users.modified_date,users.address,users.code, " +
                "users.email,users.full_name,phone_number,users.role,users.status , count(maintenance_cards.id) as totalMaintenanceCard " +
                "from users left join maintenance_cards on maintenance_cards.repairman_id  = users.id or   maintenance_cards.coordinator_id = users.id " +
                "where users.status = 1  and (users.code like :search or users.full_name like :search or users.email like :search or users.phone_number like :search) " +
                "group by users.id " +
                "order by "+sortBy+" "+descending +
                "   limit :size offset :page ";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("page", page*size)
                .addValue("size", size)
                .addValue("search", search);
        return  jdbcTemplate.queryForList(sql, sqlParameterSource);

    }

    @Override
    public int countTotalElementsUser(String key) {
        String sql = "SELECT count(*) as total FROM users where users.status = 1 " +
                "and (users.code like :key \n" +
                "or  users.email like :key \n" +
                "or  users.full_name like :key \n" +
                "or  users.phone_number like :key ) \n";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("key", key);
        return jdbcTemplate.queryForObject(sql, sqlParameterSource, (rs, rowNum) -> {
            return rs.getInt("total");
        });
    }


}
