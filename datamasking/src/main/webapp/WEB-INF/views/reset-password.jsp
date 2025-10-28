<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - DataMasking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset-password.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <div class="row">
            <div class="col-md-6 col-lg-5">
                <div class="form-container">
                    <div class="form-content">
                        <div class="form-header">
                            <i class="fas fa-shield-alt fa-3x"></i>
                            <h2>Reset Your Password</h2>
                            <p>Choose a new, strong password</p>
                        </div>

                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-circle"></i>${errorMessage}
                                <button type="button" class="btn-close" onclick="this.parentElement.style.display='none'"></button>
                            </div>
                        </c:if>

                        <form action="/reset-password" method="post">
                            <input type="hidden" name="token" value="${token}" />

                            <div class="mb-4">
                                <label for="password" class="form-label">
                                    <i class="fas fa-lock"></i>New Password
                                </label>
                                <input type="password" class="form-control" id="password" name="password" required>
                                <div class="form-text">
                                    Min 8 chars, with uppercase, lowercase, number and special character.
                                </div>
                            </div>
                            
                            <div class="mb-4">
                                <label for="confirmPassword" class="form-label">
                                    <i class="fas fa-lock"></i>Confirm New Password
                                </label>
                                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                            </div>

                            <div class="d-grid mb-3">
                                <button type="submit" class="btn btn-submit">
                                    <i class="fas fa-save"></i>Reset Password
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>