# Hospital Management
## A Hospital Management System built using HTML,CSS,Javascript for frontend and Java for backend.
#### This application is built for the patients and administrator in the hospital. Using this Application, administrator can add patients, as they register and already registered patient can login using their PatientId. PatientId is a unique Id given by the hospital administrator for each of the patient. Administrator has a separate login page, where if the adminId is given, it will login to admin's page where the admin can see already existing patients in the hospital and their details. An Admin can edit the details of the patient(once they are confirmed of covid-19 or cured from it) and delete a patient if they are no longer admitted in the hospital. I have build this application using HTML,CSS and Javascript for frontend and Java for backend. Initially, I used the servlet-api for HTTP requests(that's why i have downloaded the jar file for servlet-api), but I got stuck in between and then used the default http handler in the JDK. I have downloaded MySQL-connector(This jar contains mysql jdbc driver) and json jar file(for json library in java). Used JDBC for connectivity with MySQL database.
#### Step 1: open XAMPP and start MySQL
#### Step 2: open MySQL Workbench and create a database(my database is called jdbc-video) and tables(I have two tables here: admin and patients)
#### (Admin is registered with id - 100)
#### (Patients table has columns namely: name, id, contact, age, covid, address)
#### Step 3: Run the main function in MyJDBC.java file
#### Step 4: Fill the form

