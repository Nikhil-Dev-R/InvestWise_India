# 📈 InvestWise India – Your Smart Investment Companion

InvestWise India is a powerful and beginner-friendly **FinTech mobile application** designed to educate users about various investment options and help them make smarter mutual fund decisions. It features AI-powered guidance, curated fund insights, and intuitive tools for comparison — all tailored for users with little or no financial background.

![InvestWise](https://github.com/user-attachments/assets/48f53d2b-6821-4ac1-8ea8-d3b4f100ef96)<br>


---

## 🧩 Roadmap

- ✅ AI Chatbot with Gemini API
- ✅ Mutual fund discovery & comparison
- ✅ Firebase login & personalization
- 🛠 SIP calculator (coming soon)
- 🔜 Portfolio tracker (planned)
- 🔔 Notifications for NAV/fund updates
  
---

## 🚀 Features

### 📚 Investment Knowledge Center
- Categorized explanation of **Fixed Returns** (e.g., FD, PPF, NSC) and **Variable Returns** (e.g., Stocks, Mutual Funds, ETFs)
- Detailed breakdown of each instrument: Risk, Return, Liquidity, Tax, and Ideal Time Horizon

### 📊 Mutual Fund Explorer
- Real-time top-performing mutual funds across all categories:
  - Large Cap, Mid Cap, Small Cap, Flexi Cap
  - ELSS (Tax Saving), Thematic, International
  - Debt Funds (Ultra Short, Liquid, Floating Rate, etc.)
- Data sourced from [mfapi.in](https://mfapi.in)

### 🧠 AI-Powered Assistant (Gemini)
- Ask finance-related queries anytime via an **AI Chatbot**
- Powered by **Google Gemini API**
- Learns and improves with tuned prompt data (not financial advice, but education-focused)

### 📈 Smart Fund Comparison
- Compare funds by:
  - Historical NAV
  - Expense Ratio
  - Risk Level
  - Category and Fund House

### 🔐 User Personalization
- **Login with Google or Email** via Firebase
- Save favorite funds and preferences
- Tailored recommendations (planned in roadmap)

### 📡 Market Snapshot
- Live updates for **Nifty 50**, **Sensex**, **Bank Nifty** via Yahoo Finance API

---

## 📱 Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| Architecture | MVVM |
| UI Framework | Jetpack Compose (Material 3) |
| Dependency Injection | Hilt (Dagger) |
| Backend | Firebase Authentication & Firestore |
| APIs | Google Gemini API, mfapi.in, Yahoo Finance |
| Background Jobs | WorkManager |
| Networking | Retrofit + Coroutines |
| State Management | ViewModel + StateFlow |

---

## 🏆 Achievements

- 🥇 **Top 25 Finalist** out of 400+ teams at [QubitX Hackathon 2025](#), held at GL Bajaj, Mathura
- Successfully built and presented the app in a **24-hour national-level hackathon** organized by HackwithIndia

---

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>InvestWise India – App Screenshots</title>
  <style>
    body { font-family: Arial, sans-serif; padding: 20px; background: #f8f8f8; }
    h2 { color: #2c3e50; border-bottom: 2px solid #ccc; padding-bottom: 5px; }
    .screenshot-row {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      margin-bottom: 40px;
    }
    .screenshot-row img {
      width: 240px;
      border-radius: 8px;
      box-shadow: 0 0 10px rgba(0,0,0,0.1);
    }
  </style>
</head>
<body>

<h1>📱 InvestWise India – App Screenshots</h1>

<h2>🚀 Launch Screen</h2>
<div class="screenshot-row">
  <img src="https://github.com/user-attachments/assets/363b4f88-92cb-43a5-9654-c9b962386f3a">
  <img src="https://github.com/user-attachments/assets/8c394fd8-eee8-49f4-8c43-6f30196f646f">
</div>

<h2>🏠 Home Screen</h2>
<div class="screenshot-row">
  <img src="https://github.com/user-attachments/assets/d3b59a5c-a53c-452c-9117-12b578036ea0">
  <img src="https://github.com/user-attachments/assets/35474970-6993-4ca8-adac-bdc080dfc280">
  <img src="https://github.com/user-attachments/assets/b89736be-58bc-400a-8a6b-037fe1571061">
  <img src="https://github.com/user-attachments/assets/24ad03e8-a10e-4039-be75-198b5d132e50">
</div>

<h2>📊 Fund Comparison Screen</h2>
<div class="screenshot-row">
  <img src="https://github.com/user-attachments/assets/d91c50b5-23d4-4a04-a6db-3c095ed814f4">
  <img src="https://github.com/user-attachments/assets/d2f78092-8a50-499a-89ba-33b418b3b4af">
  <img src="https://github.com/user-attachments/assets/df75380d-114a-4b4a-8c6e-855e73bc014c">
</div>

<h2>🤖 ChatBot Screen</h2>
<div class="screenshot-row">
  <img src="https://github.com/user-attachments/assets/6952037f-9999-40a1-b911-41f47581bdea">
</div>

<h2>📁 Mutual Fund Screen</h2>
<div class="screenshot-row">
  <img src="https://github.com/user-attachments/assets/e6202440-dad2-4d6b-ab47-a5b9e043355b">
  <img src="https://github.com/user-attachments/assets/89287606-c67c-4e50-bb46-4afaf8806c7b">
  <img src="https://github.com/user-attachments/assets/bbc90a84-ed4e-48c0-88e1-f627ea65efe5">
  <img src="https://github.com/user-attachments/assets/56a942f6-0e66-4698-a346-50484c3fddb6">
</div>

<h2>👤 Account Screen</h2>
<div class="screenshot-row">
  <img src="https://github.com/user-attachments/assets/d711e68c-f490-432d-b90b-872130413d31">
  <img src="https://github.com/user-attachments/assets/42bbc1b7-de27-4836-9653-9bb39972ce15">
</div>

</body>
</html>

---

## 📁 Project Structure

The project follows a modular and clean **MVVM architecture**, with clear separation of concerns.

```
InvestWiseIndia/
├── app/
│   ├── manifests/
│   │   └── AndroidManifest.xml
│   ├── kotlin+java/
│       └── com.investwise_india/
│           ├── auth/                  # Auth UI, ViewModel, and helpers
│           ├── chatbot/               # AI model prompts, messages, constants
│           ├── data/
│           │   ├── cache/             # (Optional future use)
│           │   ├── models/            # Data models (MutualFund, InvestmentOption)
│           │   ├── repository/        # Data source logic
│           │   ├── di/                # Dependency Injection modules
│           │   ├── network/           # Retrofit API services
│           │   ├── DataModule.kt
│           │   └── BackgroundDataLoader.kt
│           ├── ui/
│           │   ├── components/        # Reusable Composables (tables, dialogs, headers)
│           │   ├── navigation/        # Navigation setup (NavHost, BottomNav)
│           │   ├── screens/           # All screens (Home, Compare, Chat, Account, etc.)
│           │   └── theme/             # Material 3 theme and colors
│           ├── viewmodel/             # ViewModels per screen
│           ├── util/                  # Utility functions and main application
│           └── MainActivity.kt        # Entry point
```

---

## 🛠 Clean Architecture Highlights

- **📦 Modularized structure** with `screens`, `components`, `navigation`, and `theme`
- **🧠 MVVM Pattern** for clear logic separation: each screen has its own `ViewModel`
- **🔌 Hilt Dependency Injection**: All network and data dependencies injected via `DataModule.kt`, `NetworkModule.kt`
- **🌐 Retrofit Integration**: `MutualFundApiService.kt` connects to [mfapi.in](https://mfapi.in) to fetch live NAV data
- **🤖 AI Chat**: Powered by Google Gemini, fully isolated in the `chatbot/` module
- **🔄 Background Syncing**: Handled via `WorkManager` in `BackgroundDataLoader.kt`

---


## 🔒 Disclaimer

This app is **educational only** and does **not offer financial advice or trading services**.  
Always consult with a **SEBI-registered advisor** before making any investment decisions.

