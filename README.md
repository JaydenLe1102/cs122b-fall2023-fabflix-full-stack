## CS 122B Project Bobaholic Team

This repository build Fablix Movie Project

### Project Demo URLs

#### Project 1: https://www.youtube.com/watch?v=Yx577l4Oh8o

#### Project 2: https://youtu.be/nDvWTj4hUrA

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
