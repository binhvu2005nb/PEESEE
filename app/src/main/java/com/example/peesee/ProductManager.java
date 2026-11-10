
package com.example.peesee;

import java.util.ArrayList;

public class ProductManager {

    public static class Product {
        public String name;
        public String type;
        public String icon;
        public int price;
        public int oldPrice;
        public int discount;
        public int rating;

        public Product(String name, String type, String icon,
                       int price, int oldPrice,
                       int discount, int rating) {
            this.name = name;
            this.type = type;
            this.icon = icon;
            this.price = price;
            this.oldPrice = oldPrice;
            this.discount = discount;
            this.rating = rating;
        }
    }

    public static final ArrayList<Product> products = new ArrayList<>();

    public static void initialize() {
        if (!products.isEmpty()) return;

        products.add(new Product(
                "Logitech G Pro X Superlight", "Chuột", "🖱️",
                2399000, 2990000, 20, 49));

        products.add(new Product(
                "Laptop gaming Acer Aspire 7", "Laptop", "💻",
                17990000, 21990000, 15, 48));

        products.add(new Product(
                "Keychron K8 Pro RGB", "Bàn phím", "⌨️",
                2289000, 2690000, 15, 48));

        products.add(new Product(
                "HyperX Cloud II", "Tai nghe", "🎧",
                1869000, 2190000, 18, 49));

        products.add(new Product(
                "Logitech G502 HERO", "Chuột", "🖱️",
                1299000, 1590000, 18, 49));

        products.add(new Product(
                "Razer BlackWidow V3", "Bàn phím", "⌨️",
                2549000, 2999000, 15, 47));

        products.add(new Product(
                "LG UltraGear", "Màn hình", "🖥️",
                4990000, 5590000, 11, 48));

        products.add(new Product(
                "Gaming PC Basic", "PC", "🖥️",
                15990000, 17990000, 11, 49));
    }
}
