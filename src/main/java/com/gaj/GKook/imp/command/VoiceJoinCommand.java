package com.gaj.GKook.imp.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaj.GKook.BotManager;
import com.gaj.GKook.framework.commad.AbstractCommand;
import com.gaj.GKook.framework.commad.GCommand;
import org.bytedeco.javacpp.Loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpResponse;

@GCommand("voiceJoin")
public class VoiceJoinCommand extends AbstractCommand {

    @Override
    protected void executeCommand() {
        HttpResponse<String> response = BotManager.voiceJoin();

        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = null;
        try {
            root = mapper.readTree(response.body());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String ip = root.get("data").get("ip").asText();
        String port = root.get("data").get("port").asText();
        String rtcpPort = root.get("data").get("rtcp_port").asText();
        String audioSsrc = root.get("data").get("audio_ssrc").asText();
        String audioPt = root.get("data").get("audio_pt").asText();
        String bitrate = root.get("data").get("bitrate").asText();
        Boolean rtcpMux = root.get("data").get("rtcp_mux").asBoolean();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
// 加载ffmpeg可执行文件的路径
                String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

                ProcessBuilder load = new ProcessBuilder(
                        ffmpeg, "-i", "output.mp3",
                        "-format", "pcm_s16le",
                        "-ab", "48k",
                        "-f", "wav", "-"
                );

                ProcessBuilder processBuilder = new ProcessBuilder(
                        ffmpeg, "-re",
                        "-i", "-",
                        "-map", "0:a:0",
                        "-acodec", "libopus",
                        "-ab", "32k",
                        "-ac", "2",
                        "-ar", "48000",
                        "-filter:a", "volume=0.8",
                        "-f", "tee",
                        "[select=a:f=rtp:ssrc=1111:payload_type=111]rtp://" + ip + ":" + port + "?rtcpport=" + rtcpPort
                );

                // 启动第一个进程
                try {
                    Process loadProcess = load.start();
                    InputStream loadOutput = loadProcess.getInputStream();

                    // 启动第二个进程，并将第一个进程的输出作为第二个进程的输入
                    Process process = processBuilder.start();
                    OutputStream processInput = process.getOutputStream();

                    // 将第一个进程的输出写入第二个进程的输入
                    new Thread(() -> {
                        try {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = loadOutput.read(buffer)) != -1) {
                                processInput.write(buffer, 0, bytesRead);
                            }
                            processInput.close();  // 关闭输出流，表示写入结束
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                    // 等待两个进程完成
                    try {
                        int loadExitCode = loadProcess.waitFor();
                        int processExitCode = process.waitFor();
                        System.out.println("First process exited with code: " + loadExitCode);
                        System.out.println("Second process exited with code: " + processExitCode);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();
        try {
            Thread.sleep(10000);
            t.interrupt();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
