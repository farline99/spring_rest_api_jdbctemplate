create table if not exists phone_owners (
    id bigint auto_increment primary key,
    last_name varchar(100) not null,
    first_name varchar(100) not null,
    middle_name varchar(100) not null,
    phone_number varchar(30) not null unique
);

create table if not exists addresses (
    id bigint auto_increment primary key,
    postal_code varchar(20) not null,
    country varchar(100) not null,
    region varchar(100) not null,
    district varchar(100) not null,
    city varchar(100) not null,
    street varchar(150) not null,
    house varchar(30) not null,
    apartment varchar(30) not null,
    owner_id bigint not null unique,
    foreign key (owner_id) references phone_owners(id)
);
