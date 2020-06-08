insert into rsss.users (
    username,
    password,
    permissions,
    validity
) values (
    'Administrator',
    '5f4dcc3b5aa765d61d8327deb882cf99',     -- password
    '100',
    true
) on conflict (username) do nothing;