# BUET Hub

BUET Hub is a Java desktop application designed as a university academic collaboration platform. It provides separate student and teacher workflows for course management, quizzes, performance tracking, profiles, and messaging.

## Features

- Student and teacher account creation and login
- Course enrollment with add/drop functionality
- Course and participant management
- Quiz creation and participation
- Automatic quiz result/performance tracking
- Student profile and dashboard
- Course-based and user-to-user chat
- Client-server communication using Java sockets
- JavaFX GUI with FXML and CSS
- File-based persistence for application data

## Technology Stack

- **Java 21**
- **JavaFX 24.0.2**
- **Maven**
- **FXML / CSS**
- **Java Sockets**
- **Multithreading**

## Project Structure

```text
src/main/java/
└── com/example/demo1/
    ├── Animation/       # UI animations
    ├── datatypes/       # User, Course, Quiz, Message, etc.
    ├── pages/           # JavaFX controllers/pages
    ├── threads/         # Client-server communication
    ├── BuetHub.java     # JavaFX client entry point
    └── server.java      # Socket server entry point

src/main/resources/
└── com/example/demo1/
    ├── *.fxml            # JavaFX UI layouts
    └── styles.css        # Application styling

assets/
└── accounts, course, inbox
    # Runtime file-based application data
```

## Running the Project

1. Install **JDK 21**.
2. Install **Maven** or use the included Maven Wrapper.
3. Open the project as a Maven project in IntelliJ IDEA or another Java IDE.
4. Start `server.java`.
5. Start `BuetHub.java`.
6. The JavaFX login window should open.

The client connects to the server through the host and port configured in `datatypes/Info.java`.

> **Important:** The current project contains sample account/data files and a hard-coded login test value. Remove or replace these before publishing the repository.

## GitHub Publishing Checklist

Before pushing a public repository:

- Remove the `.git/` directory from this project copy if you are creating a fresh repository.
- Remove `target/` build output.
- Do not commit `.idea/` or IDE-specific workspace files.
- Remove or sanitize `assets/accounts/`, `assets/inbox/`, and other runtime data containing real names, IDs, passwords, messages, or quiz results.
- Remove the hard-coded test credentials from `pages/Login.java`.
- Never commit passwords, API keys, tokens, or other secrets.
- Keep `.mvn/`, `mvnw`, `mvnw.cmd`, and `pom.xml`; they are useful for reproducible Maven builds.
- Make sure `.gitignore` includes `target/` and `.idea/`.

## Note

This project is intended as an academic Java/JavaFX application demonstrating GUI development, object-oriented programming, networking, multithreading, and file-based data handling.
