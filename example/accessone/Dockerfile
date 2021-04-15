FROM adoptopenjdk/openjdk15:alpine

# WORKDIR /bin
COPY ./build/libs/server-all.jar ./server-all.jar

EXPOSE 5000

CMD ["java", "-jar", "./server-all.jar"]