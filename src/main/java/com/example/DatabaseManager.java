package com.example;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理器 - 管理文件转换记录
 */
public class DatabaseManager {
    
    private final ConfigManager configManager;
    private Connection connection;
    
    public DatabaseManager(ConfigManager configManager) {
        this.configManager = configManager;
        try {
            // 加载PostgreSQL驱动
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL驱动加载成功");
            
            // 建立数据库连接
            System.out.println("尝试连接数据库: " + configManager.getDbUrl());
            connection = DriverManager.getConnection(
                configManager.getDbUrl(), 
                configManager.getDbUser(), 
                configManager.getDbPassword()
            );
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("数据库连接成功");
                
                // 创建表
                createTableIfNotExists();
            } else {
                System.err.println("数据库连接失败：连接为null或已关闭");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL驱动未找到: " + e.getMessage());
            System.err.println("请确保PostgreSQL JDBC驱动在classpath中");
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            System.err.println("请检查数据库服务是否运行，以及连接参数是否正确");
        } catch (Exception e) {
            System.err.println("初始化数据库管理器时发生未知错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建文件转换记录表
     */
    private void createTableIfNotExists() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + configManager.getDbSchema() + ".file_conversion_records (" +
            "id SERIAL PRIMARY KEY, " +
            "folder_name VARCHAR(255) NOT NULL, " +
            "file_name VARCHAR(255) NOT NULL, " +
            "file_path TEXT NOT NULL, " +
            "output_path TEXT NOT NULL, " +
            "file_size BIGINT, " +
            "conversion_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "status VARCHAR(50) DEFAULT 'SUCCESS', " +
            "UNIQUE(folder_name, file_name)" +
            ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("文件转换记录表已创建或已存在");
        }
    }
    
    /**
     * 检查数据库连接是否可用
     */
    private boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * 检查文件夹是否已处理过
     */
    public boolean isFolderProcessed(String folderName) {
        if (!isConnectionValid()) {
            System.err.println("数据库连接不可用，无法检查文件夹状态");
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM " + configManager.getDbSchema() + ".file_conversion_records WHERE folder_name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, folderName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("检查文件夹状态失败: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * 检查特定文件是否已处理过
     */
    public boolean isFileProcessed(String folderName, String fileName) {
        if (!isConnectionValid()) {
            System.err.println("数据库连接不可用，无法检查文件状态");
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM " + configManager.getDbSchema() + ".file_conversion_records WHERE folder_name = ? AND file_name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, folderName);
            pstmt.setString(2, fileName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("检查文件状态失败: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * 记录文件转换结果
     */
    public void recordFileConversion(String folderName, String fileName, String filePath, 
                                   String outputPath, long fileSize, String status) {
        if (!isConnectionValid()) {
            System.err.println("数据库连接不可用，无法记录文件转换结果");
            return;
        }
        
        String sql = "INSERT INTO " + configManager.getDbSchema() + ".file_conversion_records " +
            "(folder_name, file_name, file_path, output_path, file_size, status) " +
            "VALUES (?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (folder_name, file_name) " +
            "DO UPDATE SET " +
            "file_path = EXCLUDED.file_path, " +
            "output_path = EXCLUDED.output_path, " +
            "file_size = EXCLUDED.file_size, " +
            "conversion_time = CURRENT_TIMESTAMP, " +
            "status = EXCLUDED.status";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, folderName);
            pstmt.setString(2, fileName);
            pstmt.setString(3, filePath);
            pstmt.setString(4, outputPath);
            pstmt.setLong(5, fileSize);
            pstmt.setString(6, status);
            
            pstmt.executeUpdate();
            System.out.println("文件转换记录已保存: " + folderName + "/" + fileName);
            
        } catch (SQLException e) {
            System.err.println("保存文件转换记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文件夹的处理记录
     */
    public List<FileConversionRecord> getFolderRecords(String folderName) {
        List<FileConversionRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM " + configManager.getDbSchema() + ".file_conversion_records WHERE folder_name = ? ORDER BY conversion_time DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, folderName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    FileConversionRecord record = new FileConversionRecord();
                    record.setId(rs.getLong("id"));
                    record.setFolderName(rs.getString("folder_name"));
                    record.setFileName(rs.getString("file_name"));
                    record.setFilePath(rs.getString("file_path"));
                    record.setOutputPath(rs.getString("output_path"));
                    record.setFileSize(rs.getLong("file_size"));
                    record.setConversionTime(rs.getTimestamp("conversion_time").toLocalDateTime());
                    record.setStatus(rs.getString("status"));
                    
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            System.err.println("获取文件夹记录失败: " + e.getMessage());
        }
        
        return records;
    }
    
    /**
     * 获取所有处理过的文件夹名称
     */
    public List<String> getProcessedFolders() {
        List<String> folders = new ArrayList<>();
        String sql = "SELECT DISTINCT folder_name FROM " + configManager.getDbSchema() + ".file_conversion_records ORDER BY folder_name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                folders.add(rs.getString("folder_name"));
            }
        } catch (SQLException e) {
            System.err.println("获取已处理文件夹列表失败: " + e.getMessage());
        }
        
        return folders;
    }
    
    /**
     * 关闭数据库连接
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("数据库连接已关闭");
            }
        } catch (SQLException e) {
            System.err.println("关闭数据库连接失败: " + e.getMessage());
        }
    }
    
    /**
     * 文件转换记录实体类
     */
    public static class FileConversionRecord {
        private Long id;
        private String folderName;
        private String fileName;
        private String filePath;
        private String outputPath;
        private Long fileSize;
        private LocalDateTime conversionTime;
        private String status;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getFolderName() { return folderName; }
        public void setFolderName(String folderName) { this.folderName = folderName; }
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public String getOutputPath() { return outputPath; }
        public void setOutputPath(String outputPath) { this.outputPath = outputPath; }
        
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        
        public LocalDateTime getConversionTime() { return conversionTime; }
        public void setConversionTime(LocalDateTime conversionTime) { this.conversionTime = conversionTime; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}