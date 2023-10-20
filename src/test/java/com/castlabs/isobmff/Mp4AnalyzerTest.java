package com.castlabs.isobmff;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Mp4AnalyzerTest {

    @Test
    public void testAnalyzeMp4_SingleTopLevelBox() {
        // Create a test MP4 file with a single top-level 'moov' box
        // Test that the 'moov' box is correctly identified and stored as a top-level box
        // Assert that the 'moov' box is a top-level box
        // Assert that there is only one top-level box
    }

    @Test
    public void testAnalyzeMP4_MultipleTopLevelBoxes() {
        // Create a test MP4 file with multiple top-level 'moov' boxes
        // Test that all 'moov' boxes are correctly identified and stored as top-level boxes
        // Assert that all 'moov' boxes are top-level boxes
        // Assert that there are multiple top-level boxes
    }

    @Test
    public void testAnalyzeMP4_HierarchicalBoxes() {
        // Create a test MP4 file with a hierarchical structure of boxes
        // Test that the hierarchy is correctly created
        // Assert the hierarchical relationships between parent and child boxes
    }

    @Test
    public void testAnalyzeMP4_ComplexStructure() throws IOException {
        RandomAccessFile file = new RandomAccessFile(getClass().getClassLoader().getResource("text0.mp4").getFile(), "r");
        ISOBmff mp4 = Mp4Analyzer.analyzeMp4(file);
        assertEquals(2, mp4.getBoxNodes().size());

        //Assert moof box
        var moofNode = mp4.getBoxNodes().get(0);
        var moof = moofNode.getBox();
        assertEquals("moof", moof.getType());
        assertEquals(181, moof.getLength());
        assertEquals(2, moofNode.getChilds().size());
        //Assert mfhd box
        var mfhd = moofNode.getChilds().get(0).getBox();
        assertEquals("mfhd", mfhd.getType());
        assertEquals(16, mfhd.getLength());
        //Assert traf box
        var trafNode = moofNode.getChilds().get(1);
        var traf = trafNode.getBox();
        assertEquals("traf", traf.getType());
        assertEquals(157, traf.getLength());
        assertEquals(4, trafNode.getChilds().size());
        //Assert tfhd
        var tfhd = trafNode.getChilds().get(0).getBox();
        assertEquals("tfhd", tfhd.getType());
        assertEquals(24, tfhd.getLength());
        //Assert tfhd
        var trun = trafNode.getChilds().get(1).getBox();
        assertEquals("trun", trun.getType());
        assertEquals(20, trun.getLength());
        //Assert uuid 1
        var uuid1 = trafNode.getChilds().get(2).getBox();
        assertEquals("uuid", uuid1.getType());
        assertEquals(44, uuid1.getLength());
        //Assert uuid 2
        var uuid2 = trafNode.getChilds().get(3).getBox();
        assertEquals("uuid", uuid2.getType());
        assertEquals(61, uuid2.getLength());
        //Assert mdat
        var mdat = mp4.getBoxNodes().get(1).getBox();
        assertEquals("mdat", mdat.getType());
        assertEquals(17908, mdat.getLength());
    }
}
