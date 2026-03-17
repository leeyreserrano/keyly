USE keyly;

DELIMITER $$

CREATE TRIGGER trg_after_insert_sucursal
AFTER INSERT ON Sucursals
FOR EACH ROW
BEGIN
  INSERT INTO Config (
    uuid,
    sucursal_id,
    permetre_tots_dominis,
    dies_expiracio
  ) VALUES (
    UUID_TO_BIN(UUID()),
    NEW.id,
    FALSE,
    30
  );
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_usuaris_create_baul
AFTER INSERT ON Usuaris
FOR EACH ROW
BEGIN
    INSERT INTO Baguls (uuid, propietari_id, data_creacio)
    VALUES (UUID_TO_BIN(UUID()), NEW.id, NOW());
END$$

DELIMITER ;