package org.example.expert.domain.user.repository;

import org.example.expert.config.TestQuerydslConfig;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Rollback(false)
//@Import(TestQuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = {"classpath:/application-test.properties"})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBulkRepository userBulkRepository;

    @Test
    void 유저_데이터를_100만_건_생성() {
        List<User> userList = new ArrayList<>();
        for (long i = 0L; i < 1000000; i++){
            String email = "user" + i + "@example.com";
            String password = "securePassword123";
            UserRole role = UserRole.of("ROLE_USER");
            String nickName = UUID.randomUUID().toString().substring(0, 8);
            User user = new User(email, password, role, nickName);

            userList.add(user);
        }
        userBulkRepository.batchUpdate(userList);
    }
}