# Boardify
Initially named after Trello - Boardify is a collaborative to-do list sharing application.

# Important Note
The Database for this web app is hosted on Dalhousie University's server and is only accessible to Dalhousie students and staff who are connected to Dal's wifi or use Dalhousie's VPN with a valid account. However if you are not a Dal student or staff you will have to create the database and replace the information in Trello/src/main/java/com0
/trello/TrelloBackendApplication.java. More information on this is provided in the "How to run" section.

# Features:
1- The app was developed using React.js (Front-end), Spring Boot (Back-end) and SQL.
2- The backend was developed in a Test-Driven Development (TDD) process with a >70% testing line coverage
3- The app has the following features:
- User registration, authentication, and password retrieval based on security questions
- Creating Workspaces and adding other users to them (Each user sees the workspaces that they are added to)
- Creating Boards within Workspaces and to-do list entries within the Boards
- Removing Boards and list items
- Changing the status of the list items between to-do, doing and done
- Filtering the To-do entries based on status and due date
4- The interface design was not the goal of this project and therefore the style is very basic.

# Prerequisites
1- Ensure you have Node.js and npm installed for React.js.
2- Ensure you have Java and Maven or Gradle installed for Spring Boot.

# How to run
0- (For Dal students: Get connected to Dal's wifi on campus or connect to Dal's VPN - more info available on [here](https://software.library.dal.ca/index.php). Other users need to create a database according to the provided ERD and connect the backend app to the database by modifying information in Boardify/Boardify - Trello/src/main/resources
/application.properties

1- Build and run the back-end using a Java-compatible IDE by running TrelloBackendApplication.java located at Boardify/Boardify - Trello/src/main/java/com0
/trello/TrelloBackendApplication.java

Running the Spring Boot Backend without an IDE:
Navigate to the Spring Boot project directory:

cd path/to/spring-boot-project

Build and run the Spring Boot application:

If you're using Maven:

mvn spring-boot:run

If you're using Gradle:

gradlew bootRun

This will start the Spring Boot server, typically on http://localhost:8080

2- Run the React.js app using an IDE compatible with React.js (e.g. VS Code)

Running the React.js app without an IDE:
Navigate to the React project directory:

cd path/to/react-project

Install the dependencies:

npm install

Start the React development server:

npm start

This will usually start the React development server on http://localhost:3000.

3- Accessing the Application
Open your web browser and go to http://localhost:3000 to view the React frontend.
The frontend should be able to communicate with the Spring Boot backend running on http://localhost:8080.

# Notes
Ensure that the React application is configured to proxy requests to the Spring Boot backend. This is usually done in the package.json file of the React project by adding a "proxy": "http://localhost:8080" entry. If your Spring Boot application uses a different port, make sure to update the proxy configuration in the React project accordingly.

# Author
Arash Tashakori


[Website and Contact information](https://arashtash.github.io/)

# Acknowledgement
This app is inspired by Trello. It was developed in collaboration with two teammates as the project for our CSCI3130 Software Engineering course at Dalhousie University. Despite the entire process of development being done by us, the idea and expectations of this project was provided by the course instructor, Dr. Tushar Sharma

# MIT License

Copyright (c) 2024 arashtash - Arash Tashakori

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

