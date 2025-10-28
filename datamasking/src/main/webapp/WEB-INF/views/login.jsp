<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - DataMasking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <div class="row">
            <div class="col-md-6 col-lg-4">
                <div class="login-container">
                    <div class="login-form">
                        <div class="login-header">
                            <i class="fas fa-shield-alt fa-3x"></i>
                            <h2>DataMasking</h2>
                            <p>Sign in to your account</p>
                        </div>

                        <!-- Success Message -->
                        <c:if test="${not empty successMessage}">
                            <div class="alert alert-success" role="alert">
                                <i class="fas fa-check-circle"></i>${successMessage}
                                <button type="button" class="btn-close" onclick="this.parentElement.style.display='none'"></button>
                            </div>
                        </c:if>

                        <!-- Error Message -->
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-circle"></i>${errorMessage}
                                <button type="button" class="btn-close" onclick="this.parentElement.style.display='none'"></button>
                            </div>
                        </c:if>

                        <form action="/login" method="post">
                            <div class="mb-4">
                                <label for="email" class="form-label">
                                    <i class="fas fa-envelope"></i>Email Address
                                </label>
                                <input type="email" class="form-control" id="email" name="email" required>
                            </div>

                            <div class="mb-4">
                                <label for="password" class="form-label">
                                    <i class="fas fa-lock"></i>Password
                                </label>
                                <input type="password" class="form-control" id="password" name="password" required>
                            </div>

                            <div class="d-grid mb-3">
                                <button type="submit" class="btn btn-login">
                                    <i class="fas fa-sign-in-alt"></i>Sign In
                                </button>
                            </div>

                            <div class="text-center">
                                <a href="/forgot-password" class="text-decoration-none">
                                    <i class="fas fa-key"></i>Forgot Password?
                                </a>
                            </div>

                            <hr class="my-4">

                            <div class="text-center">
                                <span class="text-muted">Don't have an account?</span>
                                <a href="/signup" class="text-decoration-none ms-1">
                                    <i class="fas fa-user-plus"></i>Sign Up
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>