USE keyly;

INSERT INTO Sucursals (uuid, nom, direccio, ciutat, pais, telefon, correu) VALUES
(UUID_TO_BIN(UUID()), 'Prova', 'Carrer 4', 'Barcelona', 'España', '+34 679664352', 'prova@gmail.com'),
(UUID_TO_BIN(UUID()), 'Prova2', 'Carrer 8', 'Madrid', 'España', '+34 679664352', 'prova2@gmail.com'),
(UUID_TO_BIN(UUID()), 'Será eliminado', 'No importa va a morir', 'Para que si no va a existir', 'Dejará de vivir no importa', '+34 666', 'hell@paradise.com'),
(UUID_TO_BIN(UUID()), 'Venecia', 'Venecia 134', 'Venecia', 'Italia', '+34 979664365', 'venecia@keyly.com');

INSERT INTO Departaments (uuid, sucursal_id, departament) VALUES
(UUID_TO_BIN(UUID()), 1, 'Metrologia'),
(UUID_TO_BIN(UUID()), 2, 'Chapistas'),
(UUID_TO_BIN(UUID()), 1, 'IT'),
(UUID_TO_BIN(UUID()), 1, 'RRHH');

INSERT INTO Dominis (uuid, sucursal_id, domini) VALUES
(UUID_TO_BIN(UUID()), 1, '@gmail.com'),
(UUID_TO_BIN(UUID()), 1, '@yahoo.com'),
(UUID_TO_BIN(UUID()), 2, '@insgabrielamistral.cat'),
(UUID_TO_BIN(UUID()), 1, '@keyly.com');

INSERT INTO Rols (uuid, sucursal_id, nom) VALUES
(UUID_TO_BIN(UUID()), 1, 'Administrador'),
(UUID_TO_BIN(UUID()), 2, 'Cap IT'),
(UUID_TO_BIN(UUID()), 1, 'Cap RRHH'),
(UUID_TO_BIN(UUID()), 1, 'Usuario');


INSERT INTO Usuaris (uuid, sucursal_id, departament_id, rol_id, nom, correu, 
contrasenya_master, data_creacio,pot_administrar) VALUES
(UUID_TO_BIN(UUID()), 2, 1, 1, 'Geri', 'user@domain.com', 
'$2a$12$gC/zYDtNNNJrejFER9FpLe6adKCXuWUJ2cjRe7QOXz8dz1OOf0nVi', NOW(), true),
(UUID_TO_BIN(UUID()), 2, 2, 2, 'Yami', 'yami@gmail.com',
'$2a$12$WV7KWjmusFGOKrYQB9Kj5OeqCIqoui3XEOmcV4.MtWKkzHPT9auH2', NOW(), false),
(UUID_TO_BIN(UUID()), 2, 2, 1, 'Ley', 'ley@insgabrielamistral.cat',
'$2a$12$moNVzQp13kG/cFVets45s.wtLNnekKndjFiSgPAz8QNsCR6UAf/wy', NOW(), true),
(UUID_TO_BIN(UUID()), 1, 2, 1, 'Pau', 'p@gmail.com',
'$2a$12$uQT50ira624UjVe3Two7ieM21fvJeGK.20.OU8g1U9up8ltJCXmVe', NOW(), false);

INSERT INTO Baguls (uuid, propietari_id, data_creacio) VALUES
(UUID_TO_BIN(UUID()), 1, NOW()),
(UUID_TO_BIN(UUID()), 2, NOW()),
(UUID_TO_BIN(UUID()), 1, NOW()),
(UUID_TO_BIN(UUID()), 1, NOW());

INSERT INTO Carpetes (uuid, bagul_id, nom, data_creacio) VALUES
(UUID_TO_BIN(UUID()), 1, 'Mail', NOW()),
(UUID_TO_BIN(UUID()), 2, 'Trabajo', NOW()),
(UUID_TO_BIN(UUID()), 1, 'Casa', NOW()),
(UUID_TO_BIN(UUID()), 1, 'Amazon', NOW());

INSERT INTO Items (uuid, bagul_id, titol, nom_usuari, contrasenya, iv, url, notes, favorit, data_creacio) VALUES
(UUID_TO_BIN(UUID()), 1, 'Moodle', 'alumneDAM', 'pepito1234', RANDOM_BYTES(12), 
'https://educaciodigital.cat/iesgabrielamistral/moodle/', 'El moodle del cole jijiji', false, NOW()),
(UUID_TO_BIN(UUID()), 1, 'Gmail', 'alumneDAM', 'pepito1234', RANDOM_BYTES(12),
'https://gmail.com', 'El gmail del cole jijijija', false, NOW()),
(UUID_TO_BIN(UUID()), 2, 'Amazon', 'personal', 'personal1234', RANDOM_BYTES(12),
'https://amazon.com', '', false, NOW()),
(UUID_TO_BIN(UUID()), 1, 'Steam', 'juegardos', 'juegardos jugosos', RANDOM_BYTES(12), '', '', true, NOW());

INSERT INTO Carpetes_Items VALUES
(1, 2),
(1, 1),
(2, 3),
(2, 1);

INSERT INTO Compartits (uuid, usuari_id, tipus_entitat, entitat_uuid, permisos, data_creacio) VALUES
(UUID_TO_BIN(UUID()), 1, 'CARPETA', 
(SELECT uuid FROM Carpetes WHERE id = 1 LIMIT 1),
'ADMINISTRADOR', NOW()),
(UUID_TO_BIN(UUID()), 2, 'ITEM', 
(SELECT uuid FROM Items WHERE id = 2 LIMIT 1),
'LECTURA', NOW()),
(UUID_TO_BIN(UUID()), 2, 'ITEM', 
(SELECT uuid FROM Items WHERE id = 1 LIMIT 1),
'ESCRIPTURA', NOW());