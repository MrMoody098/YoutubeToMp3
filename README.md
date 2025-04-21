# YouTube Download API and JavaFX Frontend

This project provides an API built using **Spring Boot** to interact with **yt-dlp** for downloading YouTube videos, and a **JavaFX**-based frontend to allow users to interact with the backend through a user-friendly interface.

## Table of Contents
- [Overview](#overview)
- [Disclaimer](#disclaimer)
- [Installation](#installation)
  - [Backend (Spring Boot API)](#backend-spring-boot-api)
  - [Frontend (JavaFX)](#frontend-javafx)
- [Usage](#usage)
  - [API](#api)
  - [Frontend](#frontend)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Overview

The project consists of two main components:

1. **Backend (Spring Boot API)**  
   - A REST API to download videos from YouTube using the yt-dlp command-line tool.  
   - Supports single‑video MP3 conversion and playlist ZIP download endpoints.  

2. **Frontend (JavaFX)**  
   - A desktop application built with JavaFX.  
   - Provides an interface for users to input YouTube URLs, toggle between single‑video and playlist modes, and track download progress.

## Disclaimer

This project is provided **for educational purposes only**.  
The author is **not responsible** for any unauthorized or copyrighted content downloaded using this software.  
Use at your own risk and ensure you comply with YouTube’s Terms of Service and your local copyright laws.

## Installation

### Backend (Spring Boot API)

1. **Clone the repository**  
   ```bash
   git clone https://github.com/yourusername/yt-dlp-api.git
   cd yt-dlp-api
   ```
2. **Prerequisites**

-Node
-Java 17 or later
-Maven (or use the included ./mvnw)
-yt-dlp installed and on your PATH
-ffmpeg (which provides ffprobe) installed and on your PATH

3. **Build and run**
  ```bash
  ./npm install
  ./mvnw clean package
  ./mvnw spring-boot:run
  ./npm start
  ```
The API will start on http://localhost:8080. along with the front end react application on http://localhost:3000

