package com.castlabs.isobmff;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class Mp4Analyzer {

    private static String readBoxType(RandomAccessFile file) throws IOException {
        var typeBytes = new byte[4];
        file.readFully(typeBytes);
        return new String(typeBytes, StandardCharsets.UTF_8);
    }

    public static ISOBmff analyzeMp4(RandomAccessFile file) throws IOException {
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
