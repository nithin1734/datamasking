<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home - DataMasking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/home.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar">
        <div class="navbar-container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/home">
                <i class="fas fa-shield-alt"></i> DataMasking App
            </a>
            <div class="navbar-nav">
                <a href="${pageContext.request.contextPath}/upload" class="nav-link">
                    <i class="fas fa-upload"></i> Upload Files
                </a>
                <form action="${pageContext.request.contextPath}/logout" method="post" class="logout-form">
                    <button type="submit" class="btn-logout">
                        <i class="fas fa-sign-out-alt"></i> Logout
                    </button>
                </form>
            </div>
        </div>
    </nav>

    <div class="container">
        <c:choose>
            <c:when test="${not empty user}">
                <!-- Hero Section - New Dashboard-Style Layout -->
                <header class="hero-section">
                    <div class="hero-dashboard">
                        <div class="hero-welcome">
                            <h1>Welcome back, ${user.name}!</h1>
                            <p>Ready to secure your data? Upload a file to get started.</p>
                            <a href="${pageContext.request.contextPath}/upload" class="btn-primary">
                                <i class="fas fa-upload"></i> Go to Upload
                            </a>
                        </div>
                        <div class="hero-profile">
                            <h4><i class="fas fa-user-circle"></i> Your Profile</h4>
                            <div class="profile-grid">
                                <div class="profile-item">
                                    <i class="fas fa-user"></i>
                                    <span>${user.name}</span>
                                </div>
                                <div class="profile-item">
                                    <i class="fas fa-envelope"></i>
                                    <span>${user.email}</span>
                                </div>
                                <div class="profile-item">
                                    <i class="fas fa-phone"></i>
                                    <span>${user.mobileNumber}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </header>

                <!-- Main Content - New Card-Based Dashboard Layout -->
                <main class="dashboard-main">
                    <!-- Features Grid -->
                    <section class="features-grid">
                        <div class="section-header">
                            <h3><i class="fas fa-cogs"></i> Key Features</h3>
                        </div>
                        <div class="features-container">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-file-upload"></i>
                                </div>
                                <h4>Versatile File Upload</h4>
                                <p>Supports CSV, Excel, JSON, and TXT files.</p>
                            </div>
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-eye-slash"></i>
                                </div>
                                <h4>Smart Masking</h4>
                                <p>Auto-detects sensitive data for quick configuration.</p>
                            </div>
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-shield-alt"></i>
                                </div>
                                <h4>Secure Processing</h4>
                                <p>Advanced algorithms to ensure data privacy.</p>
                            </div>
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-download"></i>
                                </div>
                                <h4>Instant Download</h4>
                                <p>Access your masked files immediately after processing.</p>
                            </div>
                        </div>
                    </section>

                    <!-- Masking Techniques - New Compact List Layout -->
                    <section class="techniques-section content-card">
                        <div class="section-header">
                            <h3><i class="fas fa-magic"></i> Masking Techniques</h3>
                        </div>
                        <div class="techniques-list">
                            <div class="technique-item">
                                <i class="fas fa-asterisk"></i>
                                <h4>Full Mask</h4>
                                <p>"John Doe" → "********"</p>
                            </div>
                            <div class="technique-item">
                                <i class="fas fa-eye-slash"></i>
                                <h4>Partial Mask</h4>
                                <p>"john@email.com" → "j***@email.com"</p>
                            </div>
                            <div class="technique-item">
                                <i class="fas fa-random"></i>
                                <h4>Random Replace</h4>
                                <p>"John Doe" → "Amit Sharma"</p>
                            </div>
                            <div class="technique-item">
                                <i class="fas fa-lock"></i>
                                <h4>Hash Mask</h4>
                                <p>"data" → "HASH_A1B2C3..."</p>
                            </div>
                            <div class="technique-item">
                                <i class="fas fa-calendar"></i>
                                <h4>Date Shift</h4>
                                <p>"2023-01-15" → "2023-06-22"</p>
                            </div>
                        </div>
                    </section>
                </main>
            </c:when>
            <c:otherwise>
                <div class="error-message">
                    <i class="fas fa-exclamation-triangle"></i> 
                    Could not load user data. Please log in again.
                    <a href="${pageContext.request.contextPath}/login" class="btn-primary">Login</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>