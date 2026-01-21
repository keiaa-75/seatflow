package com.xinnsuu.seatflow.dto;

import java.util.List;

import lombok.Data;

@Data
public class LayoutPreset {
    private String name;
    private String displayName;
    private String description;
    private int rows;
    private int columns;
    private List<String> disabledSeats;
}