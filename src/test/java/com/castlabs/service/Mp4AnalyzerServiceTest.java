package com.castlabs.service;

import com.castlabs.isobmff.ISOBmff;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Mp4AnalyzerServiceTest {

    @Test
    public void testAnalyzeMP4() throws IOException {
        var mp4Analyzer = new Mp4AnalyzerService();
        RandomAccessFile file = new RandomAccessFile(getClass().getClassLoader().getResource("text0.mp4").getFile(), "r");
        ISOBmff mp4 = mp4Analyzer.analyzeMp4(file);
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
