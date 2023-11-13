DELIMITER //
CREATE FUNCTION GetNextMovieId() RETURNS VARCHAR(10)
BEGIN
    DECLARE v_maxMovieId VARCHAR(10);
    DECLARE v_nextMovieId INT;
    SELECT MAX(id) INTO v_maxMovieId FROM moviedb.movies;
    SET v_nextMovieId = CAST(SUBSTRING(v_maxMovieId, 3) AS SIGNED) + 1;
    SET v_maxMovieId = CONCAT('tt', v_nextMovieId);
    RETURN v_maxMovieId;
END //
DELIMITER ;
