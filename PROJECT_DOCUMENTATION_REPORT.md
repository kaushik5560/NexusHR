# NexusHR – AI-Enabled Enterprise HR & Workforce Intelligence Platform
### Comprehensive Project Documentation & Technical Architecture Report
**Zidio Development Internship – Java Full-Stack Domain (March 2026 Edition)**  
**Author:** Full Stack Engineering Intern  
**Date:** March 2026 | **Version:** 2.0 – Industry Production Edition  

---

## 1. Hero & Cover Information

- **Project Title:** NexusHR – AI-Enabled Enterprise HR & Workforce Intelligence Platform
- **Tagline:** Production-Grade Java Full-Stack Enterprise HR System with Real-Time Workforce Analytics & Autonomous Talent Intelligence
- **Domain:** Java Full-Stack Web Development, Cloud-Native Microservices, AI/ML Talent Scoring
- **Monorepo Structure:** `nexus-backend` (Spring Boot 4 / Java 21) & `nexus-frontend` (React 19 / TypeScript / Tailwind CSS v4)

---

## 2. Executive Project Overview

### 2.1 Vision & Objectives
Traditional corporate Human Resource management relies heavily on disconnected spreadsheets, manual attendance registers, disjointed payroll calculations, and reactive employee retention strategies. Such legacy setups suffer from high error rates, statutory compliance risks, and an inability to detect employee disengagement before resignation letters arrive.

**NexusHR** resolves these challenges by delivering an integrated, autonomous, role-governed platform covering the complete employee lifecycle—from digital onboarding and daily biometric simulation to automated tax-deductible payroll, 360-degree performance calibrations, and AI-powered flight risk forecasting.

### 2.2 Target Users & Real-World Use Cases
1. **Employees (`ROLE_EMPLOYEE`)**:
   - Biometric check-in/out simulation with automated shift duration logging.
   - Self-service leave portal: apply for time-off with status tracking.
   - Monthly itemized payslip breakdown (CTC, HRA, Provident Fund, TDS Tax).
   - 360° performance scorecards with manager qualitative feedback.
   - AI Talent Radar: personalized 90-day upskilling roadmap toward target career horizons.
2. **HR Managers & Leads (`ROLE_HR`, `ROLE_MANAGER`)**:
   - Centralized workforce directory with PostgreSQL 17 relational synchronization.
   - Real-time leave approval queue with 1-click Approve/Reject and reviewer remarks.
   - NexusAI Attrition Radar: proactive flight risk scores with automated retention action plans.
   - Executive workforce analytics: monthly salary outflow, headcount distribution, retention health.
   - Multi-channel broadcast hub: dispatch announcements across Email & SMS gateways.
3. **Enterprise Administrators (`ROLE_ADMIN`)**:
   - Role-Based Access Control (RBAC) governance.
   - Employee lifecycle provisioning and offboarding.
   - System audit logs and compliance reporting.

### 2.3 Non-Functional Engineering Goals & SLAs
- **API Latency:** `< 300 ms` for 95th percentile transactional requests.
- **Dashboard Load Times:** `< 2 s` cold load; instantaneous SPA navigation.
- **Throughput:** Engineered to handle `10,000+` concurrent users during month-end payroll cycles.
- **Availability Target:** `99.95%` uptime SLA with Kubernetes Horizontal Pod Autoscaling (HPA).
- **Security:** Strict OWASP Top 10 compliance, Argon2 password hashing, stateless 24-hour JWT tokens.

---

## 3. Core Functional Capabilities

| ID | Capability | Business Value & Detailed Description | Acceptance Criteria & Status |
| :---: | :--- | :--- | :---: |
| **F-01** | **Employee Lifecycle Management** | Secure registration, profile management, role assignment, and offboarding. | Complete workflow with RBAC security; verified. |
| **F-02** | **Attendance & Biometric Simulation** | Simulated hardware biometric clock; auto-detects `LATE` arrivals and calculates work hours. | Real-time status sync with HikariCP; verified. |
| **F-03** | **Automated Payroll Engine** | Itemized CTC math: Basic (50%), HRA (40%), Special Allowance, statutory PF (12%), and TDS tax deductions. | Verified itemized payslips with unique references. |
| **F-04** | **360° Performance Management** | 4-Pillars evaluation: Technical, Delivery, Communication, Leadership with manager feedback. | Calibrated 5-star scoring and qualitative notes; verified. |
| **F-05** | **AI Workforce Intelligence** | Triangulates biometric irregularity, leave frequency, and poaching risks to score attrition probability (0–100%). | Real-time predictive radar with retention plans; verified. |
| **F-06** | **AI Career Pathway & Skill Gap** | Maps verified skills against target roles (e.g. Solutions Architect) and generates a 90-day roadmap. | Personalized skill gap analysis; verified. |
| **F-07** | **Executive Analytics & PDF Reporting** | Real-time salary outflow, department distribution, retention health index, and 1-click print PDF export. | Print-optimized executive dashboard; verified. |
| **F-08** | **Multi-Channel Communications Hub** | Bell icon notifications with unread badge counter, plus broadcast dispatcher for Email & SMS gateways. | Real-time polling and carrier delivery ledger; verified. |

---

## 4. Production Technology Stack

| Architecture Layer | Chosen Technology | Version | Engineering Rationale |
| :--- | :--- | :---: | :--- |
| **Backend Framework** | Java / Spring Boot | 21 LTS / 4.1.1 | Enterprise microservices standard, virtual threads, high concurrency. |
| **Frontend Framework** | React / TypeScript / Vite | 19.0 / 5.6 / 6.0 | Modern functional React, strict type safety, sub-second HMR builds. |
| **Styling & Theme** | Tailwind CSS / Lucide Icons | v4.0 | Utility-first CSS, dark/light theme engine, responsive layout. |
| **Database** | PostgreSQL | 17.11 | ACID compliance, JSONB extensibility, robust foreign key constraints. |
| **Security & Auth** | Spring Security / JJWT / Argon2 | 6.x / 0.12.6 | Memory-hard password hashing, stateless Bearer token authorization. |
| **Containerization** | Docker & Docker Compose | Multi-Stage | Minimal Alpine JRE/Nginx runtime images with non-root security users. |
| **Orchestration** | Kubernetes & Helm | v1.28+ | Declarative deployments, HPA autoscaling targeting 70% CPU utilization. |
| **CI/CD** | GitHub Actions | v4 | Automated multi-job pipeline for unit testing and container build checks. |
| **Observability** | Prometheus | Latest | Pull-based metrics scraping via Spring Boot Actuator endpoints. |
| **Load Testing** | K6 Load Testing Tool | Latest | High-concurrency performance validation under heavy virtual user traffic. |

---

## 5. System Architecture & Component Design

```text
+-----------------------------------------------------------------------------------+
|                                 CLIENT TIER                                       |
|  React 19 + TypeScript Single Page Application (SPA)                              |
|  - AuthContext (JWT persistence, 24h expiration handling)                         |
|  - ThemeContext (Dark/Light mode persistence via localStorage)                    |
|  - NotificationDropdown (10s real-time unread badge polling)                      |
|  - Employee Portal (/dashboard) & Managerial Console (/manager-dashboard)        |
+------------------------------------------+----------------------------------------+
                                           | HTTPS / JSON
                                           v
+-----------------------------------------------------------------------------------+
|                         INGRESS & SECURITY GATEWAY                                |
|  Nginx Ingress Controller (Kubernetes) / Reverse Proxy                            |
|  - SSL Termination, Gzip compression, SPA fallback routing                       |
|  - OWASP Top 10 header policies, CORS validation, rate-limiting                   |
+------------------------------------------+----------------------------------------+
                                           | HTTP (Port 8080)
                                           v
+-----------------------------------------------------------------------------------+
|                       APPLICATION SERVICES TIER (Spring Boot 4)                   |
|  +-----------------------------------------------------------------------------+  |
|  | AuthTokenFilter -> UserDetailsServiceImpl -> SecurityConfig (Argon2)         |  |
|  +-----------------------------------------------------------------------------+  |
|  | REST Controllers:                                                           |  |
|  |   - AuthController (/api/auth)                                              |  |
|  |   - EmployeeController (/api/employees)                                     |  |
|  |   - AttendanceController (/api/attendance)                                   |  |
|  |   - LeaveController (/api/leaves)                                           |  |
|  |   - PayrollController (/api/payroll)                                         |  |
|  |   - PerformanceReviewController (/api/reviews)                              |  |
|  |   - AiIntelligenceController (/api/ai/attrition, /api/ai/skills)             |  |
|  |   - AnalyticsController (/api/analytics)                                    |  |
|  |   - CommunicationController (/api/communications)                           |  |
|  |   - NotificationController (/api/notifications)                             |  |
|  +-----------------------------------------------------------------------------+  |
|  | Business Services & AI Logic:                                               |  |
|  |   - WorkforceAiService: Multi-factor attrition & flight risk calculator     |  |
|  |   - SkillIntelligenceService: Competency gap & 90-day learning roadmap       |  |
|  |   - AnalyticsService: Real-time company KPIs & salary aggregation           |  |
|  |   - CommunicationService: Multi-channel broadcast dispatcher (Email & SMS)  |  |
|  |   - NotificationService: Internal alerts & unread ledger                     |  |
|  +-----------------------------------------------------------------------------+  |
+------------------------------------------+----------------------------------------+
                                           | HikariCP JDBC Pool
                                           v
+-----------------------------------------------------------------------------------+
|                           DATABASE TIER (PostgreSQL 17)                           |
|  Relational Tables: employees, roles, employee_roles, attendances,                |
|  leave_requests, payrolls, performance_reviews, notifications, dispatch_logs       |
+-----------------------------------------------------------------------------------+
```

---

## 6. Detailed 28-Days Execution Timeline

- **Week 1 (Days 1–7): Core Backend & Authentication Foundation**
  - Initialized Spring Boot monorepo with PostgreSQL 17 via HikariCP.
  - Implemented Argon2 password encoder, JJWT 0.12.6, and `@PreAuthorize` RBAC.
  - Developed Employee CRUD, Attendance Biometrics, and Leave Approval workflow.
  - *Checkpoint Audit: All 15 REST endpoints passed via Postman (`v0.1.0-week1`).*

- **Week 2 (Days 8–14): Payroll, Performance & Frontend Foundation**
  - Bootstrapped React 19 + TypeScript + Vite + Tailwind CSS v4.
  - Engineered centralized Axios interceptors for automatic Bearer token injection.
  - Developed Payroll calculation engine (CTC, HRA, PF 12%, TDS).
  - Developed 360° Review scorecard module with 4-pillar evaluation.
  - Built Employee Self-Service Dashboard & Managerial Overview Console.
  - *Checkpoint Audit: Full-stack integration verified across dual portals (`v0.2.0-week2`).*

- **Week 3 (Days 15–21): AI Workforce Intelligence & Real-Time Features**
  - Created NexusAI Attrition Engine: correlated biometrics, leaves, and headhunter poaching risk.
  - Created AI Career Pathway Engine: verified skills, identified gaps, and 90-day roadmaps.
  - Built Real-Time Notification System with animated pulse badge and interactive popover.
  - Implemented Executive Workforce Analytics with visual charts and 1-click PDF export.
  - Built Multi-Channel Broadcast Hub supporting simulated SMTP Email and Telecom SMS.
  - Implemented full-stack Light / Dark Mode theme engine with persistent state.
  - *Checkpoint Audit: AI radar and notifications verified operational (`v0.3.0-week3`).*

- **Week 4 (Days 22–28): Deployment, Monitoring & Production Polish**
  - Multi-stage Docker containerization for backend (Alpine JRE 21) and frontend (Nginx Alpine).
  - Kubernetes manifests with HorizontalPodAutoscaler (HPA) targeting 70% CPU utilization.
  - GitHub Actions CI/CD automated pipeline for build, test, and container validation.
  - Prometheus observability metrics integration via Spring Boot Actuator.
  - K6 high-concurrency load testing verifying `<300ms` SLA under 500+ virtual users.
  - Comprehensive technical documentation, README, and final release packaging (`v1.0.0-final`).

---

## 7. Security, Reliability & OWASP Mitigations

1. **Password Storage (Argon2id)**:
   Passwords are hashed using Argon2 (`Argon2PasswordEncoder`), the winner of the Password Hashing Competition. It resists GPU/ASIC brute-force attacks via configurable memory, time, and parallelism parameters.
2. **Stateless Authorization (JJWT 0.12.6)**:
   JWTs are cryptographically signed with HMAC-SHA256. Tokens are transmitted in `Authorization: Bearer <token>` headers and expire after 24 hours.
3. **Role-Based Access Control (RBAC)**:
   Endpoints enforce strict `@PreAuthorize` expressions. For example, `/api/ai/attrition/overview` requires `ROLE_HR` or `ROLE_MANAGER`. Unauthorized attempts yield an immediate HTTP `403 Forbidden`.
4. **Least-Privilege Container Security**:
   The production Docker runtime container does not run as `root`. A dedicated unprivileged system user `nexushr:nexushr` executes the JVM process.
5. **Database Parameterization & SQL Injection Prevention**:
   All database access is handled via Spring Data JPA and Hibernate prepared statements, eliminating SQL injection vulnerabilities.

---

## 8. Deployment & Cloud Operations

### 8.1 Docker Compose Deployment (1-Command Run)
The entire multi-tier stack can be launched locally with:
```bash
docker compose up --build -d
```
- **Frontend Web UI:** `http://localhost:80` (or `http://localhost:5173`)
- **Backend API Gateway:** `http://localhost:8080/api`
- **PostgreSQL Database:** `localhost:5432`

### 8.2 Kubernetes Production Deployment
Manifests located in `/k8s` deploy cleanly to any standard cluster (EKS, GKE, Minikube, K3s):
```bash
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-configmap-secrets.yaml
kubectl apply -f k8s/02-postgres.yaml
kubectl apply -f k8s/03-backend.yaml
kubectl apply -f k8s/04-frontend.yaml
kubectl apply -f k8s/05-hpa.yaml
kubectl apply -f k8s/06-ingress.yaml
```

---

## 9. Evaluation Test Credentials

| Account Role | Employee Name | Email Address | Password | Capabilities |
| :--- | :--- | :--- | :--- | :--- |
| **Employee** | Rahul Sharma | `rahul@nexus.com` | `Password123` | Self-Service Portal, Attendance Check-In/Out, Apply Leaves, Payslips, AI Career Pathway |
| **HR Lead / Manager** | Priya Singh | `priya@nexus.com` | `Password123` | Manager Console, Leave Approvals, AI Attrition Radar, Workforce Analytics, Broadcast Hub |

---

## 10. Personal Reflection & Key Learnings

- **Transitioning from MERN to Java Full-Stack:** Transitioning from JavaScript/Node.js to Java 21 and Spring Boot deepened my appreciation for strong type safety, dependency injection, and compiled enterprise architectures.
- **Enterprise Security Mindset:** Implementing Argon2, Spring Security filters, and fine-grained RBAC reinforced the importance of defense-in-depth across every API endpoint.
- **Autonomous AI Engineering:** Designing the predictive attrition engine demonstrated how raw relational data (biometric time stamps, leave trends, scorecards) can be correlated into actionable business intelligence.
- **DevOps Maturity:** Writing multi-stage Dockerfiles and Kubernetes HPA manifests bridged the gap between code on `localhost` and scalable cloud-native infrastructure.

---
*NexusHR – Engineered for Zidio Development • March 2026 Edition.*
