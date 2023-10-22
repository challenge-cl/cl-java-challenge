package com.castlabs.controller;

import com.castlabs.isobmff.ISOBmff;
import com.castlabs.isobmff.Mp4Analyzer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

@Controller
@RestController
@RequestMapping("/mp4/analyze")
public class Mp4AnalyzerController {

    @GetMapping
    public ISOBmff analyzeFile(@RequestParam String url) throws IOException {
        System.out.println("Analyzing mp4 file: " + url);
        try (var readableByteChannel = Channels.newChannel(new URL(url).openStream());
             var fileOutputStream = new FileOutputStream("tmp");
             var fileChannel = fileOutputStream.getChannel()) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }

        // Create a RandomAccessFile from the local file
        var randomAccessFile = new RandomAccessFile("tmp", "r");

        // You can now use the RandomAccessFile for your needs
        return Mp4Analyzer.analyzeMp4(randomAccessFile);
    }
}
