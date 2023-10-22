package com.castlabs.controller;

import com.castlabs.isobmff.ISOBmff;
import com.castlabs.service.Mp4AnalyzerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RestController
@RequestMapping("/mp4/analyze")
public class Mp4AnalyzerController {

    private final Mp4AnalyzerService analyzer;

    public Mp4AnalyzerController(Mp4AnalyzerService analyzer) {
        this.analyzer = analyzer;
    }

    @GetMapping
    public Mono<ISOBmff> analyzeFile(@RequestParam String url) throws IOException, URISyntaxException {
        return Mono.just(analyzer.analyzeMp4(url));
    }
}
