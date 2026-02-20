ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '12345678';
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '12345678';

CREATE USER IF NOT EXISTS 'keyly'@'%' IDENTIFIED WITH mysql_native_password BY '12345678';
CREATE USER IF NOT EXISTS 'keyly'@'localhost' IDENTIFIED WITH mysql_native_password BY '12345678';

GRANT ALL PRIVILEGES ON keyly.* TO 'keyly'@'%';
GRANT ALL PRIVILEGES ON keyly.* TO 'keyly'@'localhost';

FLUSH PRIVILEGES;