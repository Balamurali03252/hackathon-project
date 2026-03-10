# 🛒 Product Sorting Automation – Price: Low to High

## 📌 Project Overview

This project automates the validation of the **product sorting feature** on an e-commerce platform.

The test verifies whether products are correctly arranged when the user selects the **"Price: Low to High"** sorting option.

The automation extracts product prices from the webpage and validates if they are displayed in **ascending order**.

---

## 🎯 Objective

To ensure that the **sorting functionality of the e-commerce website works correctly** when the user selects:

**Price: Low to High**

---

## 🧪 Test Scenario

**Scenario:** Verify product sorting functionality

**Steps:**

1. Open the e-commerce website
2. Search for a product (Example: *iPhone*)
3. Apply the sorting filter **Price: Low to High**
4. Capture the prices of the displayed products
5. Verify the products are sorted in **ascending order**

**Expected Result**

Products should be displayed from **lowest price → highest price**

---

## 🧰 Tech Stack

* Java
* Selenium WebDriver
* TestNG / Cucumber
* Eclipse IDE
* Git & GitHub

---

## 📂 Project Structure

```id="2j3e6p"
project-root
│
├── src/test/java
│   ├── pages
│   │    └── ProductPage.java
│   │
│   ├── stepdefinitions
│   │    └── ProductSteps.java
│   │
│   └── runner
│        └── TestRunner.java
│
├── src/test/resources
│   └── features
│        └── product_sort.feature
│
└── README.md
```

---

## ⚙️ How to Run the Project

### 1️⃣ Clone the repository

```id="rc6lsk"
git clone https://github.com/your-username/product-sorting-automation.git
```

### 2️⃣ Open in Eclipse

Import the project into **Eclipse IDE**

```
File → Import → Existing Maven Project
```

### 3️⃣ Install dependencies

Update Maven dependencies.

### 4️⃣ Run Test

Run the **TestRunner class**.

---

## 🔍 Validation Logic

1. Extract product prices from the webpage.
2. Store prices in a list.
3. Create another list sorted in ascending order.
4. Compare both lists.

If both lists match → **Sorting functionality works correctly**.

---

## 📊 Example

Displayed Prices

```
₹45,000
₹52,000
₹60,000
₹75,000
```

Sorted Prices

```
₹45,000
₹52,000
₹60,000
₹75,000
```

Result → ✅ **Pass**

---

## 🚀 Future Enhancements

* Verify **Price: High to Low** sorting
* Validate sorting for **ratings**
* Test sorting across multiple pages
* Generate automated reports

---

## 👨‍💻 Team Members

* Bala
* Hackathon Team
