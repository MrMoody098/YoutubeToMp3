package moody.bot.youtubedl.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class YoutubeService {

    private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

    /**
     * Downloads the audio from YouTube using yt-dlp and converts it to MP3 with FFmpeg.
     * @param videoId The YouTube video ID
     * @return The resulting MP3 file
     * @throws IOException If yt-dlp or FFmpeg fails
     * @throws InterruptedException If the process is interrupted
     */
    public File downloadAndConvertToMp3(String videoId) throws IOException, InterruptedException {
        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

        // Prepare the temporary file paths
        File audioFile = tempDir.resolve(videoId + ".m4a").toFile();
        File mp3File = tempDir.resolve(videoId + ".mp3").toFile();

        // Step 1: Download audio-only stream with yt-dlp
        ProcessBuilder ytDlpBuilder = new ProcessBuilder(
                "C:\\Users\\ticta\\MyRepos\\SoftwareDesign_Project\\youtubeDL\\src\\yt-dlp.exe",
                "-f", "bestaudio",
                "-o", audioFile.getAbsolutePath(),
                videoUrl
        );
        ytDlpBuilder.inheritIO();
        Process ytDlpProcess = ytDlpBuilder.start();
        int ytExitCode = ytDlpProcess.waitFor();
        if (ytExitCode != 0) {
            throw new IOException("yt-dlp exited with code " + ytExitCode);
        }

        // Step 2: Convert downloaded audio to MP3 using FFmpeg
        ProcessBuilder ffmpegBuilder = new ProcessBuilder(
                "C:\\Users\\ticta\\Downloads\\ffmpeg-master-latest-win64-gpl-shared\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe",
                "-y",
                "-i", audioFile.getAbsolutePath(),
                "-vn",
                "-codec:a", "libmp3lame",
                "-qscale:a", "2",
                mp3File.getAbsolutePath()
        );
        ffmpegBuilder.inheritIO();
        Process ffmpegProcess = ffmpegBuilder.start();
        int ffExitCode = ffmpegProcess.waitFor();
        if (ffExitCode != 0) {
            throw new IOException("FFmpeg exited with code " + ffExitCode);
        }

        // Clean up intermediate file if desired
        // audioFile.delete();

        return mp3File;
    }
}