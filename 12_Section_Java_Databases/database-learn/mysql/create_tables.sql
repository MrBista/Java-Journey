create table users (
    id int not null auto_increment,
    username varchar(255),
    password varchar(255),
    name varchar(255),
    constraint email_uniqe unique(username),
    primary key(id)
);