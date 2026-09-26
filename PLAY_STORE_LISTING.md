# BharatOne — Google Play Store Listing & Testing Submission Guide

Use this document to prepare your application for submission to the Google Play testing team and resolve any policy issues (such as the **Misleading Claims policy: Missing Source Link & Disclaimer**).

---

## 1. App Store Details (Copy & Paste Ready)

### App Name (Title)
*(Max 30 characters)*
```text
BharatOne: Rentals, Jobs, News
```

---

### Short Description
*(Max 80 characters)*
```text
Rentals, marketplace, verified job alerts & local community updates in India.
```

---

### Full Description (en-US)
*(Copy and paste this exact text into the Google Play Console **Main store listing -> Full description** field)*

```text
⚠️ IMPORTANT DISCLAIMER: NON-GOVERNMENT ENTITY
BharatOne is an independent, privately developed mobile application. BharatOne DOES NOT represent, affiliate with, hold authorization from, or claim endorsement from the Government of India, any State Government, or any government agency, department, or ministry.

All government job recruitment notices, exam alerts, and public scheme updates displayed in this app are aggregated strictly for public informational convenience from authentic, publicly accessible official government portals (.gov.in / .nic.in). BharatOne is not a recruitment agency and does not issue admit cards or offer government employment. Users must verify all notifications and submit applications exclusively on the respective official government portal.

🏛️ OFFICIAL SOURCES OF GOVERNMENT INFORMATION:
All government recruitment and public circulars featured in this app are sourced directly from the following official government websites:
• Union Public Service Commission (UPSC): https://upsc.gov.in
• Staff Selection Commission (SSC): https://ssc.gov.in
• National Career Service (Ministry of Labour & Employment): https://www.ncs.gov.in
• National Portal of India: https://www.india.gov.in
• Madhya Pradesh Public Service Commission (MPPSC): https://mppsc.mp.gov.in
• MP Online Portal (Citizen & Recruitment Services): https://mponline.gov.in
• MP Employees Selection Board (ESB): https://esb.mp.gov.in
• National Health Mission MP (NHM): https://nhmmp.gov.in
• District Administration Balaghat (NIC): https://balaghat.nic.in
• MOIL Limited (Govt of India Enterprise): https://moil.nic.in

━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Welcome to BharatOne — India's Unified Hyperlocal Community & Services Platform!

BharatOne connects citizens across Indian cities and towns to find rental housing, buy and sell secondhand goods, explore verified employment opportunities, and stay informed with regional news updates in Hindi and English.

🌟 KEY FEATURES:

🏠 1. ROOM & PROPERTY RENTALS
• Find 1RK, 1BHK, 2BHK flats, independent houses, student hostels, and PG accommodations.
• Connect directly with verified property owners with zero brokerage.
• Filter by rent budget, location, amenities, and family/bachelor friendly status.

🛒 2. BUY & SELL CLASSIFIEDS MARKETPLACE
• Buy and sell pre-owned smartphones, bikes, scooters, furniture, electronics, and home appliances.
• Chat or call local sellers safely in your own district.
• Post your free listing with photos and price within minutes.

💼 3. JOBS & CAREER OPPORTUNITIES
• Private Sector Vacancies: Local shop assistants, delivery executives, sales managers, accountants, and office staff.
• Public Information Aggregator: Timely notifications of public recruitment exams with direct links to official government portals (.gov.in / .nic.in).
• Scam Protection: Mandatory salary disclosures, zero-fee warnings, and official source badges.

📰 4. LOCAL BREAKING NEWS & BULLETINS
• Fast, reliable daily news alerts and district updates in Hindi and English.
• Categories: Local Governance, Weather Alerts, Mandi Bhav (Crop Prices), Sports, and Education.
• Interactive citizen feedback and community polling.

👥 5. SOCIAL COMMUNITY & CITIZEN FEED
• Share local stories, recommendations, festival celebrations, and community announcements.
• Connect with neighbors, follow trusted publishers, and build local networks.

🔒 PRIVACY & SAFETY COMMITMENT:
• No banking or sensitive identity documents collected.
• Complete user data control: Edit or remove your listings and delete your account anytime directly from your profile settings.
• Dedicated customer support and grievance redressal: vishaluikey74@gmail.com
```

---

## 2. Google Play Console Setup for Review & Testing Team

When submitting to Closed Testing or Internal Testing in the Play Console, configure the following settings:

### A. App Access (Instructions for Reviewers / Testing Team)
*In Play Console: Policy and programs > App content > App access*
- Choose: **"All or some functionality in my app is restricted"** (or "All functionality is available without special access").
- If selecting restricted access, add test credentials so testers don't need SMS:
  - **Instruction Name**: `Standard Reviewer Access`
  - **Username / Phone**: `9876543210`
  - **Password / OTP**: `123456`
  - **Explanation**: *"Reviewers can explore all features (Rentals, Marketplace, Jobs, News) immediately as a guest without signing in. If testing user profile or authentication, enter phone 9876543210 and OTP 123456."*

### B. Government Apps Declaration
*In Play Console: Policy and programs > App content > Government apps*
- **Select**: `No, this app does not represent a government entity or authority`.
- (This is critical to prevent automated rejection under the Government Apps impersonation policy).

### C. Financial Features
*In Play Console: Policy and programs > App content > Financial features*
- **Select**: `My app does not provide any financial features` (or does not provide personal loans).

### D. Target Audience and Content
- **Target Age**: 18 and over (or 13+).
- **Appeal to Children**: No.

### E. Data Safety Form
- **Data collected**:
  - Name, Phone Number (for optional contact on marketplace/rental listings)
  - User-generated content (photos of items/rooms for listings, user posts)
- **Data sharing**: No user data is shared with 3rd-party advertisers or data brokers.
- **Security**: Data is transferred over secure HTTPS connections.
- **Data Deletion**: Users can request account and data deletion in-app via **Profile > Delete Account**.

### F. Privacy Policy URL
Enter your public privacy policy link in Play Console (*Policy and programs > App content > Privacy policy*).
You can point to:
`https://ais-pre-mzywcxfabmv2q44o6cchuv-499197419343.asia-southeast1.run.app` (or your GitHub privacy policy page).

---

## 3. How to Release the New Build

1. In AI Studio, open the **Settings** menu at top-right.
2. Select **Generate AAB** (Android App Bundle).
   - Current Version Code: **6**
   - Current Version Name: **6.0**
   - Application ID: `com.aistudio.bharatone.app`
3. In your **Google Play Console**:
   - Go to your app > **Testing > Closed testing** (or Internal testing).
   - Click **Create new release**.
   - Upload the newly generated `.aab` file.
   - Update the **Release notes** (e.g., *"Added prominent non-government entity disclaimer, verified .gov.in official source links, and enhanced privacy policies"*).
   - Ensure the **Full description** on the Main Store Listing is updated with Section 1 above.
   - Click **Review release** and **Start rollout to testing**!
