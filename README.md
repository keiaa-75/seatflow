# Seatflow

<p align="left">
    <img alt = "Spring" src="https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white"/>
    <img alt = "Thymeleaf" src="https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white"/>
    <img alt = "jQuery" src="https://img.shields.io/badge/jquery-%230769AD.svg?style=for-the-badge&logo=jquery&logoColor=white"/>
    <img alt = "Bulma" src="https://img.shields.io/badge/Bulma-00D1B2?style=for-the-badge&logo=Bulma&logoColor=white"/>
</p>

**Seatflow** is a simple, responsive web application for classroom seat assignment management.

## Features

- **Academic Structure Management**: Organize students by sections with grade levels and academic strands
- **Student Management**: Add students individually or import via CSV bulk upload
- **Classroom Layouts**: Create and manage custom classroom seat configurations
- **Seat Assignment**: Generate seat assignments using various presets (A-Z, Z-A, Random)
- **SPA-like Experience**: Smooth page transitions with jQuery-powered routing

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Apache Maven 3.6.0 or higher

**1. Clone the repository:**

```sh
git clone https://github.com/keiaa-75/seatflow.git
cd seatflow
```

**2. Build the project:**

```sh
mvn clean install
```

**3. Run the application:**

From your IDE, you can run the `DemoApplication.java` file as a Spring Boot app. Alternatively, you can use the command line:

```sh
mvn spring-boot:run
```

The application will start on port `8080`.

## Project Structure

```
com.xinnsuu.seatflow/
├── controller/          # REST API controllers and Web controllers
├── service/            # Business logic services and implementations
├── repository/         # JPA repositories
├── model/              # JPA entities and enums
├── config/             # Configuration classes
└── converter/          # Custom converters
```