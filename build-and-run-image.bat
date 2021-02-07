call cd ./cloud-service
call ./mvnw clean package
call cd ..
call docker-compose up -d