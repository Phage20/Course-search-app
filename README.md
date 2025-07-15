Course Search Application
This project is a Spring Boot application that uses Elasticsearch to provide search and autocomplete functionality for a catalog of courses.

1. Setup & Running
Running Elasticsearch
The project uses Docker Compose to run a local Elasticsearch instance.

Start the container:
Navigate to the project's root directory and run the following command to start Elasticsearch in the background.

Bash

docker-compose up -d
Verify it's running:
To ensure the cluster is accessible on localhost:9200, run the following command. It may take a minute for the service to become available.

Bash

curl http://localhost:9200
You should see a JSON response confirming that Elasticsearch is running.

Building and Running the Application
Build the project:
You can build the application using the Maven wrapper included in the project.

Bash

./mvnw clean install
Run the application:
After a successful build, you can run the application with the following command:

Bash

./mvnw spring-boot:run
The application will start on http://localhost:8080, and it will automatically index the course data from sample-courses.json on the first startup.

2. API Endpoints
Standard Search
The primary search endpoint allows for full-text search, filtering, sorting, and pagination.

Endpoint: GET /api/search

Example:

Bash

curl "http://localhost:8080/api/search?q=java&category=Technology"
Advanced Search (Bonus Features)
Fuzzy Search
The search functionality is enhanced to handle typos. For example, a search for "Scince" will correctly match courses with the word "Science".

Example (with typos):

Bash

curl "http://localhost:8080/api/search?q=Scince"
Bash

curl "http://localhost:8080/api/search?q=Cretive+Witing"
Autocomplete Suggestions
This endpoint provides title suggestions based on a partial query.

Endpoint: GET /api/search/suggest

Example:

Bash

curl "http://localhost:8080/api/search/suggest?q=phy"
Expected Response: A JSON array of suggested course titles.

JSON

[
    "Physics Fun: Simple Machines"
]
