package com.samdev.videoOnDemand.Service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.Set;

@Service
public class RedisUploadService {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUploadService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveChunkInfo(String videoId, int chunkNumber){
        redisTemplate.opsForSet().add("upload:chunks:" + videoId, chunkNumber);
    }

    public Set<Object> getUploadChunks(String videoId){
        return redisTemplate.opsForSet().members("upload:chunks:" + videoId);
    }

    public void setUploadStatus(String videoId, String status){
        redisTemplate.opsForValue().set("upload:status:" + videoId, status);
    }

    public String getUploadStatus(String videoId){
        Object value = redisTemplate.opsForValue().get("upload:status:" + videoId);
        return value != null ? value.toString():null;
    }

    public void clearUpload(String videoId){
        redisTemplate.delete("Uploaded:chunks:" + videoId);
        redisTemplate.delete("uploaded:status" + videoId);
    }

    public String assembleChunks(String videoId, String fileName, int totalChunks) throws Exception {
        String chunkDir = "/tmp/uploads/" + videoId;

        String outputFilePath = chunkDir + "/" + fileName;
        File listFile = new File(chunkDir + "/merge.txt");

        // Step 1: Create merge.txt with paths to all chunks
        try(FileWriter writer = new FileWriter(listFile)) {
            for(int i=1; i <= totalChunks; i++){
                writer.write("file 'chunk-" + i + "'\n");
            }
        }

        // Step 2: Use ffmpeg to concatenate using concat demuxer
        ProcessBuilder pb  = new ProcessBuilder(
                "ffmpeg", "-f", "concat", "-safe", "0",
                "-i", "merge.txt", "-c", "copy", fileName
        );
        pb.directory(new File(chunkDir));
        pb.redirectErrorStream(true);

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null){
            System.out.println(line);
        }
        int exitCode = process.waitFor();
        if(exitCode != 0){
            throw new RuntimeException("ffmpeg merge failed with code "+ exitCode);
        }

        return outputFilePath;
    }
}
