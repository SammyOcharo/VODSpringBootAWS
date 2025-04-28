package com.samdev.videoOnDemand.Controller;

import com.samdev.videoOnDemand.Config.UploadProperties;
import com.samdev.videoOnDemand.RequestDTO.AssembleRequest;
import com.samdev.videoOnDemand.Service.RedisUploadService;
import com.samdev.videoOnDemand.Service.S3Uploader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/videos/upload")
public class VideoUploadController {

    private final RedisUploadService redisUploadService;
    private final UploadProperties uploadProperties;
    private final S3Uploader s3Uploader;

    public VideoUploadController(RedisUploadService redisUploadService, UploadProperties uploadProperties, S3Uploader s3Uploader) {
        this.redisUploadService = redisUploadService;
        this.uploadProperties = uploadProperties;
        this.s3Uploader = s3Uploader;
    }

    @PostMapping("/chunk")
    public ResponseEntity<?> uploadChunk(
            @RequestParam("videoId") String videoId,
            @RequestParam("chunkNumber") Integer chunkNumber,
            @RequestParam("totalChunks") Integer totalChunks,
            @RequestParam("file")MultipartFile file
            ) throws IOException {
        File dir = new File(uploadProperties.getTempDir(), videoId);
        if(!dir.exists()) dir.mkdirs();

        File chunkFile = new File(dir, "chunk-" + chunkNumber);
        file.transferTo(chunkFile);

        System.out.println("Received chunk: " + chunkNumber + " of " + totalChunks + " for video " + videoId);


        //Redis state
        redisUploadService.saveChunkInfo(videoId, chunkNumber);
        redisUploadService.setUploadStatus(videoId, "UPLOADING");

        Set<Object> uploadedChunks = redisUploadService.getUploadChunks(videoId);
        if(uploadedChunks.size() == totalChunks){
            redisUploadService.setUploadStatus(videoId, "COMPLETED");
        }

        String uploadStatus = redisUploadService.getUploadStatus(videoId);
        if (uploadStatus == null) {
            uploadStatus = "UNKNOWN"; // Set a default status if it's null
        }

        return ResponseEntity.ok(Map.of(
                "message", "Chunk uploaded",
                "uploaded", uploadedChunks.size(),
                "status", uploadStatus
        ));
    }

    @PostMapping("/assemble")
    public ResponseEntity<?> assembleVideo(@RequestBody AssembleRequest request){
        try {
            String path = redisUploadService.assembleChunks(
                    request.getVideoId(),
                    request.getFileName(),
                    request.getTotalChunks()
            );

            File finalVideoFile = new File(path);

            //upload to s3
            String s3Url = s3Uploader.uploadFile("videos/" + request.getFileName(), finalVideoFile);
            return ResponseEntity.ok(Map.of("message", "Video assembled successfully", "path", s3Url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
