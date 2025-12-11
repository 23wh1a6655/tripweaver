require('dotenv').config();
const express = require('express');
const axios = require('axios');
const qs = require('qs');
const cors = require('cors');

const app = express();
app.use(express.json());
app.use(cors());

const PORT = process.env.PORT || 5000;
const AMADEUS_ID = process.env.AMADEUS_CLIENT_ID;
const AMADEUS_SECRET = process.env.AMADEUS_CLIENT_SECRET;

let cachedToken = null;
let tokenExpiry = 0;

async function getToken() {
  if (cachedToken && Date.now() < tokenExpiry) return cachedToken;
  if (!AMADEUS_ID || !AMADEUS_SECRET) throw new Error('Missing Amadeus credentials on server');

  const body = qs.stringify({ grant_type: 'client_credentials', client_id: AMADEUS_ID, client_secret: AMADEUS_SECRET });
  const res = await axios.post('https://test.api.amadeus.com/v1/security/oauth2/token', body, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  });

  cachedToken = res.data.access_token;
  tokenExpiry = Date.now() + (res.data.expires_in - 60) * 1000;
  return cachedToken;
}

app.get('/api/flights', async (req, res) => {
  try {
    const token = await getToken();
    const amRes = await axios.get('https://test.api.amadeus.com/v2/shopping/flight-offers', {
      headers: { Authorization: `Bearer ${token}` },
      params: req.query,
    });
    res.json(amRes.data);
  } catch (err) {
    console.error('Proxy error:', err?.response?.data || err.message || err);
    const status = err.response?.status || 500;
    const body = err.response?.data || { message: err.message };
    res.status(status).json(body);
  }
});

app.listen(PORT, () => console.log(`Proxy listening on http://localhost:${PORT}`));
