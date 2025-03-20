package org.example.expert.domain.user.repository;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public int[][] batchUpdate(final List<User> users) {
        int[][] insertCounts = jdbcTemplate.batchUpdate(
                "INSERT INTO users(email, image_url, nick_name, password, user_role)" + "VALUES (?, ?, ?, ?, ?)",
                users,
                100,
                (PreparedStatement ps, User user) -> {
                        ps.setString(1, user.getEmail());
                        ps.setString(2, user.getImageUrl());
                        ps.setString(3, user.getNickName());
                        ps.setString(4, user.getPassword());
                        ps.setString(5, String.valueOf(user.getUserRole()));
                });
        return insertCounts;
    }

//    @Transactional
//    public void saveAll(List<User> users) {
//        String sql = "INSERT INTO users(email, image_url, nick_name, password, user_role)"
//                + "VALUES (?, ?, ?, ?, ?)";
//
//        jdbcTemplate.batchUpdate(sql,
//                new BatchPreparedStatementSetter() {
//                    @Override
//                    public void setValues(PreparedStatement ps, int i) throws SQLException {
//                        User user = users.get(i);
//                        ps.setString(1, user.getEmail());
//                        ps.setString(2, user.getImageUrl());
//                        ps.setString(3, user.getNickName());
//                        ps.setString(4, user.getPassword());
//                        ps.setString(5, String.valueOf(user.getUserRole()));
//                    }
//
//                    @Override
//                    public int getBatchSize() {
//                        return users.size();
//                    }
//                });
//    }
}
