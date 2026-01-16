package org.kon.postr.post;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kon.postr.user.User;
import org.kon.postr.user.UserRepository;
import org.kon.postr.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@DataJpaTest
@ActiveProfiles("test")
public class PostRepositoryTest {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Autowired
    public PostRepositoryTest(PostRepository postRepository, UserRepository userRepository, EntityManager entityManager) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Test
    @Transactional
    public void saveReplyTest() {
        Post mikePost1 = postRepository.findById(UUID.fromString("28bb88bc-6fc5-47e3-96f0-42b554c332dd")).get();
        User userEmily = userRepository.findByUsername("emily").get();

        List<Post> mikeReplies = postRepository.findByReplyParent_Id(
                mikePost1.getId(), PageRequest.of(0, 10)).getContent();

        assertEquals(2, mikeReplies.size());

        for (Post reply : mikeReplies) {
            assertEquals(reply.getReplyParent().getContent(), "mike-post-1");
        }

        List<Post> emilyReplies = postRepository.findByUser_UsernameAndType(
                "emily", Post.Type.REPLY, PageRequest.of(0, 10)).getContent();

        assertEquals(2, emilyReplies.size());

        postRepository.save(
                new Post(Post.Type.REPLY, "emily-reply-new", "emily-media", userEmily,
                        postRepository.getReferenceById(UUID.fromString("28bb88bc-6fc5-47e3-96f0-42b554c332dd")))
        );

        mikeReplies = postRepository.findByReplyParent_Id(
                mikePost1.getId(), PageRequest.of(0, 10)).getContent();

        assertEquals(3, mikeReplies.size());

        emilyReplies = postRepository.findByUser_UsernameAndType(
                "emily", Post.Type.REPLY, PageRequest.of(0, 10)).getContent();

        assertEquals(3, emilyReplies.size());

        System.out.println("Deleting user.....");

        entityManager.clear();

        postRepository.deleteById(UUID.fromString("28bb88bc-6fc5-47e3-96f0-42b554c332dd"));

        postRepository.flush();
        entityManager.clear();



        assertFalse(postRepository.findById(UUID.fromString("28bb88bc-6fc5-47e3-96f0-42b554c332dd")).isPresent());



        emilyReplies = postRepository.findByUser_UsernameAndType(
                "emily", Post.Type.REPLY, PageRequest.of(0, 10)).getContent();

        assertEquals(3, emilyReplies.size());

        int nullCounter = 0;
        for (Post reply : emilyReplies) {
            if (reply.getReplyParent() == null) {
                nullCounter++;
            }
        }

        assertEquals(2, nullCounter);

        List<Post> mikePosts = postRepository.findByUser_UsernameAndType(
                "mike", Post.Type.POST, PageRequest.of(0, 10)).getContent();

        assertEquals(1, mikePosts.size());

    }

    @Test
    @Transactional
    public void getPosts() {
        System.out.println("getting posts....");
        List<Post> posts = postRepository.findAll(PageRequest.of(0, 10)).getContent();

    }

    @Test
    @Transactional
    public void deletePostTest() {
        List<Post> jasonPosts = postRepository.findByUser_UsernameAndType(
                "jason", Post.Type.POST, PageRequest.of(0, 100)).getContent();

        assertEquals(1, jasonPosts.size());

        List<Post> emilyLikes = postRepository.findByLikes_Id(
                userRepository.findByUsername("emily").get().getId(), PageRequest.of(0, 100)
        ).getContent();

        assertEquals(5, emilyLikes.size());

        entityManager.clear();

        postRepository.deleteById(UUID.fromString("fb606c0d-34a9-46fb-a9ea-1721ac20cd59"));

        postRepository.flush();
        entityManager.clear();

        jasonPosts = postRepository.findByUser_UsernameAndType(
                "jason", Post.Type.POST, PageRequest.of(0, 100)).getContent();

        assertFalse(postRepository.findById(UUID.fromString("fb606c0d-34a9-46fb-a9ea-1721ac20cd59")).isPresent());

        assertEquals(0, jasonPosts.size());

        emilyLikes = postRepository.findByLikes_Id(
                userRepository.findByUsername("emily").get().getId(), PageRequest.of(0, 100)
        ).getContent();

        assertEquals(4, emilyLikes.size());

    }

    @Test
    @Transactional
    public void removingRepostTest() {
        User userEmily = userRepository.findByUsername("emily").get();

        List<Post> emilyReposts = postRepository.findRepostsByUser_Id
                (userEmily.getId(), PageRequest.of(0, 100)).getContent();

        assertEquals(0, emilyReposts.size());

        userEmily.addRepost(postRepository.getReferenceById(UUID.fromString("0e7870ab-6348-47bd-aa66-528fc87c5c54")));
        userEmily.addRepost(postRepository.getReferenceById(UUID.fromString("28bb88bc-6fc5-47e3-96f0-42b554c332dd")));
        userRepository.save(userEmily);

        userRepository.flush();
        entityManager.clear();

        emilyReposts = postRepository.findRepostsByUser_Id
                (userEmily.getId(), PageRequest.of(0, 100)).getContent();

        assertEquals(2, emilyReposts.size());


        entityManager.clear();


        Post post = postRepository.getReferenceById(UUID.fromString("0e7870ab-6348-47bd-aa66-528fc87c5c54"));

        userEmily = userRepository.findByUsername("emily").get();

        System.out.println("removing repost...");
        userEmily.removeRepost(post);
        userRepository.save(userEmily);

        entityManager.flush();
        entityManager.clear();

        emilyReposts = postRepository.findRepostsByUser_Id
                (userEmily.getId(), PageRequest.of(0, 100)).getContent();

        assertEquals(1, emilyReposts.size());

    }

    @Test
    @Transactional
    public void deletingRepostedPostTest() {
        User userEmily = userRepository.findByUsername("emily").get();

        List<Post> emilyReposts = postRepository.findRepostsByUser_Id
                (userEmily.getId(), PageRequest.of(0, 100)).getContent();

        assertEquals(0, emilyReposts.size());

        userEmily.addRepost(postRepository.getReferenceById(UUID.fromString("0e7870ab-6348-47bd-aa66-528fc87c5c54")));
        userEmily.addRepost(postRepository.getReferenceById(UUID.fromString("28bb88bc-6fc5-47e3-96f0-42b554c332dd")));
        userRepository.save(userEmily);

        userRepository.flush();
        entityManager.clear();

        Post post = postRepository.findById(UUID.fromString("0e7870ab-6348-47bd-aa66-528fc87c5c54")).get();
        assertEquals(1, post.getUsersReposters().size());

        emilyReposts = postRepository.findRepostsByUser_Id
                (userEmily.getId(), PageRequest.of(0, 100)).getContent();

        assertEquals(2, emilyReposts.size());

        entityManager.clear();


        post = postRepository.getReferenceById(UUID.fromString("0e7870ab-6348-47bd-aa66-528fc87c5c54"));
        userEmily = userRepository.findByUsername("emily").get();

        System.out.println("deleting reposted post...");
        postRepository.deleteById(UUID.fromString("0e7870ab-6348-47bd-aa66-528fc87c5c54"));

        entityManager.flush();
        entityManager.clear();

        emilyReposts = postRepository.findRepostsByUser_Id
                (userEmily.getId(), PageRequest.of(0, 100)).getContent();

        assertEquals(1, emilyReposts.size());

        assertTrue(userRepository.findByUsername("emily").isPresent());

    }

}
