

CREATE TABLE test_user (                                  
            `IDENTIFIER` bigint(20) NOT NULL auto_increment,         
            `EMAIL_ADDRESS` varchar(100) default NULL,               
            `FIRST_NAME` varchar(50) default NULL,                   
            `LAST_NAME` varchar(50) default NULL,                    
            `ACTIVITY_STATUS` varchar(100) default NULL,
			`ADDRESS_ID` bigint,
            PRIMARY KEY  (`IDENTIFIER`)                              
          ) 


create table XYZ_ADDRESS (
   IDENTIFIER bigint not null auto_increment,
   STREET varchar(255),
   primary key (IDENTIFIER)
)


create table xyz_user_order (
	`IDENTIFIER` bigint(20) NOT NULL auto_increment,  
	 user_id BIGINT,
	 primary key (IDENTIFIER)
)

alter table xyz_user_order 
    add constraint user_order_index foreign key (user_id) references xyz_user(IDENTIFIER);

alter table XYZ_USER add index user_add_index (ADDRESS_ID), 
	add constraint user_add_index foreign key (ADDRESS_ID) references xyz_ADDRESS (IDENTIFIER);

create sequence USER_SEQ;
create sequence Address_SEQ;
create sequence ORDER_SEQ;

----------------

DROP TABLE TEST_USER cascade constraints;
DROP TABLE TEST_TABLE_HASHED cascade constraints;

CREATE TABLE TEST_USER (                                  
            IDENTIFIER NUMBER(19,0) NOT NULL,      
            EMAIL_ADDRESS VARCHAR(100) DEFAULT NULL,               
            FIRST_NAME VARCHAR(50) DEFAULT NULL,                   
            LAST_NAME VARCHAR(50) DEFAULT NULL,                    
            ACTIVITY_STATUS VARCHAR(100) DEFAULT NULL,
			primary key (IDENTIFIER)                              
          );
		  
DROP sequence 	USER_SEQ;	  
CREATE sequence USER_SEQ;		 


CREATE TABLE TEST_TABLE_HASHED (                    
                         IDENTIFIER NUMBER(19,0) NOT NULL ,        
                         AVAILABLE NUMBER(1,0),                      
                         CREATED_ON_DATE DATE DEFAULT NULL,                    
                         COLLECTION_STATUS VARCHAR(50) DEFAULT NULL,           
                         PRIMARY KEY  (IDENTIFIER)                             
                       )  ;
					   
INSERT INTO TEST_USER (IDENTIFIER,EMAIL_ADDRESS,FIRST_NAME,LAST_NAME,ACTIVITY_STATUS) VALUES ('1','john@per.co.in','JOHN','REBER','Active');
INSERT INTO TEST_USER (IDENTIFIER,EMAIL_ADDRESS,FIRST_NAME,LAST_NAME,ACTIVITY_STATUS)VALUES ('2','sri@per.co.in','srikanth',NULL,'Active');

