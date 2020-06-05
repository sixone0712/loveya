DO '
    begin
        if not exists (select schema_name from information_schema.schemata where schema_name=''rsss'') THEN
            create schema rsss;
            alter schema rsss owner to rssadmin;

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
                laststatus text default ''registered''::text not null,
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
                status text default ''new''::text,
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
                id integer default nextval(''rsss.seq_users''::regclass) not null
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
                id integer default nextval(''rsss.seq_permissions''::regclass) not null
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
        end if;
    end;
'  LANGUAGE plpgsql

