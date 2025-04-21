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
    public ResponseEntity<Resource> convert(@RequestParam String url) throws IOException, InterruptedException {
        String videoId = YoutubeUtil.extractVideoId(url);
        File mp3File = youtubeService.downloadAndConvertToMp3(videoId);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(mp3File));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + mp3File.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(mp3File.length())
                .body(resource);
    }
}
