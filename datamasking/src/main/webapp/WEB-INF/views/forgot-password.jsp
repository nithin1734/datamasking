<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password - DataMasking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forgot-password.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <div class="row">
            <div class="col-md-6 col-lg-5">
                <div class="form-container">
                    <div class="form-content">
                        <div class="form-header">
                            <i class="fas fa-key fa-3x"></i>
                            <h2>Forgot Password</h2>
                            <p>Enter your email to receive a reset link</p>
                        </div>

                        <c:if test="${not empty successMessage}">
                            <div class="alert alert-success" role="alert">
                                <i class="fas fa-check-circle"></i>${successMessage}
                                <button type="button" class="btn-close" onclick="this.parentElement.style.display='none'"></button>
                            </div>
                        </c:if>

                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-circle"></i>${errorMessage}
                                <button type="button" class="btn-close" onclick="this.parentElement.style.display='none'"></button>
                            </div>
                        </c:if>

                        <form action="/forgot-password" method="post">
                            <div class="mb-4">
                                <label for="email" class="form-label">
                                    <i class="fas fa-envelope"></i>Email Address
                                </label>
                                <input type="email" class="form-control" id="email" name="email" required>
                            </div>

                            <div class="d-grid mb-3">
                                <button type="submit" class="btn btn-submit">
                                    <i class="fas fa-paper-plane"></i>Send Reset Link
                                </button>
                            </div>

                            <div class="text-center">
                                <a href="/login" class="text-decoration-none">
                                    <i class="fas fa-arrow-left"></i>Back to Login
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