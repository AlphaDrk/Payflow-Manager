CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS client_db;
CREATE DATABASE IF NOT EXISTS produit_db;
CREATE DATABASE IF NOT EXISTS facture_db;
CREATE DATABASE IF NOT EXISTS reglement_db;

CREATE USER IF NOT EXISTS 'payflow_user'@'%' IDENTIFIED BY 'payflow_password';

GRANT ALL PRIVILEGES ON auth_db.* TO 'payflow_user'@'%';
GRANT ALL PRIVILEGES ON client_db.* TO 'payflow_user'@'%';
GRANT ALL PRIVILEGES ON produit_db.* TO 'payflow_user'@'%';
GRANT ALL PRIVILEGES ON facture_db.* TO 'payflow_user'@'%';
GRANT ALL PRIVILEGES ON reglement_db.* TO 'payflow_user'@'%';

FLUSH PRIVILEGES;


