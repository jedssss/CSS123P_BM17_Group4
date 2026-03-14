# Product Inventory Management System
A Java Swing desktop application with MySQL database integration. Customers can view products and stock in real time. Administrators can log in to manage suppliers, products, and restock inventory.

---

## Prerequisites
- [XAMPP](https://www.apachefriends.org/) (includes MySQL/MariaDB)
- [Eclipse IDE for Enterprise Java and Web Developers](https://www.eclipse.org/downloads/packages/) (includes WindowBuilder)
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) – download the **Platform Independent** ZIP, extract the `.jar` file

---

## Setup Instructions

### 1. Start MySQL in XAMPP
- Open **XAMPP Control Panel**.
- Click **Start** for **MySQL**.

### 2. Create the Database
- Open your browser and go to [http://localhost/phpmyadmin](http://localhost/phpmyadmin).
- Click the **SQL** tab.
- Copy the entire SQL script from `database.sql` (included in this repository) and paste it into the SQL editor.
- Click **Go** to create the `dbsample` database and tables.

Default admin user:
- Username: `admin`
- Password: `admin123`

### 3. Import the Project into Eclipse
- Launch **Eclipse**.
- Go to **File → Import → General → Existing Projects into Workspace**.
- Select the root directory of this project (the folder containing the `.project` file).
- Click **Finish**.

### 4. Add MySQL Connector JAR
- In Eclipse, right‑click the project in **Package Explorer** → **Build Path → Configure Build Path**.
- Go to the **Libraries** tab → **Add External JARs…**.
- Select the `mysql-connector-j-xxx.jar` file you downloaded.
- Click **Apply and Close**.

### 5. Run the Application
- In Eclipse, navigate to `SetAProductInventoryManagementSystem.java` inside the package `com.javaswing.practicedb`.
- Right‑click → **Run As → Java Application**.
- The public view will open, displaying all products with stock and price.
- Click **Admin Login** (top‑right) to access the admin panel.

---

## Default Login
- Username: `admin`
- Password: `admin123`

---

## Project Structure
- `PublicView.java` – main customer view with product table and login button  
- `AdminMenu.java` – admin menu after login  
- `SupplierManager.java` – manage suppliers (add, edit, delete)  
- `ProductManager.java` – manage products (add, edit, delete)  
- `InventoryManagerFrame.java` – restock products  
- `UserLogin.java` – embedded login panel inside PublicView  
- Model classes: `Supplier.java`, `Product.java`, custom exceptions  

---

## License
This project is for educational purposes.
