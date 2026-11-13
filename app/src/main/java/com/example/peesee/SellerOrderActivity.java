package com.example.peesee;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class SellerOrderActivity extends Activity {

    private final int RED = Color.rgb(215, 25, 32);
    private final int DARK = Color.rgb(30, 35, 44);
    private final int GRAY = Color.rgb(115, 125, 143);
    private final int LIGHT = Color.rgb(246, 247, 250);
    private final int GREEN = Color.rgb(22, 145, 85);

    private LinearLayout orderList;
    private TextView countText;

    private final String[] statuses = {
            "Chờ xác nhận",
            "Đang xử lý",
            "Đang giao",
            "Đã giao",
            "Đã hủy"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String role = getSharedPreferences(
                "PEESEE",
                MODE_PRIVATE
        ).getString("current_role", "");

        if (!"admin".equals(role)
                && !"seller".equals(role)) {

            Toast.makeText(this,
                    "Bạn không có quyền truy cập!",
                    Toast.LENGTH_SHORT).show();

            finish();
            return;
        }

        buildPage();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (orderList != null) {
            refreshOrders();
        }
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
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

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private String money(int value) {
        return String.format(Locale.US, "%,dđ", value)
                .replace(',', '.');
    }

    private void buildPage() {
        LinearLayout root = column();
        root.setBackgroundColor(LIGHT);
        setContentView(root);

        // Thanh tiêu đề
        LinearLayout header = row();
        header.setPadding(dp(12), 0, dp(12), 0);
        header.setBackgroundColor(Color.WHITE);

        TextView back = text("‹", 32, DARK, false);
        back.setGravity(Gravity.CENTER);
        header.addView(back, lp(42, 54));
        back.setOnClickListener(v -> finish());

        TextView title = text("Quản lý đơn hàng", 18, DARK, true);
        title.setGravity(Gravity.CENTER);
        header.addView(title, new LinearLayout.LayoutParams(
                0, dp(54), 1
        ));

        TextView refresh = text("↻", 25, RED, true);
        refresh.setGravity(Gravity.CENTER);
        header.addView(refresh, lp(40, 54));
        refresh.setOnClickListener(v -> refreshOrders());

        root.addView(header, lp(-1, 54));

        ScrollView scroll = new ScrollView(this);
        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        LinearLayout content = column();
        content.setPadding(dp(14), dp(16), dp(14), dp(20));
        scroll.addView(content);

        content.addView(text(
                "Đơn hàng của cửa hàng",
                21, DARK, true
        ));

        TextView description = text(
                "Theo dõi đơn hàng và cập nhật trạng thái xử lý.",
                13, GRAY, false
        );

        LinearLayout.LayoutParams descParams = lp(-1, -2);
        descParams.topMargin = dp(5);
        content.addView(description, descParams);

        countText = text("0 đơn hàng", 14, DARK, true);
        LinearLayout.LayoutParams countParams = lp(-1, -2);
        countParams.topMargin = dp(16);
        countParams.bottomMargin = dp(10);
        content.addView(countText, countParams);

        orderList = column();
        content.addView(orderList, lp(-1, -2));

        refreshOrders();
    }

    private void refreshOrders() {
        if (orderList == null) {
            return;
        }

        orderList.removeAllViews();

        ArrayList<OrderActivity.Order> orders =
                OrderActivity.orders;

        if (countText != null) {
            countText.setText(orders.size() + " đơn hàng");
        }

        if (orders.isEmpty()) {
            TextView empty = text(
                    "Chưa có đơn hàng nào.\n\n"
                            + "Khi khách hàng đặt hàng, "
                            + "đơn hàng sẽ hiển thị tại đây.",
                    14, GRAY, false
            );

            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(18), dp(28), dp(18), dp(28));
            empty.setBackground(bg(Color.WHITE, 12));

            orderList.addView(empty, lp(-1, -2));
            return;
        }

        // Duyệt ngược để giữ an toàn khi danh sách thay đổi.
        for (int i = 0; i < orders.size(); i++) {
            final OrderActivity.Order order = orders.get(i);
            addOrderCard(order);
        }
    }

    private void addOrderCard(OrderActivity.Order order) {
        LinearLayout card = column();
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(bg(Color.WHITE, 12));

        LinearLayout.LayoutParams cardParams = lp(-1, -2);
        cardParams.bottomMargin = dp(12);
        orderList.addView(card, cardParams);

        TextView orderId = text(
                order.id, 14, RED, true
        );
        card.addView(orderId);

        TextView date = text(
                "Ngày đặt: " + order.date,
                12, GRAY, false
        );

        LinearLayout.LayoutParams dateParams = lp(-1, -2);
        dateParams.topMargin = dp(5);
        card.addView(date, dateParams);

        LinearLayout productRow = row();

        LinearLayout.LayoutParams productParams = lp(-1, -2);
        productParams.topMargin = dp(12);
        card.addView(productRow, productParams);

        TextView icon = text(
                order.icon, 30, DARK, false
        );
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(bg(LIGHT, 8));
        productRow.addView(icon, lp(55, 55));

        LinearLayout info = column();
        info.setPadding(dp(10), 0, 0, 0);

        productRow.addView(info, new LinearLayout.LayoutParams(
                0, -2, 1
        ));

        info.addView(text(
                order.product, 14, DARK, true
        ));

        TextView category = text(
                order.category, 12, GRAY, false
        );

        LinearLayout.LayoutParams categoryParams = lp(-1, -2);
        categoryParams.topMargin = dp(4);
        info.addView(category, categoryParams);

        TextView quantity = text(
                "Số lượng: " + order.quantity,
                12, GRAY, false
        );

        LinearLayout.LayoutParams quantityParams = lp(-1, -2);
        quantityParams.topMargin = dp(4);
        info.addView(quantity, quantityParams);

        LinearLayout totalRow = row();

        LinearLayout.LayoutParams totalParams = lp(-1, -2);
        totalParams.topMargin = dp(12);
        card.addView(totalRow, totalParams);

        totalRow.addView(text(
                "Tổng tiền", 13, GRAY, false
        ), new LinearLayout.LayoutParams(0, -2, 1));

        totalRow.addView(text(
                money(order.total()), 16, RED, true
        ));

        // Trạng thái hiện tại
        TextView status = text(
                "Trạng thái: " + order.status,
                13, statusColor(order.status), true
        );

        LinearLayout.LayoutParams statusParams = lp(-1, -2);
        statusParams.topMargin = dp(12);
        card.addView(status, statusParams);

        // Các nút thao tác
        LinearLayout actions = row();

        LinearLayout.LayoutParams actionsParams = lp(-1, 44);
        actionsParams.topMargin = dp(10);
        card.addView(actions, actionsParams);

        Button updateButton = new Button(this);
        updateButton.setText("Cập nhật trạng thái");
        updateButton.setAllCaps(false);
        updateButton.setTextSize(11);
        updateButton.setTextColor(Color.WHITE);
        updateButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(RED)
        );

        actions.addView(updateButton,
                new LinearLayout.LayoutParams(0, -1, 1));

        Button cancelButton = new Button(this);
        cancelButton.setText("Hủy đơn");
        cancelButton.setAllCaps(false);
        cancelButton.setTextSize(11);
        cancelButton.setTextColor(RED);
        cancelButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(255, 235, 237)
                )
        );

        LinearLayout.LayoutParams cancelParams =
                new LinearLayout.LayoutParams(0, -1, 1);
        cancelParams.leftMargin = dp(8);
        actions.addView(cancelButton, cancelParams);

        boolean finished = order.status.equals("Đã giao")
                || order.status.equals("Đã hủy");

        updateButton.setEnabled(!finished);
        cancelButton.setEnabled(!finished);

        updateButton.setAlpha(finished ? 0.5f : 1f);
        cancelButton.setAlpha(finished ? 0.5f : 1f);

        updateButton.setOnClickListener(v ->
                showStatusDialog(order));

        cancelButton.setOnClickListener(v ->
                confirmCancel(order));
    }

    private int statusColor(String status) {
        if ("Đã giao".equals(status)) {
            return GREEN;
        }

        if ("Đã hủy".equals(status)) {
            return RED;
        }

        return DARK;
    }

    private void showStatusDialog(OrderActivity.Order order) {
        if ("Đã giao".equals(order.status)
                || "Đã hủy".equals(order.status)) {
            return;
        }

        String[] nextStatuses;

        if ("Chờ xác nhận".equals(order.status)) {
            nextStatuses = new String[]{
                    "Đang xử lý", "Đang giao", "Đã giao"
            };
        } else if ("Đang xử lý".equals(order.status)) {
            nextStatuses = new String[]{
                    "Đang giao", "Đã giao"
            };
        } else if ("Đang giao".equals(order.status)) {
            nextStatuses = new String[]{
                    "Đã giao"
            };
        } else {
            nextStatuses = new String[]{
                    "Đang xử lý", "Đang giao", "Đã giao"
            };
        }

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật trạng thái")
                .setItems(nextStatuses, (dialog, which) -> {
                    order.status = nextStatuses[which];
                    refreshOrders();

                    Toast.makeText(this,
                            "Đã cập nhật: " + order.status,
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void confirmCancel(OrderActivity.Order order) {
        if ("Đã giao".equals(order.status)
                || "Đã hủy".equals(order.status)) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc muốn hủy đơn "
                        + order.id + " không?")
                .setNegativeButton("Không", null)
                .setPositiveButton("Hủy đơn", (dialog, which) -> {
                    order.status = "Đã hủy";
                    refreshOrders();

                    Toast.makeText(this,
                            "Đã hủy đơn hàng.",
                            Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}