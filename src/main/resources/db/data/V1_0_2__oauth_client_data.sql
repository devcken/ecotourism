INSERT INTO `oauth_client_details`(client_id, resource_ids, client_secret, scope, authorized_grant_types,
                                   web_server_redirect_uri, authorities, access_token_validity,
                                   refresh_token_validity, additional_information, autoapprove)
VALUES ('client', null, '$2a$10$iP9ejueOGXO29.Yio7rqeuW9.yOC4YaV8fJp3eIWbP45eZSHFEwMG', 'read',
        'password,refresh_token', 'http://localhost:9000/callback', 'USER', 3000, 6000, null, 'false');
