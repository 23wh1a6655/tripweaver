import React, { useState, useRef } from "react";
import axios from "axios";
import "./DestinationSearch.css";
import Navbar from "./navbar";

export default function DestinationSearch() {
  const resultsRef = useRef(null);

  const [query, setQuery] = useState("");
  const [category, setCategory] = useState("tourism");
  const [results, setResults] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const API_BASE = "http://localhost:8090/api";

  const handleSearch = async () => {
    if (!query.trim()) {
      setError("Search query cannot be empty.");
      return;
    }

    setError("");
    setLoading(true);

    try {
      const url = `${API_BASE}/destination/search?query=${encodeURIComponent(
        query
      )}&category=${encodeURIComponent(category)}`;

      const res = await axios.get(url);

      const normalized = (res.data || []).map((d) => ({
        name: d.name || "Unknown Place",
        formatted: d.address || "",
        categories: d.category || "",
        latitude: d.latitude,
        longitude: d.longitude,
      }));

      setResults(normalized);

      if (resultsRef.current) {
        window.scrollTo({
          top: resultsRef.current.offsetTop - 80,
          behavior: "smooth",
        });
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Failed to fetch destinations. Check backend or network."
      );
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="explore-container">
      <Navbar />

      {/* MAIN CONTENT */}
      <div className="main-content">
        <h2>Destination Finder</h2>

        <div className="search-box">
          <input
            type="text"
            placeholder="Search a city, country, or landmark"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />

          <select value={category} onChange={(e) => setCategory(e.target.value)}>
            <option value="tourism">Tourism (General)</option>
            <option value="tourism.sights">Tourist Spots</option>
            <option value="entertainment">Entertainment</option>
            <option value="accommodation">Hotels & Stays</option>
            <option value="catering">Restaurants & Food</option>
          </select>

          <button onClick={handleSearch} disabled={loading}>
            {loading ? "SEARCHING..." : "SEARCH"}
          </button>
        </div>

        {results.length === 0 && !loading && (
          <p className="info-message">
            Enter a destination and category to see results below.
          </p>
        )}

        {error && <p className="error-message">⚠️ {error}</p>}

        {/* RESULTS */}
        <div className="results-container" ref={resultsRef}>
          {results.map((d, i) => (
            <div key={i} className="dest-card">
              <h3>{d.name}</h3>
              <p className="address">{d.formatted || "Address Not Available"}</p>

              <div className="details-row">
                <span>
                  <strong>Categories:</strong>{" "}
                  <span className="category-value">
                    {(d.categories || "N/A").split(",").join(", ")}
                  </span>
                </span>

                <span>
                  <strong>Coordinates:</strong>{" "}
                  <span className="coords-value">
                    {d.latitude && d.longitude
                      ? `${d.latitude}, ${d.longitude}`
                      : "N/A"}
                  </span>
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}