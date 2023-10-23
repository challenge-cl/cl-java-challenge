package com.castlabs.controller;

import com.castlabs.isobmff.Box;
import com.castlabs.isobmff.BoxNode;
import com.castlabs.isobmff.ISOBmff;
import com.castlabs.service.Mp4AnalyzerService;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;


@ExtendWith(SpringExtension.class)
@WebFluxTest(Mp4AnalyzerController.class)
public class Mp4AnalyzerControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private Mp4AnalyzerService mp4Analyzer;

    private BoxNode getBoxNode(String type, int length) {
        return new BoxNode(new Box(type, length));
    }

    private ISOBmff getMockedIsoBmff() {
        var isobmff = new ISOBmff();
        var moof = getBoxNode("moof", 181);
        var mfhd = getBoxNode("mfhd", 16);
        var traf = getBoxNode("traf", 157);
        moof.getChilds().addAll(List.of(mfhd, traf));
        var tfhd = getBoxNode("tfhd", 24);
        var trun = getBoxNode("trun", 20);
        var uuid = getBoxNode("uuid", 44);
        var uuid2 = getBoxNode("uuid", 61);
        traf.getChilds().addAll(List.of(tfhd, trun, uuid, uuid2));
        var mdat = getBoxNode("mdat", 17908);
        isobmff.getBoxNodes().addAll(List.of(moof, mdat));
        return isobmff;
    }

    @Test
    void analyzeUrlShouldReturnAJsonRepresentationOfMp4Boxes() throws Exception {
        Mockito.when(mp4Analyzer.analyzeMp4(Mockito.anyString())).thenReturn(getMockedIsoBmff());

        webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/mp4/analyze")
                        .queryParam("url", "http://fake-url.com/file.mp4")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.boxes").value(hasSize(2))
                .jsonPath("$.boxes[0].box.type").value(is("moof"))
                .jsonPath("$.boxes[0].box.length").value(is(181))
                .jsonPath("$.boxes[0].childs").value(hasSize(2))
                .jsonPath("$.boxes[0].childs[0].box.type").value(is("mfhd"))
                .jsonPath("$.boxes[0].childs[0].box.length").value(is(16))
                .jsonPath("$.boxes[0].childs[1].box.type").value(is("traf"))
                .jsonPath("$.boxes[0].childs[1].box.length").value(is(157))
                .jsonPath("$.boxes[0].childs[1].childs").value(hasSize(4))
                .jsonPath("$.boxes[0].childs[1].childs[0].box.type").value(is("tfhd"))
                .jsonPath("$.boxes[0].childs[1].childs[0].box.length").value(is(24))
                .jsonPath("$.boxes[0].childs[1].childs[1].box.type").value(is("trun"))
                .jsonPath("$.boxes[0].childs[1].childs[1].box.length").value(is(20))
                .jsonPath("$.boxes[0].childs[1].childs[2].box.type").value(is("uuid"))
                .jsonPath("$.boxes[0].childs[1].childs[2].box.length").value(is(44))
                .jsonPath("$.boxes[0].childs[1].childs[3].box.type").value(is("uuid"))
                .jsonPath("$.boxes[0].childs[1].childs[3].box.length").value(is(61))
                .jsonPath("$.boxes[1].box.type").value(is("mdat"))
                .jsonPath("$.boxes[1].box.length").value(is(17908));
    }
}
