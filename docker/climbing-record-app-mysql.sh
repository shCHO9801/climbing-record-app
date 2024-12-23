docker run -d --name climbing-mysql \
-e MYSQL_ROOT_PASSWORD="root" \
-e MYSQL_USER="climbing" \
-e MYSQL_PASSWORD="climbing" \
-e MYSQL_DATABASE="climbing" \
-p 3306:3306 \
mysql:latest