
DROP TABLE test_order cascade constraints;
DROP TABLE test_person cascade constraints;
DROP TABLE test_address cascade constraints;


CREATE TABLE test_person (                                  
            IDENTIFIER NUMBER(19,0) NOT NULL,         
            NAME varchar(50) default NULL,                   
            ADDRESS_ID NUMBER(19,0),
            PRIMARY KEY  (IDENTIFIER)                              
          );



CREATE TABLE test_address (                              
                IDENTIFIER NUMBER(19,0) NOT NULL ,         
                STREET varchar(255) not null unique,              
                PRIMARY KEY  (IDENTIFIER)                              
            ); 


ALTER TABLE test_person ADD constraint person_address_index foreign key (ADDRESS_ID) references test_address (IDENTIFIER);	



create table test_order (
	 IDENTIFIER NUMBER(19,0) NOT NULL,  
	 PERSON_ID NUMBER(19,0),
	 primary key (IDENTIFIER)
);


alter table test_order add constraint person_order_index foreign key (PERSON_ID) references test_person (IDENTIFIER);


DROP sequence 	PER_SEQ;	
DROP sequence 	Address_SEQ;	
DROP sequence 	ORDER_SEQ;	

create sequence PER_SEQ;
create sequence Address_SEQ;
create sequence ORDER_SEQ;

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
					   

