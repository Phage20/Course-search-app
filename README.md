<h1>Course Search Application</h1>
<p>This project is a Spring Boot application that uses Elasticsearch to provide search and autocomplete functionality for a catalog of courses.</p>
  <hr style="border: 0; border-top: 1px solid #30363d; margin: 24px 0;">

  <h2>1. Setup & Running</h2>
  
  <h3>Running Elasticsearch</h3>
  <p>The project uses Docker Compose to run a local Elasticsearch instance.</p>
  <ol>
    <li><strong>Start the container</strong>:<br>Navigate to the project's root directory and run the following command to start Elasticsearch in the background.
      <pre style="background-color: #161b22; padding: 16px; border-radius: 6px; color: #c9d1d9; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace; font-size: 14px; overflow: auto;"><code>docker-compose up -d</code></pre>
    </li>
    <li><strong>Verify it's running</strong>:<br>To ensure the cluster is accessible on <code>localhost:9200</code>, run the following command. It may take a minute for the service to become available.
      <pre style="background-color: #161b22; padding: 16px; border-radius: 6px; color: #c9d1d9; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace; font-size: 14px; overflow: auto;"><code>curl http://localhost:9200</code></pre>
      You should see a JSON response confirming that Elasticsearch is running.
    </li>
  </ol>
  
  <h3>Building and Running the Application</h3>
  <ol>
    <li><strong>Build the project</strong>:<br>You can build the application using the Maven wrapper included in the project.
      <pre style="background-color: #161b22; padding: 16px; border-radius: 6px; color: #c9d1d9; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace; font-size: 14px; overflow: auto;"><code>./mvnw clean install</code></pre>
    </li>
    <li><strong>Run the application</strong>:<br>After a successful build, you can run the application with the following command:
      <pre style="background-color: #161b22; padding: 16px; border-radius: 6px; color: #c9d1d9; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace; font-size: 14px; overflow: auto;"><code>./mvnw spring-boot:run</code></pre>
      The application will start on <code>http://localhost:8080</code>, and it will automatically index the course data from <code>sample-courses.json</code> on the first startup.
    </li>
  </ol>
  
  <hr style="border: 0; border-top: 1px solid #30363d; margin: 24px 0;">

  <h2>2. API Endpoints</h2>
  
  <h3>Standard Search</h3>
  <p>The primary search endpoint allows for full-text search, filtering, sorting, and pagination.</p>
  <ul style="padding-left: 20px;">
    <li><strong>Endpoint</strong>: <code>GET /api/search</code></li>
    <li><strong>Example</strong>:
      <pre style="background-color: #161b22; padding: 16px; border-radius: 6px; color: #c9d1d9; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace; font-size: 14px; overflow: auto;"><code>curl "http://localhost:8080/api/search?q=java&amp;category=Technology"</code></pre>
    </li>
  </ul>
  
  <h3>Advanced Search (Bonus Features)</h3>
  
  <h4>Fuzzy Search</h4>
  <p>The search functionality is enhanced to handle typos. For example, a search for <code>"Scince"</code> will correctly match courses with the word "Science".</p>
  <ul style="padding-left: 20px;">
    <li><strong>Example (with typos)</strong>:
      <pre style="background-color: #161b22; padding: 16px; border-radius: 6px; color: #c9d1d9; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace; font-size: 14px; overflow: auto;"><code>curl "http://localhost:8080/api/search?q=Scince"</code></pre>
      <pre style="background-color: #161b22; padding: 16px; border-radius: 6px; color: #c9d1d9; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace; font-size: 14px; overflow: auto;"><code>curl "http://localhost:8080/api/search?q=Cretive+Witing"</code></pre>
    </li>
  </ul>
  
  <h4>Autocomplete Suggestions</h4>
  <p>This endpoint provides title suggestions based on a partial query.</p>
  <ul style="padding-left: 20px;">
    <li><strong>Endpoint</strong>: <code>GET /api/search/suggest</code></li>
    <li><strong>Example</strong>:
      <pre style="background-color: #161b22; padding: 16px; border-radius: 6px; color: #c9d1d9; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace; font-size: 14px; overflow: auto;"><code>curl "http://localhost:8080/api/search/suggest?q=phy"</code></pre>
    </li>
    <li><strong>Expected Response</strong>: A JSON array of suggested course titles.
      <pre style="background-color: #161b22; padding: 16px; border-radius: 6px; color: #c9d1d9; font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace; font-size: 14px; overflow: auto;"><code>[
"Physics Fun: Simple Machines"
]
