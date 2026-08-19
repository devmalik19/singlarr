You can say that, it is a mixtape management library.

I use it to manage my custom album folders in my music library.
For example : Best of Christmas Songs, Best of 90s, Movie Soundtracks etc.

You search for a song and assign it to one of the album.
After downloading, it will be moved to that album's folder and Artist and Album meta tags will be updated accordingly (This can be disabled via settings).

### Docker command

````
docker run -p 3600:3600 ghcr.io/devmalik19/singlarr:latest
````

### Docker compose

````
version: "3.8"
services:
    singlarr:
        image: ghcr.io/devmalik19/singlarr:latest
        ports:
            - "3600:3600"
        volumes:
            - /path/to/config:/config 
            - /path/to/library:/library #optional
            - /path/to/download-client-downloads:/downloads #optional
        environment:
            # - PORT=3600  # Optional, if you want to change the port.
            # - BASE_URL=/singlarr  # Optional, if you want to run the app under subfolder like domain.com/singlarr (useful for reverse proxy)
            # - USER=user # Optional, if you want to change the default username.
            # - PASSWORD=XXXXXX  # Optional, if you want to change the default password.            
            # - LOGGING_LEVEL=DEBUG  # Optional
        restart: unless-stopped
````

# TODO
## alpha release :


## Good to have (Not needed) :
