USE keyly;

DELIMITER $$

CREATE TRIGGER trg_after_insert_sucursal
AFTER INSERT ON Sucursals
FOR EACH ROW
BEGIN
  INSERT INTO Config (
    uuid,
    sucursal_id,
    permetre_tots_dominis
  ) VALUES (
    UUID_TO_BIN(UUID()),
    NEW.id,
    FALSE
  );
END$$

DELIMITER ;