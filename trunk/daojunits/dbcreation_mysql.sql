drop table test_order;
drop table test_person;
drop table test_address;


CREATE TABLE `test_person` (                                  
            `IDENTIFIER` bigint(20) NOT NULL auto_increment,         
            `NAME` varchar(50) default NULL,                   
            `ADDRESS_ID` bigint,
            PRIMARY KEY  (`IDENTIFIER`)                              
          ) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=latin1;

CREATE TABLE `test_address` (                              
                `IDENTIFIER` bigint(20) NOT NULL auto_increment,         
                `STREET` varchar(255) not null unique,              
                PRIMARY KEY  (`IDENTIFIER`)                              
            ) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=latin1  ;


alter table test_person add index person_address_index (ADDRESS_ID), 
	add constraint person_address_index foreign key (ADDRESS_ID) references test_address (IDENTIFIER);



create table test_order (
	`IDENTIFIER` bigint(20) NOT NULL auto_increment,  
	 PERSON_ID BIGINT,
	 primary key (IDENTIFIER)
)ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=latin1;


alter table test_order add index person_order_index 
	(PERSON_ID), add constraint person_order_index foreign key (PERSON_ID) references test_person (IDENTIFIER);


drop table test_user;
drop table test_table_hashed;

CREATE TABLE `test_user` (                                                                         
            `IDENTIFIER` bigint(20) NOT NULL auto_increment,                                                
            `EMAIL_ADDRESS` varchar(100) default NULL,                                                      
            `FIRST_NAME` varchar(50) default NULL,                                                          
            `LAST_NAME` varchar(50) default NULL,                                                           
            `ACTIVITY_STATUS` varchar(100) default NULL,                                                    
            PRIMARY KEY  (`IDENTIFIER`)                                                                 
          ) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;                                                       

CREATE TABLE test_table_hashed (                    
                         IDENTIFIER bigint(20) NOT NULL ,        
                         AVAILABLE tinyint,                      
                         CREATED_ON_DATE DATE DEFAULT NULL,                    
                         COLLECTION_STATUS VARCHAR(50) DEFAULT NULL,           
                         PRIMARY KEY  (IDENTIFIER)                             
                       )  ;
insert into test_user (IDENTIFIER,EMAIL_ADDRESS,FIRST_NAME,LAST_NAME,ACTIVITY_STATUS)VALUES (1,"john@per.co.in","JOHN","REBER","Active");
insert into test_user (IDENTIFIER,EMAIL_ADDRESS,FIRST_NAME,LAST_NAME,ACTIVITY_STATUS)VALUES (2,"sri@per.co.in","srikanth",null,"Active");
