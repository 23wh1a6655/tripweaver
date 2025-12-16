import React, { useState } from "react";
import axios from "axios";
import Navbar from "./navbar";
import "./Trips.css";

const API_BASE = "http://localhost:8090/api"; // backend URL

function Trips() {
    const [origin, setOrigin] = useState("");
    const [destination, setDestination] = useState("");
    const [date, setDate] = useState("");
    const [trip, setTrip] = useState(null);
    const [error, setError] = useState("");
    const [info, setInfo] = useState("");
    const [loading, setLoading] = useState(false);

    // Mock flights function (remains the same)
    const mockFlights = (o, d, dt) => {
        const baseDate = dt || new Date().toISOString().slice(0, 10);
        const times = ["08:25", "11:10", "15:40", "21:05"];
        const airlines = ["IndiGo", "Air India", "Vistara", "SpiceJet"];
        return times.map((t, idx) => ({
            airline: airlines[idx % airlines.length],
            flightNumber: `${airlines[idx % airlines.length].slice(0, 2).toUpperCase()}${100 + idx}`,
            departureTime: `${baseDate} ${t}`,
            arrivalTime: `${baseDate} ${t.split(':')[0]}:00 +${idx < 2 ? 2 : 3}h`, 
        }));
    };

    const handleSearch = async () => {
        setError("");
        setInfo("");
        setLoading(true);
        setTrip(null);

        const o = origin.trim().toUpperCase();
        const d = destination.trim().toUpperCase();
        const depDate = date || new Date().toISOString().slice(0, 10);
        
        let flights = [];
        let hotels = [];
        let fallbackMessages = [];

        if (!o || !d) {
            setTrip({ flights: mockFlights(o, d, depDate), hotels: [] });
            setInfo("Showing demo flights. Please enter both origin and destination (IATA codes).");
            setLoading(false);
            return;
        }

        // --- 1. Fetch Flights ---
        try {
            const res = await axios.get(`${API_BASE}/trip/search`, {
                // *** FIX: Added 'origin: o' to match TripController ***
                params: { origin: o, destination: d, date: depDate }, 
                withCredentials: true,
            });
            flights = res.data?.flights || [];
        } catch (err) {
            console.error("Flights API Error:", err.response || err.message);
            flights = mockFlights(o, d, depDate);
            fallbackMessages.push("Flights: Showing demo data due to API failure.");
        }

        // --- 2. Fetch Hotels ---
        try {
            // *** FIX: Updated path to /destination/search/google ***
            const res = await axios.get(`${API_BASE}/destination/search/google`, {
                params: { query: d, category: "accommodation" },
                withCredentials: true,
            });
            hotels = res.data || []; 
        } catch (err) {
            console.error("Hotels API Error:", err.response || err.message);
            fallbackMessages.push("Hotels: Could not fetch data.");
            hotels = []; 
        }

        // --- Final State Update ---
        setTrip({ flights, hotels });
        
        if (fallbackMessages.length > 0) {
            setInfo(fallbackMessages.join(' | ')); 
        } else if (flights.length === 0 && hotels.length === 0) {
            setInfo("No results found for your search criteria.");
        }
        
        setLoading(false);
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
                        placeholder="To (IATA e.g. DEL)"
                        value={destination}
                        onChange={(e) => setDestination(e.target.value)}
                    />
                    <input
                        type="date"
                        value={date}
                        onChange={(e) => setDate(e.target.value)}
                    />
                    <button onClick={handleSearch} disabled={loading || !origin || !destination}>
                        {loading ? "Searching..." : "Search"}
                    </button>
                </div>

                {error && <p className="message error">{error}</p>}
                {info && <p className="message info">{info}</p>}

                {/* ... (Results JSX remains the same) ... */}
            </div>
        </>
    );
}

export default Trips;