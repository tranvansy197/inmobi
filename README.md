# Setup environment
Java 17
Gradle

# Run Gradle Wrapper
./gradlew build
./gradlew bootRun

# Build and Run Jar file
./gradlew bootJar
java -jar build/libs/demo-0.0.1-SNAPSHOT.jar

# Test API
* POST /guess
Header: Bearer <token>
Request param: "number" : 1

* POST /buy-turns
Header: Bearer <token>
* GET /me
Header: Bearer <token>
* GET /leaderboard
Role: ADMIN
Header: Bearer <token>

* POST /auth/login 
* Role: ADMIN
email: admin@gmail.com
* Role: USER
you have to create an account to login

* POST /auth/register
Request body:
{
	"email": "yourEmail",
	"password": "yourPassword"
}

#VNPAY config
URL address: https://sandbox.vnpayment.vn/merchantv2/
document:  https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html
code demo: https://sandbox.vnpayment.vn/apis/vnpay-demo/code-demo-tích-hợp