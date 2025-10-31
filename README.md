# datamasking 🔒🧩
---
## 📘 About the Project
Data Masking is a **Spring Boot-based Java web application** that secures sensitive user information by masking personal or confidential data. It supports CSV and Excel uploads, integrates authentication, and stores results securely in a MySQL database.

## 🚀 Features
1. User authentication with Spring Security  
2. Data masking for sensitive fields (Name, Email, Phone, etc.)  
3. Upload and download of masked files (CSV or Excel)  
4. Email notifications using Spring Mail  
5. JSP frontend for user interaction  
6. MySQL integration for data persistence  

## 🛠️ Tech Stack
- **Language:** Java 17  
- **Framework:** Spring Boot 3.2.0  
- **Build Tool:** Maven  
- **Database:** MySQL  
- **Frontend:** JSP  
- **Libraries:** Apache POI, OpenCSV, Apache Commons IO/CSV  

## ⚙️ Setup and Installation

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/nithin1734/datamasking.git
cd datamasking
```

### 2️⃣ Configure Database
Edit the `src/main/resources/application.properties` file:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/datamasking
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3️⃣ Run the Application (Linux/macOS)
```bash
./mvnw spring-boot:run
```

### 4️⃣ Run the Application (Windows)
```bash
mvnw.cmd spring-boot:run
```

### 5️⃣ Package the Application
```bash
./mvnw clean package
```
Then run the generated WAR file:
```bash
java -jar target/data-masking-0.0.1-SNAPSHOT.war
```

### 6️⃣ Access the App
Open your browser and navigate to:
```
http://localhost:8080
```

## 🧪 Run Tests
```bash
./mvnw test
```

## 📂 Project Structure
```
datamasking/
├── src/
│   ├── main/
│   │   ├── java/com/mask/datamasking/  # Java source files
│   │   ├── resources/                   # application.properties, templates, static files
│   └── test/                            # Unit tests
├── pom.xml                              # Maven dependencies and build config
├── mvnw / mvnw.cmd                      # Maven wrapper scripts
└── README.md
```
---
 
📦 **Repository:** [Data Masking](https://github.com/nithin1734/datamasking)

## 📜 License

This repository includes two license files so you can choose the licensing approach:
- LICENSE (MIT) — very permissive  
- LICENSE-APACHE-2.0 — permissive with an explicit patent grant

If you prefer a single license, remove the other file. If you want dual-licensing, keep both.

---

## ✨ Authors
- nithin1734 — repository owner

---


