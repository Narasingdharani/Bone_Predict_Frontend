from flask import Blueprint, request, jsonify
from ..models.database import get_db_connection, sync_patients_to_csv

patient_bp = Blueprint('patient', __name__)

@patient_bp.route("/api/patients", methods=["GET", "POST"])
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
            cursor.execute("USE bone_predict_db")
            cursor.execute('''
                INSERT INTO patients (id, firstName, lastName, dob, gender, contactNumber, doctorId, createdAt) 
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
            ''', (p_id, fname, lname, dob, gender, contact, doc_id, created_at))
            conn.commit()
            cursor.close()
            conn.close()
            sync_patients_to_csv()
            return jsonify(data), 201
        except Exception as e:
            return jsonify({"error": str(e)}), 500
    
    # GET Logic
    doc_id = request.args.get("doctorId")
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("USE bone_predict_db")
        cursor.execute("SELECT id, firstName, lastName, dob, gender, contactNumber, doctorId, createdAt FROM patients WHERE doctorId = %s", (doc_id,))
        rows = cursor.fetchall()
        cursor.close()
        conn.close()
        return jsonify([{
            "id": r[0], "firstName": r[1], "lastName": r[2], "dob": r[3],
            "gender": r[4], "contactNumber": r[5], "doctorId": r[6], "createdAt": r[7]
        } for r in rows]), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@patient_bp.route("/api/clinical-data", methods=["POST"])
def clinical_data():
    data = request.json
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("USE bone_predict_db")
        cursor.execute('''
            INSERT INTO clinical_data (
                id, patientId, weight, smokingStatus, alcoholConsumption, 
                hasDiabetes, hasHypertension, hasOsteoporosis, 
                probingDepth, cal, bleedingOnProbing, bleedingIndex, 
                plaqueIndex, toothMobility, gingivalPhenotype, 
                cbctImageUrl, createdAt
            ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
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
        return jsonify(data), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@patient_bp.route("/api/patients-with-risk", methods=["GET"])
def patients_with_risk():
    doc_id = request.args.get("doctorId")
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("USE bone_predict_db")
        query = '''
            SELECT p.*, (
                SELECT predictions.riskCategory
                FROM predictions
                JOIN clinical_data ON predictions.clinicalDataId = clinical_data.id
                WHERE clinical_data.patientId = p.id
                ORDER BY predictions.createdAt DESC
                LIMIT 1
            ) as riskCategory
            FROM patients p
            WHERE p.doctorId = %s
        '''
        cursor.execute(query, (doc_id,))
        rows = cursor.fetchall()
        cursor.close()
        conn.close()
        return jsonify([{
            "id": r[0], "firstName": r[1], "lastName": r[2], "dob": r[3],
            "gender": r[4], "contactNumber": r[5], "doctorId": r[6], "createdAt": r[7],
            "riskStatus": r[8] or "Pending"
        } for r in rows]), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500
