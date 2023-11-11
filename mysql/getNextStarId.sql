DELIMITER //
CREATE FUNCTION GetNextStarId() RETURNS VARCHAR(10)
BEGIN
    DECLARE v_maxStarId VARCHAR(10);
    DECLARE v_nextStarId INT;
    SELECT MAX(id) INTO v_maxStarId FROM moviedb.stars;
    SET v_nextStarId = CAST(SUBSTRING(v_maxStarId, 3) AS SIGNED) + 1;
    SET v_maxStarId = CONCAT('nm', v_nextStarId);
    RETURN v_maxStarId;
END //
DELIMITER ;