import os
import joblib
from flask import Flask, render_template
from flask_cors import CORS
from .models.database import init_db
from .routes.auth_routes import auth_bp
from .routes.patient_routes import patient_bp

def create_app():
    # Configure Flask to find HTML in the new 'frontend/src' and CSS/JS in 'frontend/static'
    template_dir = os.path.abspath('../frontend/src')
    static_dir = os.path.abspath('../frontend/static')
    
    app = Flask(__name__, template_folder=template_dir, static_folder=static_dir, static_url_path='/static')
    CORS(app)
    
    # Initialize Database
    with app.app_context():
        init_db()
    
    # Load Machine Learning Model
    model_path = os.path.join(os.path.dirname(__file__), "models", "model.pkl")
    if os.path.exists(model_path):
        app.config['ML_MODEL'] = joblib.load(model_path)
    
    # Register Blueprints (The modular routes)
    app.register_blueprint(auth_bp)
    app.register_blueprint(patient_bp)
    
    # Root & Page Routes
    @app.route("/")
    def splash(): return render_template("splash.html")
    
    @app.route("/dashboard")
    def dashboard(): return render_template("dashboard.html")
    
    @app.route("/patients")
    def patients_page(): return render_template("patients.html")
    
    @app.route("/add-patient")
    def add_patient(): return render_template("add_patient.html")
    
    @app.route("/login")
    @app.route("/signin")
    def login(): return render_template("login.html")
    
    @app.route("/register")
    @app.route("/signup")
    def register(): return render_template("register.html")

    return app

if __name__ == "__main__":
    app = create_app()
    app.run(debug=True, host='0.0.0.0', port=8000)
