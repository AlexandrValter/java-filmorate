# java-filmorate
<<<<<<< HEAD

### ER-диаграмма: 

![](https://github.com/AlexandrValter/java-filmorate/blob/add-director/DB%20for%20Filmorate.png)

В проекте используется база данных H2. Таблицы mpa_rating и genres заполняются данными автоматически.  

### Некоторые примеры SQL запросов:
1. Получение всех фильмов:  
SELECT * FROM films;  
2. Получение фильма по id:  
SELECT * FROM films WHERE id=?;  
3. Получение списка популярных фильмов:  
SELECT f.id, f.name, f.description, f.release_date, f.duration, COUNT (l.user_id)  
FROM films AS f   
LEFT JOIN likes AS l ON f.id = l.film_id  
GROUP BY f.id  
ORDER BY COUNT (l.user_id) DESC   
LIMIT ?;  
4. Получение всех пользователей:  
SELECT * FROM users;  
5. Получение пользователя по id:  
SELECT * FROM users WHERE id = ?;  
6. Получение списка друзей пользователя по id:  
SELECT fr.to_user_id, u.login, u.email, u.name, u.birthday  
FROM users AS u   
RIGHT OUTER JOIN friendship AS fr ON fr.to_user_id = u.id   
WHERE fr.from_user_id = ?;  
7. Получение списка всех жанров:  
SELECT * FROM genres;
8. Получение списка режиссеров:
SELECT * FROM directors;
9. Получение списка фильмов режиссера с сортировкой по дате релиза:
SELECT f.id, f.name, f.description, f.release_date, f.duration, COUNT (l.user_id)
FROM films AS f LEFT JOIN likes AS l ON f.id = l.film_id
WHERE f.ID IN (SELECT ID_FILM FROM DIRECTORS_FILMS_LINK WHERE ID_DIRECTOR=?
GROUP BY f.id ORDER BY f.release_date
10. Получение списка фильмов режиссера с сортировкой по количеству лайков:
SELECT f.id, f.name, f.description, f.release_date, f.duration, COUNT (l.user_id)
FROM films AS f LEFT JOIN likes AS l ON f.id = l.film_id
WHERE f.ID IN (SELECT ID_FILM FROM DIRECTORS_FILMS_LINK WHERE ID_DIRECTOR=?
GROUP BY f.id ORDER BY COUNT(l.user_id) DESC


=======
Template repository for Filmorate project.
>>>>>>> 1300c36 (Initial commit)
