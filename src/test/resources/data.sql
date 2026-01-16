
INSERT INTO users (id, username, picture_url, followers_count, following_count, post_count)
VALUES ('17ed79b9-f6bd-416b-a11a-7ab4da112c5d', 'mike', 'M-profile', 3, 2, 2),
       ('e5b11711-e74d-4383-858a-8e5ce5926c53', 'george', 'G-profile', 2, 2, 2),
       ('5c5d6174-a343-434e-9ad8-d53905e70013', 'emily', 'E-profile', 1, 1, 1),
       ('df18bd97-38d7-4630-8617-8fa23eca4d90', 'jason', 'J-profile', 3, 0, 1),
       ('44a381ad-a0b5-444a-abe7-01b45628123a', 'nick', 'N-profile', 0, 0, 1);

INSERT INTO user_following (follower_id, followed_id)
VALUES ('e5b11711-e74d-4383-858a-8e5ce5926c53', 'df18bd97-38d7-4630-8617-8fa23eca4d90'),
       ('5c5d6174-a343-434e-9ad8-d53905e70013', 'df18bd97-38d7-4630-8617-8fa23eca4d90'),
       ('17ed79b9-f6bd-416b-a11a-7ab4da112c5d', 'df18bd97-38d7-4630-8617-8fa23eca4d90'),
       ('17ed79b9-f6bd-416b-a11a-7ab4da112c5d', 'e5b11711-e74d-4383-858a-8e5ce5926c53'),
       ('e5b11711-e74d-4383-858a-8e5ce5926c53', '17ed79b9-f6bd-416b-a11a-7ab4da112c5d'),
       ('17ed79b9-f6bd-416b-a11a-7ab4da112c5d', '5c5d6174-a343-434e-9ad8-d53905e70013'),
       ('5c5d6174-a343-434e-9ad8-d53905e70013', '17ed79b9-f6bd-416b-a11a-7ab4da112c5d');

INSERT INTO posts (id, creation_timestamp, like_count, reply_count, parent_id, user_id, content, media_url, type)
VALUES
    ('28bb88bc-6fc5-47e3-96f0-42b554c332dd', CURRENT_TIMESTAMP, 2, 0, null,
     '17ed79b9-f6bd-416b-a11a-7ab4da112c5d', 'mike-post-1', 'mike-media-1', 'POST'),
    ('9d6d8194-dde1-46ad-b25c-87f4721798aa', CURRENT_TIMESTAMP, 2, 0, null,
     '44a381ad-a0b5-444a-abe7-01b45628123a', 'nick-post-1', 'nick-media-1', 'POST'),
    ('d17374cf-1e2e-4dfb-ae84-1763a14b6d0b', CURRENT_TIMESTAMP, 4, 0, null,
     '5c5d6174-a343-434e-9ad8-d53905e70013', 'emily-post-1', 'emily-media-1', 'POST'),
    ('a9521f7a-a379-4ea7-923d-2fa7749d1c79', CURRENT_TIMESTAMP, 3, 0, null,
     '17ed79b9-f6bd-416b-a11a-7ab4da112c5d', 'mike-post-2', 'mike-media-2', 'POST'),
    ('0e7870ab-6348-47bd-aa66-528fc87c5c54', CURRENT_TIMESTAMP, 1, 0, null,
     'e5b11711-e74d-4383-858a-8e5ce5926c53', 'george-post-1', 'george-media-1', 'POST'),
    ('fb606c0d-34a9-46fb-a9ea-1721ac20cd59', CURRENT_TIMESTAMP, 1, 0, null,
     'df18bd97-38d7-4630-8617-8fa23eca4d90', 'jason-post-1', 'jason-media-2', 'POST'),
    ('314d2421-c545-498f-95fc-fb4a7414c98c', CURRENT_TIMESTAMP, 3, 0, null,
     'e5b11711-e74d-4383-858a-8e5ce5926c53', 'george-post-2', 'george-media-2', 'POST'),

    ('eb10ce8b-a9b7-4d31-96e9-be5d82f3f428', CURRENT_TIMESTAMP, 0, 0, '28bb88bc-6fc5-47e3-96f0-42b554c332dd',
     'e5b11711-e74d-4383-858a-8e5ce5926c53', 'george-reply-1-mike-post-1', 'gm-1-1-media', 'REPLY'),
    ('32ca90b2-1ae2-477f-88d8-9ecafd1606a5', CURRENT_TIMESTAMP, 0, 0, '28bb88bc-6fc5-47e3-96f0-42b554c332dd',
     '5c5d6174-a343-434e-9ad8-d53905e70013', 'emily-reply-1-mike-post-1', 'em-1-1-media', 'REPLY'),
    ('73b6bd1a-e3d1-4619-bfe6-42e598d5bf3e', CURRENT_TIMESTAMP, 0, 0, '0e7870ab-6348-47bd-aa66-528fc87c5c54',
     'df18bd97-38d7-4630-8617-8fa23eca4d90', 'jason-reply-1-george-post-1', 'jg-1-1-media', 'REPLY'),
    ('ddbd5b39-46fb-4905-988c-c383b6e4b237', CURRENT_TIMESTAMP, 0, 0, 'fb606c0d-34a9-46fb-a9ea-1721ac20cd59',
     '5c5d6174-a343-434e-9ad8-d53905e70013', 'emily-reply-2-jason-post-1', null, 'REPLY');

INSERT INTO post_user_likes (post_id, user_id)
VALUES
    ('28bb88bc-6fc5-47e3-96f0-42b554c332dd', '5c5d6174-a343-434e-9ad8-d53905e70013'),
    ('28bb88bc-6fc5-47e3-96f0-42b554c332dd', '44a381ad-a0b5-444a-abe7-01b45628123a'),
    ('9d6d8194-dde1-46ad-b25c-87f4721798aa', 'e5b11711-e74d-4383-858a-8e5ce5926c53'),
    ('9d6d8194-dde1-46ad-b25c-87f4721798aa', 'df18bd97-38d7-4630-8617-8fa23eca4d90'),
    ('d17374cf-1e2e-4dfb-ae84-1763a14b6d0b', '17ed79b9-f6bd-416b-a11a-7ab4da112c5d'),
    ('d17374cf-1e2e-4dfb-ae84-1763a14b6d0b', '5c5d6174-a343-434e-9ad8-d53905e70013'),
    ('d17374cf-1e2e-4dfb-ae84-1763a14b6d0b', 'df18bd97-38d7-4630-8617-8fa23eca4d90'),
    ('d17374cf-1e2e-4dfb-ae84-1763a14b6d0b', '44a381ad-a0b5-444a-abe7-01b45628123a'),
    ('a9521f7a-a379-4ea7-923d-2fa7749d1c79', '5c5d6174-a343-434e-9ad8-d53905e70013'),
    ('a9521f7a-a379-4ea7-923d-2fa7749d1c79', '17ed79b9-f6bd-416b-a11a-7ab4da112c5d'),
    ('a9521f7a-a379-4ea7-923d-2fa7749d1c79', '44a381ad-a0b5-444a-abe7-01b45628123a'),
    ('0e7870ab-6348-47bd-aa66-528fc87c5c54', '17ed79b9-f6bd-416b-a11a-7ab4da112c5d'),
    ('fb606c0d-34a9-46fb-a9ea-1721ac20cd59', '5c5d6174-a343-434e-9ad8-d53905e70013'),
    ('314d2421-c545-498f-95fc-fb4a7414c98c', '5c5d6174-a343-434e-9ad8-d53905e70013'),
    ('314d2421-c545-498f-95fc-fb4a7414c98c', '17ed79b9-f6bd-416b-a11a-7ab4da112c5d'),
    ('314d2421-c545-498f-95fc-fb4a7414c98c', 'df18bd97-38d7-4630-8617-8fa23eca4d90');