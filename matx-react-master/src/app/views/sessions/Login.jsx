import React, { useState } from "react";
import { useDispatch } from "react-redux";
import { loginSuccess, loginFailure } from "../../redux/slices/authSlice";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleLogin = (e) => {
    e.preventDefault();

    // Hardcoded credentials
    const hardcodedCredentials = {
      username: "admin",
      password: "password123",
    };

    if (username === hardcodedCredentials.username && password === hardcodedCredentials.password) {
      dispatch(loginSuccess({ username }));
      navigate("/dashboard"); // Redirect to dashboard after login
    } else {
      dispatch(loginFailure("Invalid username or password"));
    }
  };

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={handleLogin}>
        <div>
          <label>Username:</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit">Login</button>
      </form>
    </div>
  );
};

export default Login;