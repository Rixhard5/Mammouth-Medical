package com.example.mammouthmedicalpharmacyapp.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItem {
    private String userId;
    private String itemId;
    private String name;
    private String imageResource;
    private Integer itemCount;
}
