package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AllInterests
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.VerificationBadge
import com.example.ui.theme.*

enum class AuthMode {
    LOGIN,
    REGISTER,
    OTP_VERIFY,
    EMAIL_VERIFY,
    FORGOT_PASSWORD,
    CHANGE_PASSWORD,
    PRIVACY_SETTINGS,
    DELETE_ACCOUNT
}

@Composable
fun AuthDialog(
    initialMode: AuthMode = AuthMode.LOGIN,
    currentUser: User,
    language: String,
    onDismiss: () -> Unit,
    onLoginSuccess: (User) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val isHindi = language == "hi"
    var currentMode by remember { mutableStateOf(initialMode) }

    // Form states
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Register fields
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var state by remember { mutableStateOf(currentUser.state) }
    var district by remember { mutableStateOf(currentUser.district) }
    var city by remember { mutableStateOf(currentUser.city) }
    var area by remember { mutableStateOf(currentUser.area) }
    var selectedInterests by remember { mutableStateOf(currentUser.interests.toSet()) }

    // Verification states
    var otpCode by remember { mutableStateOf("") }
    var emailVerificationSent by remember { mutableStateOf(false) }
    var privacyMode by remember { mutableStateOf("PUBLIC") } // PUBLIC, FRIENDS, PRIVATE
    var statusMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SaffronPrimary.copy(alpha = 0.08f))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SaffronPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (currentMode) {
                                    AuthMode.LOGIN -> Icons.Default.Lock
                                    AuthMode.REGISTER -> Icons.Default.PersonAdd
                                    AuthMode.OTP_VERIFY -> Icons.Default.Sms
                                    AuthMode.EMAIL_VERIFY -> Icons.Default.MarkEmailRead
                                    AuthMode.FORGOT_PASSWORD -> Icons.Default.Key
                                    AuthMode.CHANGE_PASSWORD -> Icons.Default.Password
                                    AuthMode.PRIVACY_SETTINGS -> Icons.Default.Security
                                    AuthMode.DELETE_ACCOUNT -> Icons.Default.DeleteForever
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = when (currentMode) {
                                AuthMode.LOGIN -> if (isHindi) "🔐 खाता लॉगिन (Sign In)" else "🔐 Sign In to BharatOne"
                                AuthMode.REGISTER -> if (isHindi) "📝 नया खाता बनाएं (Sign Up)" else "📝 Create New Account"
                                AuthMode.OTP_VERIFY -> if (isHindi) "📱 OTP सत्यापन" else "📱 Mobile OTP Verification"
                                AuthMode.EMAIL_VERIFY -> if (isHindi) "✉️ ईमेल सत्यापन" else "✉️ Email Verification"
                                AuthMode.FORGOT_PASSWORD -> if (isHindi) "🔑 पासवर्ड भूल गए?" else "🔑 Reset Password"
                                AuthMode.CHANGE_PASSWORD -> if (isHindi) "🔒 पासवर्ड बदलें" else "🔒 Change Password"
                                AuthMode.PRIVACY_SETTINGS -> if (isHindi) "🛡️ गोपनीयता सेटिंग्स" else "🛡️ Privacy Settings"
                                AuthMode.DELETE_ACCOUNT -> if (isHindi) "⚠️ खाता हटाएं" else "⚠️ Delete Account"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(color = SlateBorder)

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (statusMessage.isNotEmpty()) {
                        item {
                            Surface(
                                color = IndiaGreen.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, IndiaGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = IndiaGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = statusMessage, fontSize = 12.sp, color = IndiaGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    when (currentMode) {
                        AuthMode.LOGIN -> {
                            item {
                                Text(
                                    text = if (isHindi) "अपने मोबाइल नंबर या ईमेल से लॉगिन करें" else "Sign in with your verified email or mobile number",
                                    fontSize = 12.5.sp,
                                    color = SlateTextSecondary
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = emailOrPhone,
                                    onValueChange = { emailOrPhone = it },
                                    label = { Text(if (isHindi) "ईमेल या मोबाइल नंबर" else "Email or Mobile Number") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SaffronPrimary) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_email_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text(if (isHindi) "पासवर्ड" else "Password") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SaffronPrimary) },
                                    trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(
                                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_password_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = { currentMode = AuthMode.OTP_VERIFY }) {
                                        Text(
                                            text = if (isHindi) "📱 OTP से लॉगिन करें" else "Login via Mobile OTP",
                                            fontSize = 12.sp,
                                            color = SaffronPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    TextButton(onClick = { currentMode = AuthMode.FORGOT_PASSWORD }) {
                                        Text(
                                            text = if (isHindi) "पासवर्ड भूल गए?" else "Forgot Password?",
                                            fontSize = 12.sp,
                                            color = SlateTextSecondary
                                        )
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        val loggedIn = currentUser.copy(
                                            fullName = if (emailOrPhone.contains("@")) emailOrPhone.substringBefore("@").replace(".", " ").capitalize() else currentUser.fullName,
                                            email = if (emailOrPhone.contains("@")) emailOrPhone else currentUser.email,
                                            phone = if (!emailOrPhone.contains("@") && emailOrPhone.isNotEmpty()) emailOrPhone else currentUser.phone
                                        )
                                        onLoginSuccess(loggedIn)
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("auth_login_submit_btn")
                                ) {
                                    Text(
                                        text = if (isHindi) "लॉगिन करें (Sign In)" else "Sign In",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Divider(modifier = Modifier.weight(1f))
                                    Text(
                                        text = if (isHindi) " या " else " OR ",
                                        fontSize = 11.sp,
                                        color = SlateTextMuted,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    Divider(modifier = Modifier.weight(1f))
                                }
                            }

                            // Google Login Simulation
                            item {
                                OutlinedButton(
                                    onClick = {
                                        val googleUser = currentUser.copy(
                                            fullName = if (currentUser.fullName != "Guest User" && currentUser.fullName.isNotBlank()) currentUser.fullName else "BharatOne User",
                                            email = if (currentUser.email.isNotBlank()) currentUser.email else "user@bharatone.app",
                                            role = UserRole.USER,
                                            verificationBadges = emptySet(),
                                            isLoggedIn = true
                                        )
                                        onLoginSuccess(googleUser)
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Text(text = "🌐 Sign in with Google Account", fontWeight = FontWeight.Bold)
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "नया खाता बनाना चाहते हैं?" else "Don't have an account?",
                                        fontSize = 13.sp,
                                        color = SlateTextSecondary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHindi) "रजिस्टर करें →" else "Sign Up →",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronPrimary,
                                        modifier = Modifier.clickable { currentMode = AuthMode.REGISTER }
                                    )
                                }
                            }
                        }

                        AuthMode.REGISTER -> {
                            item {
                                Text(
                                    text = if (isHindi) "भारत-व्यापी खाता पंजीकरण" else "All-India User Account Registration",
                                    fontSize = 12.5.sp,
                                    color = SlateTextSecondary
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    label = { Text(if (isHindi) "पूरा नाम (Full Name) *" else "Full Name *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text(if (isHindi) "यूज़रनेम (Username) *" else "Username *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text(if (isHindi) "ईमेल आईडी *" else "Email Address *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = mobile,
                                    onValueChange = { mobile = it },
                                    label = { Text(if (isHindi) "मोबाइल नंबर (+91) *" else "Mobile Number (+91) *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text(if (isHindi) "सुरक्षित पासवर्ड बनाएं *" else "Create Password *") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                Text(
                                    text = if (isHindi) "स्थान चुनें (Location):" else "Your Location Hierarchy:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = state,
                                        onValueChange = { state = it },
                                        label = { Text("State") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    OutlinedTextField(
                                        value = district,
                                        onValueChange = { district = it },
                                        label = { Text("District") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = city,
                                        onValueChange = { city = it },
                                        label = { Text("City/Tehsil") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    OutlinedTextField(
                                        value = area,
                                        onValueChange = { area = it },
                                        label = { Text("Area / Locality") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }

                            item {
                                Text(
                                    text = if (isHindi) "रुचियां चुनें (Personalization Interests):" else "Select Interests:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    AllInterests.list.forEach { interest ->
                                        val isSel = selectedInterests.contains(interest)
                                        FilterChip(
                                            selected = isSel,
                                            onClick = {
                                                selectedInterests = if (isSel) selectedInterests - interest else selectedInterests + interest
                                            },
                                            label = { Text(interest, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = SaffronPrimary,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        val newUser = User(
                                            id = "user_${System.currentTimeMillis()}",
                                            fullName = if (fullName.isNotEmpty()) fullName else "New User",
                                            username = if (username.isNotEmpty()) username else "user_${System.currentTimeMillis().toString().takeLast(4)}",
                                            email = email,
                                            phone = mobile,
                                            state = state,
                                            district = district,
                                            city = city,
                                            area = area,
                                            interests = selectedInterests.toList(),
                                            verificationBadges = setOf(VerificationBadge.VERIFIED_USER)
                                        )
                                        currentMode = AuthMode.EMAIL_VERIFY
                                        statusMessage = if (isHindi) "सत्यापन लिंक आपके ईमेल पर भेज दिया गया है।" else "Verification email link sent!"
                                        onLoginSuccess(newUser)
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) "खाता बनाएं और सत्यापित करें →" else "Register & Verify Account →",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = if (isHindi) "पहले से खाता है? लॉगिन करें" else "Already have an account? Sign In",
                                        color = SaffronPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.clickable { currentMode = AuthMode.LOGIN }
                                    )
                                }
                            }
                        }

                        AuthMode.OTP_VERIFY -> {
                            item {
                                Text(
                                    text = if (isHindi) "अपने 10 अंकों के मोबाइल नंबर पर 4-अंकीय OTP प्राप्त करें" else "Enter your 10-digit mobile number to receive instant SMS OTP",
                                    fontSize = 12.sp,
                                    color = SlateTextSecondary
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = mobile,
                                    onValueChange = { mobile = it },
                                    label = { Text(if (isHindi) "मोबाइल नंबर" else "Mobile Number") },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SaffronPrimary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = { if (it.length <= 6) otpCode = it },
                                    label = { Text(if (isHindi) "4-अंकीय OTP दर्ज करें (उदा. 7421)" else "Enter 4-digit OTP (e.g. 7421)") },
                                    leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = SaffronPrimary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                Button(
                                    onClick = {
                                        statusMessage = if (isHindi) "मोबाइल नंबर सफलतापूर्वक सत्यापित हो गया!" else "Mobile Number verified successfully!"
                                        val updated = currentUser.copy(
                                            phone = if (mobile.isNotEmpty()) mobile else currentUser.phone,
                                            verificationBadges = currentUser.verificationBadges + VerificationBadge.VERIFIED_USER
                                        )
                                        onLoginSuccess(updated)
                                        onDismiss()
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) "OTP सत्यापित करें और प्रवेश करें" else "Verify OTP & Continue",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        AuthMode.EMAIL_VERIFY -> {
                            item {
                                Text(
                                    text = if (isHindi) "ईमेल सत्यापन लिंक आपके पंजीकृत ईमेल पर भेजा गया है।" else "Verification link sent to your registered email. Tap below to confirm verification.",
                                    fontSize = 13.sp,
                                    color = SlateTextSecondary
                                )
                            }

                            item {
                                Button(
                                    onClick = {
                                        statusMessage = if (isHindi) "✓ ईमेल सफलतापूर्वक सत्यापित हो गया!" else "✓ Email successfully verified!"
                                        val updated = currentUser.copy(
                                            verificationBadges = currentUser.verificationBadges + VerificationBadge.VERIFIED_USER
                                        )
                                        onLoginSuccess(updated)
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = IndiaGreen),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.MarkEmailRead, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "ईमेल सत्यापन लिंक की पुष्टि करें" else "Confirm Email Verification Link",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        AuthMode.FORGOT_PASSWORD -> {
                            item {
                                Text(
                                    text = if (isHindi) "पासवर्ड रीसेट लिंक प्राप्त करने के लिए अपना ईमेल या फ़ोन दर्ज करें" else "Enter your email or phone to receive a password reset link",
                                    fontSize = 12.sp,
                                    color = SlateTextSecondary
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = emailOrPhone,
                                    onValueChange = { emailOrPhone = it },
                                    label = { Text("Email / Mobile") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                Button(
                                    onClick = {
                                        statusMessage = if (isHindi) "पासवर्ड रीसेट लिंक आपके ईमेल पर भेज दिया गया है।" else "Password reset link dispatched!"
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = if (isHindi) "रीसेट लिंक भेजें" else "Send Reset Link")
                                }
                            }
                        }

                        AuthMode.CHANGE_PASSWORD -> {
                            item {
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text(if (isHindi) "नया पासवर्ड" else "New Password") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    label = { Text(if (isHindi) "पासवर्ड दोबारा दर्ज करें" else "Confirm New Password") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            item {
                                Button(
                                    onClick = {
                                        statusMessage = if (isHindi) "पासवर्ड सफलतापूर्वक बदल दिया गया है!" else "Password successfully updated!"
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = if (isHindi) "पासवर्ड सेव करें" else "Save New Password")
                                }
                            }
                        }

                        AuthMode.PRIVACY_SETTINGS -> {
                            item {
                                Text(
                                    text = if (isHindi) "प्रोफाइल गोपनीयता नियंत्रण:" else "Profile Privacy Control:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    PrivacyOptionCard(
                                        title = if (isHindi) "सार्वजनिक (Public)" else "Public",
                                        desc = if (isHindi) "सभी लोग आपकी पोस्ट्स व लिस्टिंग्स देख सकते हैं" else "Everyone across India can see your public posts and listings",
                                        selected = privacyMode == "PUBLIC",
                                        onClick = { privacyMode = "PUBLIC" }
                                    )
                                    PrivacyOptionCard(
                                        title = if (isHindi) "केवल मित्र (Friends Only)" else "Friends Only",
                                        desc = if (isHindi) "केवल स्वीकृत मित्र आपकी सोशल प्रोफाइल देख सकते हैं" else "Only accepted friends can see your personal social feed",
                                        selected = privacyMode == "FRIENDS",
                                        onClick = { privacyMode = "FRIENDS" }
                                    )
                                    PrivacyOptionCard(
                                        title = if (isHindi) "निजी (Private)" else "Private",
                                        desc = if (isHindi) "कोई भी आपकी व्यक्तिगत जानकारी नहीं देख सकता" else "Completely private profile",
                                        selected = privacyMode == "PRIVATE",
                                        onClick = { privacyMode = "PRIVATE" }
                                    )
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        statusMessage = if (isHindi) "गोपनीयता सेटिंग्स अपडेट हो गईं!" else "Privacy settings updated!"
                                        onDismiss()
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = if (isHindi) "सेटिंग्स सुरक्षित करें" else "Save Settings")
                                }
                            }
                        }

                        AuthMode.DELETE_ACCOUNT -> {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = BreakingNewsRed.copy(alpha = 0.1f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BreakingNewsRed),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = if (isHindi) "⚠️ खाता हटाने की चेतावनी" else "⚠️ Account Deletion Warning",
                                            fontWeight = FontWeight.Bold,
                                            color = BreakingNewsRed,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isHindi) "खाता हटाने पर आपकी सभी लिस्टिंग्स, सोशल पोस्ट्स और संदेश हमेशा के लिए मिट जाएंगे।" else "Deleting your account permanently removes all your active listings, social posts, chats and bookmarks.",
                                            fontSize = 12.sp,
                                            color = SlateTextPrimary
                                        )
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = onDeleteAccount,
                                    colors = ButtonDefaults.buttonColors(containerColor = BreakingNewsRed),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = if (isHindi) "स्थायी रूप से खाता हटाएं" else "Permanently Delete Account", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyOptionCard(
    title: String,
    desc: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) SaffronContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) SaffronPrimary else SlateBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = onClick)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = desc, fontSize = 11.sp, color = SlateTextSecondary)
            }
        }
    }
}
