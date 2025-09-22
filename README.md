
# Bajaj API Test – Spring Boot Solution

This project is a **Spring Boot application** built for **Bajaj Finserv Health | Qualifier 1 (Java)**.
It automatically registers candidate details, retrieves a webhook & JWT token, solves the assigned SQL question based on `regNo`, and submits the final SQL query to the webhook.

---

## 📌 Problem Statement

On application startup:

1. Send a `POST` request to `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA` with candidate details.
2. Receive a response containing:

   * `webhook` (submission URL)
   * `accessToken` (JWT token)
3. Based on the last **two digits of regNo**:

   * **Odd → Question 1**
   * **Even → Question 2**
4. Submit the SQL query to the webhook with:

   * `Authorization: Bearer <accessToken>`
   * `Content-Type: application/json`
   * Body:

     ```json
     {
       "finalQuery": "SQL_QUERY"
     }
     ```

---

## 🏗 Project Structure

```
bajaj/
 ├── src/main/java/com/github/Danish811/Bajaj_API_Test/
 │     ├── BajajApiTestApplication.java   # Spring Boot entrypoint
 │     └── StartupRunner.java             # Runs flow on startup
 ├── src/main/resources/
 │     └── application.properties         # App configuration
 ├── pom.xml                              # Maven dependencies
 └── README.md
```

---

## ⚙️ Tech Stack

* **Java 17**
* **Spring Boot 3.4.0**
* **Maven**
* **RestTemplate** (HTTP requests)
* **Jackson** (JSON processing)

---

## ▶️ How to Run

### 1. Clone the repo

```bash
git clone https://github.com/Danish811/Bajaj.git
cd Bajaj
```

### 2. Build the JAR

```bash
mvn clean package -U
```

The compiled JAR will be inside `target/`:

```
target/bajaj-api-test-0.0.1-SNAPSHOT.jar
```

### 3. Run the JAR

```bash
java -jar target/bajaj-api-test-0.0.1-SNAPSHOT.jar
```

On startup, the app will:

* Register your details
* Get the webhook & JWT
* Generate the SQL query based on `regNo`
* Submit it to the webhook

---

## 📝 Example Output

```
Webhook URL: https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA
Access Token: ...
Submission Status: 200 OK
Response: {"message":"Query submitted successfully"}
```

---

## 📦 Submission Checklist

✔ Public GitHub repo
✔ Code + Final JAR
✔ Direct raw JAR link (downloadable):

```
https://raw.githubusercontent.com/Danish811/Bajaj/main/target/bajaj-api-test-0.0.1-SNAPSHOT.jar
```

---

## 👤 Author

**Mohammad Danish Sheikh**

* Reg No: `0101CS221081`
* Email: `sheikhd811@gmail.com`

