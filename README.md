# QuickFix_SimpleEngine

QuickFix_SimpleEngine is a simple FIX (Financial Information eXchange) engine and client implementation built on QuickFIX/J. This project provides a basic framework for sending and receiving FIX messages, making it suitable for financial applications that require FIX protocol communication.

## Features

- Simple FIX engine implementation
- FIX client for sending messages
- Distributed message store using Glide API
- Support for FIX 4.4 protocol
- Configurable session settings
- Maven-based project structure

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) 21 or later
- Maven 3.6 or later
- QuickFIX/J 2.3.0
- Valkey Glide 1.0.0 or later (for the distributed message store)

## Installation

To install QuickFix_SimpleEngine, follow these steps:

1. Clone the repository:
   ```
   git clone https://github.com/your-username/QuickFix_SimpleEngine.git
   ```

2. Navigate to the project directory:
   ```
   cd QuickFix_SimpleEngine
   ```

3. Build the project using Maven:
   ```
   mvn clean install
   ```

## Usage

### Running the Engine

To start the FIX engine:

1. Navigate to the engine directory:
   ```
   cd engine
   ```

2. Run the engine application:
   ```
   mvn exec:java -Dexec.mainClass="qf.engine.qf_SimpleEngineApplication"
   ```

### Running the Client

To run the client and send FIX messages:

1. Navigate to the client directory:
   ```
   cd client
   ```

2. Run the client application:
   ```
   mvn exec:java -Dexec.mainClass="qf.client.qf_ClientMessageSenderApplication"
   ```

The client will send 10 new order single messages to the engine with a 1-second delay between each message.

## Project Structure

```
.
├── client
│   ├── pom.xml
│   └── src
│       └── main
│           └── java
│               └── qf
│                   └── client
│                       ├── qf_ClientMessageSender.java
│                       └── qf_ClientMessageSenderApplication.java
├── engine
│   ├── pom.xml
│   └── src
│       ├── main
│       │   └── java
│       │       └── qf
│       │           └── engine
│       │               ├── qf_MessageStore.java
│       │               ├── qf_SimpleEngine.java
│       │               └── qf_SimpleEngineApplication.java
│       └── test
│           └── java
│               └── qf
│                   └── engine
│                       └── AppTest.java
└── README.md
```

## Configuration

Both the engine and client components use configuration files to set up FIX sessions and other parameters:

- Engine configuration: `engine/src/main/resources/qf_SimpleEngine.cfg`
- Client configuration: `client/src/main/resources/sender-fix.cfg`

Ensure these configuration files are properly set up with the correct session parameters before running the applications.

## License

This project is open source and available under the [MIT License](LICENSE).