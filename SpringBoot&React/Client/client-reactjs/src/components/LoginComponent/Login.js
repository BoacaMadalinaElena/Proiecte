import React, { useState } from 'react';
import './Login.css';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [state, setState] = useState({ username: '', password: '', error: '' });
  const navigate = useNavigate();

  const handleUsernameChange = (event) => {
    setState({ ...state, username: event.target.value });
  };

  const handlePasswordChange = (event) => {
    setState({ ...state, password: event.target.value });
  };

  const setCookie = (name, value, days) => {
    const expires = new Date(Date.now() + days * 24 * 60 * 60 * 1000).toUTCString();
    document.cookie = `${name}=${value}; expires=${expires}; path=/`;
  };

  // login
  const handleSubmit = (event) => {
    event.preventDefault();

    fetch('http://localhost:8086/api/pos/idm/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: state.username,
        password: state.password,
      }),
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((data) => {
            throw data;
          });
        } else {
          return response.json();
        }
      })
      .then((data) => {
        setCookie("Token", data.token, 1);
        setCookie("Role", data.role, 1);
        setCookie("Id", data.id, 1);
        if (data.role === "2") {
          setState({ ...state, error: '' });
          navigate('/homePatient');
        } else if (data.role === "1") {
          setState({ ...state, error: '' });
          navigate('/homeDoctor');
        }
      })
      .catch((errorData) => {
        if (errorData && errorData.message) {
          setState({ ...state, error: errorData.message });
        } else {
          setState({ ...state, error: "Datele de autentificare nu sunt valide!" });
        }
      });
  };


  return (
    <div id="primary">
      <div id="div-nested">
        <form onSubmit={handleSubmit}>
          <h1>Conectare</h1>
          <label>Nume de utilizator:</label>
          <input type="text" value={state.username} onChange={handleUsernameChange} />
          <label>Parolă:</label>
          <input type="password" value={state.password} onChange={handlePasswordChange} />
          <br />
          <p>{state.error}</p>
          <input type="submit" value="Conectare" />
        </form>
      </div>
    </div>
  );
};

export default Login;
