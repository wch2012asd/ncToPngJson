package com.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * NC文件处理器 - 监控目录变化并将NC文件转换为红黑图
 */
public class NCFileProcessor {
    
    private final ConfigManager configManager;
    private final ScheduledExecutorService scheduler;
    private final DatabaseManager databaseManager;
    
    public NCFileProcessor() {
        this.configManager = new ConfigManager();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        
        // 尝试初始化数据库管理器
        DatabaseManager tempDatabaseManager = null;
        try {
            tempDatabaseManager = new DatabaseManager(configManager);
        } catch (Exception e) {
            System.err.println("数据库初始化失败，程序将在无数据库模式下运行: " + e.getMessage());
        }
        this.databaseManager = tempDatabaseManager;
    }
    
    /**
     * 启动文件监控服务
     */
    public void start() {
        System.out.println("启动NC文件处理器...");
        System.out.println("输入目录: " + configManager.getInputDirectory());
        System.out.println("输出目录: " + configManager.getOutputDirectory());
        
        // 每3秒检查一次目录变化
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkDirectoryChanges();
            } catch (Exception e) {
                System.err.println("定时任务执行异常: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.SECONDS);
        
        System.out.println("NC文件处理器已启动，开始监控...");
    }
    
    /**
     * 停止服务
     */
    public void stop() {
        System.out.println("停止NC文件处理器...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // 关闭数据库连接（如果存在）
        if (databaseManager != null) {
            databaseManager.close();
        }
    }
    
    /**
     * 检查目录变化
     */
    private void checkDirectoryChanges() {
        try {
            Path inputDir = Paths.get(configManager.getInputDirectory());
            
            if (!Files.exists(inputDir)) {
                System.out.println("输入目录不存在: " + inputDir);
                return;
            }
            
            // 遍历输入目录下的所有子文件夹
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputDir, Files::isDirectory)) {
                for (Path folder : stream) {
                    String folderName = folder.getFileName().toString();
                    
                    // 检查数据库中是否已处理过该文件夹（如果数据库可用）
                    boolean shouldProcess = (databaseManager == null) || !databaseManager.isFolderProcessed(folderName);
                    
                    System.out.println("检查文件夹: " + folderName + " (数据库可用: " + (databaseManager != null) + ", 需要处理: " + shouldProcess + ")");
                    
                    if (shouldProcess) {
                        System.out.println("发现新文件夹: " + folderName);
                        System.out.println("开始处理文件夹: " + folderName);
                        
                        // 直接处理文件夹中的NC文件（同步处理）
                        processNCFilesInFolder(folder);
                        
                        System.out.println("完成处理文件夹: " + folderName);
                    } else {
                        // 检查是否有新的NC文件需要处理
                        checkForNewFilesInFolder(folder);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("检查目录变化时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 处理文件夹中的NC文件
     */
    private void processNCFilesInFolder(Path folder) throws Exception {
        System.out.println("  查找NC文件...");
        
        // 查找文件夹中的所有NC文件
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.nc")) {
            boolean foundFiles = false;
            for (Path ncFile : stream) {
                foundFiles = true;
                System.out.println("  找到NC文件: " + ncFile.getFileName());
                
                try {
                    processNCFile(ncFile);
                } catch (Exception e) {
                    System.err.println("  处理NC文件失败: " + ncFile.getFileName() + " - " + e.getMessage());
                    
                    // 记录失败的转换
                    String folderName = ncFile.getParent().getFileName().toString();
                    String fileName = ncFile.getFileName().toString();
                    long fileSize = 0;
                    try {
                        fileSize = Files.size(ncFile);
                    } catch (IOException ex) {
                        // 忽略文件大小获取失败
                    }
                    
                    databaseManager.recordFileConversion(folderName, fileName, 
                        ncFile.toString(), "", fileSize, "FAILED: " + e.getMessage());
                }
            }
            
            if (!foundFiles) {
                System.out.println("  文件夹中未找到NC文件: " + folder.getFileName());
                // 也尝试查找所有文件看看有什么
                try (DirectoryStream<Path> allFiles = Files.newDirectoryStream(folder)) {
                    System.out.println("  文件夹 " + folder.getFileName() + " 中的所有文件:");
                    for (Path file : allFiles) {
                        System.out.println("    - " + file.getFileName());
                    }
                }
            }
        }
    }
    
    /**
     * 检查已处理文件夹中的新文件
     */
    private void checkForNewFilesInFolder(Path folder) throws Exception {
        String folderName = folder.getFileName().toString();
        System.out.println("  检查已处理文件夹中的新文件: " + folderName);
        
        // 查找文件夹中的所有NC文件
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.nc")) {
            for (Path ncFile : stream) {
                String fileName = ncFile.getFileName().toString();
                System.out.println("    检查文件: " + fileName);
                
                // 检查该文件是否已处理过（如果数据库可用）
                boolean shouldProcessFile = (databaseManager == null) || !databaseManager.isFileProcessed(folderName, fileName);
                
                System.out.println("    文件 " + fileName + " 是否需要处理: " + shouldProcessFile);
                
                if (shouldProcessFile) {
                    System.out.println("  发现新文件: " + fileName + " (在已处理文件夹: " + folderName + ")");
                    processNCFile(ncFile);
                } else {
                    System.out.println("  跳过已处理文件: " + fileName + " (在文件夹: " + folderName + ")");
                }
            }
        }
    }
    
    /**
     * 处理单个NC文件，转换为红黑图
     */
    private void processNCFile(Path ncFilePath) throws IOException {
        System.out.println("    开始处理NC文件: " + ncFilePath.getFileName());
        
        // 首先检查数据库中是否已有该文件的记录
        String folderName = ncFilePath.getParent().getFileName().toString();
        String fileName = ncFilePath.getFileName().toString();
        
        if (databaseManager != null && databaseManager.isFileProcessed(folderName, fileName)) {
            System.out.println("    文件已处理过，跳过: " + fileName);
            return;
        }
        
        try {
            // 计算输出目录路径
            String outputDirPath = calculateOutputDirectory(ncFilePath);
            System.out.println("    输出目录: " + outputDirPath);
            
            // 确保输出目录存在
            Files.createDirectories(Paths.get(outputDirPath));
            System.out.println("    输出目录已创建");
            
            // 使用NcToPngUtils转换NC文件
            System.out.println("    开始调用NcToPngUtils.ncToPng...");
            List<NcBeanModel> results = NcToPngUtils.ncToPng(ncFilePath.toString(), outputDirPath);
            System.out.println("    NcToPngUtils.ncToPng调用完成，结果数量: " + (results != null ? results.size() : "null"));
            
            // 记录转换结果到数据库（如果可用）
            if (databaseManager != null) {
                long fileSize = Files.size(ncFilePath);
                
                if (results != null && !results.isEmpty()) {
                    // 转换成功
                    String outputInfo = "Generated " + results.size() + " images";
                    databaseManager.recordFileConversion(folderName, fileName, 
                        ncFilePath.toString(), outputDirPath, fileSize, "SUCCESS: " + outputInfo);
                    
                    System.out.println("    成功转换NC文件，生成了 " + results.size() + " 个图像");
                    for (NcBeanModel result : results) {
                        System.out.println("      - " + result.toString());
                    }
                } else {
                    // 转换失败或无结果
                    databaseManager.recordFileConversion(folderName, fileName, 
                        ncFilePath.toString(), outputDirPath, fileSize, "SUCCESS: No variables found");
                    System.out.println("    NC文件转换完成，但未找到可转换的变量");
                }
            }
            
        } catch (Exception e) {
            System.err.println("    转换NC文件失败: " + e.getMessage());
            
            // 记录失败到数据库（如果可用）
            if (databaseManager != null) {
                long fileSize = 0;
                try {
                    fileSize = Files.size(ncFilePath);
                } catch (IOException ex) {
                    // 忽略文件大小获取失败
                }
                
                databaseManager.recordFileConversion(folderName, fileName, 
                    ncFilePath.toString(), "", fileSize, "FAILED: " + e.getMessage());
            }
            
            throw new IOException("NC文件转换失败", e);
        }
    }
    
    /**
     * 计算输出目录路径，保持目录结构
     */
    private String calculateOutputDirectory(Path ncFilePath) {
        // 获取输入和输出根目录
        Path inputRoot = Paths.get(configManager.getInputDirectory());
        Path outputRoot = Paths.get(configManager.getOutputDirectory());
        
        // 计算相对路径
        Path relativePath = inputRoot.relativize(ncFilePath.getParent());
        
        // 创建对应的输出目录结构
        Path outputDir = outputRoot.resolve(relativePath);
        
        return outputDir.toString();
    }
    
    /**
     * 保存图片并保持目录结构
     */
    private String saveImageWithDirectoryStructure(Path ncFilePath) throws IOException {
        // 获取输入和输出根目录
        Path inputRoot = Paths.get(configManager.getInputDirectory());
        Path outputRoot = Paths.get(configManager.getOutputDirectory());
        
        // 计算相对路径
        Path relativePath = inputRoot.relativize(ncFilePath.getParent());
        
        // 创建对应的输出目录结构
        Path outputDir = outputRoot.resolve(relativePath);
        Files.createDirectories(outputDir);
        
        // // 生成输出文件名
        // String imageFileName = ncFilePath.getFileName().toString().replaceAll("\\.nc$", ".png");
        // Path outputFile = outputDir.resolve(imageFileName);
        
        // // 保存图片
        // ImageIO.write(image, "PNG", outputFile.toFile());
        
        // System.out.println("红黑图已保存: " + outputFile);
        // System.out.println("目录结构: " + relativePath + " -> " + imageFileName);
        
        return outputDir.toString();
    }
    
    public static void main(String[] args) {
        NCFileProcessor processor = new NCFileProcessor();
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(processor::stop));
        
        // 启动处理器
        processor.start();
        
        // 保持程序运行
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}