
package com.example.androidhomepagelayout

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidhomepagelayout.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

// APP SCREENS

enum class AppScreen {
    WELCOME, LOGIN, REGISTER, HOME
}

// DASHBOARD TABS

enum class DashboardTab {
    HOME, SECURITY, NOTIFICATIONS, PROFILE
}

// MAIN ACTIVITY

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AndroidHomePageLayoutTheme {
                ESecurityApp()
            }
        }
    }
}

// APP NAVIGATION

@Composable
fun ESecurityApp() {

    val auth = remember { FirebaseAuth.getInstance() }

    var currentScreen by remember {
        mutableStateOf(
            if (auth.currentUser != null) AppScreen.HOME
            else AppScreen.WELCOME
        )
    }

    when (currentScreen) {

        AppScreen.WELCOME -> {
            HomePageScreen(
                onGetStarted = {
                    currentScreen = AppScreen.LOGIN
                }
            )
        }

        AppScreen.LOGIN -> {
            LoginScreen(
                auth = auth,
                onLoginSuccess = {
                    currentScreen = AppScreen.HOME
                },
                onRegisterClick = {
                    currentScreen = AppScreen.REGISTER
                }
            )
        }

        AppScreen.REGISTER -> {
            RegisterScreen(
                auth = auth,
                onRegisterSuccess = {
                    currentScreen = AppScreen.HOME
                },
                onLoginClick = {
                    currentScreen = AppScreen.LOGIN
                }
            )
        }

        AppScreen.HOME -> {
            UserHomeScreen(
                email = auth.currentUser?.email ?: "User",
                onLogout = {
                    auth.signOut()
                    currentScreen = AppScreen.WELCOME
                }
            )
        }
    }
}

// WELCOME SCREEN

@Composable
fun HomePageScreen(onGetStarted: () -> Unit = {}) {

    val context = androidx.compose.ui.platform.LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                ShieldLogo(modifier = Modifier.size(150.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "E-SECURITY",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Your digital security is our top priority. Protect your data with ease.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GET STARTED", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        Toast.makeText(
                            context,
                            "E-SECURITY helps protect your digital information.",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BrandLightBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("LEARN MORE", fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Introduction skipped.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text(
                        "Skip for now",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// LOGIN SCREEN

@Composable
fun LoginScreen(
    auth: FirebaseAuth,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "E-SECURITY",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Login to your account",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                errorMessage = ""
                isLoading = true

                auth.signInWithEmailAndPassword(
                    email.trim(),
                    password
                ).addOnCompleteListener { task ->

                    isLoading = false

                    if (task.isSuccessful) {
                        onLoginSuccess()
                    } else {
                        errorMessage = task.exception?.localizedMessage
                            ?: "Login failed. Please try again."
                    }
                }
            },
            enabled = !isLoading &&
                    email.isNotBlank() &&
                    password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGold,
                contentColor = Color.Black
            )
        ) {

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black
                )
            } else {
                Text("LOGIN", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onRegisterClick) {
            Text("Don't have an account? Register here")
        }
    }
}

// REGISTRATION SCREEN

@Composable
fun RegisterScreen(
    auth: FirebaseAuth,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "E-SECURITY",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Create an Account",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {

                errorMessage = ""

                when {
                    !android.util.Patterns.EMAIL_ADDRESS
                        .matcher(email.trim()).matches() -> {
                        errorMessage = "Please enter a valid email address."
                    }

                    password.length < 6 -> {
                        errorMessage = "Password must be at least 6 characters."
                    }

                    password != confirmPassword -> {
                        errorMessage = "Passwords do not match."
                    }

                    else -> {
                        isLoading = true

                        auth.createUserWithEmailAndPassword(
                            email.trim(),
                            password
                        ).addOnCompleteListener { task ->

                            isLoading = false

                            if (task.isSuccessful) {
                                onRegisterSuccess()
                            } else {
                                errorMessage = task.exception?.localizedMessage
                                    ?: "Registration failed. Please try again."
                            }
                        }
                    }
                }
            },
            enabled = !isLoading &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    confirmPassword.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGold,
                contentColor = Color.Black
            )
        ) {

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black
                )
            } else {
                Text("REGISTER", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onLoginClick) {
            Text("Already have an account? Login")
        }
    }
}

// AUTHENTICATED HOME SCREEN WITH BOTTOM NAVIGATION

@Composable
fun UserHomeScreen(
    email: String,
    onLogout: () -> Unit
) {

    var selectedTab by rememberSaveable {
        mutableStateOf(DashboardTab.HOME)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {

                NavigationBarItem(
                    selected = selectedTab == DashboardTab.HOME,
                    onClick = { selectedTab = DashboardTab.HOME },
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandGold,
                        selectedTextColor = BrandGold,
                        indicatorColor = BrandGold.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == DashboardTab.SECURITY,
                    onClick = { selectedTab = DashboardTab.SECURITY },
                    icon = { Text("🛡️", fontSize = 20.sp) },
                    label = { Text("Security") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandGold,
                        selectedTextColor = BrandGold,
                        indicatorColor = BrandGold.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == DashboardTab.NOTIFICATIONS,
                    onClick = { selectedTab = DashboardTab.NOTIFICATIONS },
                    icon = { Text("🔔", fontSize = 20.sp) },
                    label = { Text("Notifications") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandGold,
                        selectedTextColor = BrandGold,
                        indicatorColor = BrandGold.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == DashboardTab.PROFILE,
                    onClick = { selectedTab = DashboardTab.PROFILE },
                    icon = { Text("👤", fontSize = 20.sp) },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandGold,
                        selectedTextColor = BrandGold,
                        indicatorColor = BrandGold.copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (selectedTab) {

                DashboardTab.HOME -> {
                    HomeTabContent(email = email)
                }

                DashboardTab.SECURITY -> {
                    SecurityTabContent()
                }

                DashboardTab.NOTIFICATIONS -> {
                    NotificationsTabContent()
                }

                DashboardTab.PROFILE -> {
                    ProfileTabContent(
                        email = email,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

// HOME TAB - ORIGINAL DASHBOARD

@Composable
fun HomeTabContent(email: String) {

    val username = email.substringBefore("@").ifBlank { "User" }
    val navy = Color(0xFF14263D)
    val gold = BrandGold
    val muted = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // HEADER

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ShieldLogo(modifier = Modifier.size(48.dp))

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    "E-SECURITY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    "DIGITAL SECURITY SYSTEM",
                    fontSize = 10.sp,
                    color = muted,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                "● ONLINE",
                color = Color(0xFF2E9D62),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // WELCOME BANNER

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = navy)
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            "YOUR PERSONAL DASHBOARD",
                            color = gold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "Welcome back,",
                            color = Color.White,
                            fontSize = 17.sp
                        )

                        Text(
                            username,
                            color = gold,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Your digital safety starts here.",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }

                    ShieldLogo(modifier = Modifier.size(95.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        "●",
                        color = Color(0xFF65D69A),
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            "Account authenticated",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        Text(
                            "Your current session is active.",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ACCOUNT OVERVIEW

        Text(
            "ACCOUNT OVERVIEW",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {

            Column(modifier = Modifier.padding(18.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        "My Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        "● Active",
                        color = Color(0xFF2E9D62),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    "REGISTERED EMAIL",
                    fontSize = 10.sp,
                    color = muted,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    email,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(14.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text("Account Type", fontSize = 13.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        "Registered User",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SECURITY AT A GLANCE

        Text(
            "SECURITY AT A GLANCE",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            SecurityStatCard(
                modifier = Modifier.weight(1f),
                icon = "🛡️",
                title = "Active",
                subtitle = "Account",
                color = Color(0xFFE9F4EC)
            )

            SecurityStatCard(
                modifier = Modifier.weight(1f),
                icon = "🔐",
                title = "Secure",
                subtitle = "Login",
                color = Color(0xFFE9F1FA)
            )

            SecurityStatCard(
                modifier = Modifier.weight(1f),
                icon = "✓",
                title = "Verified",
                subtitle = "Session",
                color = Color(0xFFFBF2DE)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // QUICK ACCESS

        Text(
            "EXPLORE E-SECURITY",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SecurityActionCard(
                modifier = Modifier.weight(1f),
                icon = "🛡️",
                title = "Security Tips",
                description = "Learn to protect your digital identity."
            )

            SecurityActionCard(
                modifier = Modifier.weight(1f),
                icon = "🔑",
                title = "Password Safety",
                description = "Discover better password practices."
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SecurityActionCard(
                modifier = Modifier.weight(1f),
                icon = "🌐",
                title = "Online Safety",
                description = "Stay safe while browsing online."
            )

            SecurityActionCard(
                modifier = Modifier.weight(1f),
                icon = "ℹ️",
                title = "About Us",
                description = "Learn more about E-SECURITY."
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // INFORMATION BANNER

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = navy)
        ) {

            Column(modifier = Modifier.padding(18.dp)) {

                Text(
                    "Stay protected. Stay informed.",
                    color = gold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Make digital security a habit. Protect your personal information wherever you go.",
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "E-SECURITY • DIGITAL AWARENESS",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            color = muted,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

// SECURITY TAB

@Composable
fun SecurityTabContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            "Security Center",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            "Learn how to keep your digital information safe.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        DashboardInfoCard(
            icon = "🔐",
            title = "Password Safety",
            description = "Use strong, unique passwords and avoid sharing your login credentials."
        )

        DashboardInfoCard(
            icon = "🛡️",
            title = "Account Protection",
            description = "Keep your account information private and sign out when using shared devices."
        )

        DashboardInfoCard(
            icon = "🌐",
            title = "Safe Browsing",
            description = "Avoid suspicious links, unknown downloads, and websites you do not trust."
        )

        DashboardInfoCard(
            icon = "⚠️",
            title = "Phishing Awareness",
            description = "Be careful with unexpected emails and messages asking for personal information."
        )
    }
}

// NOTIFICATIONS TAB

@Composable
fun NotificationsTabContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("🔔", fontSize = 55.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Notifications",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "You're all caught up!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            "Security alerts and updates will appear here.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            textAlign = TextAlign.Center
        )
    }
}

// PROFILE TAB

@Composable
fun ProfileTabContent(
    email: String,
    onLogout: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            "My Profile",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            "Manage your account information.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ShieldLogo(modifier = Modifier.size(90.dp))

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    email.substringBefore("@").ifBlank { "User" },
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Registered User",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "EMAIL ADDRESS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    email,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "ACCOUNT",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        DashboardInfoCard(
            icon = "✓",
            title = "Account Status",
            description = "Your account is currently active."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGold,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                "LOG OUT",
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

// REUSABLE INFORMATION CARD

@Composable
fun DashboardInfoCard(
    icon: String,
    title: String,
    description: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(icon, fontSize = 28.sp)

            Spacer(modifier = Modifier.width(14.dp))

            Column {

                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    description,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
        }
    }
}

// SECURITY STAT CARD

@Composable
fun SecurityStatCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    subtitle: String,
    color: Color
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            Text(icon, fontSize = 23.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF263B50)
            )

            Text(
                subtitle,
                fontSize = 11.sp,
                color = Color(0xFF526477)
            )
        }
    }
}

// QUICK ACCESS CARD

@Composable
fun SecurityActionCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    description: String
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {

            Text(icon, fontSize = 30.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                description,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {},
                contentPadding = PaddingValues(0.dp)
            ) {

                Text(
                    "EXPLORE →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGold
                )
            }
        }
    }
}

// SHIELD LOGO

@Composable
fun ShieldLogo(modifier: Modifier = Modifier) {

    Image(
        painter = painterResource(id = R.drawable.ic_shield_logo),
        contentDescription = "Shield Logo",
        modifier = modifier
    )
}

// PREVIEWS

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    AndroidHomePageLayoutTheme(darkTheme = false) {
        HomePageScreen(onGetStarted = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D47A1)
@Composable
fun HomePageDarkPreview() {
    AndroidHomePageLayoutTheme(darkTheme = true) {
        HomePageScreen(onGetStarted = {})
    }
}
