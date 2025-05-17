import jwt

# Use the same secret key you configured in application.properties
SECRET = "nr1q2dVrkSyUBoyZ6lT4ve4/Zi5QLaVwfOnY3UQ+ge8="

# Build a payload matching what JwtAuthenticationFilter expects
payload = {
    "userId": 1,
    "deptIds": [10, 20],         # departments your test user “belongs” to
    "roles": ["USER", "ADMIN"]   # roles you want to grant for the test
}

# Encode the token
token = jwt.encode(payload, SECRET, algorithm="HS256")

print("Your test JWT:\n")
print(token)
