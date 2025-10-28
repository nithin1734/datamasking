<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload Files - DataMasking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/upload.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar">
        <div class="navbar-container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/home">
                <i class="fas fa-shield-alt"></i> DataMasking App
            </a>
            <div class="navbar-nav">
                <a href="${pageContext.request.contextPath}/home" class="nav-link">
                    <i class="fas fa-home"></i> Home
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
        <header class="page-header">
            <h2><i class="fas fa-upload"></i> Upload & Manage Files</h2>
            <p>Select a file to begin the data masking process</p>
        </header>

        <!-- Messages -->
        <c:if test="${not empty successMessage or not empty errorMessage or not empty message or not empty error}">
            <div class="alert-container">
                <c:if test="${not empty successMessage}"><div class="alert alert-success"><i class="fas fa-check-circle"></i> ${successMessage}</div></c:if>
                <c:if test="${not empty errorMessage}"><div class="alert alert-error"><i class="fas fa-exclamation-triangle"></i> ${errorMessage}</div></c:if>
                <c:if test="${not empty message}"><div class="alert alert-success"><i class="fas fa-info-circle"></i> ${message}</div></c:if>
                <c:if test="${not empty error}"><div class="alert alert-error"><i class="fas fa-times-circle"></i> ${error}</div></c:if>
            </div>
        </c:if>
        
        <main class="main-grid">
            <!-- Upload Section -->
            <section class="upload-section content-card">
                <div class="card-header">
                    <h3><i class="fas fa-cloud-upload-alt"></i> Upload New File</h3>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/upload" method="post" 
                          enctype="multipart/form-data" id="uploadForm">
                        <div class="file-input-container">
                            <input type="file" name="file" id="fileInput" required 
                                   accept=".csv,.txt,.xlsx,.xls,.json"
                                   onchange="validateFile(this)">
                            <label for="fileInput" class="file-input-label">
                                <i class="fas fa-plus"></i>
                                <span>Choose File</span>
                                <div class="file-types">Max 10MB: CSV, TXT, XLSX, JSON</div>
                            </label>
                        </div>
                        <div class="selected-file" id="selectedFile" style="display: none;">
                            <div class="file-info">
                                <i class="fas fa-file-alt"></i>
                                <span class="file-name"></span>
                                <span class="file-size"></span>
                            </div>
                            <button type="button" class="btn-remove" onclick="removeFile()"><i class="fas fa-times"></i></button>
                        </div>
                        <div class="upload-actions">
                            <button type="submit" class="btn-upload" id="uploadBtn" disabled>
                                <i class="fas fa-upload"></i> Upload File
                            </button>
                        </div>
                    </form>
                </div>
            </section>

            <!-- Files List Section -->
            <section class="files-section content-card">
                <div class="card-header">
                    <h3><i class="fas fa-list"></i> Uploaded Files</h3>
                    <div class="files-count">
                        <c:choose>
                            <c:when test="${not empty files}">${files.size()} files</c:when>
                            <c:otherwise>0 files</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty files}">
                            <div class="files-table-container">
                                <table class="files-table">
                                    <thead>
                                        <tr>
                                            <th>File Name</th>
                                            <th>Size</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="file" items="${files}">
                                            <tr data-file-id="${file.id}">
                                                <td>
                                                    <div class="file-name-cell" title="${file.originalFileName}">
                                                        <i class="fas fa-file-alt"></i>
                                                        <span>${file.originalFileName}</span>
                                                    </div>
                                                </td>
                                                <td><fmt:formatNumber value="${file.size / 1048576}" maxFractionDigits="2" /> MB</td>
                                                <td>
                                                    <span class="status-badge status-${fn:toLowerCase(file.status)}">
                                                        <c:choose>
                                                            <c:when test="${file.status == 'UPLOADED'}"><i class="fas fa-clock"></i> Uploaded</c:when>
                                                            <c:when test="${file.status == 'PROCESSING'}"><i class="fas fa-spinner fa-spin"></i> Processing</c:when>
                                                            <c:when test="${file.status == 'COMPLETED'}"><i class="fas fa-check"></i> Completed</c:when>
                                                            <c:when test="${file.status == 'FAILED'}"><i class="fas fa-times"></i> Failed</c:when>
                                                            <c:otherwise>${file.status}</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </td>
                                                <td>
                                                    <div class="action-buttons">
                                                        <c:choose>
                                                            <c:when test="${file.status == 'UPLOADED'}">
                                                                <a href="${pageContext.request.contextPath}/select/${file.id}" class="btn-action btn-primary" title="Configure"><i class="fas fa-cog"></i></a>
                                                            </c:when>
                                                            <c:when test="${file.status == 'PROCESSING'}">
                                                                <span class="btn-action btn-disabled" title="Processing"><i class="fas fa-spinner fa-spin"></i></span>
                                                            </c:when>
                                                            <c:when test="${file.status == 'COMPLETED'}">
                                                                <a href="${pageContext.request.contextPath}/download/${file.id}" class="btn-action btn-success" title="Download"><i class="fas fa-download"></i></a>
                                                                <a href="${pageContext.request.contextPath}/select/${file.id}" class="btn-action btn-secondary" title="View"><i class="fas fa-eye"></i></a>
                                                            </c:when>
                                                            <c:when test="${file.status == 'FAILED'}">
                                                                <a href="${pageContext.request.contextPath}/select/${file.id}" class="btn-action btn-warning" title="Retry"><i class="fas fa-redo"></i></a>
                                                            </c:when>
                                                        </c:choose>
                                                        <button type="button" class="btn-action btn-danger" onclick="deleteFile(${file.id})" title="Delete"><i class="fas fa-trash"></i></button>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="fas fa-folder-open"></i>
                                <h4>No Files Uploaded</h4>
                                <p>Upload your first file to see it here.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </main>
    </div>

    <script>
        function validateFile(input) {
            const file = input.files[0];
            const selectedFileDiv = document.getElementById('selectedFile');
            const uploadBtn = document.getElementById('uploadBtn');
            if (!file) { removeFile(); return; }
            const maxSize = 10 * 1024 * 1024;
            if (file.size > maxSize) {
                alert('File size exceeds 10MB limit.');
                removeFile(); return;
            }
            const allowedTypes = ['.csv', '.txt', '.xlsx', '.xls', '.json'];
            const isValidType = allowedTypes.some(type => file.name.toLowerCase().endsWith(type));
            if (!isValidType) {
                alert('Invalid file type. Please select a supported file.');
                removeFile(); return;
            }
            selectedFileDiv.querySelector('.file-name').textContent = file.name;
            selectedFileDiv.querySelector('.file-size').textContent = formatFileSize(file.size);
            selectedFileDiv.style.display = 'flex';
            uploadBtn.disabled = false;
        }
        function removeFile() {
            const input = document.getElementById('fileInput');
            input.value = '';
            document.getElementById('selectedFile').style.display = 'none';
            document.getElementById('uploadBtn').disabled = true;
        }
        function formatFileSize(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        }
        function deleteFile(fileId) {
            if (confirm('Are you sure you want to delete this file? This cannot be undone.')) {
                fetch('${pageContext.request.contextPath}/delete/' + fileId, { method: 'DELETE' })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        window.location.reload();
                    } else {
                        alert('Error deleting file: ' + data.message);
                    }
                }).catch(error => console.error('Error:', error));
            }
        }
        function checkProcessingFiles() {
            if (document.querySelector('.status-processing')) {
                setTimeout(() => window.location.reload(), 5000);
            }
        }
        document.addEventListener('DOMContentLoaded', checkProcessingFiles);
    </script>
</body>
</html>

