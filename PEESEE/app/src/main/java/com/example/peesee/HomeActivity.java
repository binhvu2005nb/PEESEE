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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends Activity {

    private final int RED = Color.rgb(215, 25, 32);
    private final int DARK = Color.rgb(30, 35, 44);
    private final int GRAY = Color.rgb(115, 125, 143);
    private final int LIGHT = Color.rgb(246, 247, 250);
    private final int BORDER = Color.rgb(233, 237, 242);

    private LinearLayout root;
    private LinearLayout content;
    private LinearLayout flashList;
    private LinearLayout bestList;
    private ScrollView homeScroll;
    private EditText searchInput;
    private TextView cartCount;

    private String category = "Tất cả";
    private String keyword = "";

    private final ArrayList<ProductManager.Product> products =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProductManager.initialize();

        products.clear();

        if (ProductManager.products != null) {
            products.addAll(ProductManager.products);
        }

        buildPage();
    }

    // ==================== HÀM DÙNG CHUNG ====================

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), BORDER);
        return drawable;
    }

    private TextView text(
            String value, int size, int color, boolean bold) {

        TextView view = new TextView(this);
        view.setText(value == null ? "" : value);
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

    private void addText(
            LinearLayout parent,
            String value,
            int size,
            int color,
            boolean bold,
            int top) {

        TextView view = text(value, size, color, bold);

        LinearLayout.LayoutParams params = lp(-1, -2);
        params.topMargin = dp(top);

        parent.addView(view, params);
    }

    private void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    private String money(int amount) {
        return String.format(
                Locale.US,
                "%,dđ",
                amount
        ).replace(',', '.');
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    // ==================== DỰNG TRANG CHỦ ====================

    private void buildPage() {
        root = column();
        root.setBackgroundColor(Color.WHITE);
        setContentView(root);

        homeScroll = new ScrollView(this);
        homeScroll.setFillViewport(true);
        homeScroll.setVerticalScrollBarEnabled(false);

        root.addView(
                homeScroll,
                new LinearLayout.LayoutParams(
                        -1, 0, 1
                )
        );

        content = column();
        content.setPadding(
                dp(16), dp(8), dp(16), dp(12)
        );

        homeScroll.addView(content);

        buildHeader();
        buildSearch();
        buildBanner();
        buildCategories();
        buildFlashSale();
        buildBestSellers();
        buildPcBanner();
        buildBottomNav();
    }

    // ==================== HEADER ====================

    private void buildHeader() {
        LinearLayout header = row();

        TextView logo = text("peesee", 27, RED, true);

        header.addView(
                logo,
                new LinearLayout.LayoutParams(
                        0, dp(42), 1
                )
        );

        logo.setOnClickListener(v -> goToHome());

        TextView notice = text("♧", 24, DARK, false);
        notice.setGravity(Gravity.CENTER);
        header.addView(notice, lp(36, 42));

        notice.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Thông báo")
                        .setMessage("Bạn chưa có thông báo mới.")
                        .setPositiveButton("Đóng", null)
                        .show()
        );

        TextView cartButton = text("🛒", 21, DARK, false);
        cartButton.setGravity(Gravity.CENTER);
        header.addView(cartButton, lp(38, 42));

        cartButton.setOnClickListener(v -> openCart());

        cartCount = text("Giỏ hàng", 10, RED, true);
        cartCount.setGravity(Gravity.CENTER);
        header.addView(cartCount, lp(62, 42));

        cartCount.setOnClickListener(v -> openCart());

        content.addView(header, lp(-1, 42));
    }

    // ==================== TÌM KIẾM ====================

    private void buildSearch() {
        searchInput = new EditText(this);

        searchInput.setSingleLine(true);
        searchInput.setTextSize(12);
        searchInput.setHint(
                "⌕  Tìm kiếm sản phẩm, thương hiệu..."
        );
        searchInput.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchInput.setPadding(
                dp(12), 0, dp(12), 0
        );
        searchInput.setBackground(bg(LIGHT, 12));

        LinearLayout.LayoutParams inputParams = lp(-1, 42);
        inputParams.topMargin = dp(8);

        content.addView(searchInput, inputParams);

        Button searchButton = new Button(this);
        searchButton.setText("Tìm kiếm");
        searchButton.setTextSize(12);
        searchButton.setAllCaps(false);
        searchButton.setTextColor(Color.WHITE);
        searchButton.setBackgroundTintList(
                ColorStateList.valueOf(RED)
        );

        LinearLayout.LayoutParams buttonParams = lp(-1, 42);
        buttonParams.topMargin = dp(4);

        content.addView(searchButton, buttonParams);

        searchButton.setOnClickListener(v -> searchProducts());

        searchInput.setOnEditorActionListener(
                (v, actionId, event) -> {
                    searchProducts();
                    return true;
                }
        );

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

                renderBestSellers();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchProducts() {
        keyword = searchInput.getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        renderBestSellers();
        goToProductSection();
    }

    // ==================== BANNER CHÍNH ====================

    private void buildBanner() {
        LinearLayout banner = row();

        banner.setPadding(
                dp(12), dp(10), dp(8), dp(10)
        );
        banner.setBackground(bg(0xFFFCEBEC, 14));

        LinearLayout info = column();

        banner.addView(
                info,
                new LinearLayout.LayoutParams(
                        0, -1, 1.2f
                )
        );

        addText(
                info,
                "PHỤ KIỆN PC CHÍNH HÃNG",
                9, RED, true, 0
        );

        addText(
                info,
                "Nâng cấp góc máy của bạn",
                19, DARK, true, 5
        );

        addText(
                info,
                "Thiết bị phù hợp cho góc gaming.",
                10, GRAY, false, 5
        );

        Button buy = new Button(this);
        buy.setText("Mua ngay →");
        buy.setTextSize(10);
        buy.setAllCaps(false);
        buy.setTextColor(Color.WHITE);
        buy.setBackgroundTintList(
                ColorStateList.valueOf(RED)
        );

        LinearLayout.LayoutParams buyParams = lp(115, 38);
        buyParams.topMargin = dp(7);

        info.addView(buy, buyParams);

        buy.setOnClickListener(v -> {
            category = "Tất cả";
            searchInput.setText("");
            goToProductSection();
            renderBestSellers();
        });

        TextView illustration = text(
                "🎧\n⌨️ 🖱️",
                30, DARK, false
        );

        illustration.setGravity(Gravity.CENTER);

        banner.addView(
                illustration,
                new LinearLayout.LayoutParams(
                        0, -1, 0.8f
                )
        );

        LinearLayout.LayoutParams bannerParams = lp(-1, 145);
        bannerParams.topMargin = dp(12);

        content.addView(banner, bannerParams);
    }

    // ==================== DANH MỤC ====================

    private void buildCategories() {
        HorizontalScrollView scroll =
                new HorizontalScrollView(this);

        scroll.setHorizontalScrollBarEnabled(false);

        LinearLayout items = row();

        String[] names = {
                "Chuột Gaming",
                "Bàn phím",
                "Tai nghe",
                "Màn hình",
                "Xem tất cả"
        };

        String[] icons = {
                "🖱️", "⌨️", "🎧", "🖥️", "▦"
        };

        String[] types = {
                "Chuột", "Bàn phím",
                "Tai nghe", "Màn hình", "Tất cả"
        };

        for (int i = 0; i < names.length; i++) {
            final String selected = types[i];

            LinearLayout item = column();
            item.setGravity(Gravity.CENTER);
            item.setPadding(
                    dp(4), dp(5), dp(4), dp(5)
            );

            TextView icon = text(icons[i], 23, RED, false);
            icon.setGravity(Gravity.CENTER);
            icon.setBackground(bg(0xFFFFF0F0, 12));
            item.addView(icon, lp(48, 46));

            TextView label = text(names[i], 10, DARK, false);
            label.setGravity(Gravity.CENTER);
            item.addView(label, lp(68, 27));

            item.setOnClickListener(v -> {
                category = selected;
                searchInput.setText("");
                renderBestSellers();
                goToProductSection();
            });

            items.addView(item, lp(76, 85));
        }

        scroll.addView(items);

        LinearLayout.LayoutParams params = lp(-1, 90);
        params.topMargin = dp(8);

        content.addView(scroll, params);
    }

    // ==================== FLASH SALE ====================

    private void buildFlashSale() {
        LinearLayout heading = row();

        TextView title = text(
                "⚡ Flash Sale", 17, DARK, true
        );

        heading.addView(
                title,
                new LinearLayout.LayoutParams(
                        0, -2, 1
                )
        );

        TextView seeAll = text(
                "Xem tất cả →", 10, RED, false
        );

        heading.addView(seeAll);

        seeAll.setOnClickListener(v -> {
            category = "Tất cả";
            searchInput.setText("");
            renderBestSellers();
            goToProductSection();
        });

        LinearLayout.LayoutParams headingParams = lp(-1, 38);
        headingParams.topMargin = dp(6);

        content.addView(heading, headingParams);

        HorizontalScrollView scroll =
                new HorizontalScrollView(this);

        scroll.setHorizontalScrollBarEnabled(false);

        flashList = row();
        scroll.addView(flashList);

        content.addView(scroll, lp(-1, 200));

        int count = Math.min(3, products.size());

        for (int i = 0; i < count; i++) {
            ProductManager.Product product = products.get(i);

            if (product != null) {
                addProductCard(flashList, product);
            }
        }

        if (count == 0) {
            addText(
                    content,
                    "Chưa có sản phẩm để hiển thị.",
                    12, GRAY, false, 4
            );
        }
    }

    // ==================== SẢN PHẨM BÁN CHẠY ====================

    private void buildBestSellers() {
        addText(
                content,
                "Sản phẩm bán chạy",
                17, DARK, true, 14
        );

        HorizontalScrollView scroll =
                new HorizontalScrollView(this);

        scroll.setHorizontalScrollBarEnabled(false);

        LinearLayout tabs = row();

        String[] filters = {
                "Tất cả", "Chuột", "Bàn phím",
                "Tai nghe", "Màn hình"
        };

        for (String filter : filters) {
            TextView tab = text(
                    filter,
                    10,
                    filter.equals(category) ? Color.WHITE : DARK,
                    false
            );

            tab.setGravity(Gravity.CENTER);
            tab.setPadding(
                    dp(12), dp(7), dp(12), dp(7)
            );

            tab.setBackground(bg(
                    filter.equals(category) ? RED : LIGHT,
                    20
            ));

            LinearLayout.LayoutParams tabParams = lp(-2, 30);
            tabParams.rightMargin = dp(6);

            tabs.addView(tab, tabParams);

            tab.setOnClickListener(v -> {
                category = filter;
                renderBestSellers();
            });
        }

        scroll.addView(tabs);
        content.addView(scroll, lp(-1, 34));

        bestList = column();
        content.addView(bestList, lp(-1, -2));

        renderBestSellers();
    }

    private void renderBestSellers() {
        if (bestList == null) {
            return;
        }

        bestList.removeAllViews();

        ArrayList<ProductManager.Product> filtered =
                new ArrayList<>();

        for (ProductManager.Product product : products) {
            if (product == null) {
                continue;
            }

            String name = safe(product.name);
            String type = safe(product.type);

            boolean categoryMatch =
                    category.equals("Tất cả")
                            || category.equals("Xem tất cả")
                            || type.equalsIgnoreCase(category);

            boolean keywordMatch =
                    keyword.isEmpty()
                            || name.toLowerCase(Locale.ROOT)
                            .contains(keyword)
                            || type.toLowerCase(Locale.ROOT)
                            .contains(keyword);

            if (categoryMatch && keywordMatch) {
                filtered.add(product);
            }
        }

        if (filtered.isEmpty()) {
            TextView empty = text(
                    "Không tìm thấy sản phẩm phù hợp.",
                    13, GRAY, false
            );

            empty.setGravity(Gravity.CENTER);
            empty.setPadding(
                    0, dp(20), 0, dp(20)
            );

            bestList.addView(empty);
            return;
        }

        for (int i = 0; i < filtered.size(); i += 2) {
            LinearLayout productRow = row();
            productRow.setGravity(Gravity.TOP);

            addBestCard(productRow, filtered.get(i));

            if (i + 1 < filtered.size()) {
                addBestCard(productRow, filtered.get(i + 1));
            }

            LinearLayout.LayoutParams rowParams = lp(-1, -2);
            rowParams.topMargin = dp(6);

            bestList.addView(productRow, rowParams);
        }
    }

    private void addBestCard(
            LinearLayout parent,
            ProductManager.Product product) {

        LinearLayout card = column();

        card.setPadding(
                dp(8), dp(7), dp(8), dp(7)
        );
        card.setBackground(bg(0xFFF7F8FA, 10));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        0, dp(145), 1
                );

        cardParams.setMargins(
                dp(3), dp(2), dp(3), dp(2)
        );

        parent.addView(card, cardParams);

        TextView icon = text(
                safe(product.icon).isEmpty() ? "📦" : product.icon,
                25, DARK, false
        );

        icon.setGravity(Gravity.CENTER);
        card.addView(icon, lp(-1, 36));

        TextView name = text(
                safe(product.name), 10, DARK, false
        );

        name.setMaxLines(2);
        card.addView(name, lp(-1, 30));

        addText(
                card,
                money(product.price),
                11, RED, true, 2
        );

        TextView rating = text(
                "⭐ " + (product.rating / 10.0)
                        + " (" + (product.rating * 3) + ")",
                9, GRAY, false
        );

        card.addView(rating);

        card.setOnClickListener(v -> showProduct(product));
    }

    // ==================== THẺ FLASH SALE ====================

    private void addProductCard(
            LinearLayout parent,
            ProductManager.Product product) {

        LinearLayout card = column();

        card.setPadding(
                dp(8), dp(7), dp(8), dp(7)
        );

        card.setBackground(bg(Color.WHITE, 12));

        LinearLayout.LayoutParams cardParams = lp(145, -1);

        cardParams.setMargins(
                dp(2), dp(3), dp(6), dp(4)
        );

        parent.addView(card, cardParams);

        TextView discount = text(
                "-" + product.discount + "%",
                9, Color.WHITE, true
        );

        discount.setBackground(bg(RED, 5));
        discount.setPadding(
                dp(4), dp(2), dp(4), dp(2)
        );

        card.addView(discount, lp(-2, 18));

        TextView icon = text(
                safe(product.icon).isEmpty() ? "📦" : product.icon,
                36, DARK, false
        );

        icon.setGravity(Gravity.CENTER);
        card.addView(icon, lp(-1, 55));

        TextView name = text(
                safe(product.name), 10, DARK, false
        );

        name.setMaxLines(2);
        card.addView(name, lp(-1, 30));

        TextView oldPrice = text(
                money(product.oldPrice), 9, GRAY, false
        );

        oldPrice.setPaintFlags(
                oldPrice.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG
        );

        card.addView(oldPrice);

        addText(
                card,
                money(product.price),
                12, RED, true, 1
        );

        addText(
                card,
                "⭐ " + (product.rating / 10.0),
                9, GRAY, false, 1
        );

        Button add = new Button(this);
        add.setText("🛒 Thêm vào giỏ");
        add.setTextSize(9);
        add.setAllCaps(false);
        add.setTextColor(Color.WHITE);
        add.setBackgroundTintList(
                ColorStateList.valueOf(RED)
        );

        LinearLayout.LayoutParams addParams = lp(-1, 34);
        addParams.topMargin = dp(4);

        card.addView(add, addParams);

        add.setOnClickListener(v -> addToCart(product));
        card.setOnClickListener(v -> showProduct(product));
    }

    // ==================== BANNER PC ====================

    private void buildPcBanner() {
        LinearLayout banner = row();

        banner.setPadding(
                dp(12), dp(10), dp(8), dp(10)
        );

        banner.setBackground(bg(0xFFFCEBEC, 12));

        LinearLayout info = column();

        banner.addView(
                info,
                new LinearLayout.LayoutParams(
                        0, -1, 1.3f
                )
        );

        addText(
                info,
                "Build góc PC trong mơ",
                14, DARK, true, 0
        );

        addText(
                info,
                "Chọn phụ kiện phù hợp với bạn.",
                10, GRAY, false, 4
        );

        Button explore = new Button(this);
        explore.setText("Khám phá ngay");
        explore.setTextSize(10);
        explore.setAllCaps(false);
        explore.setTextColor(RED);
        explore.setBackgroundTintList(
                ColorStateList.valueOf(Color.WHITE)
        );

        LinearLayout.LayoutParams exploreParams = lp(125, 36);
        exploreParams.topMargin = dp(5);

        info.addView(explore, exploreParams);

        explore.setOnClickListener(v -> {
            category = "Tất cả";
            searchInput.setText("");
            renderBestSellers();
            goToProductSection();
        });

        TextView pc = text(
                "🖥️\n🎮", 28, DARK, false
        );

        pc.setGravity(Gravity.CENTER);

        banner.addView(
                pc,
                new LinearLayout.LayoutParams(
                        0, -1, 0.7f
                )
        );

        LinearLayout.LayoutParams bannerParams = lp(-1, 95);
        bannerParams.topMargin = dp(14);

        content.addView(banner, bannerParams);
    }

    // ==================== THANH ĐIỀU HƯỚNG ====================

    private void buildBottomNav() {
        LinearLayout nav = row();

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

            LinearLayout item = column();
            item.setGravity(Gravity.CENTER);

            int color = i == 0 ? RED : GRAY;

            TextView icon = text(
                    icons[i], 21, color, false
            );

            icon.setGravity(Gravity.CENTER);
            item.addView(icon);

            TextView label = text(
                    labels[i], 9, color, false
            );

            label.setGravity(Gravity.CENTER);
            item.addView(label);

            nav.addView(
                    item,
                    new LinearLayout.LayoutParams(
                            0, dp(56), 1
                    )
            );

            item.setOnClickListener(v -> handleNav(index));
        }

        root.addView(nav, lp(-1, 58));
    }

    private void handleNav(int index) {
        switch (index) {
            case 0:
                goToHome();
                break;

            case 1:
                category = "Tất cả";
                searchInput.setText("");
                renderBestSellers();
                goToProductSection();
                break;

            case 2:
                openCart();
                break;

            case 3:
                startActivity(
                        new Intent(this, OrderActivity.class)
                );
                break;

            case 4:
                showProfile();
                break;
        }
    }

    private void showProfile() {
        String name = getIntent().getStringExtra("name");

        if (name == null || name.trim().isEmpty()) {
            name = getSharedPreferences(
                    "PEESEE", MODE_PRIVATE
            ).getString("name", "Khách hàng");
        }

        new AlertDialog.Builder(this)
                .setTitle("Tài khoản cá nhân")
                .setMessage("Xin chào, " + name)
                .setPositiveButton("Đóng", null)
                .setNegativeButton("Đăng xuất", (dialog, which) -> {
                    Intent intent = new Intent(
                            HomeActivity.this,
                            MainActivity.class
                    );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                    finish();
                })
                .show();
    }

    // ==================== ĐIỀU HƯỚNG ====================

    private void goToHome() {
        category = "Tất cả";
        keyword = "";

        if (searchInput != null) {
            searchInput.setText("");
        }

        renderBestSellers();

        if (homeScroll != null) {
            homeScroll.post(
                    () -> homeScroll.smoothScrollTo(0, 0)
            );
        }
    }

    private void goToProductSection() {
        if (homeScroll != null && bestList != null) {
            homeScroll.post(
                    () -> homeScroll.smoothScrollTo(
                            0, bestList.getTop()
                    )
            );
        }
    }

    private void openCart() {
        startActivity(
                new Intent(this, CartActivity.class)
        );
    }

    // ==================== GIỎ HÀNG ====================

    private void addToCart(ProductManager.Product product) {
        if (product == null) {
            showMessage("Không thể thêm sản phẩm này.");
            return;
        }

        CartActivity.addItem(
                product.name,
                product.type,
                product.icon,
                product.price,
                product.oldPrice
        );

        if (cartCount != null) {
            cartCount.setText("Đã thêm");
        }

        showMessage("Đã thêm sản phẩm vào giỏ hàng");
    }

    // ==================== CHI TIẾT SẢN PHẨM ====================

    private void showProduct(ProductManager.Product product) {
        if (product == null) {
            showMessage("Không tìm thấy thông tin sản phẩm.");
            return;
        }

        Intent intent = new Intent(
                HomeActivity.this,
                ProductDetailActivity.class
        );

        intent.putExtra("name", product.name);
        intent.putExtra("type", product.type);
        intent.putExtra("icon", product.icon);
        intent.putExtra("price", product.price);
        intent.putExtra("oldPrice", product.oldPrice);
        intent.putExtra("discount", product.discount);

        intent.putExtra(
                "description",
                safe(product.name)
                        + " là sản phẩm thuộc danh mục "
                        + safe(product.type)
                        + ", phù hợp cho nhu cầu học tập, "
                        + "làm việc và giải trí."
        );

        startActivity(intent);
    }
}