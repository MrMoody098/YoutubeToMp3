package moody.bot.youtubedl.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public File downloadAndConvertToMp3(String videoId, File targetLocation) throws IOException, InterruptedException {
        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

        // Step 1: Get the video title using yt-dlp
        ProcessBuilder ytDlpInfoBuilder = new ProcessBuilder(
                "C:\\Users\\ticta\\MyRepos\\YoutubeToMp3\\youtubeDL\\src\\yt-dlp.exe",
                "--get-title", videoUrl
        );
        Process ytDlpInfoProcess = ytDlpInfoBuilder.start();
        int ytInfoExitCode = ytDlpInfoProcess.waitFor();
        if (ytInfoExitCode != 0) {
            throw new IOException("yt-dlp failed to retrieve video title");
        }

        // Capture the video title
        String videoTitle = new String(ytDlpInfoProcess.getInputStream().readAllBytes()).trim();

        // Replace spaces with underscores to create a valid filename
        String formattedTitle = videoTitle.replace(" ", "_");

        // Prepare the target file path for audio download
        File audioFile = new File(targetLocation, formattedTitle + ".m4a");

        // Step 2: Download audio-only stream with yt-dlp
        ProcessBuilder ytDlpBuilder = new ProcessBuilder(
                "C:\\Users\\ticta\\MyRepos\\YoutubeToMp3\\youtubeDL\\src\\yt-dlp.exe",
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

        // Step 3: Convert downloaded audio to MP3 using FFmpeg and save to target location
        File mp3File = new File(targetLocation, formattedTitle + ".mp3");
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

        // Optionally clean up intermediate audio file
        // audioFile.delete();

        return mp3File;  // Return the MP3 file after successful conversion
    }





    public File downloadPlaylistAndZip(String playlistUrl) throws IOException, InterruptedException {
        String playlistId = UUID.randomUUID().toString().substring(0, 8);
        Path playlistDir = tempDir.resolve("playlist_" + playlistId);
        Files.createDirectories(playlistDir);

        // Download all audio files in best audio format
        ProcessBuilder ytDlpBuilder = new ProcessBuilder(
                "C:\\Users\\ticta\\MyRepos\\YoutubeToMp3\\youtubeDL\\src\\yt-dlp.exe",
                "-f", "bestaudio",
                "-x",
                "--audio-format", "mp3",
                "-o", playlistDir.resolve("%(title)s.%(ext)s").toString(),
                playlistUrl
        );
        ytDlpBuilder.inheritIO();
        Process ytDlpProcess = ytDlpBuilder.start();
        int ytExitCode = ytDlpProcess.waitFor();
        if (ytExitCode != 0) {
            throw new IOException("yt-dlp exited with code " + ytExitCode);
        }

        // Zip the MP3s
        File zipFile = tempDir.resolve("playlist_" + playlistId + ".zip").toFile();
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Files.walk(playlistDir)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            ZipEntry zipEntry = new ZipEntry(file.getFileName().toString());
                            zos.putNextEntry(zipEntry);
                            Files.copy(file, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        // Optionally clean up unzipped files
        // FileUtils.deleteDirectory(playlistDir.toFile());

        return zipFile;
    }

}