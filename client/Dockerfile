FROM adoptopenjdk/openjdk15:alpine

# WORKDIR /bin
COPY ./build/libs/client-all.jar ./client-all.jar

# EXPOSE 5000

CMD ["java", "-jar", "./client-all.jar"]