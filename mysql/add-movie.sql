
DELIMITER //
CREATE FUNCTION GetNextMovieId() RETURNS VARCHAR(10)
READS SQL DATA
BEGIN
    DECLARE v_maxMovieId VARCHAR(10);
    DECLARE v_nextMovieId INT;

    SELECT MAX(id) INTO v_maxMovieId FROM moviedb.movies;

    SET v_nextMovieId = CAST(SUBSTRING(v_maxMovieId, 3) AS SIGNED) + 1;

    SET v_maxMovieId = CONCAT('tt', v_nextMovieId);

    RETURN v_maxMovieId;
END //
DELIMITER ;

DELIMITER //
CREATE FUNCTION GetNextStarId() RETURNS VARCHAR(10)
READS SQL DATA
BEGIN
    DECLARE v_maxStarId VARCHAR(10);
    DECLARE v_nextStarId INT;
    SELECT MAX(id) INTO v_maxStarId FROM moviedb.stars;
    SET v_nextStarId = CAST(SUBSTRING(v_maxStarId, 3) AS SIGNED) + 1;
    SET v_maxStarId = CONCAT('nm', v_nextStarId);
    RETURN v_maxStarId;
END //
DELIMITER ;


DELIMITER //
CREATE PROCEDURE add_movie(
    IN p_title VARCHAR(100),
    IN p_year INT,
    IN p_director VARCHAR(100),
    IN p_starName VARCHAR(100),
    IN p_birthYear INT,
    IN p_genreName VARCHAR(32)
)
BEGIN
    DECLARE v_starId VARCHAR(10);
    DECLARE v_genreId INT;
    DECLARE v_movieId VARCHAR(10);
    DECLARE v_nextStarId VARCHAR(10);
    DECLARE v_success INT DEFAULT 0;


    -- Check if the movie already exists
    IF EXISTS (SELECT 1 FROM moviedb.movies WHERE title = p_title AND year = p_year) THEN
        SELECT 0 as result, NULL as movieId, NULL as starId, NULL as genreId;
    ELSE
        START TRANSACTION;

        SET v_movieId = GetNextMovieId();

        SET v_nextStarId = GetNextStarId();

        IF p_birthYear IS NULL THEN
            SELECT id INTO v_starId FROM moviedb.stars WHERE name = p_starName AND birthYear IS NULL LIMIT 1;
        ELSE
            SELECT id INTO v_starId FROM moviedb.stars WHERE name = p_starName AND birthYear = p_birthYear LIMIT 1;
        END IF;

        IF v_starId IS NULL THEN
            IF p_birthYear IS NULL THEN
                INSERT INTO moviedb.stars (id, name, birthYear) VALUES (v_nextStarId, p_starName, NULL);
            ELSE
                INSERT INTO moviedb.stars (id, name, birthYear) VALUES (v_nextStarId, p_starName, p_birthYear);
            END IF;
                    SET v_success = v_success + ROW_COUNT();
            SET v_starId = v_nextStarId;
        END IF;

        SELECT id INTO v_genreId FROM moviedb.genres WHERE name = p_genreName LIMIT 1;

        IF v_genreId IS NULL THEN
            INSERT INTO moviedb.genres (id, name) VALUES (NULL, p_genreName);
                    SET v_success = v_success + ROW_COUNT();
            SET v_genreId = LAST_INSERT_ID();
        END IF;

        INSERT INTO moviedb.movies (id, title, year, director) VALUES (v_movieId, p_title, p_year, p_director);
            SET v_success = v_success + ROW_COUNT();
        INSERT INTO moviedb.stars_in_movies (starId, movieId) VALUES (v_starId, v_movieId);
            SET v_success = v_success + ROW_COUNT();
        INSERT INTO moviedb.genres_in_movies (genreId, movieId) VALUES (v_genreId, v_movieId);
            SET v_success = v_success + ROW_COUNT();

        IF v_success > 2 AND v_success <6 THEN
            COMMIT;
        ELSE
            ROLLBACK;
        END IF;

        SELECT v_success as result, v_movieId as movieId, v_starId as starId, v_genreId as genreId;
    END IF;
END //
DELIMITER ;