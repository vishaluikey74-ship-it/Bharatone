package com.example.data.model

data class IndiaLocation(
    val state: String,
    val districts: Map<String, List<String>> // District name -> List of Areas/Cities
)

object IndiaLocationsData {
    val allLocations: List<IndiaLocation> = listOf(
        IndiaLocation(
            state = "Madhya Pradesh",
            districts = mapOf(
                "Balaghat" to listOf("Paraswada", "Lalburra", "Baihar", "Katangi", "Waraseoni", "Balaghat City", "Bharveli", "Lanji"),
                "Bhopal" to listOf("MP Nagar", "Arera Colony", "Kolar Road", "Hoshangabad Road", "New Market", "Govindpura", "Bairagarh"),
                "Indore" to listOf("Vijay Nagar", "Palasia", "Bhanwarkua", "Rajwada", "Rau", "Super Corridor", "Geeta Bhawan"),
                "Jabalpur" to listOf("Civil Lines", "Wright Town", "Adhartal", "Gorakhpur", "Vijay Nagar"),
                "Gwalior" to listOf("Lashkar", "Morar", "City Center", "Thatipur"),
                "Chhindwara" to listOf("Parasia", "Chhindwara City", "Sausar", "Amarwara"),
                "Seoni" to listOf("Seoni City", "Barghat", "Lakhnadon", "Keolari")
            )
        ),
        IndiaLocation(
            state = "Delhi NCR",
            districts = mapOf(
                "Central Delhi" to listOf("Connaught Place", "Karol Bagh", "Paharganj", "Rajinder Nagar"),
                "South Delhi" to listOf("Hauz Khas", "Saket", "Lajpat Nagar", "Greater Kailash", "Malviya Nagar"),
                "North Delhi" to listOf("Civil Lines", "Model Town", "Mukherjee Nagar", "Kamla Nagar"),
                "East Delhi" to listOf("Laxmi Nagar", "Mayur Vihar", "Preet Vihar", "Shahdara"),
                "West Delhi" to listOf("Janakpuri", "Rajouri Garden", "Punjabi Bagh", "Dwarka"),
                "Noida & Gr. Noida" to listOf("Sector 62", "Sector 18", "Sector 137", "Knowledge Park"),
                "Gurugram" to listOf("Cyber City", "Sector 29", "Golf Course Road", "Sohna Road")
            )
        ),
        IndiaLocation(
            state = "Uttar Pradesh",
            districts = mapOf(
                "Lucknow" to listOf("Hazratganj", "Gomti Nagar", "Alambagh", "Indira Nagar", "Charbagh"),
                "Varanasi" to listOf("Assi Ghat", "Godowlia", "Lanka", "Cantonment", "Bhelupur"),
                "Kanpur" to listOf("Civil Lines", "Kakadeo", "Swaroop Nagar", "Kidwai Nagar"),
                "Prayagraj" to listOf("Civil Lines", "Katra", "Allahpur", "George Town"),
                "Agra" to listOf("Tajganj", "Sanjay Place", "Kamla Nagar")
            )
        ),
        IndiaLocation(
            state = "Maharashtra",
            districts = mapOf(
                "Mumbai" to listOf("Andheri West", "Bandra", "Dadar", "Powai", "Juhu", "Borivali", "Goregaon"),
                "Pune" to listOf("Kothrud", "Hinjawadi", "Viman Nagar", "Koregaon Park", "Baner", "Wakad"),
                "Nagpur" to listOf("Dharampeth", "Sitabuldi", "Manish Nagar", "Civil Lines"),
                "Thane" to listOf("Majiwada", "Ghubunder Road", "Naupada", "Kalyan")
            )
        ),
        IndiaLocation(
            state = "Bihar",
            districts = mapOf(
                "Patna" to listOf("Boring Road", "Kankarbagh", "Bailey Road", "Fraser Road", "Patliputra"),
                "Gaya" to listOf("Civil Lines", "AP Colony", "Rampur"),
                "Muzaffarpur" to listOf("Mithanpura", "Brahmpura", "Maripur")
            )
        ),
        IndiaLocation(
            state = "Rajasthan",
            districts = mapOf(
                "Jaipur" to listOf("Malviya Nagar", "Vaishali Nagar", "Mansarovar", "C-Scheme", "Raja Park"),
                "Jodhpur" to listOf("Shastri Nagar", "Ratanada", "Sardarpura"),
                "Udaipur" to listOf("Hiran Magri", "Panchwati", "Fatehpura")
            )
        ),
        IndiaLocation(
            state = "Karnataka",
            districts = mapOf(
                "Bengaluru" to listOf("Koramangala", "Indiranagar", "HSR Layout", "Whitefield", "BTM Layout", "Jayanagar", "Hebbal")
            )
        ),
        IndiaLocation(
            state = "Gujarat",
            districts = mapOf(
                "Ahmedabad" to listOf("Navrangpura", "Satellite", "Bodakdev", "Maninagar", "SG Highway"),
                "Surat" to listOf("Adajan", "Vesu", "Piplod", "Varachha")
            )
        )
    )

    fun getStates(): List<String> = allLocations.map { it.state }

    fun getDistricts(state: String): List<String> =
        allLocations.find { it.state.equals(state, ignoreCase = true) }?.districts?.keys?.toList() ?: emptyList()

    fun getAreas(state: String, district: String): List<String> =
        allLocations.find { it.state.equals(state, ignoreCase = true) }?.districts?.get(district) ?: emptyList()
}
