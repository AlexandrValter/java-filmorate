# java-filmorate

### ER-диаграмма: 

![](https://github.com/AlexandrValter/java-filmorate/blob/54abc60bf0b5b73cdbbd984c0037f488cf68c5f0/DB%20for%20Filmorate.png?raw=true)

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


