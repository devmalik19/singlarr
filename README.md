You can say that, it is a mixtape management library.

I use it to manage my custom album folders in my music library.
For example : Best of Christmas Songs, Best of 90s, Movie Soundtracks etc.

You search for a song and assign it to one of the album.
After downloading, it will be moved to that album's folder and Artist and Album meta tags will be updated accordingly (This can be disabled via settings).

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
        volumes:
            - /path/to/config:/config 
            - /path/to/library:/library #optional
            - /path/to/download-client-downloads:/downloads #optional
        environment:
            # - PORT=8019  # Optional, if you want to change the port.
            # - BASE_URL=/singlarr  # Optional, if you want to run the app under subfolder like domain.com/singlarr (useful for reverse proxy)
            # - USER=user # Optional, if you want to change the default username.
            # - PASSWORD=XXXXXX  # Optional, if you want to change the default password.
            # - SPRING_PROFILES_ACTIVE=mariadb # Optional, if you want to use your own MariaDB database.
            # - DB_URL="jdbc:mariadb://localhost:3306/singlarr" # Optional, this is the default value.
            # - DB_USER=mariadb # Optional, this is the default value.
            # - DB_PASSWORD=mariadb # Optional, this is the default value.
            # - ENCRYPTION_KEY=12345678901234567890123456789012 # Optional, please replace this with a 32 byte random string to enable encryption and decryption of credentials in DB
            # - LOGGING_LEVEL=DEBUG  # Optional
        restart: unless-stopped
````

# TODO
## alpha release :

Test torrent and Usenet
From download to artist folder

## Good to have (Not needed) :
