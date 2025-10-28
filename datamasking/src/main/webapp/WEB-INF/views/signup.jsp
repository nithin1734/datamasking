<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up - DataMasking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/signup.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <div class="row">
            <div class="col-md-8 col-lg-6">
                <div class="signup-container">
                    <div class="signup-form">
                        <div class="signup-header">
                            <i class="fas fa-user-plus fa-3x"></i>
                            <h2>Create Account</h2>
                            <p>Join DataMasking Application</p>
                        </div>

                        <!-- Error Message -->
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-circle"></i>${errorMessage}
                                <button type="button" class="btn-close" onclick="this.parentElement.style.display='none'"></button>
                            </div>
                        </c:if>

                        <form:form action="/signup" method="post" modelAttribute="user" id="signupForm">
                            <div class="row">
                                <div class="col-md-12 mb-4">
                                    <label for="name" class="form-label">
                                        <i class="fas fa-user"></i>Full Name
                                    </label>
                                    <form:input path="name" class="form-control" id="name" required="true"/>
                                    <form:errors path="name" class="text-danger small"/>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-4">
                                    <label for="email" class="form-label">
                                        <i class="fas fa-envelope"></i>Email Address
                                    </label>
                                    <form:input path="email" type="email" class="form-control" id="email" required="true"/>
                                    <form:errors path="email" class="text-danger small"/>
                                </div>

                                <div class="col-md-6 mb-4">
                                    <label for="mobileNumber" class="form-label">
                                        <i class="fas fa-phone"></i>Mobile Number
                                    </label>
                                    <form:input path="mobileNumber" class="form-control" id="mobileNumber" required="true" pattern="[0-9]{10}" placeholder="10-digit mobile number"/>
                                    <form:errors path="mobileNumber" class="text-danger small"/>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-4">
                                    <label for="password" class="form-label">
                                        <i class="fas fa-lock"></i>Password
                                    </label>
                                    <form:password path="password" class="form-control" id="password" required="true"/>
                                    <div class="password-strength">
                                        <i class="fas fa-info-circle"></i>
                                        Min 8 chars, include uppercase, lowercase, number and special character
                                    </div>
                                    <form:errors path="password" class="text-danger small"/>
                                </div>

                                <div class="col-md-6 mb-4">
                                    <label for="confirmPassword" class="form-label">
                                        <i class="fas fa-lock"></i>Confirm Password
                                    </label>
                                    <form:password path="confirmPassword" class="form-control" id="confirmPassword" required="true"/>
                                    <div class="password-match text-danger small" id="passwordMatch" style="display: none;">
                                        <i class="fas fa-times"></i>Passwords do not match
                                    </div>
                                    <form:errors path="confirmPassword" class="text-danger small"/>
                                </div>
                            </div>

                            <div class="d-grid mb-3">
                                <button type="submit" class="btn btn-signup" id="submitBtn">
                                    <i class="fas fa-user-plus"></i>Create Account
                                </button>
                            </div>

                            <div class="text-center">
                                <span class="text-muted">Already have an account?</span>
                                <a href="/login" class="text-decoration-none ms-1">
                                    <i class="fas fa-sign-in-alt"></i>Sign In
                                </a>
                            </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Password match validation
        function checkPasswordMatch() {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const matchDiv = document.getElementById('passwordMatch');
            const submitBtn = document.getElementById('submitBtn');
            
            if (password !== '' && confirmPassword !== '') {
                if (password !== confirmPassword) {
                    matchDiv.style.display = 'block';
                    submitBtn.disabled = true;
                } else {
                    matchDiv.style.display = 'none';
                    submitBtn.disabled = false;
                }
            }
        }
        
        document.getElementById('password').addEventListener('keyup', checkPasswordMatch);
        document.getElementById('confirmPassword').addEventListener('keyup', checkPasswordMatch);
        
        // Mobile number validation
        document.getElementById('mobileNumber').addEventListener('input', function(e) {
            this.value = this.value.replace(/[^0-9]/g, '').slice(0, 10);
        });
    </script>
</body>
</html>