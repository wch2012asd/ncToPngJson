package com.geovis.tools.png;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * PNG工具类
 */
public class PngUtils {
    
    public static void savePng(BufferedImage image, String filePath) {
        try {
            File outputFile = new File(filePath);
            outputFile.getParentFile().mkdirs();
            ImageIO.write(image, "PNG", outputFile);
        } catch (Exception e) {
            throw new RuntimeException("保存PNG文件失败: " + filePath, e);
        }
    }
    
    public static BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    
    /**
     * 从左下角开始写入数据到PNG
     */
    public static void writeDataToPngFromLeftBottom(String filePath, double[][] data, int width, int height) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // 遍历数据数组，从左下角开始
            for (int y = 0; y < height && y < data.length; y++) {
                for (int x = 0; x < width && x < data[y].length; x++) {
                    double value = data[y][x];
                    // 将数据值转换为颜色（简单的灰度映射）
                    int colorValue = (int) Math.max(0, Math.min(255, value * 255));
                    Color color = new Color(colorValue, colorValue, colorValue);
                    
                    // 从左下角开始，所以y坐标需要翻转
                    int imageY = height - 1 - y;
                    image.setRGB(x, imageY, color.getRGB());
                }
            }
            
            g2d.dispose();
            
            // 保存图片
            File outputFile = new File(filePath);
            outputFile.getParentFile().mkdirs();
            ImageIO.write(image, "PNG", outputFile);
        } catch (Exception e) {
            throw new RuntimeException("写入PNG文件失败: " + filePath, e);
        }
    }
    
    /**
     * 从左上角开始写入数据到PNG
     */
    public static void writeDataToPngFromLeftTop(String filePath, double[][] data, int width, int height) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            // 遍历数据数组，从左上角开始
            for (int y = 0; y < height && y < data.length; y++) {
                for (int x = 0; x < width && x < data[y].length; x++) {
                    double value = data[y][x];
                    // 将数据值转换为颜色（简单的灰度映射）
                    int colorValue = (int) Math.max(0, Math.min(255, value * 255));
                    Color color = new Color(colorValue, colorValue, colorValue);
                    image.setRGB(x, y, color.getRGB());
                }
            }
            
            // 保存图片
            File outputFile = new File(filePath);
            outputFile.getParentFile().mkdirs();
            ImageIO.write(image, "PNG", outputFile);
        } catch (Exception e) {
            throw new RuntimeException("写入PNG文件失败: " + filePath, e);
        }
    }
}