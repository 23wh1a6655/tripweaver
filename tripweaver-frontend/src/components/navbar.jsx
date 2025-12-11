import React from "react";
import { useNavigate } from "react-router-dom";
import "./navbar.css";
export default function Navbar() {
  const navigate = useNavigate();

  // Retrieve user data from localStorage
  const username = localStorage.getItem("username");
  const isLoggedIn = !!username;

  // Navigation handlers
  const goToSignin = () => navigate("/signup"); 
  const goToSearch = () => (isLoggedIn ? navigate("/search") : navigate("/signup"));
  const goToProfile = () => navigate("/profile");
  
  const logout = () => {
    localStorage.removeItem("username");
    navigate("/signup");
  };

  return (
    <nav className="navbar">
      <h1 className="logo" onClick={() => navigate("/")}>
        Trip<span>Weaver</span>
      </h1>

      <ul className="menu">
        <li onClick={goToSearch}>Destinations</li>
        <li onClick={() => navigate("/trips")}>Trips</li>
        <li onClick={goToSearch}>Bookings</li>

        {!isLoggedIn ? (
          <li onClick={goToSignin}>Login / Signup</li>
        ) : (
          <>
            <li onClick={goToProfile}>👤 {username}</li>
            <li onClick={logout} style={{ color: "red", cursor: "pointer" }}>
              Logout
            </li>
          </>
        )}
      </ul>
    </nav>
  );
}