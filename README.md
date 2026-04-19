🎓 Student Management System

A full-stack Student Management System built using Java and Spring Boot, designed to manage students, courses, and role-based access within an academic institution. The application provides separate dashboards for Admins and Students, ensuring secure access and clean separation of responsibilities.

📌 Project Overview

This project aims to simplify the administrative and academic workflow of educational institutions by digitizing student and course management. Administrators can create students and assign courses, while students can securely log in to view their profiles and enrolled courses.

The application follows a Model–View–Controller (MVC) architecture and implements Spring Security for authentication and authorization.

✨ Features
🔐 Authentication & Authorization

Secure login system using Spring Security

Role-based access control (ADMIN, STUDENT, FACULTY)

Unauthorized access prevention

👨‍💼 Admin Module

Admin login dashboard

Create and manage student accounts

Assign courses to students

View registered students and course mappings

👨‍🎓 Student Module

Student login dashboard

View assigned courses

View personal profile details

📊 Grades & transcripts (faculty & student)

Faculty enter marks (0–100) per course; CGPA is computed on a 4.0 scale from graded, approved enrollments.

Students download an official **PDF transcript** from the profile or courses page.

✅ Attendance

Faculty mark daily attendance per course; students see attendance percentage per course.

Students below **75%** are flagged automatically for follow-up.

📬 Course catalog & self-enrollment

Students browse all courses and **request enrollment**; admins **approve or reject** pending requests (see *Enrollment requests* under Admin).

Reduces manual course assignment for optional electives while keeping admin control.

📝 Assignments & submissions

Faculty post assignments with due dates; students upload files per assignment.

Faculty view submissions and record grades on the submissions screen.

📅 Academic calendar & timetable

Admins maintain institution-wide events (holidays, exams, registration windows).

Faculty add **weekly timetable slots** (day, time, room) per course; enrolled students and faculty see combined views under **Calendar & timetable**.

🏗 Architecture & Design

MVC design pattern

Clean separation of Controller, Service, and Repository layers

Thymeleaf templates for server-side rendering

🛠 Tech Stack
Layer	Technology
Backend	Java, Spring Boot
Security	Spring Security
Frontend	Thymeleaf, HTML, CSS
Database	H2 (file) or MySQL (configure JDBC URL)
ORM	Spring Data JPA / Hibernate
Build Tool	Maven
📂 Project Structure
Student-Management/
│
├── src/main/java
│   └── com.example.studentmanagement
│       ├── controller
│       ├── service
│       ├── repository
│       ├── model
│       └── config
│
├── src/main/resources
│   ├── templates
│   │   ├── admin
│   │   └── student
│   ├── static
│   └── application.properties
│
├── pom.xml
└── README.md

⚙️ Setup & Installation
Prerequisites:
Java 17+
Maven
MySQL
Git

Steps to Run Locally:
Clone the repository
git clone <>
Navigate to project directory
cd Student-Management
Configure Database
Update application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/student_db
spring.datasource.username=root
spring.datasource.password=yourpassword


Run the application

mvn spring-boot:run


Access the application

http://localhost:8080

If you upgraded from an older version that used a direct student–course join table, stop the app and delete the local H2 database files under `data/` (or your configured JDBC URL) once so Hibernate can recreate the `enrollments` schema cleanly.

🔑 Default Roles
Role	Access
Admin	Manage students & courses
Student	View assigned courses & profile

(Admin credentials can be configured in the database or during initialization)

📚 Learning Outcomes

Hands-on experience with Spring Boot & Spring Security

Understanding role-based authentication

MVC architecture implementation

Database integration using JPA

Real-world full-stack Java application design

👤 Author

MD Rasel MAmun
Softeware Engineering | Full  Stack Developer

⭐ Acknowledgements

Spring Boot & Spring Security documentation

Open-source community
