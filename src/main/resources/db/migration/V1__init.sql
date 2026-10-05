-- Skema awal user-service, di-generate Hibernate dari entity (Okt 2026), lalu dirapikan.
-- Database yang sudah ada sebelum Flyway dipasang di-baseline di versi 1 (spring.flyway.baseline-on-migrate),
-- jadi file ini hanya dijalankan pada database kosong (CI, volume Docker baru).
-- JANGAN diubah: perubahan skema berikutnya = file baru V2__..., V3__..., dst.

create table user_addresses (
    is_default boolean not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6),
    postal_code varchar(10) not null,
    id uuid not null,
    user_id uuid not null,
    phone varchar(20) not null,
    label varchar(50) not null,
    city varchar(100) not null,
    province varchar(100) not null,
    recipient_name varchar(100) not null,
    street TEXT not null,
    primary key (id)
);

create table users (
    is_active boolean not null,
    loyalty_points integer not null,
    created_at timestamp(6) not null,
    id uuid not null,
    phone varchar(20),
    email varchar(100) not null unique,
    name varchar(100) not null,
    password_hash varchar(255) not null,
    role varchar(255) not null check ((role in ('CUSTOMER','STAFF','ADMIN'))),
    primary key (id)
);

alter table if exists user_addresses
    add constraint FKn2fisxyyu3l9wlch3ve2nocgp
    foreign key (user_id)
    references users;
