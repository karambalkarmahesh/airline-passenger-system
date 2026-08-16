package com.airline.authservice.repository;

import com.airline.authservice.entity.User;
import com.airline.authservice.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .firstName("Mahesh")
                .lastName("Karambalkar")
                .email("mahesh@example.com")
                .password("$2a$10$encodedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    void shouldSaveUserSuccessfully() {

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();

        assertThat(saved.getEmail())
                .isEqualTo("mahesh@example.com");
    }

    @Test
    void shouldFindUserByEmail() {

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByEmail(
                        "mahesh@example.com"
                );

        assertThat(result).isPresent();

        assertThat(result.get().getRole())
                .isEqualTo(Role.USER);
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {

        userRepository.save(user);

        boolean result =
                userRepository.existsByEmail(
                        "mahesh@example.com"
                );

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {

        boolean result =
                userRepository.existsByEmail(
                        "unknown@example.com"
                );

        assertThat(result).isFalse();
    }
}