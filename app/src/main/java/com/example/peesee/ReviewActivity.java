
package com.example.peesee;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class ReviewActivity extends Activity {

    final int RED = Color.rgb(215, 0, 20);
    final int DARK = Color.rgb(35, 43, 57);
    final int GRAY = Color.rgb(125, 135, 150);
    final int BORDER = Color.rgb(232, 236, 242);
    final int GOLD = Color.rgb(255, 177, 30);

    static class Review {
        String name, date, content;
        int stars, helpful;

        Review(String name, int stars, String date,
               String content, int helpful) {
            this.name = name;
            this.stars = stars;
            this.date = date;
            this.content = content;
            this.helpful = helpful;
        }
    }

    static final ArrayList<Review> reviews = new ArrayList<>();

    String productName = "Laptop gaming Acer Aspire 7";
    String productPrice = "17.990.000đ";
    String productIcon = "💻";

    int selectedStars = 0;
    String sortMode = "Mới nhất";

    LinearLayout root, reviewList, ratingBars;
    TextView averageText, totalText, filterInfo;
    HorizontalScrollView filterScroll;

    final int[] starCounts = {0, 1, 5, 15, 102};

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
        d.setStroke(dp(1), BORDER);
        return d;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            productName = getIntent().getStringExtra("name") == null
                    ? productName
                    : getIntent().getStringExtra("name");

            productPrice = getIntent().getStringExtra("priceText") == null
                    ? productPrice
                    : getIntent().getStringExtra("priceText");

            productIcon = getIntent().getStringExtra("icon") == null
                    ? productIcon
                    : getIntent().getStringExtra("icon");
        }

        loadSampleReviews();
        buildPage();
    }

    static void loadSampleReviews() {
        if (!reviews.isEmpty()) return;

        reviews.add(new Review(
                "Nguyễn Minh Anh", 5, "25/10/2026",
                "Máy chạy tốt, bàn phím dễ sử dụng. Đóng gói cẩn thận.",
                18));

        reviews.add(new Review(
                "Trần Hoàng Nam", 5, "23/10/2026",
                "Giao hàng nhanh, sản phẩm đúng mô tả. Mình hài lòng.",
                12));

        reviews.add(new Review(
                "Lê Thu Hà", 4, "20/10/2026",
                "Máy dùng ổn, thiết kế đẹp. Quạt hơi ồn khi chạy tác vụ nặng.",
                7));

        reviews.add(new Review(
                "Phạm Đức Long", 3, "18/10/2026",
                "Hiệu năng đáp ứng nhu cầu học tập, nhưng máy khá nặng.",
                3));

        reviews.add(new Review(
                "Đỗ Quang Huy", 5, "15/10/2026",
                "Sản phẩm tốt trong tầm giá, tư vấn nhiệt tình.",
                9));
    }

    void buildPage() {
        root = column();
        root.setBackgroundColor(Color.WHITE);
        setContentView(root);

        // Header
        LinearLayout header = row();
        header.setPadding(dp(12), 0, dp(12), 0);
        header.setBackgroundColor(RED);

        TextView back = text("‹", 32, Color.WHITE, false);
        back.setGravity(Gravity.CENTER);
        header.addView(back, lp(38, 50));
        back.setOnClickListener(v -> finish());

        TextView title = text("Đánh giá sản phẩm",
                18, Color.WHITE, true);
        title.setGravity(Gravity.CENTER);
        header.addView(title, new LinearLayout.LayoutParams(
                0, dp(50), 1));

        TextView more = text("⋮", 25, Color.WHITE, false);
        more.setGravity(Gravity.CENTER);
        header.addView(more, lp(28, 50));

        root.addView(header, lp(-1, 50));

        // Thông tin sản phẩm
        LinearLayout product = row();
        product.setPadding(dp(14), dp(9), dp(14), dp(9));

        TextView icon = text(productIcon, 30, DARK, false);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(bg(0xFFF4F6F8, 8));
        product.addView(icon, lp(62, 58));

        LinearLayout productInfo = column();
        productInfo.setPadding(dp(10), 0, 0, 0);
        productInfo.addView(text(productName, 13, DARK, true));
        productInfo.addView(text(
                "Đánh giá và nhận xét của khách hàng",
                11, GRAY, false));

        productInfo.addView(text(productPrice, 13, RED, true));
        product.addView(productInfo, new LinearLayout.LayoutParams(
                0, -2, 1));

        root.addView(product);
        addDivider(root);

        // Điểm đánh giá tổng quan
        LinearLayout summary = row();
        summary.setGravity(Gravity.CENTER_VERTICAL);
        summary.setPadding(dp(14), dp(10), dp(14), dp(10));

        LinearLayout score = column();
        score.setGravity(Gravity.CENTER);
        score.addView(averageText = text("4.8/5",
                30, DARK, true));

        TextView stars = text("★★★★★", 19, GOLD, true);
        stars.setGravity(Gravity.CENTER);
        score.addView(stars);

        score.addView(totalText = text(
                "(5 đánh giá)", 10, GRAY, false));

        summary.addView(score, lp(125, 115));

        View divider = new View(this);
        divider.setBackgroundColor(BORDER);
        summary.addView(divider, lp(1, 95));

        ratingBars = column();
        ratingBars.setPadding(dp(10), 0, 0, 0);
        summary.addView(ratingBars, new LinearLayout.LayoutParams(
                0, -2, 1));

        root.addView(summary);
        addDivider(root);

        // Bộ lọc
        filterScroll = new HorizontalScrollView(this);
        filterScroll.setHorizontalScrollBarEnabled(false);

        LinearLayout filters = row();
        filters.setPadding(dp(10), dp(8), dp(10), dp(8));

        String[] names = {
                "Tất cả", "5 sao", "4 sao", "3 sao", "2 sao", "1 sao"
        };

        for (String name : names) {
            TextView chip = text(name, 10, DARK, false);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(dp(13), dp(7), dp(13), dp(7));
            chip.setBackground(bg(Color.WHITE, 7));

            LinearLayout.LayoutParams cp = lp(-2, 32);
            cp.setMargins(0, 0, dp(6), 0);
            filters.addView(chip, cp);

            chip.setOnClickListener(v -> {
                selectedStars = name.equals("Tất cả")
                        ? 0
                        : Integer.parseInt(
                        name.substring(0, 1));
                refresh();
            });
        }

        filterScroll.addView(filters);
        root.addView(filterScroll);

        // Sắp xếp
        LinearLayout sort = row();
        sort.setPadding(dp(14), dp(5), dp(14), dp(7));

        sort.addView(text("Nhận xét của khách hàng",
                        13, DARK, true),
                new LinearLayout.LayoutParams(0, -2, 1));

        Spinner spinner = new Spinner(this);
        String[] sortOptions = {"Mới nhất", "Hữu ích nhất"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, sortOptions);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        sort.addView(spinner, lp(125, 38));
        spinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view, int position, long id) {
                        sortMode = sortOptions[position];
                        refresh();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {}
                });

        root.addView(sort);

        filterInfo = text("", 11, GRAY, false);
        filterInfo.setPadding(dp(14), dp(2), dp(14), dp(6));
        root.addView(filterInfo);

        // Danh sách nhận xét
        ScrollView scroll = new ScrollView(this);
        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1));

        reviewList = column();
        reviewList.setPadding(dp(12), dp(3), dp(12), dp(12));
        scroll.addView(reviewList);

        // Nút gửi đánh giá
        Button addReview = new Button(this);
        addReview.setText("★  Viết đánh giá");
        addReview.setTextColor(Color.WHITE);
        addReview.setTextSize(14);
        addReview.setAllCaps(false);
        addReview.setBackground(bg(RED, 8));
        LinearLayout.LayoutParams bp = lp(-1, 48);
        bp.setMargins(dp(12), dp(7), dp(12), dp(10));
        root.addView(addReview, bp);

        addReview.setOnClickListener(v -> showAddReviewDialog());

        refresh();
    }

    void addDivider(LinearLayout parent) {
        View line = new View(this);
        line.setBackgroundColor(BORDER);
        parent.addView(line, lp(-1, 1));
    }

    void refresh() {
        if (reviewList == null) return;

        reviewList.removeAllViews();

        ArrayList<Review> filtered = new ArrayList<>();
        for (Review review : reviews) {
            if (selectedStars == 0
                    || review.stars == selectedStars) {
                filtered.add(review);
            }
        }

        if (sortMode.equals("Hữu ích nhất")) {
            Collections.sort(filtered,
                    (a, b) -> Integer.compare(b.helpful, a.helpful));
        } else {
            Collections.sort(filtered,
                    (a, b) -> b.date.compareTo(a.date));
        }

        int total = reviews.size();
        double average = 0;

        for (Review review : reviews) {
            average += review.stars;
        }

        if (total > 0) average /= total;

        averageText.setText(String.format(
                Locale.getDefault(), "%.1f/5", average));
        totalText.setText("(" + total + " đánh giá)");
        filterInfo.setText("Có " + filtered.size() + " nhận xét");

        renderRatingBars(total);

        if (filtered.isEmpty()) {
            TextView empty = text(
                    "Chưa có đánh giá ở mức sao này.",
                    13, GRAY, false);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, dp(35), 0, dp(35));
            reviewList.addView(empty);
            return;
        }

        for (Review review : filtered) {
            addReviewCard(review);
        }
    }

    void renderRatingBars(int total) {
        ratingBars.removeAllViews();

        for (int star = 5; star >= 1; star--) {
            int count = 0;

            for (Review review : reviews) {
                if (review.stars == star) count++;
            }

            LinearLayout line = row();

            line.addView(text(star + " sao", 10, GRAY, false),
                    lp(43, 19));

            ProgressBar bar = new ProgressBar(this, null,
                    android.R.attr.progressBarStyleHorizontal);

            bar.setMax(Math.max(total, 1));
            bar.setProgress(count);
            bar.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(RED));
            bar.setProgressBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(BORDER));

            line.addView(bar, new LinearLayout.LayoutParams(
                    0, dp(7), 1));

            TextView countText = text("(" + count + ")",
                    9, GRAY, false);
            countText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            line.addView(countText, lp(32, 19));

            ratingBars.addView(line, lp(-1, 20));
        }
    }

    void addReviewCard(Review review) {
        LinearLayout card = column();
        card.setPadding(dp(12), dp(10), dp(12), dp(10));
        card.setBackground(bg(Color.WHITE, 9));

        LinearLayout.LayoutParams cardParams = lp(-1, -2);
        cardParams.bottomMargin = dp(9);
        reviewList.addView(card, cardParams);

        LinearLayout top = row();

        TextView avatar = text(
                review.name.substring(0, 1).toUpperCase(
                        Locale.getDefault()),
                17, RED, true);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(bg(0xFFFFF0F1, 30));
        top.addView(avatar, lp(38, 38));

        LinearLayout user = column();
        user.setPadding(dp(9), 0, 0, 0);
        user.addView(text(review.name, 12, DARK, true));
        user.addView(text(review.date, 10, GRAY, false));

        top.addView(user, new LinearLayout.LayoutParams(0, -2, 1));

        card.addView(top);

        TextView stars = text(
                "★".repeat(review.stars)
                        + "☆".repeat(5 - review.stars),
                17, GOLD, true);

        LinearLayout.LayoutParams sp = lp(-1, 27);
        sp.topMargin = dp(5);
        card.addView(stars, sp);

        TextView content = text(review.content, 12, DARK, false);
        content.setLineSpacing(dp(3), 1);
        LinearLayout.LayoutParams contentParams = lp(-1, -2);
        contentParams.topMargin = dp(3);
        card.addView(content, contentParams);

        LinearLayout bottom = row();
        bottom.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        TextView helpful = text(
                "👍 Hữu ích (" + review.helpful + ")",
                11, GRAY, false);
        helpful.setPadding(dp(8), dp(6), dp(8), dp(6));
        bottom.addView(helpful);

        helpful.setOnClickListener(v -> {
            review.helpful++;
            refresh();
            Toast.makeText(this, "Cảm ơn bạn!",
                    Toast.LENGTH_SHORT).show();
        });

        LinearLayout.LayoutParams bottomParams = lp(-1, 32);
        bottomParams.topMargin = dp(4);
        card.addView(bottom, bottomParams);
    }

    void showAddReviewDialog() {
        final int[] stars = {5};

        LinearLayout form = column();
        form.setPadding(dp(8), dp(5), dp(8), dp(5));

        form.addView(text("Bạn đánh giá sản phẩm thế nào?",
                13, DARK, true));

        TextView starPicker = text("★★★★★  5/5",
                24, GOLD, true);
        starPicker.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams starParams = lp(-1, 48);
        form.addView(starPicker, starParams);

        starPicker.setOnClickListener(v -> {
            String[] options = {
                    "1 sao", "2 sao", "3 sao", "4 sao", "5 sao"
            };

            new AlertDialog.Builder(this)
                    .setTitle("Chọn số sao")
                    .setItems(options, (dialog, which) -> {
                        stars[0] = which + 1;
                        starPicker.setText(
                                "★".repeat(stars[0])
                                        + "☆".repeat(5 - stars[0])
                                        + "  " + stars[0] + "/5");
                    })
                    .show();
        });

        EditText nameInput = new EditText(this);
        nameInput.setSingleLine(true);
        nameInput.setHint("Tên người đánh giá");
        form.addView(nameInput, lp(-1, 48));

        EditText commentInput = new EditText(this);
        commentInput.setHint("Nhập nhận xét của bạn...");
        commentInput.setGravity(Gravity.TOP);
        commentInput.setMinLines(3);
        commentInput.setMaxLines(5);
        commentInput.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        form.addView(commentInput, lp(-1, 105));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Viết đánh giá")
                .setView(form)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Gửi đánh giá", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {
                        String name = nameInput.getText().toString().trim();
                        String comment = commentInput.getText()
                                .toString().trim();

                        if (name.isEmpty()) {
                            nameInput.setError("Vui lòng nhập tên");
                            return;
                        }

                        if (comment.isEmpty()) {
                            commentInput.setError(
                                    "Vui lòng nhập nội dung đánh giá");
                            return;
                        }

                        String date = new SimpleDateFormat(
                                "dd/MM/yyyy", Locale.getDefault())
                                .format(new Date());

                        reviews.add(0, new Review(
                                name, stars[0], date, comment, 0));

                        selectedStars = 0;
                        refresh();
                        dialog.dismiss();

                        Toast.makeText(this, "Đã thêm đánh giá",
                                Toast.LENGTH_SHORT).show();
                    });
        });

        dialog.show();
    }
}
