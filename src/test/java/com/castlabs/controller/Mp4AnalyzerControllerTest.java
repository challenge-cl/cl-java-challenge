package com.castlabs.controller;

import com.castlabs.isobmff.Box;
import com.castlabs.isobmff.BoxNode;
import com.castlabs.isobmff.ISOBmff;
import com.castlabs.service.Mp4AnalyzerService;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

@WebMvcTest(Mp4AnalyzerController.class)
public class Mp4AnalyzerControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

        mockMvc.perform(MockMvcRequestBuilders.get("/mp4/analyze")
                .accept(MediaType.APPLICATION_JSON)
                .param("url", "http://fake-url.com/file.mp4")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.boxes", hasSize(2)))
                .andExpect(jsonPath("$.boxes[0].box.type", is("moof")))
                .andExpect(jsonPath("$.boxes[0].box.length", is(181)))
                .andExpect(jsonPath("$.boxes[0].childs", hasSize(2)))
                .andExpect(jsonPath("$.boxes[0].childs[0].box.type", is("mfhd")))
                .andExpect(jsonPath("$.boxes[0].childs[0].box.length", is(16)))
                .andExpect(jsonPath("$.boxes[0].childs[1].box.type", is("traf")))
                .andExpect(jsonPath("$.boxes[0].childs[1].box.length", is(157)))
                .andExpect(jsonPath("$.boxes[0].childs[1].childs", hasSize(4)))
                .andExpect(jsonPath("$.boxes[0].childs[1].childs[0].box.type", is("tfhd")))
                .andExpect(jsonPath("$.boxes[0].childs[1].childs[0].box.length", is(24)))
                .andExpect(jsonPath("$.boxes[0].childs[1].childs[1].box.type", is("trun")))
                .andExpect(jsonPath("$.boxes[0].childs[1].childs[1].box.length", is(20)))
                .andExpect(jsonPath("$.boxes[0].childs[1].childs[2].box.type", is("uuid")))
                .andExpect(jsonPath("$.boxes[0].childs[1].childs[2].box.length", is(44)))
                .andExpect(jsonPath("$.boxes[0].childs[1].childs[3].box.type", is("uuid")))
                .andExpect(jsonPath("$.boxes[0].childs[1].childs[3].box.length", is(61)))
                .andExpect(jsonPath("$.boxes[1].box.type", is("mdat")))
                .andExpect(jsonPath("$.boxes[1].box.length", is(17908)));
    }
}
