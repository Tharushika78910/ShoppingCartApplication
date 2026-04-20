# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    libgtk-3-0 \
    libgl1 \
    libglib2.0-0 \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libfreetype6 \
    libfontconfig1 \
    libasound2t64 \
    && rm -rf /var/lib/apt/lists/*

RUN wget https://download2.gluonhq.com/openjfx/17.0.10/openjfx-17.0.10_linux-x64_bin-sdk.zip \
    && unzip openjfx-17.0.10_linux-x64_bin-sdk.zip \
    && mv javafx-sdk-17.0.10 /opt/javafx \
    && rm openjfx-17.0.10_linux-x64_bin-sdk.zip

COPY --from=build /app/target/shopping-cart-app.jar ./shopping-cart-app.jar

CMD ["java", "--module-path", "/opt/javafx/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "shopping-cart-app.jar"]