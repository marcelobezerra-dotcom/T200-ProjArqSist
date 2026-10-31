# Imagem do servidor do Missao Marte (para teste em nuvem que rode Docker).
# Build: docker build -t missao-marte-servidor .
# Execucao: docker run -p 8080:8080 -v missaomarte-dados:/dados -e MISSAOMARTE_JWT_SEGREDO=<32+ caracteres> missao-marte-servidor

FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew
COPY src src
RUN ./gradlew --no-daemon bootJar -x test

FROM eclipse-temurin:17-jre
RUN useradd --system --uid 1001 app && mkdir /dados && chown app /dados
WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
# Dentro do contêiner o servidor precisa escutar em todas as interfaces; a nuvem informa a porta em PORT.
ENV SERVER_ADDRESS=0.0.0.0 \
    MISSAOMARTE_DADOS=/dados
VOLUME /dados
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
