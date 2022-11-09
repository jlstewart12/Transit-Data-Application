# Transit-Data-Application
A prototype of a web application that uses Mapbox to display the positions of buses along Route 1 for the MBTA

## Tools
* Docker
* Jupyter Notebook
* [MBTA API](https://www.mbta.com/developers/v3-api)
* MYSQL
* MongoDB
* Flask
* Debezium CDC
* Maven SpringBoot

## Installation
1. All Docker containers are associated with a network called ```MBTANetwork```
    ```
    docker network create MBTANetwork
    ```
2. This python script is used to analyze calls to the MBTA API.
    ```python
    mbtaURL = "https://api-v3.mbta.com/vehicles?filter[route]=1&include=trip"

    import urllib.request, json
    with urllib.request.urlopen(mbtaURL) as url:
        data = json.loads(url.read().decode())
    
        with open('data.json', 'w') as outfile:
            json.dump(data, outfile)
    
        with open('data.txt', 'w') as outfile:
            json.dump(json.dumps(data, indent=4, sort_keys=True), outfile)
        
        print(json.dumps(data, indent=4, sort_keys=True))
    ```
    * The [MBTA.sql](https://github.com/jlstewart12/Transit-Data-Application/blob/main/mysqlDocker/MBTA.sql) is used to build a table using the desired fields from the service.
    * A Docker image called ```mysqlmbtamasterimg``` is built from the [Dockerfile](https://github.com/jlstewart12/Transit-Data-Application/blob/main/mysqlDocker/Dockerfile).
        ```
        docker build -t mysqlmbtamasterimg .
        ```
    * A MYSQL Docker container, also within the ```MBTANetwork```, is created.
        ```
        docker run --rm --name mysqlserver -p 3306:3306 --network MBTANetwork -d mysqlmbtamasterimg
        ```
3. A MongoDB Docker container is created to be used for CDC.
    ```
    docker run -p 27017:27017 -- name some-mongo --network MBTANetwork -d mongo
    ```