package com.castlabs.service;

import com.castlabs.isobmff.Box;
import com.castlabs.isobmff.BoxNode;
import com.castlabs.isobmff.ISOBmff;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

@Service
public class Mp4AnalyzerService {

    private String readBoxType(ReadableByteChannel channel) throws IOException {
        ByteBuffer typeBytes = ByteBuffer.allocate(4);
        channel.read(typeBytes);
        typeBytes.flip();
        return StandardCharsets.UTF_8.decode(typeBytes).toString();
    }

    public ISOBmff analyzeMp4(String url) throws URISyntaxException, IOException {
        try (var channel = Channels.newChannel(new URI(url).toURL().openStream())) {
            return analyzeMp4(channel);
        }
    }

    protected ISOBmff analyzeMp4(ReadableByteChannel channel) throws IOException {
        ISOBmff isoBmff = new ISOBmff();
        BoxNode parentNode = null;
        long parentNodeOffset = 0;
        long totalBytesRead = 0;
        var buff = ByteBuffer.allocate(4);
        while (channel.read(buff) != -1) {
            totalBytesRead += 4;
            if (totalBytesRead >= parentNodeOffset) {
                parentNode = null;
            }
            buff.flip();
            int boxLength = buff.getInt();
            buff.clear();
            String boxType = readBoxType(channel);
            totalBytesRead += 4;

            Box box = new Box(boxType, boxLength);
            BoxNode node = new BoxNode(box);

            if (parentNode == null) {
                isoBmff.addBoxNode(node);
                parentNodeOffset = totalBytesRead + box.getLength() - 8;
            } else {
                parentNode.addChild(node);
            }

            switch (boxType) {
                case "moof", "traf" -> parentNode = node;
                default -> {
                    ByteBuffer skipBuffer = ByteBuffer.allocate(boxLength - 8);
                    while (skipBuffer.hasRemaining()) {
                        channel.read(skipBuffer);
                    }
                    totalBytesRead += (boxLength - 8);
                }
            }
        }
        return isoBmff;
    }

}
