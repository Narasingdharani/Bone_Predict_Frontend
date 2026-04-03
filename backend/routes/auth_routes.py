import re
import random
import time
import smtplib
import traceback
from flask import Blueprint, request, jsonify
from email.mime.text import MIMEText
from ..models.database import get_db_connection, sync_to_csv

auth_bp = Blueprint('auth', __name__)

# SMTP Configuration (In a real app, use environment variables!)
SMTP_EMAIL = "narasingdharani25@gmail.com" 
SMTP_PASSWORD = "gldwjabrjgybeycz"

def validate_credentials(email, password, name=None):
    if name and not re.match(r"^[a-zA-Z\s\.]+$", name):
        return "Name must contain only alphabets"
    email_pattern = r"^[a-zA-Z0-9._%+-]+@gmail\.com$"
    if not re.match(email_pattern, email.strip()):
        return "Only @gmail.com email addresses are acceptable"
    if len(password) < 8:
        return "Password must be at least 8 characters long"
    if not password[0].isupper():
        return "First letter of password must be capital"
    if not re.search(r"[!@#$%^&*(),.?\":{}|<>]", password):
        return "Password must contain at least one special character"
    if not re.search(r"[0-9]", password):
        return "Password must contain at least one number"
    return None

@auth_bp.route("/register", methods=["POST"])
def register():
    # Multi-input support (JSON/Form)
    data = request.get_json(silent=True) or request.form
    name = data.get("name")
    email = data.get("email")
    password = data.get("password")
    
    if not name or not email or not password:
        return jsonify({"error": "Missing fields"}), 400
    
    validation_error = validate_credentials(email, password, name=name)
    if validation_error:
        return jsonify({"error": validation_error}), 400
        
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("USE bone_predict_db")
        cursor.execute("INSERT INTO users (name, email, password) VALUES (%s, %s, %s)", (name, email, password))
        doc_id = cursor.lastrowid
        conn.commit()
        cursor.close()
        conn.close()
        sync_to_csv()
        return jsonify({"message": "Registration successful", "name": name, "doctorId": str(doc_id)}), 201
    except Exception as e:
        return jsonify({"error": f"Registration error: {str(e)}"}), 500

@auth_bp.route("/login", methods=["POST"])
def login():
    data = request.get_json(silent=True) or request.form
    identifier = data.get("email") or data.get("username")
    password = data.get("password")
    
    if not identifier or not password:
        return jsonify({"error": "Missing fields"}), 400
    
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("USE bone_predict_db")
        cursor.execute("SELECT id, name FROM users WHERE email = %s AND password = %s", (identifier, password))
        user = cursor.fetchone()
        cursor.close()
        conn.close()
        if user:
            return jsonify({"message": "Login successful", "doctorId": str(user[0]), "name": user[1]}), 200
        return jsonify({"error": "Invalid email or password"}), 401
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@auth_bp.route("/api/send-otp", methods=["POST"])
def send_otp():
    data = request.json
    email = data.get("email")
    if not email:
        return jsonify({"error": "Email required"}), 400
    
    otp = "".join([str(random.randint(0, 9)) for _ in range(6)])
    created_at = int(time.time() * 1000)
    
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("USE bone_predict_db")
        cursor.execute("INSERT INTO otps (email, otp, createdAt) VALUES (%s, %s, %s)", (email, otp, created_at))
        conn.commit()
        
        # SMTP LOGIC
        try:
            msg = MIMEText(f"Your verification code for BonePredict is: {otp}")
            msg['Subject'] = 'BonePredict Verification Code'
            msg['From'] = SMTP_EMAIL
            msg['To'] = email
            with smtplib.SMTP('smtp.gmail.com', 587, timeout=15) as server:
                server.starttls()
                server.login(SMTP_EMAIL, SMTP_PASSWORD)
                server.send_message(msg)
        except:
            print(f"SMTP Fallback for {email}: {otp}")

        cursor.close()
        conn.close()
        return jsonify({"message": "OTP sent successfully"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
