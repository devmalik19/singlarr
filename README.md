### Docker command

````
docker run -p 8019:8019 ghcr.io/devmalik19/singlarr:latest
````

### Docker compose

````
version: "3.8"
services:
    singlarr:
        image: ghcr.io/devmalik19/singlarr:latest
        ports:
            - "8019:8019"
        environment:
            - PORT=8019  # Optional, if you want to change the port.
            - BASE_URL=/singlarr  # Optional, if you want to run the app under subfolder like domain.com/singlarr (useful for reverse proxy)
            - SPRING_PROFILES_ACTIVE=mariadb # Optional, if you want to use your own MariaDB database.
            - DB_URL="jdbc:mariadb://localhost:3306/singlarr" # Optional, this is the default value.
            - DB_USER=mariadb # Optional, this is the default value.
            - DB_PASSWORD=mariadb # Optional, this is the default value.
        restart: unless-stopped
````

# TODO
## alpha release :
1. Test setup
