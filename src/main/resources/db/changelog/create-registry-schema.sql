create table registrar
(
    id                         serial not null
        constraint registrar_pk
            primary key,
    name                       varchar(150),
    short_name                 varchar(30),
    description                text,
    parent_registrar_id        integer
        constraint registrar_registrar_id_fk
            references registrar,
    shard_key                  varchar(5),
    institution_type           varchar(15),
    institution_classification varchar(25),
    status                     varchar(15),
    address                    text,
    country                    varchar(50),
    region                     varchar(50),
    province                   varchar(50),
    city                       varchar(50),
    zip_code                   varchar(5),
    phone                      varchar(50),
    mobile                     varchar(50),
    email                      varchar(50),
    representative             varchar(100),
    rep_designation            varchar(50),
    rep_phone                  varchar(50),
    rep_mobile                 varchar(50),
    rep_email                  varchar(50),
    website                    varchar(100),
    reference1                 varchar(50),
    reference2                 varchar(50),
    updates                    text,
    date_time_created          timestamp,
    date_time_updated          timestamp
);

create table registrar_user
(
    id                serial not null
        constraint registrar_user_pk
            primary key,
    registrar_id      integer
        constraint registrar_user_registrar_id_fk
            references registrar,
    access_type       varchar(50),
    role              varchar(50),
    status            varchar(20),
    reference1        varchar(50),
    reference2        varchar(50),
    first_name        varchar(50),
    last_name         varchar(50),
    address           varchar(150),
    province          varchar(50),
    city              varchar(50),
    mobile            varchar(50),
    email             varchar(50),
    socmed1           varchar(50),
    socmed2           varchar(50),
    username          varchar(20),
    password          varchar(140),
    access_key        varchar(140),
    comment           text,
    updates           text,
    valid_from        timestamp,
    valid_to          timestamp,
    date_time_created timestamp,
    date_time_updated timestamp
);

create table registrant
(
    id                 serial not null
        constraint registrant_pk
            primary key,
    registrar_id       integer
        constraint registrant_registrar_id_fk
            references registrar,
    registrant_type    int,
    registrant_name    varchar(20),
    priority           int,
    organization_name  varchar(10),
    organization_id    varchar(10),
    organization_class varchar(10),
    reference_id_type  varchar(20),
    reference_id       varchar(50),
    first_name         varchar(50),
    last_name          varchar(50),
    birth_date         date,
    address            varchar(150),
    province           varchar(50),
    city               varchar(50),
    work_name          varchar(100),
    work_address       varchar(150),
    work_province      varchar(50),
    work_city          varchar(50),
    mobile             varchar(50),
    email              varchar(50),
    socmed1            varchar(50),
    socmed2            varchar(50),
    comment            text,
    updates            text,
    status             varchar(20),
    date_time_created  timestamp,
    date_time_updated  timestamp
);

create table access_pass
(
    id                   serial not null
        constraint access_pass_pk
            primary key,
    registrant_id        integer
        constraint access_pass_registrant_id_fk
            references registrant,
    reference_id        varchar(30),
    pass_type            varchar(10),
    access_type          varchar(10),
    control_code         int,
    id_type              varchar(10),
    plate_or_id          varchar(50),
    name                 varchar(100),
    company              varchar(100),
    remarks              varchar(150),
    scope                integer,
    limitations          varchar(200),
    origin_name          varchar(100),
    origin_address       varchar(150),
    origin_province      varchar(50),
    origin_city          varchar(50),
    destination_name     varchar(100),
    destination_address  varchar(150),
    destination_province varchar(50),
    destination_city     varchar(50),
    valid_from           timestamp,
    valid_to             timestamp,
    issued_by            varchar(20),
    updates              text,
    status               varchar(20),
    date_time_created    timestamp,
    date_time_updated    timestamp
);

create table region
(
    id            serial not null
        constraint region_pk
            primary key,
    code          varchar(5),
    name          varchar(45),
    configuration text,
    status        varchar(15)
);

create table province
(
    id            serial not null
        constraint province_pk
            primary key,
    region_id     integer
        constraint province_region_id_fk
            references region,
    code          varchar(5),
    name          varchar(45),
    configuration text,
    status        varchar(15)
);

create table city
(
    id            serial not null
        constraint city_pk
            primary key,
    province_id   integer
        constraint city_province_id_fk
            references province,
    code          varchar(5),
    name          varchar(45),
    configuration text,
    status        varchar(15)
);

create table system_user
(
    id                serial not null
        constraint system_user_pk
            primary key,
    role              varchar(20),
    name              varchar(100),
    username          varchar(50),
    password          varchar(140),
    access_key        varchar(140),
    email             varchar(50),
    mobile            varchar(50),
    description       text,
    valid_from        timestamp,
    valid_to          timestamp,
    updates           text,
    password_history  text,
    status            varchar(15),
    created_by        varchar(20),
    date_time_created timestamp
);

create table registrar_user_activity_log
(
    id               serial  not null
        constraint scanner_device_pk
            primary key,
    user_id          integer
        constraint activity_log_registrar_user_id_fk
            references registrar_user,
    session_id       varchar(45),
    ip_address       varchar(40),
    type             varchar(25),
    action_timestamp timestamp,
    action           varchar(50),
    comments         text
);

create table access_pass_log
(
    id                serial not null
        constraint checkpoint_transactions_pk
            primary key,
    access_pass_id    integer
        constraint access_pass_log_access_pass_id_fk
            references access_pass,
    event             varchar,
    latitude          numeric,
    longitude         numeric,
    ip_address        varchar(80),
    checkpoint_id     integer,
    scanner_device_id integer,
    date_time_created timestamp,
    date_time_updated timestamp
);

create table system_user_activity_log
(
    id              serial  not null
        constraint scanner_device_pk
            primary key,
    user_id          integer
        constraint activity_log_system_user_id_fk
            references system_user,
    session_id       varchar(45),
    ip_address       varchar(40),
    type             varchar(25),
    action_timestamp timestamp,
    action           varchar(50),
    comments         text
);

create table scanner_device
(
    id                  serial  not null
        constraint scanner_device_pk
            primary key,
    device_type         integer not null,
    unique_device_id    varchar not null,
    registrar_id        integer
        constraint scanner_device_registrar_id_fk
            references registrar,
    registrar_user_id   integer
        constraint scanner_device_registrar_user_id_fk
            references registrar_user,
    date_time_last_used timestamp,
    date_time_created   timestamp,
    date_time_updated   timestamp
);