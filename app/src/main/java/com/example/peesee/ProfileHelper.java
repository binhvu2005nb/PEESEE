
package com.example.peesee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

public class ProfileHelper {

    public static void showProfile(Activity activity) {

        // Lấy tên người dùng
        String name = activity.getIntent()
                .getStringExtra("name");

        if (name == null || name.trim().isEmpty()) {
            name = activity.getSharedPreferences(
                    "PEESEE",
                    Activity.MODE_PRIVATE
            ).getString("name", "Khách hàng");
        }

        // Hiển thị thông tin cá nhân
        new AlertDialog.Builder(activity)
                .setTitle("Tài khoản cá nhân")
                .setMessage("Xin chào, " + name
                        + "\n\nBạn có muốn đăng xuất không?")
                .setPositiveButton("Đóng", null)
                .setNegativeButton("Đăng xuất", (dialog, which) -> {

                    Intent intent = new Intent(
                            activity,
                            MainActivity.class
                    );

                    // Xóa các màn hình trước đó
                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    activity.startActivity(intent);
                    activity.finish();
                })
                .show();
    }
}
