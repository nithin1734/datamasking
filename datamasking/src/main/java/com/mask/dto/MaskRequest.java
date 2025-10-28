package com.mask.dto;

import java.util.Map;

public class MaskRequest {
    // per-column technique map: columnName -> technique name
    private Map<String, String> columnTechniques;

    // optional: global technique
    private String globalTechnique;

    public Map<String, String> getColumnTechniques() { return columnTechniques; }
    public void setColumnTechniques(Map<String, String> columnTechniques) { this.columnTechniques = columnTechniques; }
    public String getGlobalTechnique() { return globalTechnique; }
    public void setGlobalTechnique(String globalTechnique) { this.globalTechnique = globalTechnique; }
}
