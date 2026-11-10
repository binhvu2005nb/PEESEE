
package com.example.peesee;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class SellerReviewActivity extends Activity {

    private final int RED = Color.rgb(215, 25, 32);
    private final int DARK = Color.rgb(35, 43, 57);
    private final int GRAY = Color.rgb(125, 135, 150);
    private final int GOLD = Color.rgb(255, 177, 30);
    private final int LIGHT = Color.rgb(246, 247, 250);

    private LinearLayout root;
    private LinearLayout reviewList;
    private LinearLayout summary;
    private TextView averageText;
    private TextView totalText;

    private int selectedStars = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Đảm bảo có dữ liệu mẫu để hiển thị
        ReviewActivity.loadSampleReviews();

        buildPage();
    }

    private void buildPage() {

        root = column();
        root.setBackgroundColor(LIGHT);
        setContentView(root);

        // HEADER
        LinearLayout header = row();
        header.setPadding(dp(14), 0, dp(14), 0);
        header.setBackgroundColor(RED);

        TextView back = text("‹", 32, Color.WHITE, false);
        back.setGravity(Gravity.CENTER);
        header.addView(back, lp(40, 52));

        back.setOnClickListener(v -> finish());

        TextView title = text(
                "Đánh giá khách hàng",
                18, Color.WHITE, true
        );

        title.setGravity(Gravity.CENTER);

        header.addView(title,
                new LinearLayout.LayoutParams(0, dp(52), 1));

        root.addView(header);

        // SCROLL VIEW
        ScrollView scroll = new ScrollView(this);

        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        LinearLayout content = column();
        content.setPadding(dp(15), dp(16), dp(15), dp(20));
        scroll.addView(content);

        // TIÊU ĐỀ
        content.addView(text(
                "Tổng quan đánh giá",
                19, DARK, true
        ));

        // THỐNG KÊ
        summary = column();
        summary.setPadding(dp(15), dp(15), dp(15), dp(15));
        summary.setBackground(bg(Color.WHITE, 12));

        LinearLayout.LayoutParams summaryParams = lp(-1, -2);
        summaryParams.topMargin = dp(12);

        content.addView(summary, summaryParams);

        averageText = text("0.0", 32, RED, true);
        averageText.setGravity(Gravity.CENTER);
        summary.addView(averageText);

        TextView stars = text(
                "★★★★★",
                22, GOLD, true
        );
        stars.setGravity(Gravity.CENTER);
        summary.addView(stars);

        totalText = text(
                "0 đánh giá",
                12, GRAY, false
        );
        totalText.setGravity(Gravity.CENTER);
        summary.addView(totalText);

        // THỐNG KÊ THEO SỐ SAO
        LinearLayout ratingBars = column();

        LinearLayout.LayoutParams barParams = lp(-1, -2);
        barParams.topMargin = dp(12);

        summary.addView(ratingBars, barParams);

        // BỘ LỌC
        TextView filterTitle = text(
                "Đánh giá gần đây",
                18, DARK, true
        );

        LinearLayout.LayoutParams filterTitleParams = lp(-1, -2);
        filterTitleParams.topMargin = dp(22);

        content.addView(filterTitle, filterTitleParams);

        HorizontalScrollView filterScroll =
                new HorizontalScrollView(this);

        filterScroll.setHorizontalScrollBarEnabled(false);

        LinearLayout.LayoutParams filterParams = lp(-1, 42);
        filterParams.topMargin = dp(10);

        content.addView(filterScroll, filterParams);

        LinearLayout filters = row();
        filterScroll.addView(filters);

        String[] filterNames = {
                "Tất cả", "5 sao", "4 sao",
                "3 sao", "2 sao", "1 sao"
        };

        for (int i = 0; i < filterNames.length; i++) {

            final int starsFilter = i == 0 ? 0 : 6 - i;

            TextView filter = text(
                    filterNames[i],
                    12, RED, true
            );

            filter.setGravity(Gravity.CENTER);
            filter.setPadding(dp(14), 0, dp(14), 0);
            filter.setBackground(bg(Color.WHITE, 20));

            LinearLayout.LayoutParams params = lp(-2, 34);
            params.setMargins(0, 0, dp(7), 0);

            filters.addView(filter, params);

            filter.setOnClickListener(v -> {
                selectedStars = starsFilter;
                refreshReviews();
            });
        }

        // DANH SÁCH ĐÁNH GIÁ
        reviewList = column();

        LinearLayout.LayoutParams listParams = lp(-1, -2);
        listParams.topMargin = dp(12);

        content.addView(reviewList, listParams);

        // Hiển thị dữ liệu
        refreshReviews();
    }

    private void refreshReviews() {

        reviewList.removeAllViews();
        summary.removeViewAt(3);

        ArrayList<ReviewActivity.Review> reviews =
                ReviewActivity.reviews;

        int total = reviews.size();
        int sum = 0;

        for (ReviewActivity.Review review : reviews) {
            sum += review.stars;
        }

        double average = total == 0 ? 0 : (double) sum / total;

        averageText.setText(String.format(
                Locale.getDefault(), "%.1f", average
        ));

        totalText.setText(total + " đánh giá");

        // Tạo lại thống kê sao
        LinearLayout ratingBars = column();
        LinearLayout.LayoutParams barParams = lp(-1, -2);
        barParams.topMargin = dp(12);

        summary.addView(ratingBars, barParams);

        for (int star = 5; star >= 1; star--) {

            int count = 0;

            for (ReviewActivity.Review review : reviews) {
                if (review.stars == star) count++;
            }

            LinearLayout line = row();

            TextView label = text(
                    star + " sao",
                    11, GRAY, false
            );

            line.addView(label, lp(45, 23));

            TextView countText = text(
                    count + "",
                    11, DARK, false
            );

            line.addView(countText, lp(25, 23));

            ratingBars.addView(line);
        }

        // Lọc đánh giá
        boolean hasReview = false;

        for (ReviewActivity.Review review : reviews) {

            if (selectedStars == 0
                    || review.stars == selectedStars) {

                hasReview = true;
                addReviewCard(review);
            }
        }

        if (!hasReview) {
            TextView empty = text(
                    "Chưa có đánh giá ở mức sao này.",
                    13, GRAY, false
            );

            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, dp(25), 0, dp(25));

            reviewList.addView(empty);
        }
    }

    private void addReviewCard(ReviewActivity.Review review) {

        LinearLayout card = column();

        card.setPadding(dp(13), dp(12), dp(13), dp(12));
        card.setBackground(bg(Color.WHITE, 10));

        LinearLayout.LayoutParams cardParams = lp(-1, -2);
        cardParams.bottomMargin = dp(10);

        reviewList.addView(card, cardParams);

        // TÊN NGƯỜI ĐÁNH GIÁ
        TextView name = text(
                "👤  " + review.name,
                14, DARK, true
        );

        card.addView(name);

        // NGÀY ĐÁNH GIÁ
        TextView date = text(
                review.date,
                11, GRAY, false
        );

        LinearLayout.LayoutParams dateParams = lp(-1, -2);
        dateParams.topMargin = dp(4);

        card.addView(date, dateParams);

        // SỐ SAO
        TextView stars = text(
                "★".repeat(review.stars)
                        + "☆".repeat(5 - review.stars),
                18, GOLD, true
        );

        LinearLayout.LayoutParams starParams = lp(-1, -2);
        starParams.topMargin = dp(7);

        card.addView(stars, starParams);

        // NỘI DUNG
        TextView content = text(
                review.content,
                13, DARK, false
        );

        content.setLineSpacing(dp(3), 1);

        LinearLayout.LayoutParams contentParams = lp(-1, -2);
        contentParams.topMargin = dp(6);

        card.addView(content, contentParams);

        // LƯỢT HỮU ÍCH
        TextView helpful = text(
                "👍 Hữu ích: " + review.helpful,
                11, GRAY, false
        );

        LinearLayout.LayoutParams helpfulParams = lp(-1, -2);
        helpfulParams.topMargin = dp(8);

        card.addView(helpful, helpfulParams);
    }
}
