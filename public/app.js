// ParkEasy Real-Time Web Application Logic (Firebase Cloud Firestore v10)
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-app.js";
import { 
  getFirestore, 
  collection, 
  onSnapshot, 
  doc, 
  setDoc 
} from "https://www.gstatic.com/firebasejs/10.8.0/firebase-firestore.js";

// Firebase Configuration from google-services.json
const firebaseConfig = {
  apiKey: "AIzaSyAOfp5nHZhRg4csd5O0pGDunTCNDq49jD0",
  authDomain: "parkeasy-20d83.firebaseapp.com",
  projectId: "parkeasy-20d83",
  storageBucket: "parkeasy-20d83.firebasestorage.app",
  messagingSenderId: "437275182001",
  appId: "1:437275182001:web:e1f69b3bfe57556d777390"
};

// Initialize Firebase & Firestore
const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

// Application State
let allSpaces = [];
let allBookings = [];
let activeCategoryFilter = "All";
let selectedCityFilter = "Bengaluru";
let currentSearchQuery = "";
let selectedSpotForBooking = null;

// Leaflet Map & Markers Storage
let map = null;
let mapMarkers = [];

// DOM Element References
const spotsListContainer = document.getElementById("spots-list-container");
const spotsCountEl = document.getElementById("spots-count");
const searchInput = document.getElementById("search-input");
const btnClearSearch = document.getElementById("btn-clear-search");
const citySelect = document.getElementById("city-select");
const btnNearMe = document.getElementById("btn-near-me");

// Modals
const modalListSpace = document.getElementById("modal-list-space");
const modalBooking = document.getElementById("modal-booking");
const modalPass = document.getElementById("modal-pass");

// Views & Tabs
const viewSeeker = document.getElementById("view-seeker");
const viewProvider = document.getElementById("view-provider");
const tabSeeker = document.getElementById("tab-seeker");
const tabProvider = document.getElementById("tab-provider");

// Initialize Map
function initMap() {
  const defaultLat = 12.9716;
  const defaultLng = 77.5946;

  map = L.map("leaflet-map").setView([defaultLat, defaultLng], 13);

  // Modern Dark CartoDB Tile Layer
  L.tileLayer("https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png", {
    attribution: '&copy; <a href="https://carto.com/">CARTO</a>',
    subdomains: 'abcd',
    maxZoom: 19
  }).addTo(map);
}

// Real-Time Firebase Firestore Listeners
function setupRealtimeListeners() {
  // 1. Stream Parking Spaces Collection
  onSnapshot(collection(db, "parking_spaces"), (snapshot) => {
    allSpaces = [];
    snapshot.forEach((doc) => {
      const data = doc.data();
      const id = data.id || doc.id;
      allSpaces.push({
        id: id,
        docId: doc.id,
        title: data.title || "Parking Spot",
        address: data.address || "",
        area: data.area || "Indiranagar",
        city: data.city || "Bengaluru",
        hourlyPrice: data.hourlyPrice || 40,
        rating: data.rating || 4.8,
        reviewsCount: data.reviewsCount || 12,
        isCovered: data.isCovered ?? true,
        hasCctv: data.hasCctv ?? true,
        hasSecurityGuard: data.hasSecurityGuard ?? false,
        hasEvCharging: data.hasEvCharging ?? false,
        has24x7Access: data.has24x7Access ?? true,
        latitude: data.latitude || 12.9716,
        longitude: data.longitude || 77.5946,
        parkingPhoto: data.parkingPhoto || "https://images.unsplash.com/photo-1590674899484-d5640e854abe?w=400&q=80",
        verificationStatus: data.verificationStatus || "Verified",
        status: data.status || "Active"
      });
    });

    console.log(`🔥 Real-Time Firestore Sync: ${allSpaces.length} parking spaces received!`);
    renderSpots();
    renderMapMarkers();
    updateHostDashboard();
  }, (error) => {
    console.error("Firestore snapshot error:", error);
  });

  // 2. Stream Bookings Collection
  onSnapshot(collection(db, "bookings"), (snapshot) => {
    allBookings = [];
    snapshot.forEach((doc) => {
      allBookings.push({ docId: doc.id, ...doc.data() });
    });
    console.log(`🔥 Real-Time Firestore Sync: ${allBookings.length} bookings received!`);
    updateHostDashboard();
  });
}

// Filter Spaces
function getFilteredSpaces() {
  return allSpaces.filter(space => {
    // Search query overrides city filter if user searches directly
    if (currentSearchQuery.trim() !== "") {
      const q = currentSearchQuery.toLowerCase();
      const matchesSearch = space.title.toLowerCase().includes(q) ||
        space.area.toLowerCase().includes(q) ||
        space.city.toLowerCase().includes(q) ||
        space.address.toLowerCase().includes(q);
      if (!matchesSearch) return false;
    } else if (selectedCityFilter !== "All Cities") {
      if (space.city.toLowerCase() !== selectedCityFilter.toLowerCase() && space.area.toLowerCase() !== selectedCityFilter.toLowerCase()) {
        return false;
      }
    }

    // Category Filter
    if (activeCategoryFilter === "Covered" && !space.isCovered) return false;
    if (activeCategoryFilter === "EV Charging" && !space.hasEvCharging) return false;
    if (activeCategoryFilter === "Under ₹50" && space.hourlyPrice > 50) return false;
    if (activeCategoryFilter === "24/7" && !space.has24x7Access) return false;

    return true;
  });
}

// Smart Google Maps Destination Resolver
function getSmartMapsUrl(space) {
  if (!space) return "https://www.google.com/maps";
  const titleUpper = (space.title || "").toUpperCase() + " " + (space.address || "").toUpperCase() + " " + (space.area || "").toUpperCase();
  
  if (titleUpper.includes("SDGI") || titleUpper.includes("SUNDER DEEP")) {
    return "https://www.google.com/maps/dir/?api=1&destination=28.6738,77.4912";
  }
  if (titleUpper.includes("IMS")) {
    return "https://www.google.com/maps/dir/?api=1&destination=28.6472,77.4526";
  }
  
  const isDefaultCoords = !space.latitude || 
    (Math.abs(space.latitude - 12.9716) < 0.05 && (titleUpper.includes("GHAZIABAD") || titleUpper.includes("DELHI") || titleUpper.includes("NOIDA") || titleUpper.includes("NCR")));
    
  if (isDefaultCoords) {
    const searchQuery = encodeURIComponent(`${space.title} ${space.address || space.area || ''} ${space.city || ''}`.trim());
    return `https://www.google.com/maps/dir/?api=1&destination=${searchQuery}`;
  }
  
  return `https://www.google.com/maps/dir/?api=1&destination=${space.latitude},${space.longitude}`;
}

// Render Parking Spot Cards
function renderSpots() {
  const filtered = getFilteredSpaces();
  spotsCountEl.textContent = `${filtered.length} parking spaces available live`;

  if (filtered.length === 0) {
    spotsListContainer.innerHTML = `
      <div class="glass-card rounded-2xl p-8 text-center text-slate-400">
        <i class="fa-solid fa-magnifying-glass text-3xl mb-3 text-slate-500"></i>
        <h4 class="text-white font-bold text-base mb-1">No parking spaces found</h4>
        <p class="text-xs">Try selecting "All Cities" or clearing search query.</p>
      </div>
    `;
    return;
  }

  spotsListContainer.innerHTML = filtered.map(space => {
    const mapsUrl = getSmartMapsUrl(space);
    
    return `
      <div class="glass-card rounded-2xl p-4 transition hover:border-blue-500/40 hover:shadow-xl group" data-spot-id="${space.id}">
        <div class="flex gap-4">
          <!-- Thumbnail -->
          <div class="w-24 h-24 rounded-xl overflow-hidden bg-slate-800 shrink-0 relative">
            <img src="${space.parkingPhoto}" alt="${space.title}" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300">
            ${space.hasEvCharging ? '<span class="absolute top-1 left-1 px-1.5 py-0.5 rounded-md bg-emerald-500 text-white text-[10px] font-bold">EV</span>' : ''}
          </div>

          <!-- Content -->
          <div class="flex-1 flex flex-col justify-between">
            <div>
              <div class="flex items-center justify-between">
                <a href="${mapsUrl}" target="_blank" class="font-bold text-white text-sm hover:text-blue-400 transition" title="Click to view navigation on Google Maps">${space.title}</a>
                <span class="px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 text-[10px] font-bold border border-emerald-500/20">Verified</span>
              </div>
              <p class="text-xs text-slate-400 mt-0.5">${space.area}, ${space.city}</p>
            </div>

            <!-- Rating & Distance -->
            <div class="flex items-center gap-2 text-xs text-slate-300">
              <span class="text-amber-400 font-bold"><i class="fa-solid fa-star"></i> ${space.rating}</span>
              <span class="text-slate-600">•</span>
              <span class="text-slate-400">${space.isCovered ? 'Covered Roof' : 'Open Surface'}</span>
            </div>

            <!-- Price & Actions -->
            <div class="flex items-center justify-between mt-2 pt-2 border-t border-slate-800">
              <div>
                <span class="text-lg font-black text-white">₹${space.hourlyPrice}</span>
                <span class="text-xs text-slate-400">/hr</span>
              </div>

              <div class="flex items-center gap-2">
                <!-- Direct Google Maps Navigation -->
                <a href="${mapsUrl}" target="_blank" title="Navigate to ${space.title} in Google Maps" class="w-8 h-8 rounded-lg bg-slate-800 hover:bg-slate-700 text-blue-400 flex items-center justify-center transition border border-slate-700">
                  <i class="fa-solid fa-location-arrow text-xs"></i>
                </a>

                <!-- Reserve Pass -->
                <button class="btn-book-spot px-3 py-1.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold transition shadow-md shadow-blue-600/30" data-id="${space.id}">
                  Book
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    `;
  }).join("");

  // Attach Event Listeners to Book Buttons
  document.querySelectorAll(".btn-book-spot").forEach(btn => {
    btn.addEventListener("click", (e) => {
      const id = e.currentTarget.getAttribute("data-id");
      const spot = allSpaces.find(s => String(s.id) === String(id));
      if (spot) openBookingModal(spot);
    });
  });
}

// Render Leaflet Map Markers
function renderMapMarkers() {
  if (!map) return;

  // Clear existing markers
  mapMarkers.forEach(m => map.removeLayer(m));
  mapMarkers = [];

  const filtered = getFilteredSpaces();

  filtered.forEach(space => {
    const isDefaultCoords = !space.latitude || (Math.abs(space.latitude - 12.9716) < 0.001 && Math.abs(space.longitude - 77.5946) < 0.001);
    const searchQuery = encodeURIComponent(`${space.title} ${space.address || space.area || ''} ${space.city || ''}`.trim());
    const mapsUrl = isDefaultCoords 
      ? `https://www.google.com/maps/dir/?api=1&destination=${searchQuery}` 
      : `https://www.google.com/maps/dir/?api=1&destination=${space.latitude},${space.longitude}`;

    const pinHtml = `
      <div class="custom-map-pin">
        <span>₹${space.hourlyPrice}</span>
      </div>
    `;

    const icon = L.divIcon({
      html: pinHtml,
      className: '',
      iconSize: [60, 24],
      iconAnchor: [30, 12]
    });

    const marker = L.marker([space.latitude, space.longitude], { icon: icon }).addTo(map);
    
    // Popup
    const popupContent = `
      <div class="p-2 text-xs">
        <div class="font-bold text-white text-sm mb-1">${space.title}</div>
        <div class="text-slate-300 mb-2">${space.area}, ${space.city} • ₹${space.hourlyPrice}/hr</div>
        <a href="${mapsUrl}" target="_blank" class="inline-block px-3 py-1 bg-blue-600 text-white font-bold rounded-lg text-center w-full">
          🧭 Google Maps Directions
        </a>
      </div>
    `;
    marker.bindPopup(popupContent);
    mapMarkers.push(marker);
  });

  if (filtered.length > 0) {
    const group = L.featureGroup(mapMarkers);
    map.fitBounds(group.getBounds().pad(0.15));
  }
}

// Host Dashboard Update
function updateHostDashboard() {
  document.getElementById("host-listings-count").textContent = `${allSpaces.length} Listings`;
  document.getElementById("host-bookings-count").textContent = `${allBookings.length} Reservations`;

  const providerContainer = document.getElementById("provider-listings-list");
  if (allSpaces.length === 0) {
    providerContainer.innerHTML = `<p class="text-slate-400 text-sm">No parking slots listed yet.</p>`;
    return;
  }

  providerContainer.innerHTML = allSpaces.map(s => `
    <div class="flex items-center justify-between p-3 rounded-xl bg-slate-800/80 border border-slate-700 text-xs">
      <div>
        <div class="font-bold text-white">${s.title}</div>
        <div class="text-slate-400">${s.area}, ${s.city} • ₹${s.hourlyPrice}/hr</div>
      </div>
      <span class="px-2.5 py-1 rounded-full bg-emerald-500/10 text-emerald-400 font-bold border border-emerald-500/20">Active</span>
    </div>
  `).join("");
}

// Open Booking Modal
function openBookingModal(spot) {
  selectedSpotForBooking = spot;
  const mapsUrl = getSmartMapsUrl(spot);

  document.getElementById("bk-title").textContent = spot.title;
  document.getElementById("bk-address").textContent = `${spot.area}, ${spot.city}`;
  document.getElementById("bk-price").textContent = `₹${spot.hourlyPrice} / hour`;
  
  const btnDirections = document.getElementById("bk-directions-btn");
  if (btnDirections) {
    btnDirections.href = mapsUrl;
  }

  updateBookingTotal();
  modalBooking.classList.remove("hidden");
}

function updateBookingTotal() {
  if (!selectedSpotForBooking) return;
  const hours = parseInt(document.getElementById("bk-hours").value);
  const total = (selectedSpotForBooking.hourlyPrice * hours) + 10;
  document.getElementById("bk-total-amount").textContent = `₹${total}`;
}

// Publish New Space to Firebase Cloud Firestore
async function publishNewSpace(e) {
  e.preventDefault();
  
  const title = document.getElementById("space-title").value;
  const area = document.getElementById("space-area").value;
  const city = document.getElementById("space-city").value;
  const address = document.getElementById("space-address").value;
  const price = parseFloat(document.getElementById("space-price").value);
  const capacity = parseInt(document.getElementById("space-capacity").value);
  const type = document.getElementById("space-type").value;
  
  const isCovered = document.getElementById("chk-covered").checked;
  const hasCctv = document.getElementById("chk-cctv").checked;
  const hasGuard = document.getElementById("chk-guard").checked;
  const hasEv = document.getElementById("chk-ev").checked;

  const newId = Date.now();
  const docRef = doc(db, "parking_spaces", String(newId));

  const titleUpper = (title + " " + area + " " + city).toUpperCase();
  let defaultLat = 12.9716 + (Math.random() - 0.5) * 0.05;
  let defaultLng = 77.5946 + (Math.random() - 0.5) * 0.05;

  if (titleUpper.includes("SDGI") || titleUpper.includes("SUNDER DEEP")) {
    defaultLat = 28.6738;
    defaultLng = 77.4912;
  } else if (titleUpper.includes("IMS")) {
    defaultLat = 28.6472;
    defaultLng = 77.4526;
  }

  const spaceData = {
    id: newId,
    title: title,
    area: area,
    city: city,
    address: address,
    hourlyPrice: price,
    vehicleCapacity: capacity,
    parkingType: type,
    isCovered: isCovered,
    hasCctv: hasCctv,
    hasSecurityGuard: hasGuard,
    hasEvCharging: hasEv,
    has24x7Access: true,
    rating: 5.0,
    reviewsCount: 1,
    status: "Active",
    verificationStatus: "Verified",
    latitude: defaultLat,
    longitude: defaultLng,
    parkingPhoto: "https://images.unsplash.com/photo-1590674899484-d5640e854abe?w=400&q=80",
    createdAt: Date.now()
  };

  try {
    await setDoc(docRef, spaceData);
    alert(`🎉 Success! '${title}' published live to Cloud Database.`);
    modalListSpace.classList.add("hidden");
    document.getElementById("form-list-space").reset();
  } catch (err) {
    console.error("Failed publishing to Firestore:", err);
    alert("Error publishing listing: " + err.message);
  }
}

// Confirm Driver Booking
async function confirmBooking() {
  if (!selectedSpotForBooking) return;
  const regNo = document.getElementById("bk-reg-no").value;
  const hours = parseInt(document.getElementById("bk-hours").value);
  const upiApp = document.getElementById("bk-upi").value;
  const total = (selectedSpotForBooking.hourlyPrice * hours) + 10;
  const bookingCode = `PS-${Math.floor(10000 + Math.random() * 90000)}`;

  const bookingData = {
    id: Date.now(),
    bookingCode: bookingCode,
    parkingSpaceId: selectedSpotForBooking.id,
    parkingTitle: selectedSpotForBooking.title,
    parkingAddress: selectedSpotForBooking.address,
    parkingCity: selectedSpotForBooking.city,
    vehicleRegNumber: regNo,
    durationHours: hours,
    totalAmount: total,
    paymentMethod: upiApp,
    status: "Confirmed",
    createdAt: Date.now()
  };

  const passMapsUrl = getSmartMapsUrl(selectedSpotForBooking);

  try {
    await setDoc(doc(db, "bookings", bookingCode), bookingData);
    modalBooking.classList.add("hidden");
    
    // Show Digital Pass Modal
    document.getElementById("pass-code").textContent = `Pass #${bookingCode}`;
    document.getElementById("pass-spot-name").textContent = selectedSpotForBooking.title;
    document.getElementById("pass-time").textContent = `Valid Today • ${hours} Hours • ${upiApp}`;
    document.getElementById("pass-reg").textContent = `Vehicle: ${regNo.toUpperCase()}`;
    
    const passMapsBtn = document.getElementById("pass-maps-btn");
    if (passMapsBtn) {
      passMapsBtn.href = passMapsUrl;
    }

    // Generate QR
    const qrContainer = document.getElementById("qrcode-container");
    qrContainer.innerHTML = "";
    new QRCode(qrContainer, {
      text: `PARKSPACE:${bookingCode}`,
      width: 140,
      height: 140
    });

    modalPass.classList.remove("hidden");
  } catch (err) {
    console.error("Booking error:", err);
    alert("Booking failed: " + err.message);
  }
}

// Event Listeners Setup
function setupEventListeners() {
  // Search
  searchInput.addEventListener("input", (e) => {
    currentSearchQuery = e.target.value;
    btnClearSearch.classList.toggle("hidden", currentSearchQuery === "");
    renderSpots();
    renderMapMarkers();
  });

  btnClearSearch.addEventListener("click", () => {
    searchInput.value = "";
    currentSearchQuery = "";
    btnClearSearch.classList.add("hidden");
    renderSpots();
    renderMapMarkers();
  });

  // City Selector
  citySelect.addEventListener("change", (e) => {
    selectedCityFilter = e.target.value;
    renderSpots();
    renderMapMarkers();
  });

  // Category Filter Pills
  document.querySelectorAll(".filter-pill").forEach(pill => {
    pill.addEventListener("click", (e) => {
      document.querySelectorAll(".filter-pill").forEach(p => p.classList.remove("active", "bg-blue-600", "text-white"));
      e.target.classList.add("active", "bg-blue-600", "text-white");
      activeCategoryFilter = e.target.getAttribute("data-filter");
      renderSpots();
      renderMapMarkers();
    });
  });

  // Near Me GPS
  btnNearMe.addEventListener("click", () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((pos) => {
        const lat = pos.coords.latitude;
        const lng = pos.coords.longitude;
        map.setView([lat, lng], 14);
        L.circle([lat, lng], { radius: 1000, color: '#059669', fillColor: '#059669', fillOpacity: 0.15 }).addTo(map);
        alert(`📍 GPS Position Locked: ${lat.toFixed(4)}, ${lng.toFixed(4)}`);
      });
    }
  });

  // Modals Open / Close
  document.getElementById("btn-open-list-modal").addEventListener("click", () => modalListSpace.classList.remove("hidden"));
  document.getElementById("btn-dashboard-add").addEventListener("click", () => modalListSpace.classList.remove("hidden"));
  
  document.querySelectorAll(".btn-close-modal").forEach(b => {
    b.addEventListener("click", () => {
      modalListSpace.classList.add("hidden");
      modalBooking.classList.add("hidden");
      modalPass.classList.add("hidden");
    });
  });

  document.getElementById("form-list-space").addEventListener("submit", publishNewSpace);
  document.getElementById("bk-hours").addEventListener("change", updateBookingTotal);
  document.getElementById("btn-confirm-booking").addEventListener("click", confirmBooking);

  // Tabs
  tabSeeker.addEventListener("click", () => {
    viewSeeker.classList.remove("hidden");
    viewSeeker.classList.add("grid");
    viewProvider.classList.add("hidden");
    tabSeeker.classList.add("bg-blue-600", "text-white");
    tabProvider.classList.remove("bg-blue-600", "text-white");
  });

  tabProvider.addEventListener("click", () => {
    viewProvider.classList.remove("hidden");
    viewProvider.classList.add("flex");
    viewSeeker.classList.add("hidden");
    viewSeeker.classList.remove("grid");
    tabProvider.classList.add("bg-blue-600", "text-white");
    tabSeeker.classList.remove("bg-blue-600", "text-white");
  });
}

// App Initialization
window.addEventListener("DOMContentLoaded", () => {
  initMap();
  setupRealtimeListeners();
  setupEventListeners();
});
