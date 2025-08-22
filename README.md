# MFA Callback Service

A secure Spring Boot application that serves as a callback service for Multi-Factor Authentication (MFA) integration with Transmit Security and Twilio for SMS-based OTP verification.

## Features

- JWT validation for Transmit Security authentication
- SMS-based OTP generation and validation using Twilio
- Secure REST API endpoints for MFA flow
- Comprehensive error handling and logging
- Configurable OTP length and expiration
- Asynchronous processing for better performance

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Twilio account (for SMS functionality)
- Transmit Security account (for JWT validation)

## Configuration

Copy the `.env.example` file to `.env` and update the following environment variables:

```bash
# JWT Configuration
JWT_ISSUER_URI=https://your-transmit-security-issuer
JWT_AUDIENCE=your-audience

# Twilio Configuration
TWILIO_ACCOUNT_SID=your-account-sid
TWILIO_AUTH_TOKEN=your-auth-token
TWILIO_PHONE_NUMBER=your-twilio-phone-number

# Application Configuration
SERVER_PORT=8080
```

## API Endpoints

### 1. Initiate MFA

Initiates the MFA process by sending an OTP to the provided phone number.

```http
POST /api/v1/auth/initiate-mfa
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "phoneNumber": "+1234567890"
}
```

**Request Body:**
- `phoneNumber` (string, required): The recipient's phone number in E.164 format

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "OTP sent successfully",
  "data": {
    "phoneNumber": "+1234567890",
    "expiresInMinutes": 5
  }
}
```

**Error Responses:**
- `400 Bad Request`: Invalid phone number format
- `401 Unauthorized`: Missing or invalid JWT token
- `429 Too Many Requests`: Too many OTP requests
- `500 Internal Server Error`: Failed to send SMS

### 2. Verify OTP

Verifies the OTP sent to the user's phone number.

```http
POST /api/v1/auth/verify-otp
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "phoneNumber": "+1234567890",
  "otp": "123456"
}
```

**Request Body:**
- `phoneNumber` (string, required): The phone number that received the OTP
- `otp` (string, required): The one-time password to verify

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "OTP verified successfully",
  "data": {
    "phoneNumber": "+1234567890",
    "verified": true
  }
}
```

**Error Responses:**
- `400 Bad Request`: Invalid OTP format
- `401 Unauthorized`: Invalid or expired OTP
- `404 Not Found`: No OTP found for phone number
- `429 Too Many Requests`: Too many verification attempts

### 3. Health Check

Check the health status of the service.

```http
GET /api/actuator/health
```

**Success Response (200 OK):**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 1234567890,
        "threshold": 10485760
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

## Testing the Application

### Using cURL

1. **Start the application**:
   ```bash
   mvn spring-boot:run
   ```

2. **Get a JWT token** (from your authentication service)

3. **Initiate MFA**:
   ```bash
   curl -X 'POST' \
     'http://localhost:8080/api/v1/auth/initiate-mfa' \
     -H 'Content-Type: application/json' \
     -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
     -d '{"phoneNumber": "+1234567890"}'
   ```

4. **Verify OTP** (use the OTP received via SMS):
   ```bash
   curl -X 'POST' \
     'http://localhost:8080/api/v1/auth/verify-otp' \
     -H 'Content-Type: application/json' \
     -H 'Authorization: Bearer YOUR_JWT_TOKEN' \
     -d '{"phoneNumber": "+1234567890", "otp": "123456"}'
   ```

### Using Swagger UI

1. Access Swagger UI at: http://localhost:8080/api/swagger-ui/index.html
2. Click "Authorize" (lock icon) and enter your JWT token
3. Test the endpoints directly from the browser

## Security Considerations

- Always use HTTPS in production
- Store sensitive information (JWT secrets, Twilio credentials) in environment variables
- Implement rate limiting for the OTP endpoints
- Set appropriate CORS policies for your production environment
- Regularly rotate your API keys and tokens

## Troubleshooting

### Common Issues

1. **JWT Validation Fails**
   - Verify the issuer URL and audience match your Transmit Security configuration
   - Ensure the JWT token has the required scopes
   - Check token expiration time

2. **SMS Not Received**
   - Verify your Twilio account has sufficient balance
   - Check the phone number format (must be in E.164 format)
   - Verify the Twilio phone number is properly configured

3. **High Latency**
   - Check Twilio's service status
   - Verify your network connection
   - Monitor application logs for any errors

## Deployment

### Building the Application
```bash
mvn clean package
```

### Running the JAR
```bash
java -jar target/mfa-callback-service-0.0.1-SNAPSHOT.jar
```

### Environment Variables
All configuration can be overridden using environment variables. See the `.env.example` file for available options.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Twilio](https://www.twilio.com/)
- [Transmit Security](https://www.transmitsecurity.com/)
- [Auth0 JWT](https://github.com/auth0/java-jwt)
}
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
