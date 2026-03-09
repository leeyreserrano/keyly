USE keyly;

CREATE TABLE `Sucursals` (
  `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
  `uuid` BINARY(16) NOT NULL UNIQUE,
  `nom` VARCHAR(255) NOT NULL,
  `direccio` VARCHAR(255) NOT NULL,
  `ciutat` VARCHAR(255) NOT NULL,
  `pais` VARCHAR(255) NOT NULL,
  `telefon` VARCHAR(255) NOT NULL,
  `correu` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `Dominis` (
  `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
  `uuid` BINARY(16) NOT NULL UNIQUE,
  `sucursal_id` BIGINT NOT NULL,
  `domini` VARCHAR(255) NOT NULL UNIQUE,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_dominis_sucursals` FOREIGN KEY (`sucursal_id`) REFERENCES `Sucursals` (`id`)
);

CREATE TABLE `Rols` (
  `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
  `uuid` BINARY(16) NOT NULL UNIQUE,
  `sucursal_id` BIGINT NOT NULL,
  `nom` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_rols_sucursals` FOREIGN KEY (`sucursal_id`) REFERENCES `Sucursals` (`id`)
);


CREATE TABLE `Departaments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
  `uuid` BINARY(16) NOT NULL UNIQUE,
  `sucursal_id` BIGINT NOT NULL,
  `nom` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `dk_departaments_sucursals` FOREIGN KEY (`sucursal_id`) REFERENCES `Sucursals` (`id`)
);

CREATE TABLE `Usuaris` (
  `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
  `uuid` BINARY(16) NOT NULL UNIQUE,
  `sucursal_id` BIGINT NOT NULL,
  `departament_id` BIGINT NOT NULL,
  `rol_id` BIGINT NOT NULL,
  `nom` VARCHAR(255) NOT NULL,
  `correu` VARCHAR(255) NOT NULL UNIQUE,
  `imatge` VARCHAR(255),
  `contrasenya_master` VARCHAR(60) NOT NULL,
  `data_creacio` DATE NOT NULL,
  `data_ultim_login` DATE,
  `pot_administrar` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_usuaris_sucursals` FOREIGN KEY (`sucursal_id`) REFERENCES `Sucursals` (`id`),
  CONSTRAINT `fk_usuaris_rols` FOREIGN KEY (`rol_id`) REFERENCES `Rols` (`id`),
  CONSTRAINT `fk_usuaris_departaments` FOREIGN KEY (`departament_id`) REFERENCES `Departaments` (`id`)
);

CREATE TABLE `Baguls` (
  `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
  `uuid` BINARY(16) NOT NULL UNIQUE,
  `propietari_id` BIGINT NOT NULL,
  `data_creacio` DATE NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_baguls_usuaris` FOREIGN KEY (`propietari_id`) REFERENCES `Usuaris` (`id`)
);

CREATE TABLE `Items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
  `uuid` BINARY(16) NOT NULL UNIQUE,
  `bagul_id` BIGINT NOT NULL,
  `titol` VARCHAR(255) NOT NULL,
  `nom_usuari` VARCHAR(255) NOT NULL,
  `contrasenya` VARCHAR(255) NOT NULL,
  `iv` VARBINARY(12) NOT NULL,
  `url` VARCHAR(255) NULL,
  `notes` TEXT NULL,
  `favorit` BOOLEAN NULL DEFAULT FALSE,
  `data_creacio` DATE NOT NULL,
  `data_editat` DATE NULL,
  `ultim_access` DATE NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_items_baguls` FOREIGN KEY (`bagul_id`) REFERENCES `Baguls` (`id`)
);

CREATE TABLE `Carpetes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
  `uuid` BINARY(16) NOT NULL UNIQUE,
  `bagul_id` BIGINT NOT NULL,
  `nom` VARCHAR(255) NULL,
  `data_creacio` DATE NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_carpetes_baguls` FOREIGN KEY (`bagul_id`) REFERENCES `Baguls` (`id`)
);

CREATE TABLE `Carpetes_Items` (
  `carpeta_id` BIGINT NOT NULL,
  `item_id` BIGINT NOT NULL,
  CONSTRAINT `fk_carpetes_items_carpetes` FOREIGN KEY (`carpeta_id`) REFERENCES `Carpetes` (`id`),
  CONSTRAINT `fk_carpetes_items_items` FOREIGN KEY (`item_id`) REFERENCES `Items` (`id`)
);

CREATE TABLE `Compartits` (
  `id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
  `uuid` BINARY(16) NOT NULL UNIQUE,
  `usuari_id` BIGINT NOT NULL,
  `tipus_entitat` ENUM('CARPETA','ITEM') NOT NULL,
  `entitat_uuid` BINARY(16) NOT NULL,
  `permisos` ENUM('LECTURA','ESCRIPTURA','ADMINISTRADOR') NOT NULL,
  `data_creacio` DATE NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_compartits` (`tipus_entitat`, `entitat_uuid`),
  CONSTRAINT `fk_compartits_usuaris` FOREIGN KEY (`usuari_id`) REFERENCES `Usuaris` (`id`)
);
