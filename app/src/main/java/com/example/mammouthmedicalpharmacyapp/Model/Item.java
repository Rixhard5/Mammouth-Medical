package com.example.mammouthmedicalpharmacyapp.Model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class Item {
    private String itemId;
    private String name;
    private String details;
    private String category;
    private String price;
    private String imageResource;
}
