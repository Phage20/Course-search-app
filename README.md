# Course Search Application

This project is a Spring Boot application that uses Elasticsearch to provide search functionality for a catalog of courses.

## 1. Setup & Running

### Running Elasticsearch

The project uses Docker Compose to run a local Elasticsearch instance.

1.  **Start the container**:
    Navigate to the project's root directory and run the following command to start Elasticsearch in the background.

    ```sh
    docker-compose up -d
    ```

2.  **Verify it's running**:
    To ensure the cluster is accessible on `localhost:9200`, run the following command:

    ```sh
    curl http://localhost:9200
    ```

    You should see a JSON response confirming that Elasticsearch is running.