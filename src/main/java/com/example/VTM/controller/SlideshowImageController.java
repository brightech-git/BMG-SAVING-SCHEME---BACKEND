package com.example.VTM.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController

@RequestMapping("/api/v1/slideshow")
public class SlideshowImageController {

    @Value("${upload.slideshow-dir}")
    private String slideshowDir;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SlideshowImageController(@Qualifier("firstJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadSlide(@RequestParam("title") String title,
                                         @RequestParam("image") MultipartFile image) {
        try {
            Files.createDirectories(Paths.get(slideshowDir));

            String originalName = image.getOriginalFilename();
            String cleanedName = (originalName != null) ? originalName.replaceAll("\\s+", "_") : "image.jpg";

            String fileName = UUID.randomUUID() + "_" + cleanedName;
            Path filePath = Paths.get(slideshowDir, fileName);
            Files.write(filePath, image.getBytes());

            String relativePath = "/uploads/slideshow/" + fileName;

            jdbcTemplate.update(
                    "INSERT INTO slideshow (title, image_path) VALUES (?, ?)",
                    title, relativePath
            );

            return ResponseEntity.ok(Map.of("message", "Slide uploaded", "path", relativePath));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> listSlides() {
        List<Map<String, Object>> slides = jdbcTemplate.queryForList("SELECT * FROM slideshow");
        return ResponseEntity.ok(slides);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSlide(@RequestParam int id) {
        try {
            String path = jdbcTemplate.queryForObject("SELECT image_path FROM slideshow WHERE id = ?", String.class, id);
            Path fullPath = Paths.get(slideshowDir, Paths.get(path).getFileName().toString());

            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
            }

            jdbcTemplate.update("DELETE FROM slideshow WHERE id = ?", id);

            return ResponseEntity.ok(Map.of("message", "Slide deleted"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSlide(@RequestParam int id,
                                         @RequestParam("title") String title,
                                         @RequestParam("image") MultipartFile image) {
        try {
            // Delete old image
            String oldPath = jdbcTemplate.queryForObject("SELECT image_path FROM slideshow WHERE id = ?", String.class, id);
            Path oldFile = Paths.get(slideshowDir, Paths.get(oldPath).getFileName().toString());

            if (Files.exists(oldFile)) {
                Files.delete(oldFile);
            }

            // Save new image
            String originalName = image.getOriginalFilename();
            String cleanedName = (originalName != null) ? originalName.replaceAll("\\s+", "_") : "image.jpg";

            String newFileName = UUID.randomUUID() + "_" + cleanedName;
            Path newFilePath = Paths.get(slideshowDir, newFileName);
            Files.write(newFilePath, image.getBytes());

            String newRelativePath = "/uploads/slideshow/" + newFileName;

            // Update DB
            jdbcTemplate.update(
                    "UPDATE slideshow SET title = ?, image_path = ? WHERE id = ?",
                    title, newRelativePath, id
            );

            return ResponseEntity.ok(Map.of("message", "Slide updated", "path", newRelativePath));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}

