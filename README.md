#Superloop code test

How to install:
•	Backend
    o	Use maven to build this application. Go to the application folder and type mvn install.
    o	Go to the target folder, and start the application by "java -jar technical-test-0.1.0.jar".
•	Frontend
    o	Go to the gui folder. 
    o	Run “bower install” to install polymer and required components
    o	Run “npm install -g polymer-cli”
    o	Run “polymer serve” to start the frontend
    o	Go to the url: http://localhost:8081/, start the todo app.
Implemented feature:
•	Todo item list
•	Done item list
•	Add a new todo item
•	View the details of selected item
•	Modify the existing todo item
    o	Modify the task name, description
    o	Mark the task as done.
•	Delete the selected todo item
Endpoint:
•	Get /todo/{itemId} This is used to get the selected todo item
    o	E.g. http://localhost:8080/todo/1
•	Get /todo/listAllItems This is used to get the list of all existed todo items.
    o	E.g. http://localhost:8080/todo/listAllItems
•	Get /todo/getFilteredItemList/{itemStatus} This is used to get the item list based on the selected item status.
    o	E.g. http://localhost:8080/todo/getFilteredItemList/PENDING
•	Post /todo/ This is used to create a new todo item.
•	Put /todo/{itemId} This is used to update the selected todo item.
    o	E.g. http://localhost:8080/todo/1
•	Delete /todo/{itemId} This is used to delete the selected todo item.
    o	E.g. http://localhost:8080/todo/1
•	Delete /todo/deleteAll This is used to delete all of existed todo items.
Unit test:
•	Have unit test for services.
•	Have unit test for controllers.
