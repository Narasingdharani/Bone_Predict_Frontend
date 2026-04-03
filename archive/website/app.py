import sqlite3
import csv
import os
import joblib
from flask import Flask, render_template, request, jsonify

app = Flask(__name__)

# File paths
USERS_CSV = "users.csv"
PATIENTS_CSV = "patient_data/patient_dataset.csv"
MODEL_PATH = os.path.join(os.path.dirname(__file__), "model.pkl")

# Load model
if os.path.exists(MODEL_PATH):
    try:
        model = joblib.load(MODEL_PATH)
    except Exception as e:
        print(f"Error loading model: {e}")
        model = None
else:
    model = None

def get_db_connection():
    db_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), "bone_backend", "users.db")
    os.makedirs(os.path.dirname(db_path), exist_ok=True)
    conn = sqlite3.connect(db_path, check_same_thread=False)
    return conn

def init_db():
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name VARCHAR(255) NOT NULL,
            email VARCHAR(255) UNIQUE NOT NULL,
            password VARCHAR(255) NOT NULL,
            phone TEXT,
            specialty TEXT,
            institution TEXT,
            license_no TEXT
        )
    ''')
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
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS clinical_data (
            id VARCHAR(255) PRIMARY KEY,
            patientId VARCHAR(255) NOT NULL,
            weight FLOAT,
            smokingStatus VARCHAR(255),
            alcoholConsumption VARCHAR(255),
            hasDiabetes INTEGER,
            hasHypertension INTEGER,
            hasOsteoporosis INTEGER,
            probingDepth FLOAT,
            cal FLOAT,
            bleedingOnProbing INTEGER,
            bleedingIndex FLOAT,
            plaqueIndex FLOAT,
            toothMobility VARCHAR(255),
            gingivalPhenotype VARCHAR(255),
            cbctImageUrl VARCHAR(255),
            createdAt BIGINT NOT NULL,
            FOREIGN KEY (patientId) REFERENCES patients(id) ON DELETE CASCADE
        )
    ''')
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
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS otps (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email VARCHAR(255) NOT NULL,
            otp VARCHAR(6) NOT NULL,
            createdAt BIGINT NOT NULL
        )
    ''')
    # Add a sample doctor if not exists
    cursor.execute("SELECT * FROM users WHERE email = 'doctor@test.com'")
    if not cursor.fetchone():
        cursor.execute("INSERT INTO users (name, email, password) VALUES ('Dr. Test', 'doctor@test.com', 'doctor123')")
    
    # Ensure dharaninarasing25@gmail.com exists
    cursor.execute("SELECT * FROM users WHERE email = 'dharaninarasing25@gmail.com'")
    if not cursor.fetchone():
        cursor.execute("INSERT INTO users (name, email, password) VALUES ('Dharani', 'dharaninarasing25@gmail.com', 'Dharani123')")
        print("DEBUG: Manually added dharaninarasing25@gmail.com to database")
    
    conn.commit()
    cursor.close()
    conn.close()
    sync_to_csv()

def sync_to_csv():
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT name, email, password FROM users")
        rows = cursor.fetchall()
        
        with open(USERS_CSV, "w", newline='') as f:
            writer = csv.writer(f)
            writer.writerow(["Name", "Email", "Password"])
            writer.writerows(rows)
        cursor.close()
        conn.close()
    except Exception as e:
        print(f"Sync to CSV error: {e}")

def sync_patients_to_csv():
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT id, firstName, lastName, dob, gender, contactNumber, doctorId, createdAt FROM patients")
        rows = cursor.fetchall()
        
        with open(PATIENTS_CSV, "a", newline='') as f:
            writer = csv.writer(f)
            for row in rows:
                p_id, fname, lname, dob, gender, contact, doc_id, created = row
                full_name = f"{fname} {lname}"
                writer.writerow([p_id, full_name, "", "", "", "", "new_img.jpg", "Unknown", p_id, "", "", "", ""])
        cursor.close()
        conn.close()
    except Exception as e:
        print(f"Sync Patients to CSV error: {e}")

def sync_clinical_data_to_csv(data):
    try:
        with open(PATIENTS_CSV, "a", newline='') as f:
            writer = csv.writer(f)
            writer.writerow([
                data.get("patientId"), "", "", data.get("smokingStatus"), 
                "Yes" if data.get("hasDiabetes") else "No", "", 
                data.get("cbctImageUrl") or "new_img.jpg", "Unknown",
                data.get("patientId"), "", "", data.get("hasDiabetes"), "Unknown",
                data.get("probingDepth"), data.get("cal"), data.get("bleedingIndex"),
                data.get("plaqueIndex"), data.get("toothMobility"), data.get("gingivalPhenotype")
            ])
    except Exception as e:
        print(f"Sync Clinical Data to CSV error: {e}")

init_db()

@app.route("/")
def splash_page():
    return render_template("splash.html")

@app.route("/welcome")
def welcome_page():
    return render_template("welcome.html")

@app.route("/signin")
@app.route("/login")
def login_page():
    return render_template("login.html")

@app.route("/signup")
@app.route("/register")
def register_page():
    return render_template("register.html")

@app.route("/select-role")
def select_role_page():
    return render_template("select_role.html")

@app.route("/dashboard")
def dashboard_page():
    return render_template("dashboard.html")

@app.route("/patients")
def patients_page():
    return render_template("patients.html")

@app.route("/add-patient")
def add_patient_page():
    return render_template("add_patient.html")

@app.route("/assessment/features")
def feature_selection_page():
    return render_template("feature_selection.html")

@app.route("/assessment/demographics")
def demographics_page():
    return render_template("demographics.html")

@app.route("/assessment/periodontal")
def periodontal_page():
    return render_template("periodontal.html")

@app.route("/assessment/cbct")
def cbct_page():
    return render_template("cbct.html")

@app.route("/assessment/risk")
def risk_assessment_page():
    return render_template("risk_assessment.html")

@app.route("/assessment/explanation")
def risk_explanation_page():
    return render_template("risk_explanation.html")

@app.route("/assessment/visuals")
def bone_loss_visuals_page():
    return render_template("bone_loss_visuals.html")

@app.route("/assessment/summary")
def result_summary_page():
    return render_template("result_summary.html")

@app.route("/assessment/report/setup")
def generate_report_page():
    return render_template("generate_report.html")

@app.route("/assessment/report/preview")
def report_preview_page():
    return render_template("report_preview.html")

@app.route("/assessment/report/export")
def export_report_page():
    return render_template("export_report.html")

@app.route("/test")
def test():
    return "Flask working"

@app.route("/api/patients", methods=["GET", "POST"])
def patients():
    if request.method == "POST":
        data = request.json
        if not data:
            return jsonify({"error": "No data provided"}), 400
        
        p_id = data.get("id")
        fname = data.get("firstName")
        lname = data.get("lastName")
        dob = data.get("dob")
        gender = data.get("gender")
        contact = data.get("contactNumber")
        doc_id = data.get("doctorId")
        created_at = data.get("createdAt")
        
        try:
            conn = get_db_connection()
            cursor = conn.cursor()
            cursor.execute('''
                INSERT INTO patients (id, firstName, lastName, dob, gender, contactNumber, doctorId, createdAt) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ''', (p_id, fname, lname, dob, gender, contact, doc_id, created_at))
            conn.commit()
            cursor.close()
            conn.close()
            sync_patients_to_csv()
            return jsonify(data), 201
        except Exception as e:
            return jsonify({"error": str(e)}), 500
            
    doc_id = request.args.get("doctorId")
    rows = []
    if doc_id:
        try:
            conn = get_db_connection()
            cursor = conn.cursor()
            cursor.execute("SELECT id, firstName, lastName, dob, gender, contactNumber, doctorId, createdAt FROM patients WHERE doctorId = ?", (doc_id,))
            rows = cursor.fetchall()
            cursor.close()
            conn.close()
        except Exception as e:
            return jsonify({"error": str(e)}), 500
    
    patients_list = []
    for row in rows:
        patients_list.append({
            "id": row[0],
            "firstName": row[1],
            "lastName": row[2],
            "dob": row[3],
            "gender": row[4],
            "contactNumber": row[5],
            "doctorId": row[6],
            "createdAt": row[7]
        })
    return jsonify(patients_list), 200

@app.route("/api/clinical-data", methods=["POST"])
def clinical_data():
    data = request.json
    if not data:
        return jsonify({"error": "No data provided"}), 400
    
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute('''
            INSERT INTO clinical_data (
                id, patientId, weight, smokingStatus, alcoholConsumption, 
                hasDiabetes, hasHypertension, hasOsteoporosis, 
                probingDepth, cal, bleedingOnProbing, bleedingIndex, 
                plaqueIndex, toothMobility, gingivalPhenotype, 
                cbctImageUrl, createdAt
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ''', (
            data.get("id"), data.get("patientId"), data.get("weight"),
            data.get("smokingStatus"), data.get("alcoholConsumption"),
            data.get("hasDiabetes"), data.get("hasHypertension"), data.get("hasOsteoporosis"),
            data.get("probingDepth"), data.get("cal"), data.get("bleedingOnProbing"),
            data.get("bleedingIndex"), data.get("plaqueIndex"), data.get("toothMobility"),
            data.get("gingivalPhenotype"), data.get("cbctImageUrl"), data.get("createdAt")
        ))
        conn.commit()
        cursor.close()
        conn.close()
        sync_clinical_data_to_csv(data)
        return jsonify(data), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/clinical-data/<patient_id>", methods=["GET"])
def get_clinical_data_by_patient(patient_id):
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("""
            SELECT id, patientId, weight, smokingStatus, alcoholConsumption,
                   hasDiabetes, hasHypertension, hasOsteoporosis,
                   probingDepth, cal, bleedingOnProbing, bleedingIndex,
                   plaqueIndex, toothMobility, gingivalPhenotype, cbctImageUrl, createdAt
            FROM clinical_data WHERE patientId = ? ORDER BY createdAt DESC
        """, (patient_id,))
        rows = cursor.fetchall()
        cursor.close()
        conn.close()
        result = []
        for row in rows:
            result.append({
                "id": row[0], "patientId": row[1], "weight": row[2],
                "smokingStatus": row[3], "alcoholConsumption": row[4],
                "hasDiabetes": bool(row[5]) if row[5] is not None else None,
                "hasHypertension": bool(row[6]) if row[6] is not None else None,
                "hasOsteoporosis": bool(row[7]) if row[7] is not None else None,
                "probingDepth": row[8], "cal": row[9],
                "bleedingOnProbing": bool(row[10]) if row[10] is not None else None,
                "bleedingIndex": row[11], "plaqueIndex": row[12],
                "toothMobility": row[13], "gingivalPhenotype": row[14],
                "cbctImageUrl": row[15], "createdAt": row[16]
            })
        return jsonify(result), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/predictions", methods=["GET", "POST"])
def predictions():
    if request.method == "POST":
        data = request.json
        if not data:
            return jsonify({"error": "No data provided"}), 400
        
        try:
            conn = get_db_connection()
            cursor = conn.cursor()
            cursor.execute('''
                INSERT INTO predictions (
                    id, clinicalDataId, riskScore, riskCategory, 
                    modelUsed, confidenceScore, resultsSummary, createdAt
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ''', (
                data.get("id"), data.get("clinicalDataId"), data.get("riskScore"),
                data.get("riskCategory"), data.get("modelUsed"), 
                data.get("confidenceScore"), data.get("resultsSummary"), 
                data.get("createdAt")
            ))
            conn.commit()
            cursor.close()
            conn.close()
            return jsonify(data), 201
        except Exception as e:
            return jsonify({"error": str(e)}), 500
            
    # GET method - fetch all or filtered by clinicalDataId
    cd_id = request.args.get("clinicalDataId")
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        if cd_id:
            cursor.execute("SELECT * FROM predictions WHERE clinicalDataId = ? ORDER BY createdAt DESC", (cd_id,))
        else:
            cursor.execute("SELECT * FROM predictions ORDER BY createdAt DESC")
        
        rows = cursor.fetchall()
        cursor.close()
        conn.close()
        
        preds_list = []
        for row in rows:
            preds_list.append({
                "id": row[0],
                "clinicalDataId": row[1],
                "riskScore": row[2],
                "riskCategory": row[3],
                "modelUsed": row[4],
                "confidenceScore": row[5],
                "resultsSummary": row[6],
                "createdAt": row[7]
            })
        return jsonify(preds_list), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/patients/<patient_id>/report", methods=["GET"])
def get_patient_report(patient_id):
    """Returns the latest prediction for a given patient (joins patients -> clinical_data -> predictions)."""
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("""
            SELECT 
                p.id, p.firstName, p.lastName, p.dob, p.gender, p.contactNumber, p.doctorId, p.createdAt,
                cd.id as clinicalDataId,
                pr.id as predictionId, pr.riskScore, pr.riskCategory, pr.modelUsed, pr.confidenceScore, pr.resultsSummary, pr.createdAt as predCreatedAt
            FROM patients p
            LEFT JOIN clinical_data cd ON cd.patientId = p.id
            LEFT JOIN predictions pr ON pr.clinicalDataId = cd.id
            WHERE p.id = ?
            ORDER BY pr.createdAt DESC
            LIMIT 1
        """, (patient_id,))
        row = cursor.fetchone()
        cursor.close()
        conn.close()
        
        if not row:
            return jsonify({"error": "Patient not found"}), 404
        
        result = {
            "patient": {
                "id": row[0], "firstName": row[1], "lastName": row[2],
                "dob": row[3], "gender": row[4], "contactNumber": row[5],
                "doctorId": row[6], "createdAt": row[7]
            },
            "prediction": {
                "id": row[9], "clinicalDataId": row[8],
                "riskScore": row[10], "riskCategory": row[11],
                "modelUsed": row[12], "confidenceScore": row[13],
                "resultsSummary": row[14], "createdAt": row[15]
            } if row[9] else None
        }
        return jsonify(result), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/api/patients-with-risk", methods=["GET"])
def patients_with_risk():
    doc_id = request.args.get("doctorId")
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        query = '''
            SELECT p.*, pr.riskCategory 
            FROM patients p
            LEFT JOIN (
                SELECT clinical_data.patientId, predictions.riskCategory
                FROM predictions
                JOIN clinical_data ON predictions.clinicalDataId = clinical_data.id
                WHERE (predictions.clinicalDataId, predictions.createdAt) IN (
                    SELECT clinicalDataId, MAX(createdAt)
                    FROM predictions
                    GROUP BY clinicalDataId
                )
            ) pr ON p.id = pr.patientId
        '''
        if doc_id:
            query += " WHERE p.doctorId = ?"
            cursor.execute(query, (doc_id,))
        else:
            return jsonify([]), 200
            
        rows = cursor.fetchall()
        cursor.close()
        conn.close()
        
        patients_list = []
        for row in rows:
            patients_list.append({
                "id": row[0],
                "firstName": row[1],
                "lastName": row[2],
                "dob": row[3],
                "gender": row[4],
                "contactNumber": row[5],
                "doctorId": row[6],
                "createdAt": row[7],
                "riskStatus": row[8] or "Pending"
            })
        return jsonify(patients_list), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/register", methods=["POST"])
def register():
    # Accept both JSON (from App) and Form Data (from HTML)
    data = request.get_json(silent=True)
    if data:
        name = data.get("name")
        email = data.get("email")
        password = data.get("password")
    else:
        name = request.form.get("name")
        email = request.form.get("email")
        password = request.form.get("password")

    print(f"Received register data: {name}, {email}")
    
    if not name or not email or not password:
        return jsonify({"error": "Missing fields"}), 400
        
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("INSERT INTO users (name, email, password) VALUES (?, ?, ?)", (name, email, password))
        doc_id = cursor.lastrowid
        conn.commit()
        cursor.close()
        conn.close()
        sync_to_csv()
        return jsonify({"message": "Registration successful", "name": name, "doctorId": str(doc_id)}), 201
    except sqlite3.IntegrityError:
        return jsonify({"error": "Email already exists"}), 400
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/login", methods=["POST"])
def login():
    # Accept both JSON (from App) and Form Data (from HTML)
    data = request.get_json(silent=True)
    if data:
        identifier = data.get("email") or data.get("username")
        password = data.get("password")
    else:
        identifier = request.form.get("username")
        password = request.form.get("password")
    
    print(f"DEBUG: Login request received: {identifier}")
    if not identifier or not password:
        return jsonify({"error": "Missing fields"}), 400
        
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT id, name FROM users WHERE email = ? AND password = ?", (identifier, password))
        user = cursor.fetchone()
        cursor.close()
        conn.close()
        
        if user:
            print(f"DEBUG: Login successful for {identifier}")
            # Ensure compatibility with both Website structure (which uses user[0])
            # and App strict matching.
            return jsonify({"message": "Login successful", "doctorId": str(user[0]), "name": user[1]}), 200
        else:
            print(f"DEBUG: Login failed - user not found in database: {identifier}")
            return jsonify({"error": "Invalid email or password"}), 401
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/predict", methods=["POST"])
def predict():
    if model is None:
        return "Model not loaded", 500
    try:
        if request.is_json:
            data = request.json
            age = int(data["age"])
            smoking = int(data["smoking"])
            diabetes = int(data["diabetes"])
        else:
            age = int(request.form["age"])
            smoking = int(request.form["smoking"])
            diabetes = int(request.form["diabetes"])

        prediction = model.predict([[age, smoking, diabetes]])
        result = "High Risk" if prediction[0] == 1 else "Low Risk"

        if request.is_json:
            return jsonify({"prediction": result})
        return render_template("index.html", prediction_text=result)
    except Exception as e:
        return f"Error during prediction: {str(e)}"

@app.route("/api/send-otp", methods=["POST"])
def send_otp():
    data = request.json
    email = data.get("email")
    if not email:
        return jsonify({"error": "Email required"}), 400
    
    import random
    import time
    import smtplib
    from email.mime.text import MIMEText

    otp = "".join([str(random.randint(0, 9)) for _ in range(6)])
    created_at = int(time.time() * 1000)
    
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        
        cursor.execute("INSERT INTO otps (email, otp, createdAt) VALUES (?, ?, ?)", (email, otp, created_at))
        conn.commit()
        cursor.close()
        conn.close()
        
        # Email Sending Logic (Placeholder)
        print(f"\n\n{'='*20}")
        print(f"OTP FOR {email}: {otp}")
        print(f"{'='*20}\n\n")

        return jsonify({"message": "OTP sent successfully"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/verify-otp", methods=["POST"])
def verify_otp():
    data = request.json
    email = data.get("email")
    otp = data.get("otp")
    
    if not email or not otp:
        return jsonify({"error": "Email and OTP required"}), 400
        
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        # Get latest OTP for this email
        cursor.execute("SELECT otp FROM otps WHERE email = ? ORDER BY createdAt DESC LIMIT 1", (email,))
        row = cursor.fetchone()
        cursor.close()
        conn.close()
        
        if row and row[0] == otp:
            return jsonify({"message": "OTP verified"}), 200
        else:
            return jsonify({"error": "Invalid OTP"}), 401
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/api/reset-password", methods=["POST"])
def reset_password_api():
    data = request.json
    email = data.get("email")
    new_password = data.get("password")
    
    if not email or not new_password:
        return jsonify({"error": "Email and password required"}), 400
        
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("UPDATE users SET password = ? WHERE email = ?", (new_password, email))
        if cursor.rowcount == 0:
            cursor.close()
            conn.close()
            return jsonify({"error": "Failed to update password. User not found"}), 404
        conn.commit()
        cursor.close()
        conn.close()
        sync_to_csv()
        return jsonify({"message": "Password reset successful"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# -----------------------------
# SILENCE TRACKING & ERROR LOGS (FIX FOR 404)
# -----------------------------
@app.route('/hybridaction/<path:path>', methods=['GET', 'POST'])
def silence_tracker(path):
    return jsonify({"status": "ok"}), 200

@app.route('/favicon.ico')
def favicon():
    return "", 204

@app.route('/api/check-ip')
def check_ip():
    import socket
    hostname = socket.gethostname()
    ip_address = socket.gethostbyname(hostname)
    return jsonify({"ip": ip_address, "hostname": hostname})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=True)