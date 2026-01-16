package org.kon.postr.security;

import org.kon.postr.exception.ResourceNotFoundException;
import org.kon.postr.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(value = "authz")
public class CustomAuthorizationLogic {

    private final PostRepository postRepository;

    @Autowired
    public CustomAuthorizationLogic(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean isPostOwner(UUID postId) {
        String principalSubject = ((Jwt) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getSubject();

        boolean result =  postRepository.findById(postId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("post does not exist.")
                ).getUser().getId().equals(UUID.fromString(principalSubject));

        System.out.println("result authz is: " + result);
        return result;
    }

}
