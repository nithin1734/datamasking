package com.mask.controller;

import com.mask.model.FileStatus;
import com.mask.model.UploadedFile;
import com.mask.repository.UploadedFileRepository;
import com.mask.service.EmailService;
import com.mask.service.MaskingService;
import com.mask.service.StorageService;
import com.mask.service.UserService;
import com.mask.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Controller
public class MaskController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private MaskingService maskingService;

    @Autowired
    private UploadedFileRepository repository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    // Supported file types
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
        "csv", "txt", "xlsx", "xls", "json"
    );

    @GetMapping("/upload")
    public String uploadPage(Model model) {
        if (!isAuthenticated()) {
            return "redirect:/login";
        }
        
        List<UploadedFile> files = repository.findAll();
        // Sort files by upload date descending
        files.sort((f1, f2) -> f2.getUploadedAt().compareTo(f1.getUploadedAt()));
        
        model.addAttribute("files", files);
        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        
        if (!isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            // Validate file selection
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Please select a file to upload.");
                return "redirect:/upload";
            }

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            
            // Validate filename
            if (originalFilename.contains("..")) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Invalid filename. Filename contains invalid characters.");
                return "redirect:/upload";
            }

            // Validate file extension
            String fileExtension = getFileExtension(originalFilename).toLowerCase();
            if (!SUPPORTED_EXTENSIONS.contains(fileExtension)) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Unsupported file type. Supported formats: CSV, TXT, XLSX, XLS, JSON");
                return "redirect:/upload";
            }

            // Validate file size (10MB limit)
            long maxFileSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxFileSize) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "File size exceeds 10MB limit. Please upload a smaller file.");
                return "redirect:/upload";
            }

            // Store file
            Path storedFilePath = storageService.store(originalFilename, file.getInputStream());

            // Create database record
            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setOriginalFileName(originalFilename);
            uploadedFile.setContentType(file.getContentType());
            uploadedFile.setSize(file.getSize());
            uploadedFile.setStatus(FileStatus.UPLOADED);
            uploadedFile.setMaskedFileName("not_processed");
            uploadedFile = repository.save(uploadedFile);

            redirectAttributes.addFlashAttribute("successMessage", 
                "File uploaded successfully! Now select masking options.");
            
            return "redirect:/select/" + uploadedFile.getId();

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Upload failed: " + e.getMessage());
            return "redirect:/upload";
        }
    }

    @GetMapping("/select/{id}")
    public String selectMaskingOptions(@PathVariable("id") Long id, Model model) {
        if (!isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<UploadedFile> fileOptional = repository.findById(id);
        if (fileOptional.isEmpty()) {
            model.addAttribute("errorMessage", "File not found.");
            return "redirect:/upload";
        }

        UploadedFile uploadedFile = fileOptional.get();
        model.addAttribute("file", uploadedFile);

        // Generate auto-suggestions for supported file types
        Map<String, String> suggestions = new HashMap<>();
        try {
            String extension = getFileExtension(uploadedFile.getOriginalFileName()).toLowerCase();
            if ("csv".equals(extension)) {
                Path storedFilePath = findStoredFile(uploadedFile.getOriginalFileName());
                if (storedFilePath != null && Files.exists(storedFilePath)) {
                    suggestions = maskingService.autoSuggestTechniquesForCSV(storedFilePath);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to generate auto-suggestions: " + e.getMessage());
            e.printStackTrace();
        }

        model.addAttribute("suggestions", suggestions);
        model.addAttribute("supportedTechniques", getSupportedTechniques());
        
        return "select";
    }

    @PostMapping("/process/{id}")
    public String processFile(@PathVariable("id") Long id,
                            @RequestParam(value = "globalTechnique", required = false) String globalTechnique,
                            @RequestParam Map<String, String> allParams,
                            RedirectAttributes redirectAttributes) {

        if (!isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<UploadedFile> fileOptional = repository.findById(id);
        if (fileOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "File not found.");
            return "redirect:/upload";
        }

        UploadedFile uploadedFile = fileOptional.get();
        
        try {
            // Update status to processing
            uploadedFile.setStatus(FileStatus.PROCESSING);
            repository.save(uploadedFile);

            // Extract column-specific techniques from request parameters
            Map<String, String> columnTechniques = extractColumnTechniques(allParams);

            // Validate that at least one masking technique is specified
            boolean hasGlobalTechnique = globalTechnique != null && !globalTechnique.trim().isEmpty();
            boolean hasColumnTechniques = !columnTechniques.isEmpty() && 
                columnTechniques.values().stream().anyMatch(t -> t != null && !t.trim().isEmpty());

            if (!hasGlobalTechnique && !hasColumnTechniques) {
                uploadedFile.setStatus(FileStatus.FAILED);
                uploadedFile.setErrorMessage("No masking techniques specified");
                repository.save(uploadedFile);
                
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Please specify at least one masking technique (global or column-specific).");
                return "redirect:/select/" + id;
            }

            // Find the stored file
            Path storedFilePath = findStoredFile(uploadedFile.getOriginalFileName());
            if (storedFilePath == null || !Files.exists(storedFilePath)) {
                throw new RuntimeException("Stored file not found: " + uploadedFile.getOriginalFileName());
            }

            // Generate masked filename
            String maskedFileName = generateMaskedFileName(uploadedFile.getOriginalFileName());

            // Process the file with masking
            Path maskedFilePath = maskingService.maskFile(
                storedFilePath, 
                uploadedFile.getOriginalFileName(),
                columnTechniques, 
                globalTechnique, 
                maskedFileName
            );

            // Update database record with success status
            uploadedFile.setMaskedFileName(maskedFilePath.getFileName().toString());
            uploadedFile.setStatus(FileStatus.COMPLETED);
            uploadedFile.setErrorMessage(null);
            repository.save(uploadedFile);

            // Send completion email notification
            try {
                sendCompletionNotification(uploadedFile, columnTechniques, globalTechnique);
            } catch (Exception emailError) {
                System.err.println("Failed to send completion email: " + emailError.getMessage());
                // Don't fail the whole process due to email issues
            }

            redirectAttributes.addFlashAttribute("successMessage", 
                "File processed successfully! Download your masked file below.");
            
            return "redirect:/upload";

        } catch (Exception e) {
            e.printStackTrace();
            
            // Update status to failed
            uploadedFile.setStatus(FileStatus.FAILED);
            uploadedFile.setErrorMessage(e.getMessage());
            repository.save(uploadedFile);

            redirectAttributes.addFlashAttribute("errorMessage", 
                "Processing failed: " + e.getMessage());
            
            return "redirect:/upload";
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadMaskedFile(@PathVariable("id") Long id) {
        if (!isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        try {
            Optional<UploadedFile> fileOptional = repository.findById(id);
            if (fileOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UploadedFile uploadedFile = fileOptional.get();
            
            if (uploadedFile.getStatus() != FileStatus.COMPLETED) {
                return ResponseEntity.badRequest().build();
            }

            Path maskedFilePath = Path.of("./storage").resolve(uploadedFile.getMaskedFileName());
            if (!Files.exists(maskedFilePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(maskedFilePath.toUri());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + uploadedFile.getMaskedFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteFile(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        
        if (!isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Authentication required");
            return response;
        }

        try {
            Optional<UploadedFile> fileOptional = repository.findById(id);
            if (fileOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "File not found");
                return response;
            }

            UploadedFile uploadedFile = fileOptional.get();
            
            // Delete physical files
            try {
                Path storedFile = findStoredFile(uploadedFile.getOriginalFileName());
                if (storedFile != null && Files.exists(storedFile)) {
                    Files.delete(storedFile);
                }
                
                if (uploadedFile.getStatus() == FileStatus.COMPLETED && 
                    uploadedFile.getMaskedFileName() != null && 
                    !uploadedFile.getMaskedFileName().equals("not_processed")) {
                    
                    Path maskedFile = Path.of("./storage").resolve(uploadedFile.getMaskedFileName());
                    if (Files.exists(maskedFile)) {
                        Files.delete(maskedFile);
                    }
                }
            } catch (Exception fileDeleteError) {
                System.err.println("Error deleting physical files: " + fileDeleteError.getMessage());
            }
            
            // Delete database record
            repository.delete(uploadedFile);
            
            response.put("success", true);
            response.put("message", "File deleted successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error deleting file: " + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/preview/{id}")
    @ResponseBody
    public Map<String, Object> previewSuggestions(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        
        if (!isAuthenticated()) {
            response.put("error", "Authentication required");
            return response;
        }

        try {
            Optional<UploadedFile> fileOptional = repository.findById(id);
            if (fileOptional.isEmpty()) {
                response.put("error", "File not found");
                return response;
            }

            UploadedFile uploadedFile = fileOptional.get();
            String extension = getFileExtension(uploadedFile.getOriginalFileName()).toLowerCase();
            
            if ("csv".equals(extension)) {
                Path storedFilePath = findStoredFile(uploadedFile.getOriginalFileName());
                if (storedFilePath != null && Files.exists(storedFilePath)) {
                    Map<String, String> suggestions = maskingService.autoSuggestTechniquesForCSV(storedFilePath);
                    response.put("suggestions", suggestions);
                    response.put("success", true);
                } else {
                    response.put("error", "Stored file not found");
                }
            } else {
                response.put("suggestions", new HashMap<>());
                response.put("success", true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error generating suggestions: " + e.getMessage());
        }
        
        return response;
    }

    // Helper methods

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private Map<String, String> extractColumnTechniques(Map<String, String> allParams) {
        Map<String, String> columnTechniques = new HashMap<>();
        
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (key.startsWith("columnTechniques[") && key.endsWith("]")) {
                String columnName = key.substring("columnTechniques[".length(), key.length() - 1);
                if (value != null && !value.trim().isEmpty()) {
                    columnTechniques.put(columnName, value.trim());
                }
            }
        }
        
        return columnTechniques;
    }

    private Path findStoredFile(String originalFileName) {
        try {
            Path storageDir = Path.of("./storage");
            if (!Files.exists(storageDir)) {
                return null;
            }
            
            return Files.list(storageDir)
                    .filter(path -> path.getFileName().toString().endsWith("_" + originalFileName))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateMaskedFileName(String originalFileName) {
        String nameWithoutExtension = originalFileName.contains(".") 
            ? originalFileName.substring(0, originalFileName.lastIndexOf("."))
            : originalFileName;
        
        String extension = originalFileName.contains(".")
            ? originalFileName.substring(originalFileName.lastIndexOf("."))
            : "";
        
        return System.currentTimeMillis() + "_" + nameWithoutExtension + "_masked" + extension;
    }

    private Map<String, String> getSupportedTechniques() {
        Map<String, String> techniques = new LinkedHashMap<>();
        techniques.put("FULL_MASK", "Full Mask - Replace all characters with asterisks (*)");
        techniques.put("PARTIAL_MASK", "Partial Mask - Show first/last characters, mask middle");
        techniques.put("RANDOM_REPLACE", "Random Replace - Generate realistic fake data");
        techniques.put("HASH_MASK", "Hash Mask - Create irreversible SHA-256 hash");
        techniques.put("DATE_SHIFT", "Date Shift - Shift dates by random days (±180 days)");
        return techniques;
    }

    private void sendCompletionNotification(UploadedFile uploadedFile, 
                                          Map<String, String> columnTechniques, 
                                          String globalTechnique) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String userEmail = authentication.getName();
                Optional<User> userOptional = userService.findByEmail(userEmail);
                
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    
                    StringBuilder emailContent = new StringBuilder();
                    emailContent.append("Dear ").append(user.getName()).append(",\n\n");
                    emailContent.append("Your file masking process has been completed successfully.\n\n");
                    emailContent.append("File Details:\n");
                    emailContent.append("- Original File: ").append(uploadedFile.getOriginalFileName()).append("\n");
                    emailContent.append("- File Size: ").append(formatFileSize(uploadedFile.getSize())).append("\n");
                    emailContent.append("- Processing Time: ").append(new Date()).append("\n\n");
                    
                    if (globalTechnique != null && !globalTechnique.trim().isEmpty()) {
                        emailContent.append("Global Masking Technique: ").append(globalTechnique).append("\n\n");
                    }
                    
                    if (!columnTechniques.isEmpty()) {
                        emailContent.append("Column-Specific Masking:\n");
                        columnTechniques.forEach((column, technique) -> 
                            emailContent.append("- ").append(column).append(": ").append(technique).append("\n"));
                        emailContent.append("\n");
                    }
                    
                    emailContent.append("Important Security Notes:\n");
                    emailContent.append("- The masking process is irreversible\n");
                    emailContent.append("- Original data cannot be recovered from masked data\n");
                    emailContent.append("- Please verify the masked data meets your requirements\n\n");
                    emailContent.append("You can download your masked file from the application dashboard.\n\n");
                    emailContent.append("Best regards,\n");
                    emailContent.append("DataMasking Team");
                    
                    // This would need to be implemented in EmailService
                    // emailService.sendMaskingCompletionEmail(userEmail, 
                    //     "File Masking Completed - " + uploadedFile.getOriginalFileName(), 
                    //     emailContent.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Log error but don't fail the process
        }
    }
    
    private String formatFileSize(long size) {
        if (size < 1024) return size + " bytes";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
}