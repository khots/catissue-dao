CREATE TABLE `test_user` (                                  
            `IDENTIFIER` bigint(20) NOT NULL auto_increment,         
            `EMAIL_ADDRESS` varchar(100) default NULL,               
            `FIRST_NAME` varchar(50) default NULL,                   
            `LAST_NAME` varchar(50) default NULL,                    
            `ACTIVITY_STATUS` varchar(100) default NULL,
			`ADDRESS_ID` bigint,
            PRIMARY KEY  (`IDENTIFIER`)                              
          ) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=latin1


create table test_address (
   IDENTIFIER bigint not null auto_increment,
   STREET varchar(255),
   primary key (IDENTIFIER)
)ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=latin1


create table test_user_order (
	`IDENTIFIER` bigint(20) NOT NULL auto_increment,  
	 user_id BIGINT,
	 primary key (IDENTIFIER)
)ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=latin1

alter table test_user_order 
    add constraint user_order_index foreign key (user_id) references test_user(IDENTIFIER);

alter table test_user add index user_add_index (ADDRESS_ID), 
	add constraint user_add_index foreign key (ADDRESS_ID) references test_address (IDENTIFIER);

//-----------------------
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
