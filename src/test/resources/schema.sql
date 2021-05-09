CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
create table if not exists snack
(
    id     uuid         not null default uuid_generate_v4() primary key,
    name   varchar(500) not null,
    amount numeric       not null
);

create table if not exists review
(
    id       uuid          not null default uuid_generate_v4() primary key,
    snack_id uuid          not null,
    rating   bigint        not null,
    text     varchar(1000) not null
);
