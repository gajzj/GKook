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
import java.util.List;

@GCommand("voiceJoin")
public class VoiceJoinCommand extends AbstractCommand {

    @Override
    protected void executeCommand() {
        solution2();
    }


    void songList() {

    }

    void solution2() {
        HttpResponse<String> response = BotManager.voiceJoin(getUserArguments().get(0));

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
        if (bitrate.equals("32000")) {
            bitrate = "48k";
        } else {
            bitrate = bitrate.replace("000", "k");
        }
        System.out.println(bitrate);
        Boolean rtcpMux = root.get("data").get("rtcp_mux").asBoolean();


        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);

        ProcessBuilder processBuilder = new ProcessBuilder(
                ffmpeg, "-re",                     // 实时模式
                "-stream_loop", "0",
                "-i", "D:\\bot\\song\\wyy\\HOYO-MiX - Da Capo.mp3",                // 输入文件
                "-acodec", "pcm_s16le",            // 将文件转换为 PCM 格式
                "-ac", "2",                        // 输出音频的通道数量
                "-ar", "48000",                    // 采样率
                "-ab", bitrate,                      // 比特率
                "-filter:a", "volume=0.45",         // 音量调整
                "-map", "0:a:0",
                "-acodec", "libopus",              //编码为 Opus 格式
                "-f", "tee",                       // 使用 tee 格式
                "[select=a:f=rtp:ssrc=1111:payload_type=111]rtp://" + ip + ":" + port + "?rtcpport=" + rtcpPort // 输出目标
        );

        new Thread(() -> {
            int exitCode = 0;
            try {
                exitCode = processBuilder.inheritIO().start().waitFor();
                System.out.println("exitCode = " + exitCode);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
