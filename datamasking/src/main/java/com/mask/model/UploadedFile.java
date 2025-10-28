package com.mask.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "uploaded_files")
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;
    private String maskedFileName;
    private String contentType;
    private long size;

    @Enumerated(EnumType.STRING)
    private FileStatus status = FileStatus.UPLOADED;

    private LocalDateTime uploadedAt = LocalDateTime.now();
    private String errorMessage;

    public UploadedFile() {}

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public String getMaskedFileName() { return maskedFileName; }
    public void setMaskedFileName(String maskedFileName) { this.maskedFileName = maskedFileName; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public FileStatus getStatus() { return status; }
    public void setStatus(FileStatus status) { this.status = status; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
