create table if not exists institution
(
    id serial not null
        constraint institution_pk
            primary key,
    classification integer,
    name varchar,
    registration_number varchar,
    institution_type integer,
    date_time_created timestamp,
    date_time_updated timestamp
);

create table if not exists person
(
    id serial not null
        constraint person_pk
            primary key,
    first_name varchar not null,
    middle_name varchar,
    last_name varchar,
    suffix varchar,
    classification varchar,
    id_type integer,
    id_number varchar,
    mobile_number varchar,
    email varchar,
    instituion_id integer
        constraint person_institution_id_fk
            references institution,
    date_time_created timestamp,
    date_time_updated timestamp
);

create table if not exists access_pass
(
    id serial not null
        constraint access_code_pk
            primary key,
    type integer not null,
    access_type integer not null,
    status integer,
    control_code varchar not null,
    person_id integer
        constraint access_code_person_id_fk
            references person,
    scope integer,
    scanner_id varchar,
    vehicle_id integer,
    valid_from timestamp,
    valid_until timestamp,
    date_time_created timestamp,
    date_time_updated timestamp
);

create table if not exists vehicle
(
    id serial not null
        constraint vehicle_pk
            primary key,
    plate_number varchar not null,
    make varchar,
    model varchar,
    color varchar,
    date_time_created timestamp,
    date_time_updated timestamp
);

create table if not exists scanner_device
(
    id serial not null
        constraint scanner_device_pk
            primary key,
    device_type integer not null,
    unique_device_id varchar not null,
    assigned_user_to varchar,
    date_time_last_used timestamp,
    date_time_created timestamp,
    date_time_updated timestamp
);

create table if not exists checkpoint_transactions
(
    id serial not null
        constraint checkpoint_transactions_pk
            primary key,
    access_pass_id integer
        constraint checkpoint_transactions_access_pass_id_fk
            references access_pass,
    access_code integer,
    latitude numeric,
    longitude numeric,
    checkpoint_id integer,
    scannder_device_id integer
        constraint checkpoint_transactions_scanner_device_id_fk
            references scanner_device,
    date_time_created timestamp,
    date_time_updated timestamp
);
