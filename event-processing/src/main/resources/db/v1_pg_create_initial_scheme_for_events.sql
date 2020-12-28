create table if not exists issue_event
(
    id serial not null
    constraint issue_events_pk
    primary key,
    issue_number varchar(255),
    title varchar(255),
    body varchar(255),
    repo varchar(255),
    event_time timestamp,
    action varchar(255)
    );

alter table issue_event owner to yurii;

create unique index if not exists issue_events_id_uindex
	on issue_event (id);

create table if not exists issue_comment_event
(
    id bigserial not null
    constraint issue_comment_event_pk
    primary key,
    action varchar,
    owner varchar,
    repo varchar,
    body varchar,
    "senderType" varchar,
    login varchar
);

alter table issue_comment_event owner to yurii;

create unique index if not exists issue_comment_event_id_uindex
	on issue_comment_event (id);
