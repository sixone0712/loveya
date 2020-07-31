# rss-summer

PostgreSQL의 테이블 생성 쿼리를 설명한다.

> 갱신된 경우 제목에 업데이트 날짜를 갱신하자

### 전체 테이블 및 시퀀스(2020.07.31)
VFTP 대응을 위해 plantype, command, directory 추가함

```sql
create table rsss.download_list
(
    id      serial    not null
        constraint download_list_pk
            primary key,
    created timestamp not null,
    status  text default 'new'::text,
    planid  integer   not null,
    path    text      not null,
    title   text      not null
);

alter table rsss.download_list
    owner to rssadmin;

create unique index download_list_id_uindex
    on rsss.download_list (id);

create table rsss.permissions
(
    id       integer default nextval('rsss.seq_permissions'::regclass) not null
        constraint permissions_pkey
            primary key,
    permname varchar(20)                                               not null,
    validity boolean
);

alter table rsss.permissions
    owner to rssadmin;

create table rsss.collection_plan
(
    id             serial                                                 not null
        constraint collection_plan_pk
            primary key,
    tool           text                                                   not null,
    logtype        text                                                   not null,
    description    text,
    collecttype    integer   default 1,
    cinterval      integer   default 0                                    not null,
    ownerid        integer                                                not null,
    stop           boolean   default false,
    tscreated      timestamp default (now())::timestamp without time zone not null,
    tsstart        timestamp                                              not null,
    tsend          timestamp                                              not null,
    tslastcollect  timestamp,
    tsnext         timestamp,
    tslastpoint    timestamp,
    tscollectstart timestamp default now()                                not null,
    laststatus     text      default 'registered'::text                   not null,
    planname       text                                                   not null,
    logtypestr     text,
    fab            text,
    plantype       text      default 'ftp'::text                          not null,
    command        text,
    directory      text
);

alter table rsss.collection_plan
    owner to rssadmin;

create table rsss.cmd
(
    id       serial    not null
        constraint cmd_pk
            primary key,
    cmd_name text      not null,
    cmd_type text      not null,
    created  timestamp not null,
    modified timestamp not null,
    validity boolean   not null
);

alter table rsss.cmd
    owner to rssadmin;

create unique index cmd_id_uindex
    on rsss.cmd (id);

create table rsss.genres
(
    id       serial    not null
        constraint genres_pk
            primary key,
    name     text      not null,
    category text      not null,
    created  timestamp not null,
    modified timestamp not null,
    validity boolean   not null
);

alter table rsss.genres
    owner to rssadmin;

create unique index genres_id_uindex
    on rsss.genres (id);

create unique index genres_name_uindex
    on rsss.genres (name);

create table rsss."genreUpdate"
(
    update timestamp not null
);

alter table rsss."genreUpdate"
    owner to rssadmin;

create table rsss.users
(
    id           integer   default nextval('rsss.seq_users'::regclass) not null
        constraint users_pkey
            primary key,
    username     text                                                  not null
        constraint users_username_key
            unique,
    password     text                                                  not null,
    created      timestamp default now(),
    modified     timestamp,
    validity     boolean   default true,
    permissions  text,
    lastaccess   timestamp,
    refreshtoken text
);

alter table rsss.users
    owner to rssadmin;

create table rsss.download_history
(
    id          serial  not null,
    dl_date     timestamp default now(),
    dl_user     text,
    dl_type     text,
    dl_filename text,
    validity    boolean not null,
    dl_status   text
);

alter table rsss.download_history
    owner to rssadmin;

create unique index download_history_id_uindex
    on rsss.download_history (id);

create table rsss."blackList"
(
    index   serial    not null,
    token   text      not null,
    expired timestamp not null
);

alter table rsss."blackList"
    owner to rssadmin;

create unique index blacklist_index_uindex
    on rsss."blackList" (index);


```

### 전체 테이블 및 시퀀스(2020.06.05)
중복되는 시퀀시 삭제하여 정리함.
```sql
create table rsss.collection_plan
(
	id serial not null
		constraint collection_plan_pk
			primary key,
	tool text not null,
	logtype text not null,
	description text,
	collecttype integer default 1,
	cinterval integer default 0 not null,
	ownerid integer not null,
	stop boolean default false,
	tscreated timestamp default (now())::timestamp without time zone not null,
	tsstart timestamp not null,
	tsend timestamp not null,
	tslastcollect timestamp,
	tsnext timestamp,
	tslastpoint timestamp,
	tscollectstart timestamp default now() not null,
	laststatus text default 'registered'::text not null,
	planname text not null,
	logtypestr text,
	fab text
);
alter table rsss.collection_plan owner to rssadmin;

create table rsss.download_list
(
	id serial not null
		constraint download_list_pk
			primary key,
	created timestamp not null,
	status text default 'new'::text,
	planid integer not null,
	path text not null,
	title text not null
);
alter table rsss.download_list owner to rssadmin;

create table rsss.download_history
(
	id serial not null
    	constraint download_history_pk
			primary key,
	dl_date timestamp default now(),
	dl_user text,
	dl_type text,
	dl_filename text,
	validity boolean not null,
	dl_status text
);
alter table rsss.download_history owner to rssadmin;

create sequence rsss.seq_users
	minvalue 10000
	maxvalue 9999999999;
create table rsss.users
(
	id integer default nextval('rsss.seq_users'::regclass) not null
		constraint users_pkey
			primary key,
	username text not null
		constraint users_username_key
			unique,
	password text not null,
	created timestamp default now(),
	modified timestamp,
	validity boolean default true,
	permissions text,
	lastaccess timestamp
);
alter table rsss.users owner to rssadmin;

create sequence rsss.seq_permissions
	maxvalue 9999999999;
create table rsss.permissions
(
	id integer default nextval('rsss.seq_permissions'::regclass) not null
		constraint permissions_pkey
			primary key,
	permname varchar(20) not null,
	validity boolean
);
alter table rsss.permissions owner to rssadmin;

create table rsss.genres
(
	id serial not null
		constraint genres_pk
			primary key,
	name text not null,
	category text not null,
	created timestamp not null,
	modified timestamp not null,
	validity boolean not null
);
alter table rsss.genres owner to rssadmin;

create table rsss."genreUpdate"
(
	update timestamp not null
);
alter table rsss."genreUpdate" owner to rssadmin;

create table rsss.cmd
(
	id serial not null
        constraint cmd_pk
                primary key,
	cmd_name text not null,
	cmd_type text not null,
	created timestamp not null,
	modified timestamp not null,
	validity boolean not null
);
alter table rsss.cmd owner to rssadmin;

```




### 전체 테이블 및 시퀀스 (2020.05.25)
빠진부분들이 있어서 다시 추가하여 업로드함

```sql
create sequence rsss.seq_permissions
	maxvalue 9999999999;

alter sequence rsss.seq_permissions owner to rssadmin;

create table rsss.download_history
(
    id          serial  not null,
    dl_date     timestamp default now(),
    dl_user     text,
    dl_type     text,
    dl_filename text,
    validity    boolean not null,
    dl_status   text
);

alter table rsss.download_history
    owner to rssadmin;

create unique index download_history_id_uindex
    on rsss.download_history (id);

create sequence rsss.seq_users
	minvalue 10000
	maxvalue 9999999999;

alter sequence rsss.seq_users owner to rssadmin;

create sequence rsss.download_list_id_seq;

alter sequence rsss.download_list_id_seq owner to rssadmin;

create sequence rsss.collection_plan_id_seq;

alter sequence rsss.collection_plan_id_seq owner to rssadmin;

create sequence rsss.cmd_id_seq;

alter sequence rsss.cmd_id_seq owner to rssadmin;

create sequence rsss.genres_id_seq;

alter sequence rsss.genres_id_seq owner to rssadmin;

create table rsss.download_list
(
	id serial not null
		constraint download_list_pk
			primary key,
	created timestamp not null,
	status text default 'new'::text,
	planid integer not null,
	path text not null,
	title text not null
);

alter table rsss.download_list owner to rssadmin;

create unique index download_list_id_uindex
	on rsss.download_list (id);

create table rsss.permissions
(
	id integer default nextval('rsss.seq_permissions'::regclass) not null
		constraint permissions_pkey
			primary key,
	permname varchar(20) not null,
	validity boolean
);

alter table rsss.permissions owner to rssadmin;

create table rsss.collection_plan
(
    id             serial                                                 not null
        constraint collection_plan_pk
            primary key,
    tool           text                                                   not null,
    logtype        text                                                   not null,
    description    text,
    collecttype    integer   default 1,
    cinterval      integer   default 0                                    not null,
    ownerid        integer                                                not null,
    stop           boolean   default false,
    tscreated      timestamp default (now())::timestamp without time zone not null,
    tsstart        timestamp                                              not null,
    tsend          timestamp                                              not null,
    tslastcollect  timestamp,
    tsnext         timestamp,
    tslastpoint    timestamp,
    tscollectstart timestamp default now()                                not null,
    laststatus     text      default 'registered'::text                   not null,
    planname       text                                                   not null,
    logtypestr     text,
    fab            text
);

alter table rsss.collection_plan
    owner to rssadmin;



create table rsss.cmd
(
	id serial not null,
	cmd_name text not null,
	cmd_type text not null,
	created timestamp not null,
	modified timestamp not null,
	validity boolean not null
);

alter table rsss.cmd owner to rssadmin;

create unique index cmd_id_uindex
	on rsss.cmd (id);

create table rsss.genres
(
	id serial not null
		constraint genres_pk
			primary key,
	name text not null,
	category text not null,
	created timestamp not null,
	modified timestamp not null,
	validity boolean not null
);

alter table rsss.genres owner to rssadmin;

create unique index genres_id_uindex
	on rsss.genres (id);

create unique index genres_name_uindex
	on rsss.genres (name);

create table rsss."genreUpdate"
(
	update timestamp not null
);

alter table rsss."genreUpdate" owner to rssadmin;

create table rsss.users
(
	id integer default nextval('rsss.seq_users'::regclass) not null
		constraint users_pkey
			primary key,
	username text not null
		constraint users_username_key
			unique,
	password text not null,
	created timestamp default now(),
	modified timestamp,
	validity boolean default true,
	permissions text,
	lastaccess timestamp
);

alter table rsss.users owner to rssadmin;

```




### 전체 테이블 및 시퀀스 (2020.05.21)
이 항목에서 유저정보와 권한정보 테이블에서 사용하는 시퀀스에 대한 정의가 추가되어 있다. 


```sql
create sequence seq_permissions
    maxvalue 9999999999;

alter sequence seq_permissions owner to rssadmin;

create sequence seq_users
    minvalue 10000
    maxvalue 9999999999;

alter sequence seq_users owner to rssadmin;

create table rsss.download_list
(
    id      serial    not null
        constraint download_list_pk
            primary key,
    created timestamp not null,
    status  text default 'new'::text,
    planid  integer   not null,
    path    text      not null,
    title   text      not null
);

alter table rsss.download_list
    owner to rssadmin;

create unique index download_list_id_uindex
    on rsss.download_list (id);

create table permissions
(
    id       integer default nextval('rsss.seq_permissions'::regclass) not null
        constraint permissions_pkey
            primary key,
    permname varchar(20)                                               not null,
    validity boolean
);

alter table permissions
    owner to rssadmin;

create table users
(
    id          integer   default nextval('rsss.seq_users'::regclass) not null
        constraint users_pkey
            primary key,
    username    text                                                  not null
        constraint users_username_key
            unique,
    password    text                                                  not null,
    created     timestamp default now(),
    modified    timestamp,
    validity    boolean   default true,
    permissions text,
    lastaccess  timestamp   
);

alter table users
    owner to rssadmin;


create table rsss.collection_plan
(
    id             serial                                                 not null
        constraint collection_plan_pk
            primary key,
    tool           text                                                   not null,
    logtype        text                                                   not null,
    description    text,
    collecttype    integer   default 1,
    cinterval      integer   default 0                                    not null,
    ownerid        integer                                                not null,
    stop           boolean   default false,
    tscreated      timestamp default (now())::timestamp without time zone not null,
    tsstart        timestamp                                              not null,
    tsend          timestamp                                              not null,
    tslastcollect  timestamp,
    tsnext         timestamp,
    tslastpoint    timestamp,
    tscollectstart timestamp default now()                                not null,
    laststatus     text      default 'registered'::text                   not null,
    planname       text                                                   not null
);

alter table rsss.collection_plan
    owner to rssadmin;



create table cmd
(
    id       serial    not null,
    cmd_name text      not null,
    cmd_type text      not null,
    created  timestamp not null,
    modified timestamp not null,
    validity boolean   not null
);

alter table cmd
    owner to rssadmin;

create unique index cmd_id_uindex
    on cmd (id);

```

유저정보추가 및 변경 (2020.05.18)

```sql
create table rsss.users
(
    id          integer   default nextval('rsss.seq_users'::regclass) not null
        constraint users_pkey
            primary key,
    username    text                                                  not null
        constraint users_username_key
            unique,
    password    text                                                  not null,
    created     timestamp default now(),
    modified    timestamp,
    validity    boolean   default true,
    permissions text,
    lastaccess  timestamp    
);

```
#### VFTP관련 COMMAND table추가(2020.05.18)

```sql
create table cmd
(
    id       serial    not null,
    cmd_name text      not null,
    cmd_type text      not null,
    created  timestamp not null,
    modified timestamp not null,
    validity boolean   not null
);

alter table cmd
    owner to rssadmin;

create unique index cmd_id_uindex
    on cmd (id);

```


다운로드리스트 (2020.05.15)

```sql
create table rsss.download_list
(
    id      serial    not null
        constraint download_list_pk
            primary key,
    created timestamp not null,
    status  text default 'new'::text,
    planid  integer   not null,
    path    text      not null,
    title   text      not null
);

alter table rsss.download_list
    owner to rssadmin;

create unique index download_list_id_uindex
    on rsss.download_list (id);

```

#### 유저정보 (2020.04.28)

```sql
create table rsss.users
(
    id          integer default nextval('rsss.seq_users'::regclass) not null
        constraint users_pkey
            primary key,
    username    text                                                not null
        constraint users_username_key
            unique,
    password    text                                                not null,
    created     date    default now(),
    modifed     date,
    validity    boolean default true,
    permissions text
);

alter table rsss.users
    owner to rssadmin;


```

#### 권한정보 (2020.04.28)

```sql
create table rsss.permissions
(
    id       integer default nextval('rsss.seq_permissions'::regclass) not null
        constraint permissions_pkey
            primary key,
    permname varchar(20)                                               not null,
    validity boolean
);

alter table rsss.permissions
    owner to rssadmin;


```

#### 플랜정보 (2020.05.25)

정기(자동)수집을 위해 플랜 데이터를 저장하고 관리하기 위한 테이블

- 2020/05/21  수집 정지 기능 구현을 위해 필드 추가
- 2020/05/25  Fab 정보 수집을 위한 필드 추가

```sql
create table rsss.collection_plan
(
    id             serial                                                 not null
        constraint collection_plan_pk
            primary key,
    tool           text                                                   not null,
    logtype        text                                                   not null,
    description    text,
    collecttype    integer   default 1,
    cinterval      integer   default 0                                    not null,
    ownerid        integer                                                not null,
    stop           boolean   default false,
    tscreated      timestamp default (now())::timestamp without time zone not null,
    tsstart        timestamp                                              not null,
    tsend          timestamp                                              not null,
    tslastcollect  timestamp,
    tsnext         timestamp,
    tslastpoint    timestamp,
    tscollectstart timestamp default now()                                not null,
    laststatus     text      default 'registered'::text                   not null,
    planname       text                                                   not null,
    logtypestr     text,
    fab            text
);

alter table rsss.collection_plan
    owner to rssadmin;



```
