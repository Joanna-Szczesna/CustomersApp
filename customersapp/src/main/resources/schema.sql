CREATE TABLE CUSTOMER (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    pesel_number CHAR(11) NOT NULL
);

CREATE TABLE COMMUNICATION_METHODS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    email_address VARCHAR(100) NULL,
    residence_address VARCHAR(200) NULL,
    registered_address VARCHAR(200) NULL,
    private_phone_number VARCHAR(11) NULL,
    business_phone_number VARCHAR(11) NULL
);

ALTER TABLE COMMUNICATION_METHODS
ADD CONSTRAINT communication_methods_customer_id
FOREIGN KEY (customer_id) REFERENCES customer(id)