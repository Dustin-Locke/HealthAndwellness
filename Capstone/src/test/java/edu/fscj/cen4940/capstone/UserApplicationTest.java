package edu.fscj.cen4940.capstone;

import edu.fscj.cen4940.capstone.config.TestHelperConfig;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.repository.UserRepository;
import edu.fscj.cen4940.capstone.util.CreateAndPersist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestHelperConfig.class)
@DisplayName("User Repository CRUD Tests")
public class UserApplicationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreateAndPersist createAndPersist;

    @Test
    @DisplayName("Save user")
    void saveUser_ShouldSaveUser() {
        User savedUser = createAndPersist.user("Mr.John");

        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertThat(foundUser).isEqualTo(savedUser);
    }

    @Test
    @DisplayName("Empty optional returned when user not present")
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        Optional<User> user = userRepository.findById(9999);
        assertThat(user).isEmpty();
    }

    @Test
    @DisplayName("Delete user")
    void deleteByUsername_ShouldRemoveUser() {
        User savedUser = createAndPersist.user("deleteUser");

        userRepository.deleteByUsername(savedUser.getUsername());

        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertThat(foundUser).isNull();
    }

    @Test
    @DisplayName("Null returned when user not present")
    void deleteByUsername_ShouldNotFail_WhenUsernameDoesNotExist() {
        userRepository.deleteByUsername("nonexistentUser");

        assertThat(userRepository.findByUsername("nonexistentUser")).isNull();
    }

}
