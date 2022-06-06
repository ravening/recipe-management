# Recipe Management


Web application to manage the favourite recipes. 
Provides REST API's to search/create/delete and update the recipes. 
It is also possible to search the recipes using the creation date. 

The REST APIs are implemented in Java using Spring boot framework and then can be 
accessed from UI using Angular framework. The APIs are protected using 
JSON Web Token and hence Postman can be used to send the API request manually after 
getting the Bearer token by logging in.

Currently, APIs are protected using JWT and hence the APIs are protected against attacks.
Also, validation/security is provided in backend using Spring Security for customization.

### Login credentials
Default `username` and `password` user is created for quick login. 


### Accessing UI and backend
Backend runs on port 8080 and frontend runs on port 4200. So navigate to below link to access UI. 
```html
http://localhost:4200
```

To access backend,

```html
http://localhost:8080
```

**Its a requirement that both backend and frontend has to run on the same machine**


### TODO 

HTTPS security can be provided using Letsencrypt SSL.

 
## Prerequisites to run the application

Docker - to run backend container \
Postman - to send API requests manually


### For backend
Java 11 \
Maven \
MySQL (optional) \
Dockerfile is provided, so it can run in container too

### For frontend

Node v18.2.0 \
npm 8.9.0 \
Angular CLI: 13.3.7

Dockerfile is provided but it's not complete yet. 
So need to run on physical machine to access the APIs

If you have trouble installing the packages and setting up the frontend then you \
can also use Postman to send API requests manually. These steps will be mentioned at the end of the document.


### Loading data into database

The data is loaded into database in two ways.

1. Using `src/main/resources/data.sql` . You can add your own entries here as well. \
2. Programmatically in `src/main/java/dbseeder/DatabaseSeeder.java` . You can add more java \
   code to load the data.


## How to run:

To simplify the process, I have already built docker image and pushed it to docker hub. You just need to 
pull the image and run it using

```bash
docker run -it -p 8080:8080 rakgenius/recipes:0.0.1
```

This runs only the backend process and not the frontend part. You still need to build it manually following the 
below-mentioned steps.

### Process for building/running backend

If you have already started the docker container for the backend then proceed to step 14 else read below.


1. By default, the app uses H2 database to persist the data. If you want to use H2 then you don't need to do anything else. 
2. If you want to use mysql then make sure that mysql in installed and running on port 3306.
3. Create database called 'testdb' in mysql. 
4. Comment out the part for H2 and uncomment the config for mysql or change the values in src/main/java/resources/application.properties.
5. If you want to run the backend in docker then start the docker process and follow the below steps else refer to step 11. 
6. The `Dockerfile` is present in the `Backend` folder. Navigate to that folder and run below command
7. `mvn clean package -Djps.track.ap.dependencies=false` . This will build the packages and generates the jar file
8. Once the build is successful, create docker image using `docker built -t backend .`
9. Start the docker container using `docker run -it -p 8080:8080 backend`
10. This will start the backend process. Keep it running. 
11. If you don't want to run backend process in docker then follow below steps. 
12. cd to `Backend` folder and build the packages using `mvn clean package -Djps.track.ap.dependencies=false`
13. Once the build is ready, start the process using ` java -jar target/recipes.jar`

If this is successful then it marks the end of starting the backend process. Next comes the frontend part. 


### Process for building/running frontend

14. Open a new terminal and navigate to `Angular` folder.
15. Make sure that you have nodejs, npm and Angular cli installed.
16. Install all the required node packages using `npm install --force` 
17. Once all the dependencies are installed, start the frontend app using `ng serve --open`
18. If it builds and runs successfully then it will automatically open a new tab in your default browser. 
19. A user with username `username` and password `password` is already created.
20. So you can use these credentials to login. 
21. If you want to create a new user then you can do so by clicking on sign up button.


### Sending API request manually

Refer to the end of the document for the steps.


## UI

In the main UI, you can search for recipes, search for them using the creation data, add new recipe and so on. 
Backend already creates some recipes for you, and they are filtered out according to the recipe category. 
You can create you own recipe by clicking on `New Recipe` button. 
If you want to edit a recipe, click on any of the recipes and click `Update` button at the bottom of the page. 
You can delete the recipe by selecting the recipe and click on `Delete recipe` button at the bottom

## API documentation

You can access all the API documentation for the REST endpoints in

```html
http://localhost:8080/swagger-ui.html
```

Click on the `recipe-controller` button to expand all the api's


## Health check endpoints

The backend uses spring boot actuator to provide health check endpoints so that we can monitor the status of our 
application, and we can also implement our own metrics endpoints. These can be accessed in

```html
http://localhost:8080/actuator
```


## Tests

All the tests are present under `src/test` folder. 
There are a total of 44 tests written to cover all scenarios.


## High level Design

The app uses relational database to store two tables: `recipes` and `ingredients`. 
These two have many-to-many relationship since one recipe can contain multiple ingredients 
and same ingredient can exist in different recipes too. This can also be modeled using 
one-to-many but many-to-many seems more logical.

Whenever we create a new recipe along with ingredients, they both are persisted in the database. 
So if we create a new recipe with the already existing ingredient, a new entry for ingredient won't 
be created in the database.

Each recipe has various fields which is needed as mentioned in the design document.

The exchange between frontend and backend happens through the Data Transfer Objects (DTO) and so 
the actual entities are never revealed to the outside world.


Each endpoint is protected with JWT so that the users without the proper permission can't access the APIs.

Also spring security provides other features to disable attacks on the application. 

This app follows MVC method. 
Frontend part, sends an API request to REST controller of the backend, which in turn goes to Service 
Layer and eventually to the Repository Layer.

API documentation is provided using the Swagger Library and health check endpoints are provided 
through spring boot actuator.

Mapstruct library is used to convert between entities and DTOs


## Sending api requests using postman

1. install the postman app and run it.
2. Send a POST request by passing `username` as username and `password` for password as body

```bash
http://localhost:8080/api/auth/login
{
    "username": "username",
    "password": "password"
}
```

Below figure shows how to login to get Auth token.

<img width="891" alt="Screenshot 2022-06-06 at 16 32 00" src="https://user-images.githubusercontent.com/10645273/172183412-07abd2a0-c400-4003-8030-77545645d2e3.png">


3. This will send you back the Bearer authentication token in response.
4. To send any GET/POST/DELETE/PUT request, add the above received token in the `Authorization` part by selecting `Bearer Token` 
field in Postman


Below figure shows, how to include the auth token in the request header so that api can be accessed


<img width="885" alt="Screenshot 2022-06-06 at 16 32 53" src="https://user-images.githubusercontent.com/10645273/172183700-286ab95e-34f4-4242-ac52-16a8c469c15f.png">

