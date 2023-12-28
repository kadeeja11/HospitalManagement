import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyJDBC {

    public static void main(String[] args) {
        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startServer() throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/registerPatient", new PatientHandler());
        server.createContext("/api/checkPatient", new CheckPatientHandler());
        server.createContext("/api/getPatients", new GetPatientsHandler());
        server.createContext("/api/editPatient", new EditPatientHandler());
        server.createContext("/api/removePatient", new RemovePatientHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Server is running on port " + port);
    }

    static class PatientHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getResponseHeaders();
            headers.set("Access-Control-Allow-Origin", "*");
            //headers.set("Access-Control-Allow-Methods", "POST");
            headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

            headers.set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                // Handle preflight requests
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Parse the JSON request body
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    String jsonInput = sb.toString();

                    // Extract patient details from JSON
                    JSONObject jsonObject = new JSONObject(jsonInput);
                    String name = jsonObject.getString("name");
                    String id = jsonObject.getString("id");
                    String contact = jsonObject.getString("contact");
                    String age = jsonObject.getString("age");
                    String covidStatus = jsonObject.getString("covid");
                    String address = jsonObject.getString("address");

                    // Save patient details to the database
                    saveToDatabase(name, id, contact, age, covidStatus, address);

                    // Send response back to the client
                    String response = "Patient registered successfully";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Internal Server Error");
                }
            } else {
                sendErrorResponse(exchange, "Method Not Allowed");
            }
        }

        private void sendErrorResponse(HttpExchange exchange, String message) throws IOException {
            exchange.sendResponseHeaders(500, message.length());
            OutputStream os = exchange.getResponseBody();
            os.write(message.getBytes());
            os.close();
        }

        private static void saveToDatabase(String name, String id, String contact, String age, String covidStatus, String address) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-video", "root", "")) {
                String sql = "INSERT INTO patients (name, id, contact, age, covid, address) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, id);
                    preparedStatement.setString(3, contact);
                    preparedStatement.setString(4, age);
                    preparedStatement.setString(5, covidStatus);
                    preparedStatement.setString(6, address);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //new class

    static class CheckPatientHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getResponseHeaders();
            headers.set("Access-Control-Allow-Origin", "*");
            //headers.set("Access-Control-Allow-Methods", "POST");
            headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

            headers.set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                // Handle preflight requests
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Parse the JSON request body
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    String jsonInput = sb.toString();

                    // Extract patient details from JSON
                    JSONObject jsonObject = new JSONObject(jsonInput);
                    String id = jsonObject.getString("id");
                    String contact = jsonObject.getString("contact");

                    // Check if the patient exists in the database
                    boolean patientExists = checkPatientExists(id, contact);

                    // Send response back to the client
                    String response = patientExists ? "Patient exists, login successful" : "Patient not found";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Internal Server Error");
                }
            } else {
                sendErrorResponse(exchange, "Method Not Allowed");
            }
        }

        private void sendErrorResponse(HttpExchange exchange, String message) throws IOException {
            exchange.sendResponseHeaders(500, message.length());
            OutputStream os = exchange.getResponseBody();
            os.write(message.getBytes());
            os.close();
        }

        private static boolean checkPatientExists(String id, String contact) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-video", "root", "")) {
                String sql = "SELECT * FROM patients WHERE id = ? AND contact = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, id);
                    preparedStatement.setString(2, contact);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    return resultSet.next(); // If there is a result, the patient exists
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }


    //newwwwwwwwwwwwwwwww

    static class GetPatientsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getResponseHeaders();
            headers.set("Access-Control-Allow-Origin", "*");
            headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            headers.set("Access-Control-Allow-Headers", "Content-Type");

            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    // Fetch all patients from the database
                    List<Patient> patients = getAllPatients();

                    // Convert the list of patients to JSON
                    String jsonResponse = convertPatientsListToJSON(patients);

                    // Send response back to the client
                    exchange.sendResponseHeaders(200, jsonResponse.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(jsonResponse.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Internal Server Error");
                }
            } else {
                sendErrorResponse(exchange, "Method Not Allowed");
            }
        }

        private void sendErrorResponse(HttpExchange exchange, String message) throws IOException {
            exchange.sendResponseHeaders(500, message.length());
            OutputStream os = exchange.getResponseBody();
            os.write(message.getBytes());
            os.close();
        }

        private List<Patient> getAllPatients() {
            List<Patient> patients = new ArrayList<>();

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-video", "root", "")) {
                String sql = "SELECT * FROM patients";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        Patient patient = new Patient();
                        patient.setId(resultSet.getString("id"));
                        patient.setName(resultSet.getString("name"));
                        patient.setContact(resultSet.getString("contact"));
                        patient.setAge(resultSet.getString("age"));
                        patient.setCovid(resultSet.getString("covid"));
                        patient.setAddress(resultSet.getString("address"));
                        patients.add(patient);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return patients;
        }

        private String convertPatientsListToJSON(List<Patient> patients) {
            List<JSONObject> jsonList = new ArrayList<>();
            for (Patient patient : patients) {
                JSONObject json = new JSONObject();
                json.put("id", patient.getId());
                json.put("name", patient.getName());
                json.put("contact", patient.getContact());
                json.put("age", patient.getAge());
                json.put("covid", patient.getCovid());
                json.put("address", patient.getAddress());
                jsonList.add(json);
            }

            return jsonList.toString();
        }
    }

    static class EditPatientHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getResponseHeaders();
            headers.set("Access-Control-Allow-Origin", "*");
            //headers.set("Access-Control-Allow-Methods", "POST");
            headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

            headers.set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                // Handle preflight requests
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Parse the JSON request body
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    String jsonInput = sb.toString();

                    // Extract patient details from JSON
                    JSONObject jsonObject = new JSONObject(jsonInput);
                    String id = jsonObject.getString("id");
                    String updatedName = jsonObject.getString("name");
                    String updatedContact = jsonObject.getString("contact");
                    String updatedAge = jsonObject.getString("age");
                    String updatedCovidStatus = jsonObject.getString("covid");
                    String updatedAddress = jsonObject.getString("address");

                    // Edit patient details in the database
                    editPatientDetails(id, updatedName, updatedContact, updatedAge, updatedCovidStatus, updatedAddress);

                    // Send response back to the client
                    String response = "Patient details updated successfully";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Internal Server Error");
                }
            } else {
                sendErrorResponse(exchange, "Method Not Allowed");
            }
        }

        private void sendErrorResponse(HttpExchange exchange, String message) throws IOException {
            exchange.sendResponseHeaders(500, message.length());
            OutputStream os = exchange.getResponseBody();
            os.write(message.getBytes());
            os.close();
        }


        private void editPatientDetails(String id, String name, String contact, String age, String covidStatus, String address) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-video", "root", "")) {
                String sql = "UPDATE patients SET name = ?, contact = ?, age = ?, covid = ?, address = ? WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, contact);
                    preparedStatement.setString(3, age);
                    preparedStatement.setString(4, covidStatus);
                    preparedStatement.setString(5, address);
                    preparedStatement.setString(6, id);

                    preparedStatement.executeUpdate();
                }
            } catch (Exception e) {
            e.printStackTrace();
            }

        }
    }

    static class RemovePatientHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Headers headers = exchange.getResponseHeaders();
            headers.set("Access-Control-Allow-Origin", "*");
            //headers.set("Access-Control-Allow-Methods", "POST");
            headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

            headers.set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                // Handle preflight requests
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Parse the JSON request body
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    String jsonInput = sb.toString();

                    // Extract patient details from JSON
                    JSONObject jsonObject = new JSONObject(jsonInput);
                    String id = jsonObject.getString("id");

                    // Remove patient from the database
                    removePatient(id);

                    // Send response back to the client
                    String response = "Patient removed successfully";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendErrorResponse(exchange, "Internal Server Error");
                }
            } else {
                sendErrorResponse(exchange, "Method Not Allowed");
            }
        }

        private void sendErrorResponse(HttpExchange exchange, String message) throws IOException {
            exchange.sendResponseHeaders(500, message.length());
            OutputStream os = exchange.getResponseBody();
            os.write(message.getBytes());
            os.close();
        }

        private void removePatient(String id) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-video", "root", "")) {
                String sql = "DELETE FROM patients WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, id);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Patient {
        private String id;
        private String name;
        private String contact;
        private String age;
        private String covid;
        private String address;

        // Constructor
        public Patient() {
        }

        // Getter and Setter methods

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getCovid() {
            return covid;
        }

        public void setCovid(String covid) {
            this.covid = covid;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}