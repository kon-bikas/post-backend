package org.kon.postr.user;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.kon.postr.post.PostRepository;
import org.kon.postr.user.dto.UserCardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EntityManager entityManager;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository, PostRepository postRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.entityManager = entityManager;
    }



    @Test
    @Transactional
    public void saveUserTest() {
        User user = new User(UUID.fromString(
                "dc692cde-b663-40fc-862b-a57a923011ea"), "john", "picture_john");
        userRepository.save(user);

        Optional<User> userUsernameTest = userRepository.findByUsername("john");
        assertTrue(userUsernameTest.isPresent(), "Fail: User is not present.");
        assertEquals("john", userUsernameTest.get().getUsername());

        UUID userId = userUsernameTest.get().getId();
        Optional<User> userIdTest = userRepository.findById(userId);
        assertTrue(userIdTest.isPresent(), "Fail: User is not present.");
        assertEquals(userId, userIdTest.get().getId());

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(
                    new User(UUID.fromString(
                            "dc692cde-b666-40fc-862b-a57a923011ea"), "john", "picture_johnny")
            );
            userRepository.flush();
        });

    }

    @Test
    @Transactional
    public void userFollowingTest() {
        User userGeorge = userRepository.findByUsername("george").get();
        Set<User> georgeFollowing = userGeorge.getFollowing();
        assertEquals(2, georgeFollowing.size());

        Slice<UserCardDTO> georgeFollowingSlice = userRepository.findByFollowers_Id(
                userGeorge.getId(), PageRequest.of(0, 10));

        assertEquals(2, georgeFollowingSlice.getNumberOfElements());

    }

    @Test
    @Transactional
    public void userUnfollowTest() {
        User userGeorge = userRepository.getReferenceByUsername("george");
        User userJason = userRepository.getReferenceByUsername("jason");

        System.out.println("Before removing follower");
        assertEquals(3, userJason.getFollowers().size());
        assertEquals(2, userGeorge.getFollowing().size());

        assertTrue(userGeorge.getFollowing().contains(userJason));

        System.out.println("removing follower");
        userGeorge.removeFollowing(userRepository.getReferenceByUsername("jason"));
        userRepository.save(userGeorge);

        entityManager.flush();
        entityManager.clear();

        userGeorge = userRepository.findByUsername("george").get();
        userJason = userRepository.findByUsername("jason").get();

        assertEquals(2, userJason.getFollowers().size());
        assertEquals(1, userGeorge.getFollowing().size());

        assertFalse(userGeorge.getFollowing().contains(userJason));

    }

    @Test
    @Transactional
    public void userFollowTest() {
        User userGeorge = userRepository.getReferenceByUsername("george");
        User userEmily = userRepository.getReferenceByUsername("emily");

        userGeorge.addFollowing(userEmily);
        userRepository.save(userGeorge);

        entityManager.flush();
        entityManager.clear();

        userGeorge = userRepository.findByUsername("george").get();
        userEmily = userRepository.findByUsername("emily").get();

        assertEquals(3, userGeorge.getFollowing().size());
        assertEquals(2, userEmily.getFollowers().size());

    }

    @Test
    @Transactional
    public void updateUserTest() {
        User userGeorge = userRepository.findByUsername("george").get();
        UUID userGeorgeId = userGeorge.getId();

        assertEquals("george", userGeorge.getUsername());

        userGeorge.setUsername("georgy");
        userRepository.save(userGeorge);

        entityManager.flush();
        entityManager.clear();

        userGeorge = userRepository.findById(userGeorgeId).get();

        assertEquals("georgy", userGeorge.getUsername());

    }

}
