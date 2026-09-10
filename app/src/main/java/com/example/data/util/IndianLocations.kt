package com.example.data.util

data class StateData(
    val name: String,
    val code: String,
    val cities: List<String>
)

object IndianLocations {

    val states = listOf(
        StateData(
            name = "Uttar Pradesh",
            code = "UP",
            cities = listOf(
                "Noida",
                "Ghaziabad",
                "Greater Noida",
                "Lucknow",
                "Kanpur",
                "Agra",
                "Varanasi",
                "Prayagraj",
                "Meerut",
                "Aligarh",
                "Bareilly",
                "Moradabad",
                "Gorakhpur",
                "Jhansi",
                "Mathura",
                "Ayodhya",
                "Muzaffarnagar",
                "Firozabad",
                "Saharanpur",
                "Noida Extension"
            )
        ),
        StateData(
            name = "Karnataka",
            code = "KA",
            cities = listOf(
                "Bengaluru",
                "Mysuru",
                "Mangaluru",
                "Hubballi-Dharwad",
                "Belagavi",
                "Kalaburagi",
                "Davanagere",
                "Ballari",
                "Tumakuru",
                "Shivamogga",
                "Udupi",
                "Hassan",
                "Bidar",
                "Hosapete"
            )
        ),
        StateData(
            name = "Delhi (NCT)",
            code = "DL",
            cities = listOf(
                "New Delhi",
                "Central Delhi",
                "North Delhi",
                "South Delhi",
                "East Delhi",
                "West Delhi",
                "Dwarka",
                "Rohini",
                "Connaught Place",
                "Saket",
                "Vasant Kunj",
                "Janakpuri",
                "Lajpat Nagar",
                "Karol Bagh"
            )
        ),
        StateData(
            name = "Maharashtra",
            code = "MH",
            cities = listOf(
                "Mumbai",
                "Pune",
                "Nagpur",
                "Thane",
                "Nashik",
                "Chhatrapati Sambhajinagar",
                "Solapur",
                "Navi Mumbai",
                "Kalyan-Dombivli",
                "Amravati",
                "Kolhapur",
                "Pimpri-Chinchwad",
                "Akola",
                "Latur"
            )
        ),
        StateData(
            name = "Haryana",
            code = "HR",
            cities = listOf(
                "Gurugram",
                "Faridabad",
                "Panipat",
                "Ambala",
                "Yamunanagar",
                "Rohtak",
                "Hisar",
                "Karnal",
                "Sonipat",
                "Panchkula",
                "Bahadurgarh",
                "Rewari"
            )
        ),
        StateData(
            name = "Tamil Nadu",
            code = "TN",
            cities = listOf(
                "Chennai",
                "Coimbatore",
                "Madurai",
                "Tiruchirappalli",
                "Salem",
                "Tiruppur",
                "Erode",
                "Vellore",
                "Thoothukudi",
                "Tirunelveli",
                "Dindigul",
                "Thanjavur"
            )
        ),
        StateData(
            name = "Telangana",
            code = "TG",
            cities = listOf(
                "Hyderabad",
                "Warangal",
                "Nizamabad",
                "Karimnagar",
                "Khammam",
                "Ramagundam",
                "Mahbubnagar",
                "Secunderabad",
                "Nalgonda"
            )
        ),
        StateData(
            name = "Gujarat",
            code = "GJ",
            cities = listOf(
                "Ahmedabad",
                "Surat",
                "Vadodara",
                "Rajkot",
                "Bhavnagar",
                "Jamnagar",
                "Junagadh",
                "Gandhinagar",
                "Anand",
                "Vapi",
                "Navsari",
                "Bharuch"
            )
        ),
        StateData(
            name = "Rajasthan",
            code = "RJ",
            cities = listOf(
                "Jaipur",
                "Jodhpur",
                "Kota",
                "Bikaner",
                "Ajmer",
                "Udaipur",
                "Bhilwara",
                "Alwar",
                "Sikar",
                "Sri Ganganagar",
                "Bharatpur"
            )
        ),
        StateData(
            name = "West Bengal",
            code = "WB",
            cities = listOf(
                "Kolkata",
                "Howrah",
                "Durgapur",
                "Asansol",
                "Siliguri",
                "Bardhaman",
                "Malda",
                "Kharagpur",
                "Baharampur",
                "Haldia"
            )
        ),
        StateData(
            name = "Kerala",
            code = "KL",
            cities = listOf(
                "Thiruvananthapuram",
                "Kochi",
                "Kozhikode",
                "Thrissur",
                "Kollam",
                "Palakkad",
                "Kannur",
                "Alappuzha",
                "Kottayam",
                "Malappuram"
            )
        ),
        StateData(
            name = "Punjab",
            code = "PB",
            cities = listOf(
                "Ludhiana",
                "Amritsar",
                "Jalandhar",
                "Patiala",
                "Bathinda",
                "Mohali",
                "Pathankot",
                "Hoshiarpur",
                "Phagwara"
            )
        ),
        StateData(
            name = "Madhya Pradesh",
            code = "MP",
            cities = listOf(
                "Bhopal",
                "Indore",
                "Jabalpur",
                "Gwalior",
                "Ujjain",
                "Sagar",
                "Dewas",
                "Satna",
                "Ratlam",
                "Rewa",
                "Singrauli"
            )
        ),
        StateData(
            name = "Bihar",
            code = "BR",
            cities = listOf(
                "Patna",
                "Gaya",
                "Bhagalpur",
                "Muzaffarpur",
                "Purnia",
                "Darbhanga",
                "Bihar Sharif",
                "Arrah",
                "Begusarai",
                "Katihar"
            )
        ),
        StateData(
            name = "Andhra Pradesh",
            code = "AP",
            cities = listOf(
                "Visakhapatnam",
                "Vijayawada",
                "Guntur",
                "Nellore",
                "Kurnool",
                "Kakinada",
                "Tirupati",
                "Rajamahendravaram",
                "Kadapa",
                "Anantapur"
            )
        ),
        StateData(
            name = "Odisha",
            code = "OD",
            cities = listOf(
                "Bhubaneswar",
                "Cuttack",
                "Rourkela",
                "Berhampur",
                "Sambalpur",
                "Puri",
                "Balasore",
                "Bhadrak"
            )
        ),
        StateData(
            name = "Assam",
            code = "AS",
            cities = listOf(
                "Guwahati",
                "Silchar",
                "Dibrugarh",
                "Jorhat",
                "Nagaon",
                "Tinsukia",
                "Tezpur"
            )
        ),
        StateData(
            name = "Uttarakhand",
            code = "UK",
            cities = listOf(
                "Dehradun",
                "Haridwar",
                "Roorkee",
                "Haldwani",
                "Rudrapur",
                "Rishikesh",
                "Kashipur"
            )
        ),
        StateData(
            name = "Goa",
            code = "GA",
            cities = listOf(
                "Panaji",
                "Margao",
                "Vasco da Gama",
                "Mapusa",
                "Ponda"
            )
        ),
        StateData(
            name = "Chandigarh",
            code = "CH",
            cities = listOf(
                "Chandigarh"
            )
        ),
        StateData(
            name = "Jharkhand",
            code = "JH",
            cities = listOf(
                "Ranchi",
                "Jamshedpur",
                "Dhanbad",
                "Bokaro",
                "Deoghar",
                "Hazaribagh"
            )
        ),
        StateData(
            name = "Himachal Pradesh",
            code = "HP",
            cities = listOf(
                "Shimla",
                "Dharamshala",
                "Solan",
                "Mandi",
                "Kullu",
                "Manali"
            )
        ),
        StateData(
            name = "Jammu and Kashmir",
            code = "JK",
            cities = listOf(
                "Srinagar",
                "Jammu",
                "Anantnag",
                "Baramulla",
                "Udhampur"
            )
        )
    )

    fun getAllStates(): List<String> {
        return states.map { it.name }
    }

    fun getCitiesForState(stateName: String): List<String> {
        val foundState = states.find { it.name.equals(stateName, ignoreCase = true) }
        return foundState?.cities ?: emptyList()
    }

    fun findStateForCity(cityName: String): String? {
        if (cityName.isBlank()) return null
        return states.find { state ->
            state.cities.any { it.equals(cityName, ignoreCase = true) }
        }?.name
    }

    fun isValidCityForState(stateName: String, cityName: String): Boolean {
        if (stateName.isBlank() || cityName.isBlank()) return true
        val cities = getCitiesForState(stateName)
        if (cities.isEmpty()) return true
        return cities.any { it.equals(cityName, ignoreCase = true) }
    }
}
