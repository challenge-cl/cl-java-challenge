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

    private String readBoxType(ByteBuffer buff) throws IOException {
        var typeBytes = new byte[4];
        buff.get(typeBytes);
        return new String(typeBytes, StandardCharsets.UTF_8);
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
        var buff = ByteBuffer.allocate(1000000);
        while (channel.read(buff) != -1) {
            buff.flip();
            while(buff.remaining() >= 8) {

                if (totalBytesRead >= parentNodeOffset) {
                    parentNode = null;
                }
                int boxLength = buff.getInt();
                String boxType = readBoxType(buff);
                totalBytesRead += 8;

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
                        var bytesToSkip = boxLength - 8;
                        if (buff.remaining() > bytesToSkip) {
                            buff.position(buff.position() + bytesToSkip);
                        } else {
                            ByteBuffer skipBuffer = ByteBuffer.allocate(bytesToSkip - buff.remaining());
                            buff.position(buff.position() + buff.remaining());
                            while (skipBuffer.hasRemaining()) {
                                channel.read(skipBuffer);
                            }
                        }
                        totalBytesRead += bytesToSkip;
                    }
                }
            }
            buff.compact();
        }
        return isoBmff;
    }

}
