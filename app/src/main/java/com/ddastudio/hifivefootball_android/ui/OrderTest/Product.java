package com.ddastudio.hifivefootball_android.ui.OrderTest;

import com.ddastudio.hifivefootball_android.R;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hongmac on 2017. 12. 19..
 */

public class Product implements Serializable {
    public final String name;
    public final String price;
    public final int image;
    public final int color;

    private Product(String name, String price, int image, int color) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getImage() {
        return image;
    }

    public int getColor() {
        return color;
    }

    public static List<Product> createFakeProducts() {
        return Arrays.asList(
                new Product("Shooting Stars", "$ 45", R.drawable.ic_account_box, R.color.md_amber_300),
                new Product("Pictures in Sky", "$ 575", R.drawable.ic_bookmark, R.color.md_green_300),
                new Product("The basics of buying a telescope", "$ 892", R.drawable.ic_clap_vector, R.color.md_blue_300),
                new Product("The skyrider", "$ 23", R.drawable.ic_arrow_upward, R.color.md_purple_300)
        );
    }
}
