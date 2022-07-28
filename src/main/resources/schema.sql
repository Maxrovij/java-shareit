CREATE TABLE IF NOT EXISTS users
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(1000) NOT NULL,
    requestor_id BIGINT NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    CONSTRAINT requestor_id FOREIGN KEY (requestor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(250) NOT NULL,
    description VARCHAR(600),
    available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT owner FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT request FOREIGN KEY (request_id) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS booking
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status BOOLEAN NOT NULL,
    CONSTRAINT item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT booker FOREIGN KEY (booker_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    comment_text VARCHAR(1000) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    CONSTRAINT item_fk FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT author FOREIGN KEY (author_id) REFERENCES users(id)
);