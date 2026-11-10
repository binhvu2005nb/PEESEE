
package com.example.peesee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class CartActivity extends Activity {

    static class Item {
        String name;
        String type;
        String icon;
        int price;
        int oldPrice;
        int quantity = 1;
        boolean selected = true;

        Item(String name, String type, String icon,
             int price, int oldPrice) {
            this.name = name;
            this.type = type;
            this.icon = icon;
            this.price = price;
            this.oldPrice = oldPrice;
        }
    }

    // Dữ liệu giỏ hàng dùng chung trong ứng dụng.
    static final ArrayList<Item> cartItems = new ArrayList<>();

    private final int RED = Color.rgb(215, 0, 20);
    private final int DARK = Color.rgb(30, 38, 50);
    private final int GRAY = Color.rgb(125, 135, 150);
    private final int BORDER = Color.rgb(235, 237, 241);

    private LinearLayout root;
    private LinearLayout list;

    private TextView countText;
    private TextView subtotalText;
    private TextView discountText;
    private TextView totalText;
    private Button checkoutButton;

    private boolean couponApplied = false;

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    // Thêm sản phẩm vào giỏ.
    public static void addItem(String name, String type, String icon,
                               int price, int oldPrice) {
        for (Item item : cartItems) {
            if (item.name.equals(name)) {
                item.quantity++;
                return;
            }
        }

        cartItems.add(new Item(
                name, type, icon, price, oldPrice
        ));
    }

    private GradientDrawable background(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), BORDER);
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

    private LinearLayout.LayoutParams lp(int width, int height) {
        return new LinearLayout.LayoutParams(
                width < 0 ? width : dp(width),
                height < 0 ? height : dp(height)
        );
    }

    private String money(int amount) {
        return String.format(Locale.US, "%,dđ", amount)
                .replace(',', '.');
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildPage();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Cập nhật giỏ hàng khi quay lại trang này.
        if (root != null) {
            buildPage();
        }
    }

    private void buildPage() {
        root = column();
        root.setBackgroundColor(Color.WHITE);
        setContentView(root);

        // HEADER
        LinearLayout header = row();
        header.setBackgroundColor(RED);
        header.setPadding(dp(12), 0, dp(12), 0);

        TextView back = text("‹", 34, Color.WHITE, false);
        back.setGravity(Gravity.CENTER);
        header.addView(back, lp(40, 52));
        back.setOnClickListener(v -> finish());

        TextView logo = text("peesee", 24, Color.WHITE, true);
        logo.setGravity(Gravity.CENTER);
        header.addView(logo,
                new LinearLayout.LayoutParams(0, dp(52), 1));

        TextView clear = text("Xóa", 13, Color.WHITE, false);
        clear.setGravity(Gravity.CENTER);
        header.addView(clear, lp(45, 52));
        clear.setOnClickListener(v -> clearCart());

        root.addView(header, lp(-1, 54));

        // TIÊU ĐỀ
        LinearLayout titleArea = column();
        titleArea.setPadding(dp(16), dp(10), dp(16), dp(8));

        titleArea.addView(text("Giỏ hàng", 21, DARK, true));

        countText = text("", 12, GRAY, false);
        LinearLayout.LayoutParams countParams = lp(-1, 22);
        countParams.topMargin = dp(2);
        titleArea.addView(countText, countParams);

        root.addView(titleArea);

        // NỘI DUNG CUỘN
        ScrollView scroll = new ScrollView(this);
        root.addView(scroll,
                new LinearLayout.LayoutParams(-1, 0, 1));

        list = column();
        list.setPadding(dp(12), dp(2), dp(12), dp(10));
        scroll.addView(list);

        if (cartItems.isEmpty()) {
            TextView empty = text(
                    "🛒\n\nGiỏ hàng đang trống\n\n"
                            + "Hãy chọn sản phẩm bạn yêu thích.",
                    16, GRAY, false
            );
            empty.setGravity(Gravity.CENTER);
            list.addView(empty, lp(-1, 230));
        } else {
            for (int i = 0; i < cartItems.size(); i++) {
                addCartRow(cartItems.get(i));
            }
        }

        // MÃ GIẢM GIÁ
        LinearLayout coupon = row();
        coupon.setPadding(dp(12), dp(10), dp(12), dp(10));
        coupon.setBackground(background(0xFFFFF7F7, 10));

        coupon.addView(text("♧", 22, RED, true), lp(32, 40));

        LinearLayout couponInfo = column();
        couponInfo.addView(text("Mã giảm giá", 13, RED, true));
        couponInfo.addView(text(
                couponApplied
                        ? "Đã áp dụng giảm 10%"
                        : "Chọn mã giảm giá phù hợp",
                10, GRAY, false
        ));

        coupon.addView(couponInfo,
                new LinearLayout.LayoutParams(0, -2, 1));

        coupon.addView(text("›", 26, RED, false));

        LinearLayout.LayoutParams couponParams = lp(-1, 54);
        couponParams.topMargin = dp(8);
        list.addView(coupon, couponParams);

        coupon.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                toast("Bạn chưa có sản phẩm trong giỏ hàng");
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Mã giảm giá")
                    .setMessage(
                            couponApplied
                                    ? "Bạn có muốn hủy mã giảm giá PEESEE10?"
                                    : "Áp dụng mã PEESEE10 để giảm 10% "
                                      + "trên tổng tiền sản phẩm đã chọn?"
                    )
                    .setNegativeButton("Để sau", null)
                    .setPositiveButton(
                            couponApplied ? "Hủy mã" : "Áp dụng",
                            (dialog, which) -> {
                                couponApplied = !couponApplied;
                                buildPage();
                            }
                    )
                    .show();
        });

        // TỔNG TIỀN
        LinearLayout summary = column();
        summary.setPadding(dp(12), dp(8), dp(12), dp(8));
        summary.setBackground(background(Color.WHITE, 10));

        subtotalText = text("", 12, GRAY, false);
        discountText = text("", 12, RED, false);
        totalText = text("", 14, DARK, true);

        addSummaryLine(summary, "Tạm tính", subtotalText);
        addSummaryLine(summary, "Giảm giá", discountText);

        View divider = new View(this);
        divider.setBackgroundColor(BORDER);

        LinearLayout.LayoutParams dividerParams = lp(-1, 1);
        dividerParams.topMargin = dp(5);
        dividerParams.bottomMargin = dp(5);
        summary.addView(divider, dividerParams);

        addSummaryLine(summary, "Tổng thanh toán", totalText);

        LinearLayout.LayoutParams summaryParams = lp(-1, -2);
        summaryParams.topMargin = dp(8);
        list.addView(summary, summaryParams);

        checkoutButton = new Button(this);
        checkoutButton.setText("Thanh toán");
        checkoutButton.setTextColor(Color.WHITE);
        checkoutButton.setTextSize(13);
        checkoutButton.setAllCaps(false);
        checkoutButton.setBackgroundTintList(
                ColorStateList.valueOf(RED)
        );

        LinearLayout.LayoutParams checkoutParams = lp(-1, 44);
        checkoutParams.topMargin = dp(8);
        list.addView(checkoutButton, checkoutParams);

        checkoutButton.setOnClickListener(v -> checkout());

        updateTotal();

        // THANH ĐIỀU HƯỚNG DƯỚI
        buildBottomNav();
    }

    private void buildBottomNav() {
        LinearLayout nav = row();
        nav.setBackgroundColor(Color.WHITE);
        nav.setElevation(dp(8));

        String[] labels = {
                "Trang chủ", "Danh mục", "Giỏ hàng",
                "Đơn hàng", "Cá nhân"
        };

        String[] icons = {"⌂", "▦", "🛒", "▤", "♙"};

        for (int i = 0; i < labels.length; i++) {
            final int index = i;

            LinearLayout item = column();
            item.setGravity(Gravity.CENTER);

            int color = index == 2 ? RED : GRAY;

            TextView iconView = text(icons[i], 20, color, false);
            iconView.setGravity(Gravity.CENTER);
            item.addView(iconView);

            TextView label = text(labels[i], 9, color, false);
            label.setGravity(Gravity.CENTER);
            item.addView(label);

            nav.addView(item,
                    new LinearLayout.LayoutParams(0, dp(52), 1));

            item.setOnClickListener(v -> {
                switch (index) {
                    case 0:
                        // Trở về HomeActivity, không lùi từng trang.
                        Intent homeIntent = new Intent(
                                CartActivity.this,
                                HomeActivity.class
                        );
                        homeIntent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                        );
                        startActivity(homeIntent);
                        finish();
                        break;

                    case 1:
                        toast("Trang danh mục chưa được kết nối");
                        break;

                    case 2:
                        // Đang ở giỏ hàng.
                        break;

                    case 3:
                        startActivity(new Intent(
                                CartActivity.this,
                                OrderActivity.class
                        ));
                        break;

                    case 4:
                        toast("Trang cá nhân chưa được kết nối");
                        break;
                }
            });
        }

        root.addView(nav, lp(-1, 54));
    }

    private void addSummaryLine(LinearLayout parent,
                                String label, TextView value) {
        LinearLayout line = row();

        TextView left = text(label, 12, GRAY, false);
        line.addView(left,
                new LinearLayout.LayoutParams(0, dp(25), 1));

        line.addView(value);
        parent.addView(line);
    }

    private void addCartRow(Item item) {
        LinearLayout card = row();
        card.setGravity(Gravity.TOP);
        card.setPadding(dp(7), dp(10), dp(7), dp(10));
        card.setBackground(background(Color.WHITE, 12));

        LinearLayout.LayoutParams cardParams = lp(-1, -2);
        cardParams.bottomMargin = dp(8);
        list.addView(card, cardParams);

        CheckBox check = new CheckBox(this);
        check.setButtonTintList(ColorStateList.valueOf(RED));
        check.setChecked(item.selected);
        card.addView(check, lp(28, 32));

        check.setOnCheckedChangeListener((button, checked) -> {
            item.selected = checked;
            updateTotal();
        });

        TextView image = text(item.icon, 37, DARK, false);
        image.setGravity(Gravity.CENTER);
        image.setBackground(background(0xFFF4F5F7, 10));

        LinearLayout.LayoutParams imageParams = lp(76, 84);
        imageParams.setMargins(dp(3), dp(5), dp(8), 0);
        card.addView(image, imageParams);

        LinearLayout info = column();

        TextView name = text(item.name, 12, DARK, true);
        name.setMaxLines(2);
        info.addView(name);

        TextView type = text(item.type, 10, GRAY, false);
        LinearLayout.LayoutParams typeParams = lp(-1, -2);
        typeParams.topMargin = dp(3);
        info.addView(type, typeParams);

        TextView price = text(money(item.price), 13, RED, true);
        LinearLayout.LayoutParams priceParams = lp(-1, -2);
        priceParams.topMargin = dp(4);
        info.addView(price, priceParams);

        if (item.oldPrice > item.price) {
            TextView oldPrice = text(
                    money(item.oldPrice), 9, GRAY, false
            );
            oldPrice.setPaintFlags(
                    oldPrice.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );
            info.addView(oldPrice);
        }

        LinearLayout quantity = row();
        quantity.setGravity(Gravity.CENTER_VERTICAL);
        quantity.setBackground(background(0xFFF7F8FA, 5));

        TextView minus = text("−", 17, DARK, false);
        minus.setGravity(Gravity.CENTER);
        quantity.addView(minus, lp(30, 29));

        TextView qty = text(
                String.valueOf(item.quantity), 12, DARK, false
        );
        qty.setGravity(Gravity.CENTER);
        quantity.addView(qty, lp(28, 29));

        TextView plus = text("+", 17, DARK, false);
        plus.setGravity(Gravity.CENTER);
        quantity.addView(plus, lp(30, 29));

        LinearLayout.LayoutParams quantityParams = lp(-2, 30);
        quantityParams.topMargin = dp(5);
        info.addView(quantity, quantityParams);

        minus.setOnClickListener(v -> {
            if (item.quantity > 1) {
                item.quantity--;
            } else {
                cartItems.remove(item);
            }
            buildPage();
        });

        plus.setOnClickListener(v -> {
            if (item.quantity < 99) {
                item.quantity++;
                buildPage();
            } else {
                toast("Số lượng tối đa là 99");
            }
        });

        card.addView(info,
                new LinearLayout.LayoutParams(0, -2, 1));

        TextView delete = text("×", 21, GRAY, false);
        delete.setGravity(Gravity.CENTER);
        card.addView(delete, lp(24, 30));

        delete.setOnClickListener(v -> {
            cartItems.remove(item);
            if (cartItems.isEmpty()) {
                couponApplied = false;
            }
            buildPage();
        });
    }

    private int selectedCount() {
        int count = 0;

        for (Item item : cartItems) {
            if (item.selected) {
                count += item.quantity;
            }
        }

        return count;
    }

    private int subtotal() {
        int total = 0;

        for (Item item : cartItems) {
            if (item.selected) {
                total += item.price * item.quantity;
            }
        }

        return total;
    }

    private int discountAmount() {
        return couponApplied ? subtotal() / 10 : 0;
    }

    private void updateTotal() {
        if (subtotalText == null) {
            return;
        }

        int sub = subtotal();
        int discount = discountAmount();

        countText.setText(cartItems.size() + " sản phẩm");
        subtotalText.setText(money(sub));
        discountText.setText("-" + money(discount));
        totalText.setText(money(sub - discount));

        if (checkoutButton != null) {
            checkoutButton.setText(
                    "🛒 Thanh toán (" + selectedCount() + ")"
            );
        }
    }

    private void checkout() {
        if (selectedCount() == 0) {
            toast("Vui lòng chọn sản phẩm cần thanh toán");
            return;
        }

        int total = subtotal() - discountAmount();

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage(
                        "Tổng thanh toán: " + money(total)
                                + "\n\nXác nhận tạo đơn hàng của bạn?"
                )
                .setNegativeButton("Quay lại", null)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    for (int i = cartItems.size() - 1; i >= 0; i--) {
                        Item item = cartItems.get(i);

                        if (item.selected) {
                            OrderActivity.createOrder(
                                    item.name,
                                    item.type,
                                    item.icon,
                                    item.price,
                                    item.quantity
                            );

                            cartItems.remove(i);
                        }
                    }

                    couponApplied = false;

                    Toast.makeText(
                            CartActivity.this,
                            "Đặt hàng thành công!",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            CartActivity.this,
                            OrderActivity.class
                    );
                    startActivity(intent);
                })
                .show();
    }

    private void clearCart() {
        if (cartItems.isEmpty()) {
            toast("Giỏ hàng đang trống");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xóa giỏ hàng")
                .setMessage("Bạn có muốn xóa tất cả sản phẩm?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    cartItems.clear();
                    couponApplied = false;
                    buildPage();
                })
                .show();
    }

    private void toast(String message) {
        Toast.makeText(
                this, message, Toast.LENGTH_SHORT
        ).show();
    }
}
