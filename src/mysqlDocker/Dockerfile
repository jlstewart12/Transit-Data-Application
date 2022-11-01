FROM mysql:8.0

ENV MYSQL_DATABASE \
    MYSQL_ROOT_PASSWORD

ADD MBTA.sql /docker-entrypoint-initdb.d

EXPOSE 3306