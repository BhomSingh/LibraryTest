package com.chat.befiler.adapters;

public class FileDataClass {
    public String fileName;
    public String fileSizes;
    public String mimeType;

    public FileDataClass(String fileName, String fileSizes,String mimeType) {
        this.fileName = fileName;
        this.fileSizes = fileSizes;
        this.mimeType = mimeType;
    }
}
