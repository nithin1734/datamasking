package com.mask.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mask.util.MaskingUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class MaskingService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> autoSuggestTechniquesForCSV(Path csvPath) throws Exception {
        Map<String, String> suggestions = new LinkedHashMap<>();
        
        if (!Files.exists(csvPath)) {
            System.err.println("CSV file not found: " + csvPath);
            return suggestions;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            CSVFormat format = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim()
                .withIgnoreSurroundingSpaces();
                
            try (CSVParser parser = format.parse(reader)) {
                Map<String, Integer> headerMap = parser.getHeaderMap();
                if (headerMap == null || headerMap.isEmpty()) {
                    return suggestions;
                }
                
                List<CSVRecord> records = parser.getRecords();
                
                for (String header : headerMap.keySet()) {
                    if (header == null || header.trim().isEmpty()) {
                        continue;
                    }
                    
                    String cleanHeader = header.trim();
                    boolean isSensitiveByName = MaskingUtils.headerIsSensitive(cleanHeader);
                    
                    String sampleValue = "";
                    for (CSVRecord record : records) {
                        if (record.size() > headerMap.get(header)) {
                            String value = record.get(header);
                            if (value != null && !value.trim().isEmpty()) {
                                sampleValue = value.trim();
                                break;
                            }
                        }
                    }
                    
                    Optional<String> typeByValue = MaskingUtils.detectTypeByValue(sampleValue);
                    
                    if (isSensitiveByName || typeByValue.isPresent()) {
                        String suggestedTechnique = determineBestTechnique(cleanHeader, sampleValue, typeByValue);
                        suggestions.put(cleanHeader, suggestedTechnique);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error analyzing CSV for suggestions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return suggestions;
    }

    /**
     * Main file masking dispatcher
     */
    public Path maskFile(Path inputPath, String originalFileName, 
                        Map<String, String> columnTechniques, String globalTechnique, 
                        String maskedFileName) throws Exception {
        
        String extension = getFileExtension(originalFileName).toLowerCase();
        
        switch (extension) {
            case "csv":
                return maskCsvFile(inputPath, columnTechniques, globalTechnique, maskedFileName);
            case "txt":
                return maskTextFile(inputPath, globalTechnique, maskedFileName);
            case "xlsx":
                return maskExcelFile(inputPath, columnTechniques, globalTechnique, maskedFileName, true);
            case "xls":
                return maskExcelFile(inputPath, columnTechniques, globalTechnique, maskedFileName, false);
            case "json":
                return maskJsonFile(inputPath, columnTechniques, globalTechnique, maskedFileName);
            default:
                throw new IllegalArgumentException("Unsupported file type: " + extension);
        }
    }

    /**
     * Enhanced CSV masking with precise column selection
     */
		private Path maskCsvFile(Path inputPath, Map<String, String> columnTechniques, 
		            String globalTechnique, String maskedFileName) throws Exception {
		
		Path outputPath = inputPath.getParent().resolve(maskedFileName);
		
		try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
		CSVFormat inputFormat = CSVFormat.DEFAULT
		.withFirstRecordAsHeader()
		.withIgnoreHeaderCase()
		.withTrim()
		.withIgnoreSurroundingSpaces();
		
		try (CSVParser parser = inputFormat.parse(reader);
		 BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);
		 CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
		
		// Get headers
		Map<String, Integer> headerMap = parser.getHeaderMap();
		if (headerMap == null || headerMap.isEmpty()) {
		    throw new Exception("No headers found in CSV file");
		}
		
		List<String> headers = new ArrayList<>(headerMap.keySet());
		
		// Print headers unchanged
		printer.printRecord(headers);
		
		// Process each record
		int recordCount = 0;
		for (CSVRecord record : parser) {
		    recordCount++;
		    List<String> maskedRecord = new ArrayList<>();
		    
		    for (String header : headers) {
		        String originalValue = "";
		        
		        // Safely get value from record
		        try {
		            if (record.isMapped(header) && record.get(header) != null) {
		                originalValue = record.get(header);
		            }
		        } catch (Exception e) {
		            System.err.println("Error reading value for header '" + header + "' in record " + recordCount + ": " + e.getMessage());
		            originalValue = "";
		        }
		        
		        String maskedValue = maskValue(originalValue, header, columnTechniques, globalTechnique);
		        maskedRecord.add(maskedValue);
		    }
		    
		    printer.printRecord(maskedRecord);
		}
		
		System.out.println("Successfully processed " + recordCount + " records");
		}
		}
		
		return outputPath;
		}

    /**
     * Text file masking (treats entire content as single column)
     */
    private Path maskTextFile(Path inputPath, String globalTechnique, String maskedFileName) throws Exception {
        Path outputPath = inputPath.getParent().resolve(maskedFileName);
        
        List<String> lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        List<String> maskedLines = new ArrayList<>();
        
        String technique = globalTechnique != null ? globalTechnique : "FULL_MASK";
        
        for (String line : lines) {
            String maskedLine = MaskingUtils.applyTechnique(technique, line, null);
            maskedLines.add(maskedLine);
        }
        
        Files.write(outputPath, maskedLines, StandardCharsets.UTF_8);
        return outputPath;
    }

    /**
     * Enhanced Excel masking with precise column handling
     */
    private Path maskExcelFile(Path inputPath, Map<String, String> columnTechniques, 
                              String globalTechnique, String maskedFileName, boolean isXlsx) throws Exception {
        
        Path outputPath = inputPath.getParent().resolve(maskedFileName);
        
        try (InputStream inputStream = Files.newInputStream(inputPath);
             Workbook workbook = WorkbookFactory.create(inputStream);
             OutputStream outputStream = Files.newOutputStream(outputPath)) {
            
            Workbook outputWorkbook = isXlsx ? new XSSFWorkbook() : new HSSFWorkbook();
            
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet inputSheet = workbook.getSheetAt(sheetIndex);
                Sheet outputSheet = outputWorkbook.createSheet(inputSheet.getSheetName());
                
                List<String> headers = new ArrayList<>();
                boolean isFirstRow = true;
                
                for (Row inputRow : inputSheet) {
                    Row outputRow = outputSheet.createRow(inputRow.getRowNum());
                    
                    if (isFirstRow) {
                        // Process header row
                        for (Cell inputCell : inputRow) {
                            String headerValue = getCellValueAsString(inputCell);
                            headers.add(headerValue);
                            
                            Cell outputCell = outputRow.createCell(inputCell.getColumnIndex());
                            outputCell.setCellValue(headerValue);
                        }
                        isFirstRow = false;
                    } else {
                        // Process data rows
                        for (Cell inputCell : inputRow) {
                            int columnIndex = inputCell.getColumnIndex();
                            String originalValue = getCellValueAsString(inputCell);
                            
                            String columnName = columnIndex < headers.size() ? headers.get(columnIndex) : "Column" + columnIndex;
                            String maskedValue = maskValue(originalValue, columnName, columnTechniques, globalTechnique);
                            
                            Cell outputCell = outputRow.createCell(columnIndex);
                            outputCell.setCellValue(maskedValue);
                        }
                    }
                }
            }
            
            outputWorkbook.write(outputStream);
            outputWorkbook.close();
        }
        
        return outputPath;
    }

    /**
     * Enhanced JSON masking with selective field processing
     */
    private Path maskJsonFile(Path inputPath, Map<String, String> columnTechniques, 
                             String globalTechnique, String maskedFileName) throws Exception {
        
        Path outputPath = inputPath.getParent().resolve(maskedFileName);
        
        JsonNode rootNode = objectMapper.readTree(Files.newBufferedReader(inputPath, StandardCharsets.UTF_8));
        JsonNode maskedNode = maskJsonNode(rootNode, columnTechniques, globalTechnique, "");
        
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, maskedNode);
        }
        
        return outputPath;
    }

    /**
     * Recursive JSON node masking
     */
    private JsonNode maskJsonNode(JsonNode node, Map<String, String> columnTechniques, 
                                 String globalTechnique, String parentKey) {
        
        if (node.isArray()) {
            ArrayNode arrayNode = objectMapper.createArrayNode();
            for (JsonNode element : node) {
                arrayNode.add(maskJsonNode(element, columnTechniques, globalTechnique, parentKey));
            }
            return arrayNode;
        } else if (node.isObject()) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldName = field.getKey();
                JsonNode fieldValue = field.getValue();
                
                if (fieldValue.isValueNode()) {
                    String originalValue = fieldValue.asText();
                    String maskedValue = maskValue(originalValue, fieldName, columnTechniques, globalTechnique);
                    objectNode.put(fieldName, maskedValue);
                } else {
                    objectNode.set(fieldName, maskJsonNode(fieldValue, columnTechniques, globalTechnique, fieldName));
                }
            }
            return objectNode;
        }
        
        return node;
    }

    /**
     * Core value masking logic - only masks if column is specified or global is set
     */
		    private String maskValue(String originalValue, String columnName, 
		            Map<String, String> columnTechniques, String globalTechnique) {
		
		// Handle null or empty values
		if (originalValue == null) {
		return "";
		}
		
		if (originalValue.trim().isEmpty()) {
		return originalValue;
		}
		
		try {
		// Check if this specific column should be masked
		if (columnTechniques != null && columnTechniques.containsKey(columnName)) {
		String technique = columnTechniques.get(columnName);
		if (technique != null && !technique.trim().isEmpty()) {
		    return MaskingUtils.applyTechnique(technique, originalValue, columnName);
		}
		}
		
		// Apply global technique only if no specific column technique is set
		if (globalTechnique != null && !globalTechnique.trim().isEmpty()) {
		// Only apply global if this column wasn't specifically excluded
		if (columnTechniques == null || !columnTechniques.containsKey(columnName) || 
		    columnTechniques.get(columnName) == null || columnTechniques.get(columnName).trim().isEmpty()) {
		    return MaskingUtils.applyTechnique(globalTechnique, originalValue, columnName);
		}
		}
		
		// Return original value if no masking rules apply
		return originalValue;
		
		} catch (Exception e) {
		System.err.println("Error masking value for column '" + columnName + "': " + e.getMessage());
		// Return original value on error instead of throwing exception
		return originalValue;
		}
		}


    /**
     * Determine the best masking technique based on column name and sample value
     */
		    private String determineBestTechnique(String columnName, String sampleValue, Optional<String> detectedType) {
		        if (columnName == null) {
		            return "FULL_MASK";
		        }
		        
		        String lowerColumnName = columnName.toLowerCase();
		        
		        // Specific technique recommendations based on detected type
		        if (detectedType.isPresent()) {
		            switch (detectedType.get().toLowerCase()) {
		                case "email":
		                    return "PARTIAL_MASK";
		                case "phone":
		                    return "PARTIAL_MASK";
		                case "date":
		                    return "DATE_SHIFT";
		                case "card":
		                case "aadhaar":
		                case "pan":
		                    return "HASH_MASK";
		                default:
		                    return "FULL_MASK";
		            }
		        }
		        
		        // Column name-based recommendations
		        if (lowerColumnName.contains("email")) {
		            return "PARTIAL_MASK";
		        } else if (lowerColumnName.contains("phone") || lowerColumnName.contains("mobile")) {
		            return "PARTIAL_MASK";
		        } else if (lowerColumnName.contains("date") || lowerColumnName.contains("dob") || lowerColumnName.contains("birth")) {
		            return "DATE_SHIFT";
		        } else if (lowerColumnName.contains("name") && !lowerColumnName.contains("filename")) {
		            return "RANDOM_REPLACE";
		        } else if (lowerColumnName.contains("card") || lowerColumnName.contains("account") || 
		                   lowerColumnName.contains("aadhaar") || lowerColumnName.contains("pan") ||
		                   lowerColumnName.contains("ssn") || lowerColumnName.contains("id")) {
		            return "HASH_MASK";
		        } else if (lowerColumnName.contains("address") || lowerColumnName.contains("location")) {
		            return "RANDOM_REPLACE";
		        }
		        
		        // Default recommendation
		        return "FULL_MASK";
		    }
    /**
     * Extract file extension from filename
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * Convert Excel cell value to string with proper formatting
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return "";
                    }
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}