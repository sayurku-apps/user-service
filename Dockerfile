# ---- Tahap 1: build .jar (butuh JDK + Maven) ----
FROM eclipse-temurin:26-jdk AS build
WORKDIR /app

# Salin file Maven dulu, unduh dependency. Layer ini di-cache Docker,
# jadi selama pom.xml tidak berubah, build berikutnya tidak unduh ulang.
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B -q package -DskipTests

# ---- Tahap 2: image akhir, cukup JRE + .jar ----
FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
