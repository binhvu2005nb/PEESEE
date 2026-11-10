
package com.example.peesee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderActivity extends Activity {

    private final int RED = Color.rgb(215, 0, 20);
    private final int DARK = Color.rgb(35, 43, 57);
    private final int GRAY = Color.rgb(125, 135, 150);
    private final int BORDER = Color.rgb(233, 237, 242);
    private final int GREEN = Color.rgb(23, 158, 111);

    static class Order {
        String id, product, category, icon, date, status;
        int price, quantity;

        Order(String id, String product, String category,
              String icon, String date, int price,
              int quantity, String status) {
            this.id = id;
            this.product = product;
            this.category = category;
            this.icon = icon;
            this.date = date;
            this.price = price;
            this.quantity = quantity;
            this.status = status;
        }

        int total() {
            return price * quantity;
        }
    }

    // Danh sách đơn hàng dùng chung trong ứng dụng
    static final ArrayList<Order> orders = new ArrayList<>();
    private static boolean sampleOrdersLoaded = false;

    public static void createOrder(
            String product,
            String category,
            String icon,
            int price,
            int quantity) {

        String id = "#DH" + System.currentTimeMillis();

        String date = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
        ).format(new Date());

        Order order = new Order(
                id,
                product,
                category,
                icon,
                date,
                price,
                quantity,
                "Chờ xác nhận"
        );

        orders.add(0, order);
    }

    private final String[] filters = {
            "Tất cả",
            "Chờ xác nhận",
            "Đang xử lý",
            "Đang giao",
            "Đã giao",
            "Đã hủy"
    };

    private String currentFilter = "Tất cả";
    private String keyword = "";

    private LinearLayout root;
    private LinearLayout orderList;
    private LinearLayout filterRow;
    private ScrollView orderScroll;
    private EditText searchInput;
    private TextView resultCount;

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private String money(int value) {
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

    private LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(
                w < 0 ? w : dp(w),
                h < 0 ? h : dp(h)
        );
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

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        d.setStroke(dp(1), BORDER);
        return d;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSampleOrders();
        buildPage();
    }

    public static void loadSampleOrders() {
        // Chỉ thêm dữ liệu mẫu một lần, không thêm lại
        // sau khi người dùng đã xử lý hết đơn hàng.
        if (sampleOrdersLoaded) {
            return;
        }

        sampleOrdersLoaded = true;

        orders.add(new Order(
                "#DH123456",
                "Laptop Acer Aspire 7",
                "Laptop",
                "💻",
                "12/05/2025 10:24",
                17990000,
                1,
                "Đang xử lý"
        ));

        orders.add(new Order(
                "#DH123457",
                "Keychron K8 Pro (RGB)",
                "Bàn phím",
                "⌨️",
                "12/05/2025 09:45",
                2289000,
                1,
                "Đang giao"
        ));

        orders.add(new Order(
                "#DH123458",
                "HyperX Cloud II",
                "Tai nghe",
                "🎧",
                "11/05/2025 16:30",
                1869000,
                1,
                "Đã giao"
        ));

        orders.add(new Order(
                "#DH123459",
                "Logitech G502 HERO",
                "Chuột",
                "🖱️",
                "10/05/2025 14:20",
                1299000,
                1,
                "Đã hủy"
        ));

        orders.add(new Order(
                "#DH123460",
                "Logitech G Pro X",
                "Chuột",
                "🖱️",
                "09/05/2025 08:30",
                2399000,
                1,
                "Chờ xác nhận"
        ));
    }

    private void buildPage() {
        root = column();
        root.setBackgroundColor(Color.WHITE);
        setContentView(root);

        // HEADER
        LinearLayout header = row();
        header.setPadding(dp(12), 0, dp(12), 0);
        header.setBackgroundColor(RED);

        TextView back = text("‹", 32, Color.WHITE, false);
        back.setGravity(Gravity.CENTER);
        header.addView(back, lp(38, 50));

        back.setOnClickListener(v -> finish());

        TextView title = text("Đơn hàng", 19, Color.WHITE, true);
        title.setGravity(Gravity.CENTER);

        header.addView(title,
                new LinearLayout.LayoutParams(0, dp(50), 1));

        TextView refresh = text("↻", 25, Color.WHITE, false);
        refresh.setGravity(Gravity.CENTER);
        header.addView(refresh, lp(38, 50));

        refresh.setOnClickListener(v -> {
            currentFilter = "Tất cả";
            keyword = "";
            searchInput.setText("");
            renderFilters();
            renderOrders();
        });

        root.addView(header, lp(-1, 50));

        // SEARCH
        LinearLayout searchWrap = column();
        searchWrap.setPadding(dp(12), dp(9), dp(12), dp(4));

        searchInput = new EditText(this);
        searchInput.setSingleLine(true);
        searchInput.setTextSize(12);
        searchInput.setHint(
                "⌕  Nhập mã đơn hàng, tên sản phẩm..."
        );
        searchInput.setPadding(dp(12), 0, dp(12), 0);
        searchInput.setBackground(bg(0xFFF8F9FB, 24));

        searchWrap.addView(searchInput, lp(-1, 42));
        root.addView(searchWrap);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                keyword = s.toString()
                        .trim()
                        .toLowerCase(Locale.ROOT);

                renderOrders();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // BỘ LỌC TRẠNG THÁI
        HorizontalScrollView filterScroll =
                new HorizontalScrollView(this);

        filterScroll.setHorizontalScrollBarEnabled(false);

        filterRow = row();
        filterRow.setPadding(dp(8), dp(5), dp(8), dp(7));
        filterScroll.addView(filterRow);

        root.addView(filterScroll, lp(-1, 48));

        // SỐ LƯỢNG KẾT QUẢ
        resultCount = text("", 11, GRAY, false);
        resultCount.setPadding(dp(14), dp(4), dp(14), dp(5));
        root.addView(resultCount);

        // DANH SÁCH ĐƠN HÀNG
        orderScroll = new ScrollView(this);
        orderScroll.setFillViewport(true);

        root.addView(orderScroll,
                new LinearLayout.LayoutParams(-1, 0, 1));

        orderList = column();
        orderList.setPadding(dp(10), dp(4), dp(10), dp(10));
        orderScroll.addView(orderList);

        renderFilters();
        renderOrders();

        // THANH ĐIỀU HƯỚNG DƯỚI
        buildBottomNavigation();
    }

    private void buildBottomNavigation() {
        LinearLayout nav = row();
        nav.setBackgroundColor(Color.WHITE);
        nav.setElevation(dp(8));

        String[] labels = {
                "Trang chủ", "Danh mục", "Giỏ hàng",
                "Đơn hàng", "Cá nhân"
        };

        String[] icons = {"⌂", "▦", "🛒", "▣", "♙"};

        for (int i = 0; i < labels.length; i++) {
            final int index = i;

            LinearLayout item = column();
            item.setGravity(Gravity.CENTER);

            int color = index == 3 ? RED : GRAY;

            TextView icon = text(icons[i], 19, color, false);
            icon.setGravity(Gravity.CENTER);
            item.addView(icon);

            TextView label = text(labels[i], 9, color, false);
            label.setGravity(Gravity.CENTER);
            item.addView(label);

            nav.addView(item,
                    new LinearLayout.LayoutParams(0, dp(51), 1));

            item.setOnClickListener(v -> navigate(index));
        }

        root.addView(nav, lp(-1, 53));
    }

    private void renderFilters() {
        if (filterRow == null) {
            return;
        }

        filterRow.removeAllViews();

        for (String filter : filters) {
            final String selected = filter;
            boolean active = filter.equals(currentFilter);

            TextView chip = text(
                    filter + " (" + countStatus(filter) + ")",
                    10,
                    active ? Color.WHITE : DARK,
                    active
            );

            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(12), 0, dp(12), 0);
            chip.setBackground(bg(
                    active ? RED : 0xFFF2F4F7,
                    18
            ));

            LinearLayout.LayoutParams p = lp(-2, 32);
            p.setMargins(dp(3), 0, dp(3), 0);
            filterRow.addView(chip, p);

            chip.setOnClickListener(v -> {
                currentFilter = selected;
                renderFilters();
                renderOrders();
            });
        }
    }

    private int countStatus(String filter) {
        int count = 0;

        for (Order order : orders) {
            if (filter.equals("Tất cả")
                    || order.status.equals(filter)) {
                count++;
            }
        }

        return count;
    }

    private boolean matches(Order order) {
        boolean statusMatch =
                currentFilter.equals("Tất cả")
                        || order.status.equals(currentFilter);

        String haystack = (
                order.id + " " +
                        order.product + " " +
                        order.category
        ).toLowerCase(Locale.ROOT);

        return statusMatch && haystack.contains(keyword);
    }

    private void renderOrders() {
        if (orderList == null || resultCount == null) {
            return;
        }

        orderList.removeAllViews();

        int found = 0;

        for (Order order : orders) {
            if (matches(order)) {
                found++;
                addOrderCard(order);
            }
        }

        resultCount.setText("Tìm thấy " + found + " đơn hàng");

        if (found == 0) {
            TextView empty = text(
                    "Chưa có đơn hàng phù hợp.\n"
                            + "Hãy thử từ khóa hoặc trạng thái khác.",
                    14,
                    GRAY,
                    false
            );

            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, dp(40), 0, dp(40));
            orderList.addView(empty);
        }
    }

    private int statusColor(String status) {
        switch (status) {
            case "Đang giao":
                return 0xFF159B70;
            case "Đã giao":
                return 0xFF1677FF;
            case "Đã hủy":
                return GRAY;
            case "Chờ xác nhận":
                return 0xFFDA7900;
            default:
                return RED;
        }
    }

    private void addOrderCard(Order order) {
        LinearLayout card = column();
        card.setPadding(dp(10), dp(9), dp(10), dp(10));
        card.setBackground(bg(Color.WHITE, 11));

        LinearLayout.LayoutParams cardParams = lp(-1, -2);
        cardParams.bottomMargin = dp(9);
        orderList.addView(card, cardParams);

        // MÃ ĐƠN VÀ TRẠNG THÁI
        LinearLayout top = row();

        TextView id = text(order.id, 13, DARK, true);
        top.addView(id,
                new LinearLayout.LayoutParams(0, dp(27), 1));

        TextView status = text(
                "● " + order.status,
                10,
                statusColor(order.status),
                true
        );

        status.setGravity(Gravity.CENTER);
        status.setPadding(dp(7), dp(5), dp(7), dp(5));
        status.setBackground(bg(0xFFF5F6F8, 14));
        top.addView(status);

        card.addView(top);

        TextView date = text(
                "Đặt lúc: " + order.date,
                10,
                GRAY,
                false
        );
        card.addView(date);

        // THÔNG TIN SẢN PHẨM
        LinearLayout productRow = row();

        LinearLayout.LayoutParams prp = lp(-1, 76);
        prp.topMargin = dp(5);
        card.addView(productRow, prp);

        TextView image = text(order.icon, 31, DARK, false);
        image.setGravity(Gravity.CENTER);
        image.setBackground(bg(0xFFF3F5F7, 7));
        productRow.addView(image, lp(64, 62));

        LinearLayout info = column();
        info.setPadding(dp(9), 0, dp(4), 0);

        TextView productName = text(
                order.product, 12, DARK, true);
        productName.setMaxLines(2);
        info.addView(productName);

        info.addView(text(
                order.category + " · x" + order.quantity,
                10,
                GRAY,
                false
        ));

        productRow.addView(info,
                new LinearLayout.LayoutParams(0, -2, 1));

        TextView price = text(money(order.total()), 12, RED, true);
        price.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        productRow.addView(price);

        // CÁC NÚT THAO TÁC
        LinearLayout actions = row();

        Button detail = new Button(this);
        detail.setText("Xem chi tiết");
        detail.setTextSize(10);
        detail.setAllCaps(false);
        actions.addView(detail,
                new LinearLayout.LayoutParams(0, dp(38), 1));

        Button invoice = new Button(this);
        invoice.setText("Hóa đơn");
        invoice.setTextSize(10);
        invoice.setAllCaps(false);

        LinearLayout.LayoutParams ip =
                new LinearLayout.LayoutParams(0, dp(38), 1);
        ip.leftMargin = dp(5);
        actions.addView(invoice, ip);

        Button update = new Button(this);
        update.setText("Cập nhật");
        update.setTextSize(10);
        update.setAllCaps(false);

        LinearLayout.LayoutParams up =
                new LinearLayout.LayoutParams(0, dp(38), 1);
        up.leftMargin = dp(5);
        actions.addView(update, up);

        LinearLayout.LayoutParams ap = lp(-1, 40);
        ap.topMargin = dp(5);
        card.addView(actions, ap);

        detail.setOnClickListener(v -> showDetails(order));
        invoice.setOnClickListener(v -> showInvoice(order));
        update.setOnClickListener(v -> updateStatus(order));
    }

    private void showDetails(Order order) {
        LinearLayout content = column();
        content.setPadding(dp(6), dp(5), dp(6), dp(5));

        content.addView(text(
                "Mã đơn: " + order.id, 14, DARK, true));

        content.addView(text(
                "Ngày đặt: " + order.date, 12, GRAY, false));

        TextView product = text(
                order.icon + "  " + order.product,
                14,
                DARK,
                true
        );

        LinearLayout.LayoutParams pp = lp(-1, -2);
        pp.topMargin = dp(12);
        content.addView(product, pp);

        content.addView(text(
                "Số lượng: " + order.quantity, 12, GRAY, false));

        content.addView(text(
                "Đơn giá: " + money(order.price), 12, GRAY, false));

        content.addView(text(
                "Tổng tiền: " + money(order.total()), 15, RED, true));

        View line = new View(this);
        line.setBackgroundColor(BORDER);

        LinearLayout.LayoutParams lineParams = lp(-1, 1);
        lineParams.topMargin = dp(12);
        lineParams.bottomMargin = dp(12);
        content.addView(line, lineParams);

        content.addView(text(
                "Tiến trình đơn hàng", 14, DARK, true));

        String[] steps = {
                "1. Đã đặt hàng",
                "2. Chờ xác nhận / Đã xác nhận",
                "3. Đang xử lý",
                "4. Đang giao",
                "5. Đã giao"
        };

        int current = statusIndex(order.status);

        if (order.status.equals("Đã hủy")) {
            content.addView(text(
                    "✕ Đơn hàng đã bị hủy", 13, RED, true));
        } else {
            for (int i = 0; i < steps.length; i++) {
                int color = i <= current ? GREEN : GRAY;

                content.addView(text(
                        (i <= current ? "✓  " : "○  ") + steps[i],
                        12,
                        color,
                        i == current
                ));
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Chi tiết đơn hàng")
                .setView(content)
                .setPositiveButton("Đóng", null)
                .setNeutralButton("Hủy đơn", (d, w) ->
                        cancelOrder(order))
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                    .setEnabled(
                            !order.status.equals("Đã giao")
                                    && !order.status.equals("Đã hủy")
                    );
        });

        dialog.show();
    }

    private int statusIndex(String status) {
        switch (status) {
            case "Chờ xác nhận":
                return 0;
            case "Đang xử lý":
                return 2;
            case "Đang giao":
                return 3;
            case "Đã giao":
                return 4;
            default:
                return -1;
        }
    }

    private void cancelOrder(Order order) {
        if (order.status.equals("Đã giao")
                || order.status.equals("Đã hủy")) {
            toast("Không thể hủy đơn ở trạng thái này");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage(
                        "Bạn có chắc muốn hủy " + order.id + "?"
                )
                .setNegativeButton("Không", null)
                .setPositiveButton("Xác nhận hủy", (d, w) -> {
                    order.status = "Đã hủy";
                    renderFilters();
                    renderOrders();
                    toast("Đã hủy đơn hàng");
                })
                .show();
    }

    private void updateStatus(Order order) {
        if (order.status.equals("Đã hủy")
                || order.status.equals("Đã giao")) {
            toast("Đơn hàng này không cần cập nhật trạng thái");
            return;
        }

        String[] choices = {
                "Chờ xác nhận",
                "Đang xử lý",
                "Đang giao",
                "Đã giao",
                "Đã hủy"
        };

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật trạng thái mẫu")
                .setItems(choices, (dialog, which) -> {
                    order.status = choices[which];
                    renderFilters();
                    renderOrders();
                    toast("Đã cập nhật trạng thái mẫu");
                })
                .show();
    }

    private void showInvoice(Order order) {
        String invoice =
                "HÓA ĐƠN PEESEE\n\n"
                        + "Mã đơn: " + order.id + "\n"
                        + "Ngày đặt: " + order.date + "\n"
                        + "Sản phẩm: " + order.product + "\n"
                        + "Số lượng: " + order.quantity + "\n"
                        + "Đơn giá: " + money(order.price) + "\n"
                        + "Tổng tiền: " + money(order.total()) + "\n"
                        + "Trạng thái: " + order.status + "\n\n"
                        + "Hóa đơn minh họa từ ứng dụng.";

        new AlertDialog.Builder(this)
                .setTitle("Hóa đơn")
                .setMessage(invoice)
                .setNegativeButton("Đóng", null)
                .setPositiveButton("Chia sẻ", (dialog, which) -> {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, invoice);

                    startActivity(Intent.createChooser(
                            share, "Chia sẻ hóa đơn"));
                })
                .show();
    }

    // ĐIỀU HƯỚNG GIỮA CÁC TRANG
    private void navigate(int index) {

        if (index == 0) {

            Intent intent = new Intent(
                    OrderActivity.this,
                    HomeActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            intent.putExtra("selected_category", "Tất cả");

            startActivity(intent);

        } else if (index == 1) {

            Intent intent = new Intent(
                    OrderActivity.this,
                    CategoryActivity.class
            );

            startActivity(intent);

        } else if (index == 2) {

            Intent intent = new Intent(
                    OrderActivity.this,
                    CartActivity.class
            );

            startActivity(intent);

        } else if (index == 3) {

            // Đang ở trang Đơn hàng.

        } else if (index == 4) {

            ProfileHelper.showProfile(OrderActivity.this);
        }
    }

    // Tìm Activity theo tên để không gây lỗi biên dịch
    // nếu trang danh mục hoặc cá nhân chưa được tạo.
    private void openPageByName(String className) {
        try {
            Class<?> page = Class.forName(
                    getPackageName() + "." + className
            );

            Intent intent = new Intent(this, page);
            startActivity(intent);

        } catch (ClassNotFoundException e) {
            toast("Bạn cần tạo trang " + className + " trước.");
        }
    }

    private void toast(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}
