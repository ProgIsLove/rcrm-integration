# RCRM
   My application upsert data to [raynet-crm](https://raynet.cz/vyzkouset-zdarma/). Enjoy 30 days of free use.

## Working with RCRM app in your IDE

###
The following items should be installed in your system:
* Java 21
* Maven
* Docker
* Lombok
* Your preffered IDEA
  * Eclipse
  * Intellij IDEA

### Steps:

1) On the command line
    ```
    git clone https://github.com/ProgIsLove/rcrm-integration.git
    ```
2) Run docker desktop
3) Open terminal   
   ```
   ${yourPath}/rcrm-integration>docker-compose up
    
   ${yourPath}/rcrm-integration>mvn clean install -DskipTests

   ${yourPath}/rcrm-integration>java -jar .\rcrm-integration-0.0.1-SNAPSHOT.jar
   ```
4) Check the example file, where you'll find raynet.postman_collection.json. The primary endpoint is http://localhost:${PORT}/host/uploadData.
5) Enjoy <3   
   
    
     
    
   
