package com.castlabs.service;

import com.castlabs.isobmff.Box;
import com.castlabs.isobmff.BoxNode;
import com.castlabs.isobmff.ISOBmff;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

@Service
public class Mp4AnalyzerService {

    private String readBoxType(RandomAccessFile file) throws IOException {
        var typeBytes = new byte[4];
        file.readFully(typeBytes);
        return new String(typeBytes, StandardCharsets.UTF_8);
    }

    public ISOBmff analyzeMp4(String url) throws URISyntaxException, IOException {
        try (var readableByteChannel = Channels.newChannel(new URI(url).toURL().openStream());
             var fileOutputStream = new FileOutputStream("tmp");
             var fileChannel = fileOutputStream.getChannel()) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }

        // Create a RandomAccessFile from the local file
        var randomAccessFile = new RandomAccessFile("tmp", "r");
        return analyzeMp4(randomAccessFile);
    }

    protected ISOBmff analyzeMp4(RandomAccessFile file) throws IOException {
        var fileSize = file.length();
        var isoBmff = new ISOBmff();
        BoxNode parentNode = null;
        var parentNodeOffset = 0L;
        while (file.getFilePointer() < fileSize) {
            if (file.getFilePointer() >= parentNodeOffset) {
                parentNode = null;
            }
            var boxLength = file.readInt();
            var boxType = readBoxType(file);
            var box = new Box(boxType, boxLength);
            var node = new BoxNode(box);
            if (parentNode == null) {
                isoBmff.addBoxNode(node);
                parentNodeOffset = file.getFilePointer() + box.getLength() - 8;
            } else {
                parentNode.addChild(node);
            }
            switch (boxType) {
                case "moof", "traf" -> parentNode = node;
                default -> file.seek(file.getFilePointer() + boxLength - 8);
            }
        }
        return isoBmff;
    }
}
