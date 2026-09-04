package com.paton.utils.page;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class FileUploadUtil {

    /**
     * 上传文件
     * @param file 上传的文件
     * @param uploadDir 上传目录（绝对路径）
     * @param subDir 子目录，如：avatars, book-covers
     * @return 返回文件的相对路径
     */
    public static String uploadFile(MultipartFile file, String uploadDir, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        if (!isImageFile(fileExtension)) {
            throw new IllegalArgumentException("只允许上传图片文件");
        }

        // 创建日期目录
        String dateDir = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String fullDir = uploadDir + File.separator + subDir + File.separator + dateDir;

        // 确保目录存在
        Path dirPath = Paths.get(fullDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // 保存文件
        Path filePath = dirPath.resolve(fileName);
        file.transferTo(filePath.toFile());

        // 返回相对路径（用于数据库存储）
        return subDir + "/" + dateDir + "/" + fileName;
    }

    /**
     * 删除文件
     * @param filePath 文件的相对路径
     * @param uploadDir 上传目录
     */
    public static boolean deleteFile(String filePath, String uploadDir) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }

        // 构建完整路径
        Path fullPath = Paths.get(uploadDir, filePath);

        try {
            return Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取文件扩展名
     */
    private static String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex);
    }

    /**
     * 检查是否为图片文件
     */
    private static boolean isImageFile(String extension) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
        for (String ext : imageExtensions) {
            if (ext.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查文件大小
     */
    public static boolean checkFileSize(MultipartFile file, long maxSizeMB) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        long maxSize = maxSizeMB * 1024 * 1024; // 转换为字节
        return file.getSize() <= maxSize;
    }
}