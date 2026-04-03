# Bone Predict AI Assessment

A professional clinical assessment tool for predicting bone resorption risk using AI and Machine Learning.

## Project Structure

This project follows a modern **Decoupled Architecture** to separate frontend presentation from backend logic.

*   **`frontend/`**: Contains the user interface (HTML/JS/CSS).
    *   `/src`: The HTML templates for assessment, dashboard, and auth.
    *   `/static`: Stylesheets and image assets.
*   **`backend/`**: A modular Flask API.
    *   `/routes`: Dedicated logic for Authentication and Patient Management.
    *   `/models`: Database initialization and Machine Learning model loading.
    *   `/db`: Local SQLite and CSV backups.

## Getting Started

### Prerequisites
- Python 3.8+
- MySQL (XAMPP default)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Narasingdharani/Bone_Predict_Website.git
   ```
2. Install dependencies:
   ```bash
   pip install -r backend/requirements.txt
   ```
3. Run the backend:
   ```bash
   python backend/app.py
   ```

## Technology Stack
- **Frontend**: HTML5, Vanilla JavaScript, CSS3
- **Backend**: Python Flask
- **Database**: MySQL / MariaDB
- **ML Model**: Random Forest (Joblib)

## License
MIT License
