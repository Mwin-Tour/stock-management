DROP TABLE IF EXISTS commande_products;
DROP TABLE IF EXISTS commande;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;

CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
CREATE TABLE commande (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_name VARCHAR(255),
    client_address VARCHAR(255),
    client_phone_number VARCHAR(50),
    prix_total DOUBLE,
    creat_at DATE
);

CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    category_id BIGINT,
    prix_unitaire DOUBLE NOT NULL,
    path_image VARCHAR(255),
    quantite_en_stock INT NOT NULL,
    seuil_alerte INT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE commande_products (
    commande_id BIGINT NOT NULL,
    products_id BIGINT NOT NULL,
    PRIMARY KEY (commande_id, products_id),
    FOREIGN KEY (commande_id) REFERENCES commande(id) ON DELETE CASCADE,
    FOREIGN KEY (products_id) REFERENCES product(id)
);
