<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Select Masking Options - DataMasking Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/select.css">
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
                    <i class="fas fa-arrow-left"></i> Back to Upload
                </a>
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
        <div class="page-header">
            <h2><i class="fas fa-cog"></i> Select Masking Options</h2>
        </div>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                ${successMessage}
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-triangle"></i>
                ${errorMessage}
            </div>
        </c:if>

        <div class="masking-card">
            <div class="card-header">
                <h3><i class="fas fa-file"></i> File Information</h3>
            </div>
            <div class="file-info">
                <div class="info-item">
                    <i class="fas fa-file-alt"></i>
                    <strong>File:</strong> ${file.originalFileName}
                </div>
                <div class="info-item">
                    <i class="fas fa-weight"></i>
                    <strong>Size:</strong>
                    <fmt:formatNumber value="${file.size / 1048576}" maxFractionDigits="2" /> MB
                </div>
                <div class="info-item">
                    <i class="fas fa-info-circle"></i>
                    Choose how you want to mask sensitive data in your file.
                </div>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/process/${file.id}" method="post" id="maskingForm">
            <div class="masking-section">
                <div class="section-header">
                    <h3><i class="fas fa-globe"></i> Global Masking Technique (Optional)</h3>
                    <p>This technique will be applied to all columns unless overridden below.</p>
                </div>
                <div class="form-group">
                    <select name="globalTechnique" class="form-select" id="globalTechnique">
                        <option value="">-- Select Global Technique --</option>
                        <option value="FULL_MASK">Full Mask (Replace all with *)</option>
                        <option value="PARTIAL_MASK">Partial Mask (Show first/last chars)</option>
                        <option value="RANDOM_REPLACE">Random Replace (Realistic fake data)</option>
                        <option value="HASH_MASK">Hash Mask (SHA-256 hash)</option>
                        <option value="DATE_SHIFT">Date Shift (For dates only)</option>
                    </select>
                </div>
            </div>

            <div class="masking-section">
                <div class="section-header">
                    <h3><i class="fas fa-columns"></i> Column-Specific Masking Techniques</h3>
                </div>
                
                <c:if test="${not empty suggestions}">
                    <div class="suggestions-section">
                        <div class="suggestions-header">
                            <i class="fas fa-magic"></i> 
                            <strong>Auto-detected sensitive columns:</strong>
                        </div>
                        <div class="suggestions-grid">
                            <c:forEach var="suggestion" items="${suggestions}">
                                <div class="suggestion-card">
                                    <div class="suggestion-header">
                                        <strong>${suggestion.key}</strong>
                                        <span class="suggestion-badge">
                                            Suggested: ${suggestion.value}
                                        </span>
                                    </div>
                                    <div class="form-group">
                                        <select name="columnTechniques[${suggestion.key}]" class="form-select">
                                            <option value="">-- No Masking --</option>
                                            <option value="FULL_MASK" ${suggestion.value == 'FULL_MASK' ? 'selected' : ''}>Full Mask</option>
                                            <option value="PARTIAL_MASK" ${suggestion.value == 'PARTIAL_MASK' ? 'selected' : ''}>Partial Mask</option>
                                            <option value="RANDOM_REPLACE" ${suggestion.value == 'RANDOM_REPLACE' ? 'selected' : ''}>Random Replace</option>
                                            <option value="HASH_MASK" ${suggestion.value == 'HASH_MASK' ? 'selected' : ''}>Hash Mask</option>
                                            <option value="DATE_SHIFT" ${suggestion.value == 'DATE_SHIFT' ? 'selected' : ''}>Date Shift</option>
                                        </select>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>

                <div class="manual-columns-section">
                    <div class="section-header">
                        <h4><i class="fas fa-edit"></i> Add Custom Column Masking:</h4>
                    </div>
                    <div class="custom-column-form">
                        <div class="form-row">
                            <div class="form-group">
                                <input type="text" class="form-input" placeholder="Column Name" id="customColumnName">
                            </div>
                            <div class="form-group">
                                <select class="form-select" id="customColumnTechnique">
                                    <option value="">-- Select Technique --</option>
                                    <option value="FULL_MASK">Full Mask</option>
                                    <option value="PARTIAL_MASK">Partial Mask</option>
                                    <option value="RANDOM_REPLACE">Random Replace</option>
                                    <option value="HASH_MASK">Hash Mask</option>
                                    <option value="DATE_SHIFT">Date Shift</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <button type="button" class="btn-add" onclick="addCustomColumn()">
                                    <i class="fas fa-plus"></i> Add
                                </button>
                            </div>
                        </div>
                    </div>
                    <div id="addedColumns" class="added-columns"></div>
                </div>
            </div>

            <div class="masking-section">
                <div class="section-header">
                    <h3><i class="fas fa-question-circle"></i> Masking Technique Descriptions</h3>
                </div>
                <div class="techniques-grid">
                    <div class="technique-desc">
                        <h4><i class="fas fa-asterisk"></i> Full Mask</h4>
                        <p>Replaces all characters with asterisks (*)</p>
                        <div class="example">Example: "John Doe" → "********"</div>
                    </div>
                    <div class="technique-desc">
                        <h4><i class="fas fa-eye-slash"></i> Partial Mask</h4>
                        <p>Shows first/last characters, masks middle</p>
                        <div class="example">Example: "john@email.com" → "j***@email.com"</div>
                    </div>
                    <div class="technique-desc">
                        <h4><i class="fas fa-random"></i> Random Replace</h4>
                        <p>Generates realistic fake data</p>
                        <div class="example">Example: "John Doe" → "Amit Sharma"</div>
                    </div>
                    <div class="technique-desc">
                        <h4><i class="fas fa-lock"></i> Hash Mask</h4>
                        <p>Creates irreversible SHA-256 hash</p>
                        <div class="example">Example: "data" → "HASH_A1B2C3D4"</div>
                    </div>
                    <div class="technique-desc">
                        <h4><i class="fas fa-calendar"></i> Date Shift</h4>
                        <p>Shifts dates by random days (±365)</p>
                        <div class="example">Example: "2023-01-15" → "2023-06-22"</div>
                    </div>
                </div>
            </div>

            <div class="submit-section">
                <button type="submit" class="btn-process" id="processBtn">
                    <i class="fas fa-magic"></i> Process File with Masking
                </button>
                <div class="form-help">
                    <i class="fas fa-info-circle"></i>
                    Please select at least one masking technique (either global or column-specific) before processing.
                </div>
            </div>
        </form>
    </div>

    <script>
        let customColumnCounter = 0;
        const fileId = ${file.id};
        
        function addCustomColumn() {
            const columnName = document.getElementById('customColumnName').value.trim();
            const technique = document.getElementById('customColumnTechnique').value;
            
            if (!columnName || !technique) {
                alert('Please enter both column name and technique');
                return;
            }
            
            const existingInputs = document.querySelectorAll('input[name^="columnTechniques["]');
            const existingSelects = document.querySelectorAll('select[name^="columnTechniques["]');
            
            for (let input of existingInputs) {
                if (input.name === 'columnTechniques[' + columnName + ']') {
                    alert('Column already exists. Please use a different name.');
                    return;
                }
            }
            
            for (let select of existingSelects) {
                if (select.name === 'columnTechniques[' + columnName + ']') {
                    alert('Column already exists. Please use a different name.');
                    return;
                }
            }
            
            customColumnCounter++;
            
            const hiddenInput = document.createElement('input');
            hiddenInput.type = 'hidden';
            hiddenInput.name = 'columnTechniques[' + columnName + ']';
            hiddenInput.value = technique;
            hiddenInput.id = 'customInput_' + customColumnCounter;
            
            const columnDiv = document.createElement('div');
            columnDiv.className = 'added-column-item';
            columnDiv.id = 'customColumn_' + customColumnCounter;
            columnDiv.innerHTML = 
                '<div class="column-info">' +
                    '<i class="fas fa-columns"></i>' +
                    '<span class="column-name">' + columnName + '</span>' +
                    '<span class="column-technique">' + getTechniqueName(technique) + '</span>' +
                '</div>' +
                '<button type="button" class="btn-remove" onclick="removeCustomColumn(\'' + customColumnCounter + '\', \'' + columnName + '\')">' +
                    '<i class="fas fa-times"></i>' +
                '</button>';
            
            document.getElementById('addedColumns').appendChild(columnDiv);
            document.getElementById('maskingForm').appendChild(hiddenInput);
            
            document.getElementById('customColumnName').value = '';
            document.getElementById('customColumnTechnique').value = '';
        }
        
        function removeCustomColumn(counter, columnName) {
            const columnDiv = document.getElementById('customColumn_' + counter);
            if (columnDiv) {
                columnDiv.remove();
            }
            
            const hiddenInput = document.getElementById('customInput_' + counter);
            if (hiddenInput) {
                hiddenInput.remove();
            }
        }
        
        function getTechniqueName(technique) {
            const techniques = {
                'FULL_MASK': 'Full Mask',
                'PARTIAL_MASK': 'Partial Mask',
                'RANDOM_REPLACE': 'Random Replace',
                'HASH_MASK': 'Hash Mask',
                'DATE_SHIFT': 'Date Shift'
            };
            return techniques[technique] || technique;
        }

        document.getElementById('maskingForm').addEventListener('submit', function(e) {
            const globalTechnique = document.getElementById('globalTechnique').value;
            const columnSelects = document.querySelectorAll('select[name^="columnTechniques["]');
            const hiddenInputs = document.querySelectorAll('input[name^="columnTechniques["]');
            
            let hasAnyTechnique = false;
            
            if (globalTechnique && globalTechnique.trim() !== '') {
                hasAnyTechnique = true;
            }
            
            for (let select of columnSelects) {
                if (select.value && select.value.trim() !== '') {
                    hasAnyTechnique = true;
                    break;
                }
            }
            
            if (hiddenInputs.length > 0) {
                hasAnyTechnique = true;
            }
            
            if (!hasAnyTechnique) {
                e.preventDefault();
                alert('Please select at least one masking technique (either global or column-specific).');
                return false;
            }
            
            const submitBtn = document.getElementById('processBtn');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
        });
        
        function previewTechnique(columnName, technique) {
            if (!technique) return;
            
            const examples = {
                'FULL_MASK': '"John Doe" → "********"',
                'PARTIAL_MASK': '"john@email.com" → "j***@email.com"',
                'RANDOM_REPLACE': '"John Doe" → "Amit Sharma"',
                'HASH_MASK': '"sensitive data" → "HASH_A1B2C3D4"',
                'DATE_SHIFT': '"2023-01-15" → "2023-06-22"'
            };
            
            const example = examples[technique] || 'Example not available';
            alert(columnName + ' with ' + getTechniqueName(technique) + ':\n' + example);
        }
        
        document.addEventListener('DOMContentLoaded', function() {
            const suggestionCards = document.querySelectorAll('.suggestion-card');
            suggestionCards.forEach(function(card) {
                const select = card.querySelector('select');
                const header = card.querySelector('.suggestion-header strong');
                
                if (select && header) {
                    const columnName = header.textContent;
                    
                    select.addEventListener('change', function() {
                        if (this.value) {
                            console.log(columnName + ' technique changed to ' + this.value);
                        }
                    });
                }
            });
        });
    </script>
</body>
</html>

