import React, { useState } from "react";
import axios from "axios";
import API_BASE from "../config";
import Navbar from './navbar';
import "./Trips.css";

function Trips() {
  const [origin, setOrigin] = useState("");
  const [destination, setDestination] = useState("");
  const [date, setDate] = useState("");
  const [trip, setTrip] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [info, setInfo] = useState("");

  const amadeusAccessToken = process.env.REACT_APP_AMADEUS_ACCESS_TOKEN || "";
  const amadeusClientId = process.env.REACT_APP_AMADEUS_CLIENT_ID || "";
  const amadeusClientSecret = process.env.REACT_APP_AMADEUS_CLIENT_SECRET || "";

  function mockFlights(dest, dt) {
    const baseDate = dt || new Date().toISOString().slice(0, 10);
    const times = ["08:25", "11:10", "15:40", "21:05"];
    const airlines = ["IndiGo", "Air India", "Vistara", "SpiceJet"]; 
    return times.map((t, idx) => ({
      airline: airlines[idx % airlines.length],
      flightNumber: `${airlines[idx % airlines.length].slice(0,2).toUpperCase()}${100 + idx}`,
      departureTime: `${baseDate} ${t}`,
      arrivalTime: `${baseDate} ${idx < 2 ? "+2h" : "+3h"}`,
    }));
  }

  const handleSearch = async () => {
    setError(""); setInfo(""); setLoading(true);
    try {
      const o = origin.trim().toUpperCase();
      const d = destination.trim().toUpperCase();
      const depDate = date || new Date().toISOString().slice(0, 10);

      let flights = [];
      const presetToken = amadeusAccessToken;

      if (o && d) {
        let token = presetToken;
        if (!token && amadeusClientId && amadeusClientSecret) {
          try {
            const tokenRes = await axios.post(
              "https://test.api.amadeus.com/v1/security/oauth2/token",
              new URLSearchParams({
                grant_type: "client_credentials",
                client_id: amadeusClientId,
                client_secret: amadeusClientSecret,
              }).toString(),
              { headers: { "Content-Type": "application/x-www-form-urlencoded" } }
            );
            token = tokenRes.data?.access_token || "";
          } catch {
            token = "";
          }
        }

        if (token) {
          try {
            const offersRes = await axios.get(
              "https://test.api.amadeus.com/v2/shopping/flight-offers",
              {
                headers: { Authorization: `Bearer ${token}` },
                params: {
                  originLocationCode: o,
                  destinationLocationCode: d,
                  departureDate: depDate,
                  adults: 1,
                },
              }
            );
            const data = offersRes.data?.data || [];
            flights = data.map((offer) => {
              const seg = offer.itineraries?.[0]?.segments?.[0] || {};
              const airline = seg.carrierCode || offer.validatingAirlineCodes?.[0] || "";
              const flightNumber = seg.number || "";
              const departureTime = seg.departure?.at || "";
              const arrivalTime = seg.arrival?.at || "";
              return { airline, flightNumber, departureTime, arrivalTime };
            });
          } catch {
            flights = mockFlights(d, depDate);
            setInfo("Showing demo flights due to Amadeus request failure.");
          }
        } else {
          flights = mockFlights(d, depDate);
          setInfo("Showing demo flights due to missing Amadeus credentials.");
        }
      } else {
        flights = mockFlights(d, depDate);
        setInfo("Showing demo flights. Enter both origin and destination.");
      }

      const hotelUrl = `${API_BASE}/destination/search?query=${encodeURIComponent(destination)}&category=accommodation`;
      let hotels = [];
      try {
        const hotelRes = await axios.get(hotelUrl, { withCredentials: true });
        const hotelsRaw = hotelRes.data || [];
        hotels = hotelsRaw.map((h) => ({
          name: h.name || "",
          address: h.address || "",
          rating: h.rating || "N/A",
        }));
      } catch {
        hotels = [];
      }

      setTrip({ flights, hotels });
    } catch (err) {
      setError(err.response?.data?.message || "Search failed. Try again.");
      setTrip(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Navbar />
      <div className="trips-container">
        <h2>Plan Your Trip</h2>

        <div className="input-group">
          <input
            type="text"
            placeholder="From (IATA e.g. HYD)"
            value={origin}
            onChange={(e) => setOrigin(e.target.value)}
          />
          <input
            type="text"
            placeholder="Destination (e.g. DEL)"
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
          />
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
          />
          <button onClick={handleSearch} disabled={loading}>
            {loading ? "Searching..." : "Search"}
          </button>
        </div>

        {error && <p className="message error">{error}</p>}
        {info && <p className="message info">{info}</p>}

        {trip && (
          <div className="results-section">

            <h3 className="section-title">Flights</h3>
            {trip.flights.length === 0 && <p>No flights found for the selected criteria.</p>}
            {trip.flights.map((f, i) => (
              <div key={i} className="card flight">
                <p>✈️ <b>{f.airline}</b> - {f.flightNumber}</p>
                <p>{f.departureTime} → {f.arrivalTime}</p>
              </div>
            ))}

            <h3 className="section-title">Hotels</h3>
            {trip.hotels.length === 0 && <p>No hotels found for the destination and date.</p>}
            {trip.hotels.map((h, i) => (
              <div key={i} className="card hotel">
                <p>🏨 <b>{h.name}</b></p>
                <p>{h.address}</p>
                <p>⭐ {h.rating}</p>
              </div>
            ))}

          </div>
        )}
      </div>
    </>
  );
}

export default Trips;
