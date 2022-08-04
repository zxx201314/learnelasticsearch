package com.zhang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    private static final long serialVersionUID = -8049497962627482693L;
    private String name;
    private String img;
    private String price;
}
