# MySQL Database Dockerfile for Investi Platform
FROM mysql:8.0

# Set environment variables
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=3a8
ENV MYSQL_USER=investi
ENV MYSQL_PASSWORD=investi123

# Copy the database schema
COPY 3a8.sql /docker-entrypoint-initdb.d/

# Expose MySQL port
EXPOSE 3306

# MySQL will automatically run the SQL file on first startup
