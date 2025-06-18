# 🚀 ESI 2CS IRIAD Projects

<div align="center">
    <img src="https://img.shields.io/badge/Python-3.8%2B-blue?style=for-the-badge&logo=python"/>
    <img src="https://img.shields.io/badge/JADE-4.6.0-green?style=for-the-badge"/>
    <img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge"/>
</div>

## 📋 Description

This repository houses two innovative projects developed as part of the ESI 2CS IRIAD curriculum:

### 🔍 Hybrid Clustering Analysis
> *Advanced data clustering implementation combining K-Means and K-Medoids algorithms*

Implements a novel hybrid clustering algorithm inspired by the research paper *k‑MM: A Hybrid Clustering Algorithm Based on k‑Means and k‑Medoids*. The solution enhances clustering performance and robustness when analyzing retail customer data.

### 🎮 JADE Grid Game
> *Multi-agent game system built on JADE framework*

A sophisticated multi-agent trail race game leveraging the JADE (Java Agent DEvelopment Framework). Intelligent agents collect and trade colored tokens to navigate across a grid, employing negotiation strategies when faced with obstacles.

## 📁 Folder Structure

```
esi_2cs_iriad_projects/
├── hybrid_clustering/
│   ├── data/
│   │   └── retail_customers.csv       # Dataset with retail customer information
│   ├── code.ipynb                     # Main notebook with implementation and analysis
│   └── distributed_clustering/
│       └── distributed_clustering.py  # Hybrid algorithm implementation
│
└── jade_grid_game/
        ├── src/
        │   ├── agents/                    # Agent implementations
        │   └── StartJADE.java             # Entry point for application
        ├── lib/
        │   └── jade.jar                   # JADE framework library
        └── README.md                      # Game-specific documentation
```

> **Note:** `hybrid_clustering` (formerly *projects*) is now named to reflect the hybrid algorithm focus, while `jade_grid_game` organizes all Java source code under `src/` with the JADE library under `lib/`.

## 🚀 Getting Started

### Prerequisites

- Python 3.8+ with Jupyter notebooks
- Required Python packages:
    - `numpy`
    - `pandas`
    - `scikit-learn`
    - `matplotlib`
- Java JDK 8+
- JADE 4.6.0

### ⚙️ Installation

1. **Clone the repository:**

```bash
git clone https://github.com/<username>/esi_2cs_iriad_projects.git
cd esi_2cs_iriad_projects
```

2. **Hybrid Clustering:**

```bash
cd hybrid_clustering
pip install -r requirements.txt
jupyter notebook code.ipynb
```

3. **JADE Grid Game:**

```bash
cd ../jade_grid_game
# Compile and run (macOS/Linux):
javac -cp ".:lib/jade.jar" src/*.java
java -cp ".:lib/jade.jar:src" StartJADE
```

## 📊 Project Summaries

### 🔍 Hybrid Clustering
- **Dataset:** 10,000 retail customers with demographics, shopping behavior, and digital engagement metrics
- **Experiments:** Comparative analysis of K-Means vs K-Medoids performance and execution time
- **Hybrid Method:** Innovative approach combining K-Means speed with K-Medoids robustness

### 🎮 JADE Grid Game
- **Agents:** Each with unique starting position, goal, and colored token collection
- **Mechanics:** Strategic movement on colored tiles with token trading when blocked
- **Extensions:** Advanced negotiation logic, betrayal strategies, and GUI scoreboard

## 👥 Contributing

Contributions are welcome! Please feel free to open an issue or submit a pull request.

## 📄 License

This project is licensed under the MIT License. See the LICENSE file for details.