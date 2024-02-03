# Utilisez une image Java officielle en tant que base
FROM eclipse-temurin:17-jdk

# Définir le répertoire de travail
WORKDIR /app

COPY ./ /app/

# Exécutez Maven pour construire l'application
RUN ./mvnw clean package -DskipTests=true

# Définir le point d'entrée de l'application
CMD java -jar target/service-catalogue-0.0.1-SNAPSHOT.jar