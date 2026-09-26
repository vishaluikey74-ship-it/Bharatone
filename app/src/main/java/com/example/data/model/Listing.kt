package com.example.data.model

enum class ListingDomain {
    ROOM_RENTAL,
    MARKETPLACE
}

enum class PropertyCategory(val displayName: String, val hindiName: String) {
    STUDENT_ROOM("Student Room", "छात्र कमरा"),
    PG("PG / Paying Guest", "पेइंग गेस्ट (PG)"),
    HOSTEL("Hostel", "हॉस्टल"),
    FLAT_1BHK("1 BHK Flat", "1 बीएचके फ्लैट"),
    FLAT_2BHK("2 BHK Flat", "2 बीएचके फ्लैट"),
    FLAT_3BHK("3 BHK Flat", "3 बीएचके फ्लैट"),
    HOUSE("Independent House", "मकान / विला"),
    OFFICE("Office Space", "कार्यालय / ऑफिस"),
    SHOP("Shop / Retail", "दुकान / शोरूम"),
    GODOWN("Godown / Warehouse", "गोदाम / वेयरहाउस"),
    COMMERCIAL("Commercial Property", "कमर्शियल प्रॉपर्टी")
}

enum class MarketCategory(val displayName: String, val hindiName: String, val iconName: String) {
    CARS("Cars", "कारें", "DirectionsCar"),
    BIKES("Bikes & Scooters", "बाइक और स्कूटर", "TwoWheeler"),
    MOBILES("Mobile Phones", "मोबाइल फोन", "Smartphone"),
    ELECTRONICS("Laptops & Electronics", "इलेक्ट्रॉनिक्स", "Laptop"),
    FURNITURE("Furniture & Decor", "फर्नीचर", "Chair"),
    APPLIANCES("Home Appliances", "घरेलू उपकरण", "Tv"),
    AGRICULTURE("Tractor & Agri Equipment", "कृषि उपकरण व ट्रैक्टर", "Agriculture"),
    BOOKS("Books & Education", "किताबें व पढ़ाई", "MenuBook"),
    BUSINESS("Business Tools & Machinery", "व्यापारिक मशीनें", "Build"),
    OTHER("Other Items", "अन्य सामान", "Category")
}

data class Listing(
    val id: String,
    val domain: ListingDomain,
    val title: String,
    val description: String,
    val price: Double, // Monthly rent or selling price
    val securityDeposit: Double = 0.0,
    val negotiable: Boolean = true,
    val condition: String = "Used", // "New", "Used", "Like New"
    val propertyCategory: PropertyCategory? = null,
    val marketCategory: MarketCategory? = null,
    val isFurnished: String = "Semi-Furnished", // "Furnished", "Unfurnished", "Semi-Furnished"
    val tenantPreference: String = "Anyone", // "Boys", "Girls", "Family", "Students", "Anyone"
    val ownerOrBroker: String = "Owner", // "Owner", "Broker", "Dealer"
    val availableFrom: String = "Immediate",
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val state: String = "Madhya Pradesh",
    val district: String = "Balaghat",
    val city: String = "Balaghat City",
    val area: String = "Paraswada",
    val address: String = "",
    val nearbyLandmark: String = "Near Main Market / Bus Stand",
    val electricityWater: String = "24/7 Water, Separate Submeter",
    val sellerId: String,
    val sellerName: String,
    val sellerPhone: String,
    val sellerAvatar: String = "",
    val sellerBadge: VerificationBadge? = null,
    val isPromoted: Boolean = false,
    val isSold: Boolean = false,
    val amenities: List<String> = listOf("Electricity", "Water", "Wifi", "Parking"),
    val imageUrls: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val viewsCount: Int = 0,
    val favoritesCount: Int = 0
)
