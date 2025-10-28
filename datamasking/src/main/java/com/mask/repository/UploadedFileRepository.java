package com.mask.repository;

import com.mask.model.FileStatus;
import com.mask.model.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    
    List<UploadedFile> findByStatusOrderByUploadedAtDesc(FileStatus status);
    
    List<UploadedFile> findAllByOrderByUploadedAtDesc();
    
    Optional<UploadedFile> findByIdAndStatus(Long id, FileStatus status);
    
    @Query("SELECT f FROM UploadedFile f WHERE f.uploadedAt < :cutoff AND f.status IN :statuses")
    List<UploadedFile> findOldFiles(@Param("cutoff") LocalDateTime cutoff, @Param("statuses") List<FileStatus> statuses);
    
    Long countByStatus(FileStatus status);
}