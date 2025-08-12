# java-filmorate
Template repository for Filmorate project.

# Диаграмма БД
[Диаграмма](images/BD_Diagram.png)

# Скрипты для рабты с БД

## Get all films

```SQL
SELECT * 
FROM films
```

## Get all users

```SQL
SELECT *
FROM users
```

## Found user by ID

```SQL
SELECT *
FROM users
WHERE id = ''
```

## Found film by ID

```SQL
SELECT *
FROM films
WHERE id = ''
```

## Check all user friends by ID

```SQL
SELECT friend_id
FROM frends
Where user_id = ''
```

## Get all films by genre

```SQL
SELECT f.id, f.name
FROM films AS f
JOIN film_genre AS fg ON f.id = fg.film_id
JOIN ganres g ON fg.ganr = g.id
WHERE g.name = ''
```

## Get all genres

```SQL
SELECT id, name
FROM ganres
```

## Get film rating

```SQL
SELECT f.id, f.name, r.rating
FROM films f
JOIN ratings r ON f.id = r.film_id
WHERE f.id = 5
```

## Get all films liked by user

```SQL
SELECT f.id, f.name
FROM films AS f
JOIN likes AS l ON f.id = l.film_id
WHERE l.user_id = 3
```