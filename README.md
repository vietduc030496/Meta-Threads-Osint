# Meta-Threads-Osint

## Getting started

### Prerequisites
- Git
- Java 17 or later
- Maven
- Docker

### How to run

Clone source code

```bash
$ git clone https://github.com/vietduc030496/Meta-Threads-Osint.git
```

Go to Meta-Threads-Osint folder and run command

```bash
docker build -t thread-osint .
```

Final run

```bash
docker run -d -p 8085:8085 --name thread-osint
```
