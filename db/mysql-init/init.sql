ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '12345678';
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '12345678';
ALTER USER 'keyly'@'%' IDENTIFIED WITH mysql_native_password BY '12345678';
ALTER USER 'keyly'@'localhost' IDENTIFIED WITH mysql_native_password BY '12345678';
FLUSH PRIVILEGES;