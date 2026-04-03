import mysql.connector
import csv
import os
import traceback

# Resolve paths for the new 'db' folder
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DB_DIR = os.path.join(BASE_DIR, "db")
USERS_CSV = os.path.join(DB_DIR, "users.csv")
PATIENTS_CSV = os.path.join(DB_DIR, "patients.csv")

def get_db_connection():
    try:
        conn = mysql.connector.connect(
            host="localhost",
            user="root",
            password="",
            database="bone_predict_db"
        )
        return conn
    except mysql.connector.Error as err:
        if err.errno == 1049: # Database doesn't exist
            conn = mysql.connector.connect(host="localhost", user="root", password="")
            return conn
        raise err

def init_db():
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("CREATE DATABASE IF NOT EXISTS bone_predict_db")
    cursor.execute("USE bone_predict_db")
    
    # User Table
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            email VARCHAR(255) UNIQUE NOT NULL,
            password VARCHAR(255) NOT NULL,
            phone TEXT,
            specialty TEXT,
            institution TEXT,
            license_no TEXT
        )
    ''')
    
    # Patients Table
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS patients (
            id VARCHAR(255) PRIMARY KEY,
            firstName VARCHAR(255) NOT NULL,
            lastName VARCHAR(255) NOT NULL,
            dob VARCHAR(255) NOT NULL,
            gender VARCHAR(255) NOT NULL,
            contactNumber VARCHAR(255) NOT NULL,
            doctorId VARCHAR(255) NOT NULL,
            createdAt BIGINT NOT NULL
        )
    ''')
    
    # Clinical Data
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS clinical_data (
            id VARCHAR(255) PRIMARY KEY,
            patientId VARCHAR(255) NOT NULL,
            weight FLOAT,
            smokingStatus VARCHAR(255),
            alcoholConsumption VARCHAR(255),
            hasDiabetes TINYINT(1),
            hasHypertension TINYINT(1),
            hasOsteoporosis TINYINT(1),
            probingDepth FLOAT,
            cal FLOAT,
            bleedingOnProbing TINYINT(1),
            bleedingIndex FLOAT,
            plaqueIndex FLOAT,
            toothMobility VARCHAR(255),
            gingivalPhenotype VARCHAR(255),
            cbctImageUrl VARCHAR(255),
            createdAt BIGINT NOT NULL,
            FOREIGN KEY (patientId) REFERENCES patients(id) ON DELETE CASCADE
        )
    ''')
    
    # Predictions
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS predictions (
            id VARCHAR(255) PRIMARY KEY,
            clinicalDataId VARCHAR(255) NOT NULL,
            riskScore FLOAT NOT NULL,
            riskCategory VARCHAR(255) NOT NULL,
            modelUsed VARCHAR(255) DEFAULT 'Random Forest',
            confidenceScore FLOAT,
            resultsSummary TEXT,
            createdAt BIGINT NOT NULL,
            FOREIGN KEY (clinicalDataId) REFERENCES clinical_data(id) ON DELETE CASCADE
        )
    ''')
    
    # OTPs
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS otps (
            id INT AUTO_INCREMENT PRIMARY KEY,
            email VARCHAR(255) NOT NULL,
            otp VARCHAR(6) NOT NULL,
            createdAt BIGINT NOT NULL
        )
    ''')
    
    conn.commit()
    cursor.close()
    conn.close()
    sync_to_csv()
    sync_patients_to_csv()

def sync_to_csv():
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("USE bone_predict_db")
        cursor.execute("SELECT name, email, password FROM users")
        rows = cursor.fetchall()
        with open(USERS_CSV, "w", newline='') as f:
            writer = csv.writer(f)
            writer.writerow(["Name", "Email", "Password"])
            writer.writerows(rows)
        cursor.close()
        conn.close()
    except Exception as e:
        print(f"Error syncing to CSV: {e}")

def sync_patients_to_csv():
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("USE bone_predict_db")
        cursor.execute("SELECT id, firstName, lastName, dob, gender, contactNumber, doctorId, createdAt FROM patients")
        rows = cursor.fetchall()
        
        file_exists = os.path.exists(PATIENTS_CSV)
        with open(PATIENTS_CSV, "a" if file_exists else "w", newline='') as f:
            writer = csv.writer(f)
            if not file_exists:
                writer.writerow(["ID", "Name", "Age", "Smoking", "Diabetes", "Hypertension", "Image", "Status", "PatientID", "Field1", "Field2", "Field3", "Field4"])
            for row in rows:
                p_id, fname, lname, dob, gender, contact, doc_id, created = row
                writer.writerow([p_id, f"{fname} {lname}", "", "", "", "", "new_img.jpg", "Unknown", p_id, "", "", "", ""])
        cursor.close()
        conn.close()
    except Exception as e:
        print(f"Sync Patients to CSV error: {e}")
