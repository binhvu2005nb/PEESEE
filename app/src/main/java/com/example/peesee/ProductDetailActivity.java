
package com.example.peesee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.util.Locale;

public class ProductDetailActivity extends Activity {

    int RED = Color.rgb(215, 0, 20);
    int DARK = Color.rgb(30, 38, 50);
    int GRAY = Color.rgb(115, 125, 143);

    String name, type, icon, description;
    int price, oldPrice, discount;
    int quantity = 1;
    TextView quantityText, totalPriceText;

    int dp(float n) {
        return (int) (n * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(
                w < 0 ? w : dp(w),
                h < 0 ? h : dp(h));
    }

    TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    GradientDrawable bg(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    String money(int value) {
        return String.format(Locale.US, "%,dđ", value)
                .replace(',', '.');
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        type = intent.getStringExtra("type");
        icon = intent.getStringExtra("icon");
        description = intent.getStringExtra("description");

        price = intent.getIntExtra("price", 1299000);
        oldPrice = intent.getIntExtra("oldPrice", price);
        discount = intent.getIntExtra("discount", 0);

        if (name == null) name = "Sản phẩm PEESEE";
        if (type == null) type = "Phụ kiện máy tính";
        if (icon == null) icon = "🖥️";
        if (description == null) {
            description = "Sản phẩm phù hợp cho nhu cầu học tập, làm việc và giải trí.";
        }

        buildPage();
    }

    void buildPage() {
        LinearLayout root = column();
        root.setBackgroundColor(Color.WHITE);
        setContentView(root);

        // Thanh tiêu đề
        LinearLayout header = row();
        header.setPadding(dp(12), 0, dp(12), 0);
        header.setBackgroundColor(RED);

        TextView back = text("‹", 32, Color.WHITE, false);
        back.setGravity(Gravity.CENTER);
        header.addView(back, lp(42, 48));
        back.setOnClickListener(v -> finish());

        TextView logo = text("peesee", 23, Color.WHITE, true);
        logo.setGravity(Gravity.CENTER);
        header.addView(logo, new LinearLayout.LayoutParams(0,
                dp(48), 1));

        TextView cart = text("🛒", 20, Color.WHITE, false);
        cart.setGravity(Gravity.CENTER);
        header.addView(cart, lp(42, 48));

        cart.setOnClickListener(v ->
                startActivity(new Intent(
                        ProductDetailActivity.this,
                        CartActivity.class
                )));

// Nút tài khoản cá nhân
        TextView profile = text("♙", 23, Color.WHITE, false);
        profile.setGravity(Gravity.CENTER);
        header.addView(profile, lp(42, 48));

        profile.setOnClickListener(v ->
                ProfileHelper.showProfile(ProductDetailActivity.this));
        root.addView(header, lp(-1, 48));

        // Nội dung có thể cuộn
        ScrollView scroll = new ScrollView(this);
        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1));

        LinearLayout body = column();
        body.setPadding(dp(15), dp(10), dp(15), dp(12));
        scroll.addView(body);

        // Ảnh và thông tin sản phẩm
        TextView image = text(icon, 76, DARK, false);
        image.setGravity(Gravity.CENTER);
        image.setBackground(bg(0xFFF0F3F7, 12));
        body.addView(image, lp(-1, 190));

        TextView productName = text(name, 20, DARK, true);
        LinearLayout.LayoutParams np = lp(-1, -2);
        np.topMargin = dp(12);
        body.addView(productName, np);

        TextView category = text(type + " | Sản phẩm chính hãng",
                12, GRAY, false);
        LinearLayout.LayoutParams catp = lp(-1, -2);
        catp.topMargin = dp(5);
        body.addView(category, catp);

        TextView rating = text("★★★★★  4.8 (124 đánh giá)",
                13, 0xFFFFB52E, true);
        LinearLayout.LayoutParams rp = lp(-1, -2);
        rp.topMargin = dp(7);
        body.addView(rating, rp);

        LinearLayout prices = row();
        LinearLayout.LayoutParams pp = lp(-1, -2);
        pp.topMargin = dp(8);
        body.addView(prices, pp);

        prices.addView(text(money(price), 23, RED, true));

        if (oldPrice > price) {
            TextView old = text(money(oldPrice), 12, GRAY, false);
            old.setPaintFlags(old.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            LinearLayout.LayoutParams op = lp(-2, -2);
            op.leftMargin = dp(10);
            prices.addView(old, op);

            int actualDiscount = discount > 0 ? discount
                    : (oldPrice - price) * 100 / oldPrice;

            TextView sale = text("-" + actualDiscount + "%",
                    11, RED, true);
            sale.setPadding(dp(5), dp(3), dp(5), dp(3));
            sale.setBackground(bg(0xFFFFE6E8, 5));

            LinearLayout.LayoutParams sp = lp(-2, -2);
            sp.leftMargin = dp(8);
            prices.addView(sale, sp);
        }

        addText(body, "🚚  Miễn phí giao hàng toàn quốc",
                12, 0xFF15965A, false, 12);
        addText(body, "🛡  Bảo hành theo chính sách sản phẩm",
                12, GRAY, false, 6);

        divider(body);

        // Màu sắc
        addText(body, "Màu sắc", 14, DARK, true, 12);

        LinearLayout colors = row();
        TextView black = text("●  Đen", 12, DARK, false);
        black.setPadding(dp(12), dp(8), dp(12), dp(8));
        black.setBackground(bg(0xFFF5F6F8, 7));
        colors.addView(black, lp(-2, 35));
        body.addView(colors);

        // Chọn số lượng
        addText(body, "Số lượng", 14, DARK, true, 12);

        LinearLayout quantityRow = row();
        quantityRow.setGravity(Gravity.CENTER_VERTICAL);

        Button minus = new Button(this);
        minus.setText("−");
        quantityRow.addView(minus, lp(45, 42));

        quantityText = text("1", 15, DARK, true);
        quantityText.setGravity(Gravity.CENTER);
        quantityRow.addView(quantityText, lp(45, 42));

        Button plus = new Button(this);
        plus.setText("+");
        quantityRow.addView(plus, lp(45, 42));

        body.addView(quantityRow);

        minus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantity();
            }
        });

        plus.setOnClickListener(v -> {
            if (quantity < 99) {
                quantity++;
                updateQuantity();
            }
        });

        divider(body);

        // Thông tin sản phẩm
        addText(body, "Thông tin sản phẩm", 16, DARK, true, 12);
        addText(body, description, 12, GRAY, false, 7);

        addText(body, "• Danh mục: " + type,
                12, DARK, false, 10);
        addText(body, "• Tình trạng: Còn hàng",
                12, DARK, false, 5);
        addText(body, "• Hỗ trợ đổi trả theo chính sách cửa hàng",
                12, DARK, false, 5);

        divider(body);

        // Đánh giá
        addText(body, "Đánh giá sản phẩm", 16, DARK, true, 12);
        addText(body, "4.8/5   ★★★★★",
                23, 0xFFFFB52E, true, 8);
        addText(body, "124 đánh giá của khách hàng",
                12, GRAY, false, 5);


        Button review = new Button(this);
        review.setText("Xem tất cả đánh giá");
        review.setTextSize(13);
        review.setAllCaps(false);
        review.setTextColor(RED);

        body.addView(review, lp(-1, 42));

        review.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ProductDetailActivity.this,
                    ReviewActivity.class
            );

            // Truyền thông tin sản phẩm sang trang đánh giá
            intent.putExtra("name", name);
            intent.putExtra("type", type);
            intent.putExtra("icon", icon);
            intent.putExtra("priceText", money(price));

            startActivity(intent);
        });


        divider(body);

        addText(body, "Sản phẩm liên quan", 16, DARK, true, 12);
        addText(body, "Bạn có thể tham khảo các sản phẩm khác "
                        + "trong danh mục " + type + ".",
                12, GRAY, false, 6);

        // Nút chat
        Button chat = new Button(this);
        chat.setText("Chat với cửa hàng");
        body.addView(chat, lp(-1, 42));
        chat.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Hỗ trợ khách hàng")
                        .setMessage("Chức năng chat sẽ được kết nối sau.")
                        .setPositiveButton("Đóng", null)
                        .show());

        // Thanh mua hàng cố định dưới màn hình
        LinearLayout bottom = row();
        bottom.setPadding(dp(8), dp(5), dp(8), dp(5));
        bottom.setBackgroundColor(Color.WHITE);

        Button addCart = new Button(this);
        addCart.setText("Thêm vào giỏ");
        addCart.setTextSize(11);
        addCart.setTextColor(RED);
        addCart.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.WHITE));

        bottom.addView(addCart, new LinearLayout.LayoutParams(
                0, dp(48), 1));

        Button buyNow = new Button(this);
        buyNow.setText("Mua ngay");
        buyNow.setTextSize(12);
        buyNow.setTextColor(Color.WHITE);
        buyNow.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(RED));

        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                0, dp(48), 1);
        bp.leftMargin = dp(7);
        bottom.addView(buyNow, bp);

        root.addView(bottom, lp(-1, 58));

        addCart.setOnClickListener(v -> addToCart(false));
        buyNow.setOnClickListener(v -> addToCart(true));
    }

    void addText(LinearLayout parent, String value, int size,
                 int color, boolean bold, int top) {
        TextView t = text(value, size, color, bold);
        LinearLayout.LayoutParams p = lp(-1, -2);
        p.topMargin = dp(top);
        parent.addView(t, p);
    }

    void divider(LinearLayout parent) {
        View line = new View(this);
        line.setBackgroundColor(0xFFE8EBF0);

        LinearLayout.LayoutParams p = lp(-1, 1);
        p.topMargin = dp(12);
        p.bottomMargin = dp(4);
        parent.addView(line, p);
    }

    void updateQuantity() {
        quantityText.setText(String.valueOf(quantity));
    }

    void addToCart(boolean buyNow) {
        CartActivity.addItem(name, type, icon, price, oldPrice);

        if (buyNow) {
            new AlertDialog.Builder(this)
                    .setTitle("Sản phẩm đã được thêm")
                    .setMessage("Bạn có thể tiếp tục kiểm tra giỏ hàng "
                            + "trước khi xác nhận đơn.")
                    .setNegativeButton("Tiếp tục mua", null)
                    .setPositiveButton("Xem giỏ hàng",
                            (dialog, which) -> startActivity(
                                    new Intent(this, CartActivity.class)))
                    .show();
        } else {
            Toast.makeText(this, "Đã thêm vào giỏ hàng",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
