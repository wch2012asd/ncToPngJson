package com.geovis.tools.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * JSON工具类
 */
public class JsonUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    public static <T> T fromJsonString(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 写入JSON文件 - 12个参数版本
     */
    public static void writeJsonToFile(String filePath, String param1, String param2, String param3,
                                     String param4, String param5, String param6, String param7,
                                     String param8, String param9, String param10, String param11) {
        try {
            // 创建JSON对象
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"param1\": \"").append(param1 != null ? param1 : "").append("\",\n");
            json.append("  \"param2\": \"").append(param2 != null ? param2 : "").append("\",\n");
            json.append("  \"param3\": \"").append(param3 != null ? param3 : "").append("\",\n");
            json.append("  \"param4\": \"").append(param4 != null ? param4 : "").append("\",\n");
            json.append("  \"param5\": \"").append(param5 != null ? param5 : "").append("\",\n");
            json.append("  \"param6\": \"").append(param6 != null ? param6 : "").append("\",\n");
            json.append("  \"param7\": \"").append(param7 != null ? param7 : "").append("\",\n");
            json.append("  \"param8\": \"").append(param8 != null ? param8 : "").append("\",\n");
            json.append("  \"param9\": \"").append(param9 != null ? param9 : "").append("\",\n");
            json.append("  \"param10\": \"").append(param10 != null ? param10 : "").append("\",\n");
            json.append("  \"param11\": \"").append(param11 != null ? param11 : "").append("\"\n");
            json.append("}");
            
            // 写入文件
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("写入JSON文件失败: " + filePath, e);
        }
    }
    
    /**
     * 写入JSON文件 - 14个参数版本
     */
    public static void writeJsonToFile(String filePath, String param1, String param2, String param3,
                                     String param4, String param5, String param6, String param7,
                                     String param8, String param9, String param10, String param11,
                                     String param12, String param13) {
        try {
            // 创建JSON对象
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"param1\": \"").append(param1 != null ? param1 : "").append("\",\n");
            json.append("  \"param2\": \"").append(param2 != null ? param2 : "").append("\",\n");
            json.append("  \"param3\": \"").append(param3 != null ? param3 : "").append("\",\n");
            json.append("  \"param4\": \"").append(param4 != null ? param4 : "").append("\",\n");
            json.append("  \"param5\": \"").append(param5 != null ? param5 : "").append("\",\n");
            json.append("  \"param6\": \"").append(param6 != null ? param6 : "").append("\",\n");
            json.append("  \"param7\": \"").append(param7 != null ? param7 : "").append("\",\n");
            json.append("  \"param8\": \"").append(param8 != null ? param8 : "").append("\",\n");
            json.append("  \"param9\": \"").append(param9 != null ? param9 : "").append("\",\n");
            json.append("  \"param10\": \"").append(param10 != null ? param10 : "").append("\",\n");
            json.append("  \"param11\": \"").append(param11 != null ? param11 : "").append("\",\n");
            json.append("  \"param12\": \"").append(param12 != null ? param12 : "").append("\",\n");
            json.append("  \"param13\": \"").append(param13 != null ? param13 : "").append("\"\n");
            json.append("}");
            
            // 写入文件
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("写入JSON文件失败: " + filePath, e);
        }
    }
}