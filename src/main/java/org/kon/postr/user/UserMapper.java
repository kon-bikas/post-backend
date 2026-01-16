package org.kon.postr.user;

import org.kon.postr.post.PostMapper;
import org.kon.postr.user.dto.UserCardDTO;
import org.kon.postr.user.dto.UserProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileDTO toUserProfileDTO(User user, boolean isFollowing) {
        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getPicture(),
                user.getDescription(),
                user.getFollowingCount(),
                user.getFollowersCount(),
                user.getPostCount(),
                isFollowing
        );
    }

    public UserCardDTO toUserCardDTO(User user) {
        return new UserCardDTO(
                user.getId(),
                user.getUsername(),
                user.getPicture()
        );
    }

}
