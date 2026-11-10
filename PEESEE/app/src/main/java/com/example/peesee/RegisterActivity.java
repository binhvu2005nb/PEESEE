
package com.example.peesee;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import java.util.Locale;

public class RegisterActivity extends Activity {

    EditText edtName, edtAccount, edtPassword, edtConfirm;
    Spinner spRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtAccount = findViewById(R.id.edtAccount);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);


        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String account = edtAccount.getText()
                    .toString().trim().toLowerCase(Locale.ROOT);
            String password = edtPassword.getText().toString();
            String confirm = edtConfirm.getText().toString();

            String role = "buyer";

            if (name.isEmpty() || account.isEmpty()
                    || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this,
                        "Vui lòng nhập đủ thông tin",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this,
                        "Mật khẩu cần ít nhất 6 ký tự",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this,
                        "Mật khẩu không khớp",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences data = getSharedPreferences(
                    "PEESEE", MODE_PRIVATE);

            String prefix = "user_" + account + "_";

            if (data.contains(prefix + "password")) {
                Toast.makeText(this,
                        "Tài khoản này đã tồn tại",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            data.edit()
                    .putString(prefix + "name", name)
                    .putString(prefix + "password", password)
                    .putString(prefix + "role", role)
                    .apply();

            Toast.makeText(this,
                    "Đăng ký thành công! Hãy đăng nhập.",
                    Toast.LENGTH_LONG).show();

            finish();
        });
    }
}
