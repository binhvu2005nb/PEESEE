
package com.example.peesee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SellerHomeActivity extends Activity {

    private final int RED = Color.rgb(215, 25, 32);
    private final int DARK = Color.rgb(30, 35, 44);
    private final int GRAY = Color.rgb(115, 125, 143);
    private final int LIGHT = Color.rgb(246, 247, 250);

    private LinearLayout root;
    private String adminName = "Admin PEESEE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adminName = getIntent().getStringExtra("name");
        if (adminName == null || adminName.trim().isEmpty()) {
            adminName = "Admin PEESEE";
        }

        buildPage();
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private TextView text(String value, int size,
                          int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private LinearLayout column() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private LinearLayout row() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        return layout;
    }

    private LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(
                w < 0 ? w : dp(w),
                h < 0 ? h : dp(h)
        );
    }

    private void addText(LinearLayout parent, String value,
                         int size, int color, boolean bold,
                         int topMargin) {
        TextView view = text(value, size, color, bold);

        LinearLayout.LayoutParams params = lp(-1, -2);
        params.topMargin = dp(topMargin);

        parent.addView(view, params);
    }

    private void buildPage() {
        root = column();
        root.setBackgroundColor(LIGHT);
        setContentView(root);

        // Header
        LinearLayout header = row();
        header.setPadding(dp(16), dp(8), dp(16), dp(8));
        header.setBackgroundColor(Color.WHITE);

        TextView logo = text("PEESEE", 23, RED, true);
        header.addView(logo, new LinearLayout.LayoutParams(
                0, dp(48), 1
        ));

        TextView account = text("♙ Admin", 13, DARK, true);
        account.setGravity(Gravity.CENTER);
        header.addView(account, lp(90, 48));

        root.addView(header);

        // Nội dung có thể cuộn
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        LinearLayout content = column();
        content.setPadding(dp(16), dp(16), dp(16), dp(20));
        scroll.addView(content);

        addText(content, "Tổng quan người bán",
                23, DARK, true, 0);

        addText(content, "Xin chào, " + adminName,
                14, GRAY, false, 6);

        addText(content, "Quản lý cửa hàng PEESEE",
                13, GRAY, false, 4);

        // Các ô thống kê
        LinearLayout stats1 = row();
        LinearLayout.LayoutParams statsParams = lp(-1, 112);
        statsParams.topMargin = dp(18);
        content.addView(stats1, statsParams);

        addStatCard(stats1, "📦", "Đơn hàng",
                "Xem đơn hàng", "orders");

        addStatCard(stats1, "🛍️", "Sản phẩm",
                "Quản lý hàng", "products");

        LinearLayout stats2 = row();
        LinearLayout.LayoutParams stats2Params = lp(-1, 112);
        stats2Params.topMargin = dp(10);
        content.addView(stats2, stats2Params);

        addStatCard(stats2, "💰", "Doanh thu",
                "Xem thống kê", "revenue");

        addStatCard(stats2, "⭐", "Đánh giá",
                "Phản hồi khách", "reviews");

        // Chức năng quản lý
        addText(content, "Quản lý cửa hàng",
                18, DARK, true, 24);

        addMenuButton(content, "📦  Quản lý sản phẩm",
                "products");

        addMenuButton(content, "🧾  Quản lý đơn hàng",
                "orders");

        addMenuButton(content, "⭐  Đánh giá của khách hàng",
                "reviews");

        addMenuButton(content, "📊  Thống kê doanh thu",
                "revenue");

        // Tài khoản
        addText(content, "Tài khoản",
                18, DARK, true, 24);

        Button logout = new Button(this);
        logout.setText("Đăng xuất");
        logout.setTextColor(RED);
        logout.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.WHITE
                )
        );

        LinearLayout.LayoutParams logoutParams = lp(-1, 46);
        logoutParams.topMargin = dp(10);
        content.addView(logout, logoutParams);

        logout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có muốn đăng xuất khỏi tài khoản Admin?")
                    .setNegativeButton("Hủy", null)
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        Intent intent = new Intent(
                                SellerHomeActivity.this,
                                MainActivity.class
                        );

                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        );

                        startActivity(intent);
                        finish();
                    })
                    .show();
        });
    }

    private void addStatCard(LinearLayout parent,
                             String icon, String title,
                             String subtitle, String type) {
        LinearLayout card = column();
        card.setPadding(dp(10), dp(10), dp(8), dp(8));
        card.setBackground(bg(Color.WHITE, 12));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0, -1, 1);

        params.setMargins(dp(3), dp(2), dp(3), dp(2));
        parent.addView(card, params);

        TextView iconView = text(icon, 22, RED, false);
        card.addView(iconView);

        addText(card, title, 14, DARK, true, 3);
        addText(card, subtitle, 10, GRAY, false, 3);

        card.setOnClickListener(v -> openFeature(type));
    }

    private void addMenuButton(LinearLayout parent,
                               String title, String type) {
        TextView item = text(title + "     ›",
                14, DARK, true);

        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPadding(dp(14), 0, dp(12), 0);
        item.setBackground(bg(Color.WHITE, 10));

        LinearLayout.LayoutParams params = lp(-1, 52);
        params.topMargin = dp(8);
        parent.addView(item, params);

        item.setOnClickListener(v -> openFeature(type));
    }

    private void openFeature(String type) {
        switch (type) {
            case "products":
                Intent intent = new Intent(
                        SellerHomeActivity.this,
                        SellerProductActivity.class
                );
                startActivity(intent);
                break;

            case "orders":
                Intent orderIntent = new Intent(
                        SellerHomeActivity.this,
                        SellerOrderActivity.class
                );
                startActivity(orderIntent);
                break;

            case "reviews":
                Intent reviewIntent = new Intent(
                        SellerHomeActivity.this,
                        SellerReviewActivity.class
                );

                startActivity(reviewIntent);
                break;

            case "revenue":
                Toast.makeText(this,
                        "Chức năng thống kê sẽ được xây dựng sau",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
