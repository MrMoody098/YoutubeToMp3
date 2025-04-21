package moody.bot.youtubedl.controller;

import moody.bot.youtubedl.YoutubeUtil;
import moody.bot.youtubedl.service.YoutubeService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.InputStream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;

import org.springframework.web.bind.annotation.*; // assuming you're using Spring annotations

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ConvertController {

    private final YoutubeService youtubeService;

    public ConvertController(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;
    }

    @GetMapping("/convert")
    public ResponseEntity<StreamingResponseBody> convert(@RequestParam String url) {
        try {
            String videoId = YoutubeUtil.extractVideoId(url); // You need to write this method
            File outputFile = new File("output.mp3"); // Create or specify the output file
            File mp3File = youtubeService.downloadAndConvertToMp3(videoId, outputFile);

            InputStream inputStream = new FileInputStream(mp3File);
            long fileLength = mp3File.length();

            StreamingResponseBody responseBody = outputStream -> {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                }
                inputStream.close();
                // Optionally delete file after stream
                // mp3File.delete();
            };

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(fileLength);
            headers.setContentDisposition(ContentDisposition.attachment().filename(mp3File.getName()).build());

            return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/convertPlaylist")
    public ResponseEntity<StreamingResponseBody> convertPlaylist(@RequestParam String url) {
        try {
            File zipFile = youtubeService.downloadPlaylistAndZip(url);
            InputStream inputStream = new FileInputStream(zipFile);

            StreamingResponseBody stream = outputStream -> {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                }
                inputStream.close();
                // Optional: zipFile.delete();
            };

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(zipFile.length());
            headers.setContentDisposition(ContentDisposition.attachment().filename(zipFile.getName()).build());

            return new ResponseEntity<>(stream, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
