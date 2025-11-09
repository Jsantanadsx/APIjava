# 1. Define a imagem base do Java 17
FROM eclipse-temurin:17-jre AS runtime

# 2. Cria uma pasta de trabalho dentro do container
WORKDIR /app

# 3. Copia o .jar da sua API para dentro do container
# (O Maven cria o .jar dentro da pasta "target")
# (Este nome deve ser exato: 'teleconsultas-1.0.0-SNAPSHOT.jar')
COPY target/teleconsultas-1.0.0-SNAPSHOT.jar app.jar

# 4. Expõe a porta que o servidor usa
EXPOSE 8080

# 5. Comando para "ligar" a API
CMD ["java", "-jar", "app.jar"]