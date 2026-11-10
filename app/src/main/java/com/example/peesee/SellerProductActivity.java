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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

public class SellerProductActivity extends Activity {

    private final int RED = Color.rgb(215, 25, 32);
    private final int DARK = Color.rgb(30, 35, 44);
    private final int GRAY = Color.rgb(115, 125, 143);
    private final int LIGHT = Color.rgb(246, 247, 250);
    private final int GREEN = Color.rgb(22, 145, 85);

    private LinearLayout productList;
    private TextView countText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProductManager.initialize();
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

    private LinearLayout.LayoutParams lp(int width, int height) {
        return new LinearLayout.LayoutParams(
                width < 0 ? width : dp(width),
                height < 0 ? height : dp(height)
        );
    }

    private String money(int value) {
        NumberFormat format = NumberFormat.getIntegerInstance(
                Locale.forLanguageTag("vi-VN")
        );
        return format.format(value) + "đ";
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

        TextView title = text("Quản lý sản phẩm", 19, DARK, true);
        title.setGravity(Gravity.CENTER);
        header.addView(title, new LinearLayout.LayoutParams(
                0, dp(54), 1
        ));

        TextView home = text("PEESEE", 13, RED, true);
        home.setGravity(Gravity.CENTER);
        header.addView(home, lp(66, 54));

        root.addView(header, lp(-1, 54));

        // Phần nội dung cuộn
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        LinearLayout content = column();
        content.setPadding(dp(16), dp(16), dp(16), dp(20));
        scroll.addView(content);

        // Tiêu đề và mô tả
        TextView heading = text(
                "Sản phẩm của cửa hàng", 22, DARK, true
        );
        content.addView(heading);

        TextView description = text(
                "Thêm, chỉnh sửa hoặc xóa sản phẩm trong cửa hàng.",
                13, GRAY, false
        );

        LinearLayout.LayoutParams descriptionParams = lp(-1, -2);
        descriptionParams.topMargin = dp(6);
        content.addView(description, descriptionParams);

        // Thống kê số sản phẩm
        LinearLayout summary = row();
        summary.setPadding(dp(14), dp(12), dp(14), dp(12));
        summary.setBackground(bg(Color.WHITE, 12));

        LinearLayout.LayoutParams summaryParams = lp(-1, 68);
        summaryParams.topMargin = dp(16);
        content.addView(summary, summaryParams);

        LinearLayout summaryText = column();
        summary.addView(summaryText, new LinearLayout.LayoutParams(
                0, -2, 1
        ));

        summaryText.addView(text(
                "Tổng số sản phẩm", 12, GRAY, false
        ));

        countText = text("0 sản phẩm", 18, DARK, true);
        LinearLayout.LayoutParams countParams = lp(-1, -2);
        countParams.topMargin = dp(3);
        summaryText.addView(countText, countParams);

        TextView boxIcon = text("📦", 28, RED, false);
        boxIcon.setGravity(Gravity.CENTER);
        summary.addView(boxIcon, lp(48, 48));

        // Nút thêm sản phẩm
        Button addButton = new Button(this);
        addButton.setText("+  Thêm sản phẩm");
        addButton.setAllCaps(false);
        addButton.setTextSize(14);
        addButton.setTextColor(Color.WHITE);
        addButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(RED)
        );

        LinearLayout.LayoutParams addParams = lp(-1, 48);
        addParams.topMargin = dp(14);
        content.addView(addButton, addParams);

        addButton.setOnClickListener(v -> showProductDialog(-1));

        // Danh sách sản phẩm
        TextView listTitle = text("Danh sách sản phẩm", 17, DARK, true);
        LinearLayout.LayoutParams listTitleParams = lp(-1, -2);
        listTitleParams.topMargin = dp(22);
        content.addView(listTitle, listTitleParams);

        productList = column();

        LinearLayout.LayoutParams listParams = lp(-1, -2);
        listParams.topMargin = dp(10);
        content.addView(productList, listParams);

        refreshProducts();
    }

    private void refreshProducts() {
        if (productList == null) {
            return;
        }

        productList.removeAllViews();

        int count = ProductManager.products.size();

        if (countText != null) {
            countText.setText(count + " sản phẩm");
        }

        if (count == 0) {
            TextView empty = text(
                    "Chưa có sản phẩm. Hãy bấm Thêm sản phẩm.",
                    14, GRAY, false
            );
            empty.setPadding(dp(12), dp(20), dp(12), dp(20));
            empty.setGravity(Gravity.CENTER);
            empty.setBackground(bg(Color.WHITE, 12));
            productList.addView(empty);
            return;
        }

        for (int i = 0; i < count; i++) {
            ProductManager.Product product =
                    ProductManager.products.get(i);

            final int index = i;

            LinearLayout card = column();
            card.setPadding(dp(12), dp(12), dp(12), dp(12));
            card.setBackground(bg(Color.WHITE, 12));

            LinearLayout.LayoutParams cardParams = lp(-1, -2);
            cardParams.bottomMargin = dp(10);
            productList.addView(card, cardParams);

            // Thông tin chính
            LinearLayout information = row();

            TextView icon = text(
                    product.icon, 32, DARK, false
            );
            icon.setGravity(Gravity.CENTER);
            icon.setBackground(bg(LIGHT, 10));
            information.addView(icon, lp(60, 60));

            LinearLayout details = column();
            details.setPadding(dp(10), 0, 0, 0);

            information.addView(details,
                    new LinearLayout.LayoutParams(0, -2, 1));

            TextView name = text(
                    product.name, 15, DARK, true
            );
            details.addView(name);

            TextView type = text(
                    "Danh mục: " + product.type,
                    12, GRAY, false
            );

            LinearLayout.LayoutParams typeParams = lp(-1, -2);
            typeParams.topMargin = dp(4);
            details.addView(type, typeParams);

            TextView price = text(
                    money(product.price), 15, RED, true
            );

            LinearLayout.LayoutParams priceParams = lp(-1, -2);
            priceParams.topMargin = dp(5);
            details.addView(price, priceParams);

            card.addView(information);

            // Giá gốc
            TextView oldPrice = text(
                    "Giá gốc: " + money(product.oldPrice),
                    12, GRAY, false
            );

            LinearLayout.LayoutParams oldPriceParams = lp(-1, -2);
            oldPriceParams.topMargin = dp(8);
            card.addView(oldPrice, oldPriceParams);

            // Nút sửa và xóa
            LinearLayout actions = row();

            LinearLayout.LayoutParams actionsParams = lp(-1, 42);
            actionsParams.topMargin = dp(10);
            card.addView(actions, actionsParams);

            Button editButton = new Button(this);
            editButton.setText("Sửa sản phẩm");
            editButton.setAllCaps(false);
            editButton.setTextSize(12);
            editButton.setTextColor(RED);
            editButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.rgb(255, 235, 237)
                    )
            );

            actions.addView(editButton,
                    new LinearLayout.LayoutParams(0, -1, 1));

            Button deleteButton = new Button(this);
            deleteButton.setText("Xóa");
            deleteButton.setAllCaps(false);
            deleteButton.setTextSize(12);
            deleteButton.setTextColor(Color.WHITE);
            deleteButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(RED)
            );

            LinearLayout.LayoutParams deleteParams =
                    new LinearLayout.LayoutParams(0, -1, 1);
            deleteParams.leftMargin = dp(8);
            actions.addView(deleteButton, deleteParams);

            editButton.setOnClickListener(
                    v -> showProductDialog(index)
            );

            deleteButton.setOnClickListener(
                    v -> confirmDelete(index)
            );
        }
    }

    private void showProductDialog(int index) {
        boolean isEdit = index >= 0;

        ProductManager.Product oldProduct = null;

        if (isEdit) {
            if (index >= ProductManager.products.size()) {
                refreshProducts();
                return;
            }

            oldProduct = ProductManager.products.get(index);
        }

        LinearLayout form = column();
        form.setPadding(dp(20), dp(8), dp(20), dp(4));

        EditText nameInput = createInput(
                "Tên sản phẩm",
                isEdit ? oldProduct.name : ""
        );
        form.addView(nameInput);

        EditText typeInput = createInput(
                "Danh mục (Laptop, Chuột, ...)",
                isEdit ? oldProduct.type : ""
        );
        addFormInput(form, typeInput);

        EditText iconInput = createInput(
                "Biểu tượng (💻, 🖱️, 🎧, ...)",
                isEdit ? oldProduct.icon : "📦"
        );
        addFormInput(form, iconInput);

        EditText priceInput = createInput(
                "Giá bán (chỉ nhập số)",
                isEdit ? String.valueOf(oldProduct.price) : ""
        );
        priceInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        addFormInput(form, priceInput);

        EditText oldPriceInput = createInput(
                "Giá gốc (chỉ nhập số)",
                isEdit ? String.valueOf(oldProduct.oldPrice) : ""
        );
        oldPriceInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        addFormInput(form, oldPriceInput);

        ScrollView formScroll = new ScrollView(this);
        formScroll.addView(form);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Sửa sản phẩm" : "Thêm sản phẩm")
                .setView(formScroll)
                .setNegativeButton("Hủy", null)
                .setPositiveButton(isEdit ? "Lưu thay đổi" : "Thêm",
                        null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
        ).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String type = typeInput.getText().toString().trim();
            String icon = iconInput.getText().toString().trim();
            String priceString =
                    priceInput.getText().toString().trim();
            String oldPriceString =
                    oldPriceInput.getText().toString().trim();

            if (name.isEmpty() || type.isEmpty()
                    || icon.isEmpty() || priceString.isEmpty()
                    || oldPriceString.isEmpty()) {
                Toast.makeText(this,
                        "Vui lòng nhập đầy đủ thông tin.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int price;
            int oldPrice;

            try {
                price = Integer.parseInt(priceString);
                oldPrice = Integer.parseInt(oldPriceString);
            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        "Giá sản phẩm không hợp lệ.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (price <= 0 || oldPrice <= 0) {
                Toast.makeText(this,
                        "Giá sản phẩm phải lớn hơn 0.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (oldPrice < price) {
                Toast.makeText(this,
                        "Giá gốc phải lớn hơn hoặc bằng giá bán.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int discount = oldPrice > price
                    ? (int) (((long) oldPrice - price) * 100 / oldPrice)
                    : 0;

            if (isEdit) {
                ProductManager.Product product =
                        ProductManager.products.get(index);

                product.name = name;
                product.type = type;
                product.icon = icon;
                product.price = price;
                product.oldPrice = oldPrice;
                product.discount = discount;
            } else {
                ProductManager.products.add(
                        new ProductManager.Product(
                                name, type, icon,
                                price, oldPrice, discount, 0
                        )
                );
            }

            refreshProducts();
            dialog.dismiss();

            Toast.makeText(this,
                    isEdit ? "Đã cập nhật sản phẩm."
                            : "Đã thêm sản phẩm.",
                    Toast.LENGTH_SHORT).show();
        }));

        dialog.show();
    }

    private EditText createInput(String hint, String value) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setText(value);
        input.setTextSize(14);
        input.setSingleLine(true);
        input.setPadding(dp(10), dp(8), dp(10), dp(8));
        input.setBackground(bg(LIGHT, 8));
        return input;
    }

    private void addFormInput(LinearLayout form, EditText input) {
        LinearLayout.LayoutParams params = lp(-1, 48);
        params.topMargin = dp(10);
        form.addView(input, params);
    }

    private void confirmDelete(int index) {
        if (index < 0 || index >= ProductManager.products.size()) {
            refreshProducts();
            return;
        }

        String productName = ProductManager.products.get(index).name;

        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa \""
                        + productName + "\" không?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (index < ProductManager.products.size()) {
                        ProductManager.products.remove(index);
                        refreshProducts();

                        Toast.makeText(this,
                                "Đã xóa sản phẩm.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}