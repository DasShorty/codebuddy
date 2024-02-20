FROM openjdk:21

COPY . /home
WORKDIR /home

ENTRYPOINT java -jar codebuddy.jar