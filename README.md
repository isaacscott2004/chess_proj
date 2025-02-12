# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## Chess Server Design
[![Sequence Diagram](Chess_server_design.png)](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIdTUWjFZfF5vD59SYHYsgoU+Ylj0r7lqOH5PF+SwfjAALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbrlNO5iEHqBq0uBozTNB7wmkS4YWhy3K8m+hjnDA9G2jydqUFo8gwKGTFup2BGlPUUDoqYpgERqxEsh6uqZLG-ocYxTJiZGHLRjA06zugMCQDAakOq60pJpeyGkUGhloI6zrJghJZYTy2a5pgwEgkhJRXGBI7vhMXwGY2zbBXB7Y+YU2Q9v2g69AFtwQcFNZ2WF84RQcZicKu3h+IEXgoEZWG+Mwx7pJkmCxReRTUNe0gAKK7o19SNc0LQPqoT7dKFc5Rc5QIgX16DwUNUC+eCMBofYZWYQeZU4Ri+FyoRBKKaSRgoNwqlBgG6VzppZoRoUlrpsMEA0PpB1GQAZt4wz8VAgppJQhh+oYHEwJsYAgKkagiVplmTfKMCKsqjmJoNqaoQtvoeQgeYudFdV+T0-5XijNVgH2A7dDApZLrlnj5RukK2ru0IwAA4qOrIVae1XnswvnXtTrUdfYo69TdDkAWy3lXCNaDYt5BQSdN0K06MqiYVLdNLXhkMoAUFmkuSVI0Sg+11hlR3MTpZIUjAX1OsA11xtogPHeJq3lBxysKaJSkwBrlOxNLajUYFKD69pp0cq7xvAMqrujgAktIsw6AgoANgadyzCemQJ6OsxcVzowAHKjjA90QI9nsvf9kIm1r3QxDAiSGBAt3GYsZc+7LMdx1XsDJ771sGyDKEVBUmcoJH0exyA8da0nlX6uPYfZ6OjSNI7VlnONqHyzLCNI+Ntv1WmoF9APqjjJU-QD5HR8AIy9gAzAALE8Hep0Fv5PC3o+P-cz9TAPOejMFewwI0DGO9OzY3KATcBEDIFQOgTA2BcD4EQPii+fedMj4VBPhHaQF9r53ymA-LWkEpivzHj7QhKDZ6-1-P-QBOUVwk3XIEbAPgoDYG4PAFShhPYpEnkzHILNrJ+UqLUBonNubBF5kOb+o4gElAKILUs5CUA-w-msYWY1Uxsgkp6PUnsehwA4Z7RWYAZHOnyGrd0QdNY+x1jOPWXc3QFEtBrRuyVRgwDNhbYMVtzHbydKDB28kiLO02tozIntvZQGUbMKRow-aWQDqRG0M8UDmQ2hGHuColQqkCetYJ7oNae0jrCGJncfGGwJs40+0h7HAwEVNKpi9obAnKKElAhi1CeW8oWAC-lFFn3KJfW+kV+ZL1AQgmASDEp9KwQMnBkVaF5QYQESw200LJBgAAKQgHxLhARiG8PMJozG5RqiUjvC0AePNdZziHKw4AKyoBwAgGhKA0TMEyI7E0kCcxY4PKeS8lYAB1Fg4c2otAAEK7gUHAAA0l8Kp2Db7p15uoxCdTQYACttloHCVsnk7S0TLWVkEoG6sKTvxsfZOJ5pynONNkgc205Ul5N8eCAJq0nakosQU0cxTMHUpOuycot0eS2gHoKDxDSykZPFVbHJMg0ken+qPcOt1-nQAUFQEOSAY7azuX8550A3mjEjgKliwqHrJMZLMP6KBR4wCQHXfVlB1WwC+pXcGKppXopQjAKSMlF65K5aUPwgk2m8o4rMZ1jzDWvOSaampNKEmhr1Fa7QMBkgZFSC4u4VcnW-JdbGxN6SfWZIhoEr5aYsX4tHBvLyyNfEgQ+VjZmuMEoE3RqYZciyCoBC8PcrsXpYDAGwKwiiBBEjcMZtjI5O8TlNRam1DqxgooC2RlcVFnzS0wBANwPAsJEBDqMStZCnLjotL3XCM15TIQXSuigEolg-Q8kcPnR6K7vUS09Y0oC66d2XrrV0pePTCartGa2iZeMkigYWUAA)

