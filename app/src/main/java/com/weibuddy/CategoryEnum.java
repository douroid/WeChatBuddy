package com.weibuddy;

public enum CategoryEnum {

    wenzi("文字"),
    tupian("图片"),
    yuyin("语音"),
    shipin("视频"),
    files("文件");

    public final String value;

    CategoryEnum(String value) {
        this.value = value;
    }

}
