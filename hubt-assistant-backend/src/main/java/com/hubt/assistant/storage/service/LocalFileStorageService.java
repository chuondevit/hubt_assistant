package com.hubt.assistant.storage.service;

import com.hubt.assistant.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            );

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(
                    "jpg",
                    "jpeg",
                    "png",
                    "webp"
            );

    private static final long MAX_AVATAR_SIZE =
            5L * 1024L * 1024L;

    private final Path uploadRoot;

    public LocalFileStorageService(
            @Value("${app.storage.upload-dir:uploads}")
            String uploadDir
    ) {

        this.uploadRoot =
                Paths.get(uploadDir)
                        .toAbsolutePath()
                        .normalize();

        try {

            Files.createDirectories(
                    this.uploadRoot
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Không thể tạo thư mục lưu file",
                    e
            );
        }
    }


    public String storeAvatar(
            MultipartFile file
    ) {

        validateAvatar(file);

        String originalFilename =
                file.getOriginalFilename();

        String extension =
                getExtension(
                        originalFilename
                );

        String storedFilename =
                UUID.randomUUID()
                        + "."
                        + extension;

        Path avatarDirectory =
                uploadRoot
                        .resolve("avatars")
                        .normalize();

        try {

            Files.createDirectories(
                    avatarDirectory
            );

            Path target =
                    avatarDirectory
                            .resolve(storedFilename)
                            .normalize();

            if (!target.startsWith(
                    avatarDirectory
            )) {

                throw new BusinessException(
                        "INVALID_FILE_PATH",
                        "Đường dẫn file không hợp lệ"
                );
            }

            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {

            throw new BusinessException(
                    "FILE_UPLOAD_FAILED",
                    "Không thể lưu ảnh đại diện"
            );
        }

        return "/uploads/avatars/"
                + storedFilename;
    }


    public void deleteAvatar(
            String avatarUrl
    ) {

        if (avatarUrl == null
                || avatarUrl.isBlank()
                || !avatarUrl.startsWith(
                        "/uploads/avatars/"
                )) {

            return;
        }

        String filename =
                avatarUrl.substring(
                        "/uploads/avatars/"
                                .length()
                );

        Path avatarDirectory =
                uploadRoot
                        .resolve("avatars")
                        .normalize();

        Path file =
                avatarDirectory
                        .resolve(filename)
                        .normalize();

        if (!file.startsWith(
                avatarDirectory
        )) {
            return;
        }

        try {

            Files.deleteIfExists(file);

        } catch (IOException ignored) {

            // Không làm fail request chỉ vì
            // không xóa được avatar cũ.
        }
    }


    private void validateAvatar(
            MultipartFile file
    ) {

        if (file == null
                || file.isEmpty()) {

            throw new BusinessException(
                    "AVATAR_REQUIRED",
                    "Vui lòng chọn ảnh đại diện"
            );
        }

        if (file.getSize()
                > MAX_AVATAR_SIZE) {

            throw new BusinessException(
                    "AVATAR_TOO_LARGE",
                    "Ảnh đại diện không được vượt quá 5MB"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES
                .contains(
                        contentType
                                .toLowerCase(
                                        Locale.ROOT
                                )
                )) {

            throw new BusinessException(
                    "INVALID_AVATAR_TYPE",
                    "Chỉ hỗ trợ JPG, PNG hoặc WEBP"
            );
        }

        String extension =
                getExtension(
                        file.getOriginalFilename()
                );

        if (!ALLOWED_EXTENSIONS
                .contains(extension)) {

            throw new BusinessException(
                    "INVALID_AVATAR_EXTENSION",
                    "Phần mở rộng ảnh không hợp lệ"
            );
        }
    }


    private String getExtension(
            String filename
    ) {

        if (filename == null
                || filename.isBlank()) {

            throw new BusinessException(
                    "INVALID_FILENAME",
                    "Tên file không hợp lệ"
            );
        }

        int dotIndex =
                filename.lastIndexOf('.');

        if (dotIndex < 0
                || dotIndex
                == filename.length() - 1) {

            throw new BusinessException(
                    "INVALID_FILENAME",
                    "File không có phần mở rộng hợp lệ"
            );
        }

        return filename
                .substring(dotIndex + 1)
                .toLowerCase(
                        Locale.ROOT
                );
    }
}