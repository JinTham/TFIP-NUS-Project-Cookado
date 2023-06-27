use railway;

create table item_info (
	user_id varchar(8) not null,
	item_id varchar(8) not null,
    item_name varchar(100) not null,
    topup_amount int not null,
    safety_stock int not null,
    unit varchar(100) not null,
    constraint pk_item_info primary key (item_id)
);
insert into item_info values ('abcd1234','aaaaaaaa','onion',5,1,'piece');
insert into item_info values ('abcd1234','bbbbbbbb','carrot',5,1,'piece');
insert into item_info values ('abcd1234','cccccccc','potato',5,1,'piece');

create table user_info (
	user_id varchar(8) not null,
    username varchar(100) not null,
    email varchar(100) not null,
    privilege boolean not null,
    constraint pk_user_info primary key (user_id),
    unique (username),
    unique (email)
);
insert ignore into user_info values ('abcd1234','Johnny','johnny@gmail.com',false);
insert ignore into user_info values ('abcd1238','Viktor','viktor@gmail.com',false);

create table inventory (
	user_id varchar(8) not null,
    item_id varchar(8) not null,
    quantity int not null,
    constraint fk_inventory_item foreign key (item_id) references item_info(item_id),
    constraint fk_inventory_user foreign key (user_id) references user_info(user_id)
);
insert into inventory (user_id,item_id,quantity) values ('abcd1234','aaaaaaaa','3');
insert into inventory (user_id,item_id,quantity) values ('abcd1234','bbbbbbbb','2');
insert into inventory (user_id,item_id,quantity) values ('abcd1234','cccccccc','6');

create table follow_info (
	follower_id varchar(8) not null,
    followee_id varchar(8) not null
);
insert into follow_info values ('abcd1234','abcd1238');
insert into follow_info values ('abcd1238','abcd1234');

select * from inventory;
select * from item_info;
select * from user_info;
select * from follow_info;