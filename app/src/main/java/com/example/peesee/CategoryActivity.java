
package com.example.peesee;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CategoryActivity extends Activity {

    private final int RED = Color.rgb(215, 25, 32);
    private final int DARK = Color.rgb(35, 43, 57);
    private final int GRAY = Color.rgb(125, 135, 150);
    private final int LIGHT = Color.rgb(248, 249, 251);

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, int size,
                          int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);

        if (bold) {
            t.setTypeface(null, Typeface.BOLD);
        }

        return t;
    }

    private GradientDrawable background(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(12));
        d.setStroke(dp(1), Color.rgb(235, 237, 241));
        return d;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildPage();
    }

    private void buildPage() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        setContentView(root);

        // HEADER
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(12), 0, dp(12), 0);
        header.setBackgroundColor(RED);

        TextView back = text("‹", 32, Color.WHITE, false);
        back.setGravity(Gravity.CENTER);
        header.addView(back,
                new LinearLayout.LayoutParams(dp(45), dp(52)));

        back.setOnClickListener(v -> finish());

        TextView title = text("Danh mục sản phẩm",
                18, Color.WHITE, true);
        title.setGravity(Gravity.CENTER);

        header.addView(title,
                new LinearLayout.LayoutParams(0, dp(52), 1));

        root.addView(header);

        // DANH SÁCH DANH MỤC
        ScrollView scroll = new ScrollView(this);

        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(dp(16), dp(16), dp(16), dp(20));

        scroll.addView(list);

        root.addView(scroll,
                new LinearLayout.LayoutParams(-1, 0, 1));

        TextView subtitle = text(
                "Khám phá sản phẩm theo danh mục",
                14, GRAY, false
        );

        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(-1, -2);
        subParams.bottomMargin = dp(15);
        list.addView(subtitle, subParams);

        String[] names = {
                "Tất cả",
                "Chuột",
                "Bàn phím",
                "Tai nghe",
                "Màn hình",
                "Laptop",
                "PC"
        };

        String[] icons = {
                "▦", "🖱️", "⌨️", "🎧",
                "🖥️", "💻", "🖥️"
        };

        for (int i = 0; i < names.length; i++) {

            final String selected = names[i];

            LinearLayout item = new LinearLayout(this);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(dp(14), dp(10), dp(14), dp(10));
            item.setBackground(background(LIGHT));

            TextView icon = text(
                    icons[i], 25, RED, false
            );

            icon.setGravity(Gravity.CENTER);
            item.addView(icon,
                    new LinearLayout.LayoutParams(dp(50), dp(45)));

            TextView name = text(
                    names[i], 15, DARK, true
            );

            LinearLayout.LayoutParams nameParams =
                    new LinearLayout.LayoutParams(0, -2, 1);
            nameParams.leftMargin = dp(12);

            item.addView(name, nameParams);

            TextView arrow = text(
                    "›", 25, GRAY, false
            );

            item.addView(arrow);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(-1, dp(65));
            params.bottomMargin = dp(10);

            list.addView(item, params);

            item.setOnClickListener(v -> openCategory(selected));
        }

        // THANH ĐIỀU HƯỚNG
        buildBottomNav(root);
    }

    private void openCategory(String category) {

        Intent intent = new Intent(
                CategoryActivity.this,
                HomeActivity.class
        );

        intent.putExtra("selected_category", category);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
        finish();
    }

    private void buildBottomNav(LinearLayout root) {

        LinearLayout nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER);
        nav.setBackgroundColor(Color.WHITE);
        nav.setElevation(dp(8));

        String[] labels = {
                "Trang chủ", "Danh mục",
                "Giỏ hàng", "Đơn hàng", "Cá nhân"
        };

        String[] icons = {
                "⌂", "▦", "🛒", "▤", "♙"
        };

        for (int i = 0; i < labels.length; i++) {

            final int index = i;

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);

            int color = index == 1 ? RED : GRAY;

            TextView icon = text(icons[i], 20, color, false);
            TextView label = text(labels[i], 9, color, false);

            icon.setGravity(Gravity.CENTER);
            label.setGravity(Gravity.CENTER);

            item.addView(icon);
            item.addView(label);

            nav.addView(item,
                    new LinearLayout.LayoutParams(0, dp(55), 1));

            item.setOnClickListener(v -> {

                if (index == 0) {
                    openCategory("Tất cả");
                } else if (index == 1) {
                    // Đang ở trang danh mục.
                } else if (index == 2) {
                    startActivity(new Intent(
                            this, CartActivity.class));
                } else if (index == 3) {
                    startActivity(new Intent(
                            this, OrderActivity.class));
                } else if (index == 4) {
                    ProfileHelper.showProfile(CategoryActivity.this);
                }
            });
        }

        root.addView(nav,
                new LinearLayout.LayoutParams(-1, dp(58)));
    }
}
