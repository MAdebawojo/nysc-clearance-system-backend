# Auto-Clearance API

## Project Ideator & Lead
**Adebawojo Mosopefoluwa O.**  
As the ideator of the Auto-Clearance system, I conceived the idea, designed the system architecture, and led the development of the backend. I was responsible for turning the vision into a functional, production-ready solution.

## Collaborator
**Ekemini Eduok**  
Worked on the frontend part of the application.

---

## Project Overview

Auto-Clearance is an automated system designed to streamline the Human Resources (HR) workflow for clearing National Youth Service Corps (NYSC) members (Corpers).

Each corper is required to present a monthly clearance document issued by their Place of Primary Assignment (PPA). Traditionally, this document is manually tailored for each corper using a predefined template, which can be repetitive, time-consuming, and prone to error.

Auto-Clearance eliminates this manual process by automating the generation and delivery of clearance documents based on each corper’s clearance request, saving time and improving efficiency.

---

## Problem Statement

NYSC mandates that all corpers present monthly clearance documents from their respective PPAs. Preparing these documents manually for multiple corpers involves repetitive editing and administrative overhead.

Auto-Clearance addresses this challenge by:
- Automating clearance requests
- Introducing an approval workflow
- Generating clearance documents programmatically once approvals are complete

---

## System Overview

The system supports four major user roles:

- **Global Admin** – Manages multiple PPAs and assigns Superadmins
- **Superadmin** – Oversees clearance approvals for a specific PPA
- **Admin (Unit Head)** – Manages units and performs first-level approval
- **Corper** – Requests clearance and receives the generated document

---

## System Architecture

- **Backend:** Java 21, Spring Boot 3
- **Frontend:** React
- **Database:** Stores PPAs, users, units, clearance requests, and related metadata

---

## Roles & Responsibilities

### 1. Global Admin
- Manages multiple PPAs
- Creates PPAs and assigns Superadmins
- A Global Admin is seeded on first application startup
- On subsequent restarts, the system checks for an existing Global Admin to prevent duplication

### 2. Superadmin (PPA HR)
- Each PPA has one Superadmin
- Manages units and unit heads (Admins)
- Performs final approval or rejection of clearance requests

### 3. Admin (Unit Head)
- Manages a unit within a PPA
- Approves or rejects clearance requests for corpers within their unit

### 4. Corper (NYSC Member)
- Submits clearance requests
- Receives clearance documents after full approval

---

## Workflow

1. Global Admin creates a PPA and assigns a Superadmin
2. Superadmin creates units, assigns unit heads (Admins), and registers corpers
3. A corper submits a clearance request (status: `PENDING`)
4. The unit head reviews the request:
    - If approved → status becomes `LEVEL_ONE`
    - If rejected → status becomes `REJECTED`
5. The Superadmin reviews requests with `LEVEL_ONE` status:
    - If approved → status becomes `CLEARED`
    - If rejected → status becomes `REJECTED`
6. Once approved at both levels, the clearance document is generated and delivered to the corper

---

## Clearance Request Statuses

- **PENDING** – Initial state after submission
- **LEVEL_ONE** – Approved by unit head
- **CLEARED** – Fully approved by Superadmin
- **REJECTED** – Rejected at any stage

---

## API Documentation

### Swagger UI
- Local:  
  `http://localhost:8080/swagger-ui/index.html`
- Deployed:  
  `https://nysc-clearance-system-backend-demo-test.onrender.com/swagger-ui/index.html`

---

## Future Improvements

- **Pagination:**  
  Implement API-level pagination to improve performance when handling large datasets.
---

## Setup Instructions

### Environment Variables
Review the `.env.example` file and provide values for all required environment variables.

---

### Database Configuration
1. Create a local database (e.g. `auto_clearance_db`)
2. Update the following variables in your environment:
    - `DB_USER`
    - `DB_PASSWORD`

---

### Email Configuration
The application requires a Google App Password for email functionality.

1. Generate an App Password:  
   https://myaccount.google.com/apppasswords

2. Video guide:  
   https://www.youtube.com/shorts/WDfvVRVV8Js

3. Set the following variables:
    - `EMAIL_USER` → Email address used to generate the App Password
    - `EMAIL_PASSWORD` → Generated App Password

---