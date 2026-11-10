package com.example.peesee;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class RevenueActivity extends Activity {

    private static final int RED = Color.rgb(215, 25, 32);
    private static final int DARK = Color.rgb(35, 43, 57);
    private static final int GRAY = Color.rgb(125, 135, 150);
    private static final int LIGHT = Color.rgb(246, 247, 250);
    private static final int GREEN = Color.rgb(23, 158, 111);

    private LinearLayout root;
    private LinearLayout content;
    private TextView revenueValue;
    private TextView deliveredValue;
    private TextView productsValue;
    private TextView averageValue;
    private LinearLayout recentList;
    private RevenueChart chart;

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private String money(long value) {
        return String.format(Locale.US, "%,dđ", value)
                .replace(',', '.');
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

    private LinearLayout.LayoutParams lp(int width, int height) {
        return new LinearLayout.LayoutParams(
                width < 0 ? width : dp(width),
                height < 0 ? height : dp(height)
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nạp dữ liệu mẫu nếu người bán mở trang doanh thu trước trang đơn hàng.
        OrderActivity.loadSampleOrders();

        buildPage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recentList != null) {
            refreshStatistics();
        }
    }

    private void buildPage() {
        root = column();
        root.setBackgroundColor(LIGHT);
        setContentView(root);

        // Thanh tiêu đề
        LinearLayout header = row();
        header.setPadding(dp(12), 0, dp(12), 0);
        header.setBackgroundColor(RED);

        TextView back = text("‹", 32, Color.WHITE, false);
        back.setGravity(Gravity.CENTER);
        header.addView(back, lp(42, 52));
        back.setOnClickListener(v -> finish());

        TextView title = text("Thống kê doanh thu", 18,
                Color.WHITE, true);
        title.setGravity(Gravity.CENTER);
        header.addView(title,
                new LinearLayout.LayoutParams(0, dp(52), 1));

        TextView refresh = text("↻", 24, Color.WHITE, false);
        refresh.setGravity(Gravity.CENTER);
        header.addView(refresh, lp(38, 52));
        refresh.setOnClickListener(v -> refreshStatistics());

        root.addView(header, lp(-1, 52));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        content = column();
        content.setPadding(dp(15), dp(16), dp(15), dp(22));
        scroll.addView(content);

        TextView heading = text("Tổng quan cửa hàng", 19,
                DARK, true);
        content.addView(heading);

        TextView note = text(
                "Số liệu được tính từ các đơn hàng đã giao thành công.",
                11, GRAY, false
        );
        LinearLayout.LayoutParams noteParams = lp(-1, -2);
        noteParams.topMargin = dp(5);
        content.addView(note, noteParams);

        // Thẻ tổng doanh thu
        LinearLayout revenueCard = column();
        revenueCard.setPadding(dp(16), dp(15), dp(16), dp(15));
        revenueCard.setBackground(bg(RED, 13));

        LinearLayout.LayoutParams revenueParams = lp(-1, 104);
        revenueParams.topMargin = dp(15);
        content.addView(revenueCard, revenueParams);

        revenueCard.addView(text("TỔNG DOANH THU", 11,
                Color.WHITE, true));

        revenueValue = text("0đ", 27, Color.WHITE, true);
        LinearLayout.LayoutParams valueParams = lp(-1, -2);
        valueParams.topMargin = dp(8);
        revenueCard.addView(revenueValue, valueParams);

        revenueCard.addView(text(
                "Chỉ tính đơn hàng có trạng thái Đã giao",
                10, 0xFFFFE5E5, false
        ));

        // Ba chỉ số phụ
        LinearLayout statsRow1 = row();
        LinearLayout.LayoutParams statsRowParams = lp(-1, 92);
        statsRowParams.topMargin = dp(11);
        content.addView(statsRow1, statsRowParams);

        deliveredValue = addSmallStat(statsRow1,
                "Đơn đã giao", "0", "📦");

        productsValue = addSmallStat(statsRow1,
                "Sản phẩm đã bán", "0", "🛍️");

        LinearLayout statsRow2 = row();
        LinearLayout.LayoutParams statsRow2Params = lp(-1, 92);
        statsRow2Params.topMargin = dp(9);
        content.addView(statsRow2, statsRow2Params);

        averageValue = addSmallStat(statsRow2,
                "Giá trị TB/đơn", "0đ", "💳");

        addSmallStat(statsRow2,
                "Trạng thái tính", "Đã giao", "✓");

        // Biểu đồ doanh thu theo tháng
        TextView chartTitle = text("Doanh thu theo tháng", 17,
                DARK, true);
        LinearLayout.LayoutParams chartTitleParams = lp(-1, -2);
        chartTitleParams.topMargin = dp(22);
        content.addView(chartTitle, chartTitleParams);

        TextView chartNote = text(
                "Tổng tiền các đơn đã giao, nhóm theo tháng đặt hàng.",
                10, GRAY, false
        );
        LinearLayout.LayoutParams chartNoteParams = lp(-1, -2);
        chartNoteParams.topMargin = dp(4);
        content.addView(chartNote, chartNoteParams);

        chart = new RevenueChart(this);
        LinearLayout.LayoutParams chartParams = lp(-1, 205);
        chartParams.topMargin = dp(10);
        chart.setBackground(bg(Color.WHITE, 12));
        content.addView(chart, chartParams);

        // Danh sách đơn hàng được tính doanh thu
        TextView recentTitle = text("Các đơn hàng được tính doanh thu",
                17, DARK, true);
        LinearLayout.LayoutParams recentTitleParams = lp(-1, -2);
        recentTitleParams.topMargin = dp(22);
        content.addView(recentTitle, recentTitleParams);

        recentList = column();
        LinearLayout.LayoutParams recentParams = lp(-1, -2);
        recentParams.topMargin = dp(10);
        content.addView(recentList, recentParams);

        refreshStatistics();
    }

    private TextView addSmallStat(LinearLayout parent,
                                  String label, String value,
                                  String icon) {
        LinearLayout card = column();
        card.setPadding(dp(10), dp(9), dp(8), dp(8));
        card.setBackground(bg(Color.WHITE, 11));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0, -1, 1);
        params.setMargins(dp(3), 0, dp(3), 0);
        parent.addView(card, params);

        card.addView(text(icon + "  " + label, 10,
                GRAY, false));

        TextView valueView = text(value, 16, DARK, true);
        LinearLayout.LayoutParams valueParams = lp(-1, -2);
        valueParams.topMargin = dp(8);
        card.addView(valueView, valueParams);

        return valueView;
    }

    private ArrayList<OrderActivity.Order> getDeliveredOrders() {
        ArrayList<OrderActivity.Order> delivered = new ArrayList<>();

        for (OrderActivity.Order order : OrderActivity.orders) {
            if ("Đã giao".equals(order.status)) {
                delivered.add(order);
            }
        }
        return delivered;
    }

    private void refreshStatistics() {
        ArrayList<OrderActivity.Order> delivered = getDeliveredOrders();

        long totalRevenue = 0;
        int totalProducts = 0;

        for (OrderActivity.Order order : delivered) {
            totalRevenue += order.total();
            totalProducts += order.quantity;
        }

        revenueValue.setText(money(totalRevenue));
        deliveredValue.setText(String.valueOf(delivered.size()));
        productsValue.setText(String.valueOf(totalProducts));

        long average = delivered.isEmpty()
                ? 0 : totalRevenue / delivered.size();
        averageValue.setText(money(average));

        if (chart != null) {
            chart.setMonthlyData(buildMonthlyData(delivered));
        }

        if (recentList != null) {
            recentList.removeAllViews();

            if (delivered.isEmpty()) {
                TextView empty = text(
                        "Chưa có đơn hàng nào được giao thành công.",
                        12, GRAY, false
                );
                empty.setGravity(Gravity.CENTER);
                empty.setPadding(dp(10), dp(22), dp(10), dp(22));
                empty.setBackground(bg(Color.WHITE, 10));
                recentList.addView(empty);
                return;
            }

            // Danh sách dùng thứ tự mới nhất trước, tối đa 10 đơn.
            int shown = 0;
            for (OrderActivity.Order order : delivered) {
                if (shown >= 10) break;
                addDeliveredOrder(order);
                shown++;
            }
        }
    }

    private Map<String, Long> buildMonthlyData(
            ArrayList<OrderActivity.Order> delivered) {
        Map<String, Long> result = new TreeMap<>();

        SimpleDateFormat input = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm", Locale.getDefault());
        SimpleDateFormat keyFormat = new SimpleDateFormat(
                "yyyy-MM", Locale.getDefault());

        for (OrderActivity.Order order : delivered) {
            try {
                Date date = input.parse(order.date);
                if (date != null) {
                    String key = keyFormat.format(date);
                    Long old = result.get(key);
                    result.put(key, (old == null ? 0 : old)
                            + order.total());
                }
            } catch (ParseException ignored) {
                // Bỏ qua đơn có ngày không đúng định dạng.
            }
        }

        return result;
    }

    private void addDeliveredOrder(OrderActivity.Order order) {
        LinearLayout card = row();
        card.setPadding(dp(12), dp(10), dp(12), dp(10));
        card.setBackground(bg(Color.WHITE, 10));

        LinearLayout.LayoutParams params = lp(-1, -2);
        params.bottomMargin = dp(8);
        recentList.addView(card, params);

        LinearLayout info = column();

        TextView product = text(order.product, 12, DARK, true);
        product.setMaxLines(2);
        info.addView(product);

        TextView detail = text(
                order.id + "  ·  " + order.date + "  ·  x" + order.quantity,
                9, GRAY, false
        );
        LinearLayout.LayoutParams detailParams = lp(-1, -2);
        detailParams.topMargin = dp(4);
        info.addView(detail, detailParams);

        card.addView(info, new LinearLayout.LayoutParams(0, -2, 1));

        TextView amount = text(money(order.total()), 12,
                GREEN, true);
        amount.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        card.addView(amount);
    }

    // Biểu đồ cột tự vẽ, không cần cài thêm thư viện.
    private class RevenueChart extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Map<String, Long> data = new TreeMap<>();

        RevenueChart(Activity context) {
            super(context);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        void setMonthlyData(Map<String, Long> values) {
            data = new TreeMap<>(values);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float width = getWidth();
            float height = getHeight();
            float left = dp(35);
            float right = dp(12);
            float top = dp(18);
            float bottom = dp(36);
            float chartHeight = height - top - bottom;
            float chartWidth = width - left - right;

            paint.setColor(0xFFE9EDF2);
            paint.setStrokeWidth(dp(1));
            canvas.drawLine(left, top + chartHeight,
                    width - right, top + chartHeight, paint);

            if (data.isEmpty()) {
                paint.setColor(GRAY);
                paint.setTextSize(dp(12));
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Chưa có dữ liệu doanh thu",
                        width / 2, height / 2, paint);
                return;
            }

            ArrayList<String> keys = new ArrayList<>(data.keySet());
            int start = Math.max(0, keys.size() - 6);
            int count = keys.size() - start;

            long max = 1;
            for (int i = start; i < keys.size(); i++) {
                max = Math.max(max, data.get(keys.get(i)));
            }

            float slot = chartWidth / Math.max(1, count);
            float barWidth = Math.min(dp(30), slot * 0.55f);

            for (int i = 0; i < count; i++) {
                String key = keys.get(start + i);
                long amount = data.get(key);

                float barHeight = (float) amount / (float) max
                        * (chartHeight - dp(8));
                float centerX = left + slot * i + slot / 2f;
                float barLeft = centerX - barWidth / 2f;
                float barTop = top + chartHeight - barHeight;

                paint.setColor(RED);
                canvas.drawRoundRect(barLeft, barTop,
                        barLeft + barWidth,
                        top + chartHeight,
                        dp(5), dp(5), paint);

                String month = key.substring(5, 7) + "/" +
                        key.substring(2, 4);
                paint.setColor(GRAY);
                paint.setTextSize(dp(9));
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(month, centerX,
                        height - dp(13), paint);
            }
        }
    }
}
