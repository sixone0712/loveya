with selected as (select username from rsss.users where username='Administrator')
insert into rsss.users (
    username,
    password,
    permissions,
    validity
)
select 'Administrator', '5f4dcc3b5aa765d61d8327deb882cf99', '100', true
where not exists (select * from selected);