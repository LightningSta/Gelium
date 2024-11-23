create TABLE Person(
                       id serial primary key ,
                       username varchar not null,
                       password varchar not null,
                       nickname varchar not null,
                       role varchar not null
);