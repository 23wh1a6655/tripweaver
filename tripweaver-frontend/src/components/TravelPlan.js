import React, { useState } from "react";
import axios from "axios";
import "./TravelPlan.css";

function TravelPlan() {
  const [form, setForm] = useState({
    destination: "",
    budget: "",
    startDate: "",
    endDate: ""
  });

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const searchTravel = async () => {
    setLoading(true);
    const res = await axios.post(
      "http://localhost:8090/api/budget/plan",
      form
    );
    setData(res.data);
    setLoading(false);
  };

  return (
    <div className="travel-page">
      {/* HERO */}
      <div className="hero">
        <div className="search-bar">
          <input name="destination" placeholder="Destination" onChange={handleChange} />
          <input name="budget" placeholder="Budget" onChange={handleChange} />
          <input name="startDate" type="date" onChange={handleChange} />
          <input name="endDate" type="date" onChange={handleChange} />
          <button onClick={searchTravel}>Search</button>
        </div>
      </div>

      {loading && <p className="loading">Loading...</p>}

      {/* FLIGHTS */}
      {data && (
        <Section title="✈ Flights">
          {data.flights.map((f, i) => (
            <div className="flight-card" key={i}>
              <img src={f.logo} alt={f.airline} />
              <h4>{f.airline}</h4>
              <p>{f.from} → {f.to}</p>
              <span>₹{f.price}</span>
            </div>
          ))}
        </Section>
      )}

      {/* /* HOTELS
      {data && (
        <Section title="🏨 Hotels">
          {data.hotels.map((h, i) => (
            <ImageCard
              key={i}
              image={h.image}
              title={h.name}
              subtitle={h.address}
              price={`₹${h.price}/night`}
            />
          ))}
        </Section>
      )} */}

      {/* TOURIST PLACES */}
      {data && (
        <Section title="📍 Tourist Places">
          {data.places.map((p, i) => (
            <ImageCard
              key={i}
              image={p.image}
              title={p.name}
              subtitle={p.address}
            />
          ))}
        </Section>
      )}
    </div>
  );
}

const Section = ({ title, children }) => (
  <div className="section">
    <h2>{title}</h2>
    <div className="grid">{children}</div>
  </div>
);

const ImageCard = ({ image, title, subtitle, price }) => (
  <div className="image-card">
    <img src={image} alt={title} />
    <div className="content">
      <h4>{title}</h4>
      <p>{subtitle}</p>
      {price && <span>{price}</span>}
      <button>View More Images</button>
    </div>
  </div>
);

export default TravelPlan;
