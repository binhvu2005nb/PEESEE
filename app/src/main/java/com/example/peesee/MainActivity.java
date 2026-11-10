
package com.example.peesee;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity {

    private EditText edtAccount, edtPassword;
    private SharedPreferences data;

    // Tài khoản Admin duy nhất
    private static final String ADMIN_ACCOUNT = "admin@peesee.vn";
    private static final String ADMIN_PASSWORD = "Admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        data = getSharedPreferences("PEESEE", MODE_PRIVATE);

        // Tạo sẵn tài khoản Admin khi ứng dụng chạy lần đầu
        if (!data.getBoolean("admin_initialized", false)) {
            data.edit()
                    .putString(
                            "user_" + ADMIN_ACCOUNT + "_name",
                            "Admin PEESEE"
                    )
                    .putString(
                            "user_" + ADMIN_ACCOUNT + "_password",
                            ADMIN_PASSWORD
                    )
                    .putString(
                            "user_" + ADMIN_ACCOUNT + "_role",
                            "seller"
                    )
                    .putBoolean("admin_initialized", true)
                    .apply();
        }

        edtAccount = findViewById(R.id.edtAccount);
        edtPassword = findViewById(R.id.edtPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);

        // Xử lý đăng nhập
        btnLogin.setOnClickListener(v -> {
            String account = edtAccount.getText()
                    .toString()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            String password = edtPassword.getText()
                    .toString();

            if (account.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                        MainActivity.this,
                        "Vui lòng nhập đầy đủ tài khoản và mật khẩu",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Kiểm tra Admin trước
            if (account.equals(ADMIN_ACCOUNT)
                    && password.equals(ADMIN_PASSWORD)) {

                Intent intent = new Intent(
                        MainActivity.this,
                        SellerHomeActivity.class
                );

                intent.putExtra("account", ADMIN_ACCOUNT);
                intent.putExtra("name", "Admin PEESEE");

                startActivity(intent);
                finish();
                return;
            }

            // Kiểm tra tài khoản người mua
            String prefix = "user_" + account + "_";

            String savedPassword = data.getString(
                    prefix + "password",
                    null
            );

            if (savedPassword == null
                    || !savedPassword.equals(password)) {

                Toast.makeText(
                        MainActivity.this,
                        "Tài khoản hoặc mật khẩu không đúng",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Tài khoản đăng ký trong ứng dụng chỉ được là người mua
            Intent intent = new Intent(
                    MainActivity.this,
                    HomeActivity.class
            );

            intent.putExtra("account", account);
            intent.putExtra(
                    "name",
                    data.getString(prefix + "name", "")
            );

            startActivity(intent);
            finish();
        });

        // Mở trang đăng ký người mua
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }
}
