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
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;

@Controller
@RestController
@RequestMapping("/mp4/analyze")
public class Mp4AnalyzerController {

    @GetMapping
    public ISOBmff analyzeFile(@RequestParam String url) throws IOException, URISyntaxException {
        try (var readableByteChannel = Channels.newChannel(new URI(url).toURL().openStream());
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
