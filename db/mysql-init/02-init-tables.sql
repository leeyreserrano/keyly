USE keyly;

CREATE TABLE `Sucursals` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(255) NOT NULL,
  `direccio` VARCHAR(255) NOT NULL,
  `ciutat` VARCHAR(255) NOT NULL,
  `pais` VARCHAR(255) NOT NULL,
  `telefon` VARCHAR(255) NOT NULL,
  `correu` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `Departament` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sucursal_id` BIGINT NOT NULL,
  `nom` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_departament_sucursal` FOREIGN KEY (`sucursal_id`) REFERENCES `Sucursals` (`id`)
);

CREATE TABLE `Rols` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sucursal_id` BIGINT NOT NULL,
  `nom` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_rol_sucursal` FOREIGN KEY (`sucursal_id`) REFERENCES `Sucursals` (`id`)
);

CREATE TABLE `Dominis` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `Usuaris` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sucursal_id` BIGINT NOT NULL,
  `departament_id` BIGINT NOT NULL,
  `rol_id` BIGINT NOT NULL,
  `nom` VARCHAR(255) NOT NULL,
  `correu` VARCHAR(255) NOT NULL UNIQUE,
  `contrasenya_master` VARCHAR(60) NOT NULL,
  `salt` TEXT NOT NULL,
  `data_creacio` DATE NOT NULL,
  `data_ultim_login` DATE NOT NULL,
  `pot_administrar` BOOLEAN NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_usuari_sucursal` FOREIGN KEY (`sucursal_id`) REFERENCES `Sucursals` (`id`),
  CONSTRAINT `fk_usuari_rol` FOREIGN KEY (`rol_id`) REFERENCES `Rols` (`id`),
  CONSTRAINT `fk_usuari_departament` FOREIGN KEY (`departament_id`) REFERENCES `Departament` (`id`)
);

CREATE TABLE `Bagul` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `usuari_id` BIGINT NOT NULL,
  `nom` VARCHAR(255) NOT NULL,
  `correu` VARCHAR(255) NOT NULL,
  `imatge` VARCHAR(255) NULL,
  `contrasenya` TEXT NOT NULL,
  `iv` TEXT NOT NULL,
  `data_creacio` DATE NOT NULL,
  `data_editat` DATE NULL,
  `ultim_access` DATE NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_bagul_usuari` FOREIGN KEY (`usuari_id`) REFERENCES `Usuaris` (`id`)
);

CREATE TABLE `Favorits` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `usuari_id` BIGINT NOT NULL,
  `item_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_favorits_usuari` FOREIGN KEY (`usuari_id`) REFERENCES `Usuaris` (`id`),
  CONSTRAINT `fk_favorits_item` FOREIGN KEY (`item_id`) REFERENCES `Bagul` (`id`)
);

CREATE TABLE `Carpetes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `propietari_id` BIGINT NOT NULL,
  `nom` VARCHAR(255) NOT NULL,
  `data_creacio` DATE NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_carpetes_propietari` FOREIGN KEY (`propietari_id`) REFERENCES `Usuaris` (`id`)
);

CREATE TABLE `Carpetes_Items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `carpeta_id` BIGINT NOT NULL,
  `item_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_carpetes_items_item` FOREIGN KEY (`item_id`) REFERENCES `Bagul` (`id`),
  CONSTRAINT `fk_carpetes_items_carpeta` FOREIGN KEY (`carpeta_id`) REFERENCES `Carpetes` (`id`)
);

CREATE TABLE `Carpetes_Compartides` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `carpeta_id` BIGINT NOT NULL,
  `usuari_id` BIGINT NOT NULL,
  `permisos` ENUM('lectura','escritura','administrador') NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_carpetes_compartides_carpeta` FOREIGN KEY (`carpeta_id`) REFERENCES `Carpetes` (`id`),
  CONSTRAINT `fk_carpetes_compartides_usuari` FOREIGN KEY (`usuari_id`) REFERENCES `Usuaris` (`id`)
);