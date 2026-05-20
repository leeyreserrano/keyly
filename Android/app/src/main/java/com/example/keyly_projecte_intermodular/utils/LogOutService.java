package com.example.keyly_projecte_intermodular.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.keyly_projecte_intermodular.LoginActivity;
import com.example.keyly_projecte_intermodular.R;
import com.example.keyly_projecte_intermodular.resources.Varis;

public class LogOutService {
    public static void logOut(Context context) {
        if (Varis.usuariPropi == null) {
            Toast.makeText(context,
                    context.getString(R.string.toastLogout, ""),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,
                    context.getString(R.string.toastLogout, Varis.usuariPropi.getNom()),
                    Toast.LENGTH_SHORT).show();
        }
        Varis.usuariPropi = null;
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
