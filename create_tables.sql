DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
	id varchar(10) NOT NULL,
	supervisor boolean,
	PRIMARY KEY (id)
);