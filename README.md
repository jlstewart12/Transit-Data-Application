# Transit-Data-Application
This project shows the prototyping of a web application that uses Mapbox to display bus positions along Route 1 for the MBTA.

## Tools
* Docker
* Jupyter Notebook
* [MBTA API](https://www.mbta.com/developers/v3-api)
* MySQL
* MongoDB
* Flask
* Debezium CDC
* Maven SpringBoot

## Installation
1. All Docker containers are associated with a network called ```MBTANetwork```. Create the network by running the following command in the terminal.
    ```
    docker network create MBTANetwork
    ```
2. Within the [MBTAapiRequest](https://github.com/jlstewart12/Transit-Data-Application/blob/main/src/MBTAapiRequest.ipynb) jupyter notebook, the urllib package is used to make calls to the MBTA API.
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
    * The [MBTA.sql](https://github.com/jlstewart12/Transit-Data-Application/blob/main/mysqlDocker/MBTA.sql) file contains the Data Definition Language (DDL) used to build a table containing fields needed to analyze the buses' movements throughout the selected route.
    * Created from a [Dockerfile](https://github.com/jlstewart12/Transit-Data-Application/blob/main/mysqlDocker/Dockerfile), a Docker image called ```mysqlmbtamasterimg``` serves as the template for the MySQL Docker container. 
        ```
        docker build -t mysqlmbtamasterimg .
        ```
    * To store the data pulled from the API, create a MySQL Docker container within the ```MBTANetwork```.
        ```
        docker run --rm --name mysqlserver -p 3306:3306 --network MBTANetwork -d mysqlmbtamasterimg
        ```
3. The following command creates a MongoDB Docker container that will capture the changes made within the MySQL container.
    ```
    docker run -p 27017:27017 -- name some-mongo --network MBTANetwork -d mongo
    ```
4. Flask web server:
    * [mysqldb.py](https://github.com/jlstewart12/Transit-Data-Application/blob/main/ProjectFlask/mysqldb.py) takes values associated with specified fields and inserts them into the ```mbta_buses``` MySQL table.
    * [MBTAApiClient.py](https://github.com/jlstewart12/Transit-Data-Application/blob/main/ProjectFlask/MBTAApiClient.py) parses the columns defined in the mbta_buses SQL table.
    * The ```mapboxgl.accessToken``` variable located within the [index.html](https://github.com/jlstewart12/Transit-Data-Application/blob/main/ProjectFlask/templates/index.html) file is set to a Mapbox access token obtained after registering for one[here](https://account.mapbox.com/).
    * [server.py](https://github.com/jlstewart12/Transit-Data-Application/blob/main/ProjectFlask/server.py) is run to initialize the bus list by making an API call to the MBTA database.
    * Once the server is running, opening a browser window and navigating to ```localhost:3000``` will generate a map showing bus markers.
    ![](https://github.com/jlstewart12/Transit-Data-Application/blob/main/src/images/busMap.png)
5. Debezium CDC monitor container:
    * A Docker image called ```debeziummodule16``` is generated from the [Dockerfile](https://github.com/jlstewart12/Transit-Data-Application/blob/main/DebeziumCDC/Dockerfile) located within the DebeziumCDC folder.
        ```
        docker build -t debeziummodule16 .
        ```
    * A Debezium container within the ```MBTANetwork``` serves as the change data capture (CDC) tool. It's created by executing the following command:
        ```
        docker run --it --rm --name debeziumserver --network MBTANetwork debeziummodule16 bash
        ```
    * The Spring Boot Maven plugin includes a run goal which can be used to launch the java application from the command line, as shown in the following example:
        ```
        mvn spring-boot:run
        ```
6. To verify that the MongoDB database is being populated:
    * Create a Docker Java Maven container:
        ```
        docker run --name javamaven --network MBTANetwork  -dti --rm -p 8080:8080    maven:3.6.3-openjdk-11 bash
        ```
    * Set up a new Maven project by following the instructions [here](https://www.mongodb.com/developer/languages/java/java-setup-crud-operations/) for cloning the series' git repository. 
    * Navigate to the parent directory of the ```java-quick-start``` folder and run a Docker command to copy the contents of the folder to the ```javamaven``` Docker container:
        ```
        docker cp java-quick-start javamaven:/
        ```
    * Install a ```nano``` text editor in the ```java-quick-start``` folder with the these commands.
        ```
        apt-get update
        ```
        ```
        apt-get install nano
        ```
    * Create a file called ```ReadCDC.java``` within the ```/java-quick-start/src/main/java/com/mongodb/quickstart``` directory that contains the following code:
        ```java
        package com.mongodb.quickstart;

        import com.mongodb.client.*;
        import org.bson.Document;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.function.Consumer;

        import static com.mongodb.client.model.Filters.*;
        import static com.mongodb.client.model.Projections.*;
        import static com.mongodb.client.model.Sorts.descending;

        public class ReadCDC {

            public static void main(String[] args) {
                try (MongoClient mongoClient = MongoClients.create(System.getProperty("mongodb.uri"))) {
                    MongoDatabase sampleTrainingDB = mongoClient.getDatabase("myDatabase");
                    MongoCollection<Document> myCDCCollection = sampleTrainingDB.getCollection("myCollection");

                Document cdcDocument = myCDCCollection.find(new Document("recordId", "CDC")).first();
                System.out.println("CDC Record: " + cdcDocument.toJson());

                }
            }
        }
        ```
    * When following the Maven project tutorial, set ```mongodb.uri``` to the port associated with the MySQL container.
## Data Analysis
After running the application for several hours, the API data is analyzed within the [MBTAtransitAPI](https://github.com/jlstewart12/Transit-Data-Application/blob/main/src/MBTAtransitAPI.ipynb) notebook.