## CS 122B Project Bobaholic Team

This repository build Fablix Movie Project

### Project Demo URLs

#### Project 1: https://www.youtube.com/watch?v=Yx577l4Oh8o

#### Project 2: https://youtu.be/nDvWTj4hUrA

#### Project 3: https://www.youtube.com/watch?v=K9zh4T_04f4

### Project Contribution

#### Project 1:

- Trinh Nhu Khang (Jayden) Le:
  - Build Movies Page Frontend and Backend (html, js, java Servlet)
  - Modify Single Star Page Frontend and Backend (html, js, java Servlet)
  - Do final touch on Frontend html and css
- Kashyap Patel:
  - Build Single Movie Page Frontend and Backend (html, js, java Servlet)

#### Project 2:

- Trinh Nhu Khang (Jayden) Le:
  - Implement Login Page
  - Implment Main Page except for SearchServlet (endpoint) and Search on the Frontend
  - Implement Extend Project 1 part
  - Fix bug with shopping cart and update sales table
- Kashyap Patel:
  - Implement SearchServlet (endpoint) and Search on the Frontend
  - Implement Shopping Cart
  
#### Project 3:

- Trinh Nhu Khang (Jayden) Le:
  - Implement Task 1, 2, 3, 4, 5
  - Help with ideas and some implementation for optimize the parser
  - Finalize and record video
- Kashyap Patel:
  - Implement Task 6

### Additional Notes

#### Substring Matching Design:

- Used the LIKE operator in the SQL query to perform searching
- Searched for all strings (title, director, star, year) that contained the search keywords
- For example, (? = '' OR m.title LIKE CONCAT('%', ?, '%') looks for whether the parameter ?
  is anywhere in the string or if no keyword search was given
- Used AND logic to combine the four search conditions
- Full search query:  
  SELECT DISTINCT m.id AS id,  
  m.title AS title,  
  m.director AS director,
  m.year AS year,  
  r.rating AS rating  
  FROM movies m  
  JOIN stars_in_movies sm ON m.id = sm.movieId  
  JOIN stars s ON sm.starId = s.id  
  LEFT JOIN ratings r ON m.id = r.movieId  
  WHERE (  
  (? = '' OR m.title LIKE CONCAT('%', ?, '%'))  
  AND (? = '' OR m.director LIKE CONCAT('%', ?, '%'))  
  AND (? = '' OR s.name LIKE CONCAT('%', ?, '%'))  
  AND (? = '' OR m.year = ?)  
  )

#### List of Files that use Prepared Statement:
- 2023-fall-cs122b-bobaholic/src/script/SAXParser/src/main/java/DatabaseHandler.java
- 2023-fall-cs122b-bobaholic/src/employee_dashboard/services/DashboardLoginFormService.java
- 2023-fall-cs122b-bobaholic/src/employee_dashboard/services/AddStarService.java
- 2023-fall-cs122b-bobaholic/src/main_fablix/services/BrowseByGenreService.java
- 2023-fall-cs122b-bobaholic/src/main_fablix/services/BrowseByTitleService.java
- 2023-fall-cs122b-bobaholic/src/main_fablix/services/LoginFormService.java
- 2023-fall-cs122b-bobaholic/src/main_fablix/services/PaymentService.java
- 2023-fall-cs122b-bobaholic/src/main_fablix/services/Random3Service.java
- 2023-fall-cs122b-bobaholic/src/main_fablix/services/SearchService.java
- 2023-fall-cs122b-bobaholic/src/main_fablix/services/SingleMovieService.java
- 2023-fall-cs122b-bobaholic/src/main_fablix/services/SingleStarService.java
- 2023-fall-cs122b-bobaholic/src/main_fablix/AllGenresServlet.java

#### XML Parsing Assumptions:
- Only the stars that are in actors.xml are added to the database (not casts.xml)
  
#### Optimization Report:

- Implemented connection pooling using HikariCP. Connection pooling optimizes the management
  of database connections. Instead of opening and closing a new connection for each database
  operation, a pool of connections is maintained. This reduces the overhead of opening and
  closing connections and improves the overall efficiency of database operations.
- Implemented ExecutorService along with submit to run parsers concurrently, taking advantage
  of parallel processing. Concurrent execution of parsers improves overall performance by
  utilizing multiple threads. This allows for better resource utilization, especially in
  scenarios where parsers can execute independently. The ExecutorService manages the threads
  and simplifies the coordination of concurrent tasks.
- Implemented batch processing for inserting records into the database using addBatch and
  executeBatch methods for statements. Batch processing optimizes database insertions by
  grouping multiple SQL statements into a single batch. This reduces the number of round-trips
  between the application and the database, resulting in improved performance.
- Before implementing the above three optimizations, the time to XML parse took around 25 minutes locally.
- After implementing the above three optimizations, the time to XML parse took 7 minutes locally
  for a time reduction of around 18 minutes.

#### Inconsistency Data:

##### Mains Summary:
- Movies Inserted: 12030
- Genres Inserted: 124
- Genres In Movies Inserted: 9797
- Inconsistent Values (Not Inserted): 112
##### Actors Summary:
- Stars Inserted: 6863
- Inconsistent Values (Not Inserted): 73
- Duplicate Stars: 0
##### Casts Summary:
- Stars in Movies Inserted: 32614
- Inconsistent Values (Not Inserted): 755
- Duplicate Stars In Movies: 13683