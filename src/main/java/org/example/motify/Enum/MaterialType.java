package org.example.motify.Enum;

import lombok.Getter;

@Getter
public enum MaterialType {
    OIL("机油"),
    FILTER("滤芯"),
    BRAKE("刹车系统"),
    TIRE("轮胎"),
    BATTERY("电池"),
    ELECTRICAL("电气系统"),
    BODY("车身部件"),
    OTHER("其他");

    private final String description;

    MaterialType(String description) {
        this.description = description;
    }
} 