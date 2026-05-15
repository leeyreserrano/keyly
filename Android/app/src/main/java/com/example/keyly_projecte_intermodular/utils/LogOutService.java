package com.example.keyly_projecte_intermodular.utils;

import android.content.Context;
import android.content.Intent;

import com.example.keyly_projecte_intermodular.LoginActivity;
import com.example.keyly_projecte_intermodular.resources.Varis;

public class LogOutService {
    public static void logOut(Context context) {
        Varis.usuariPropi = null;
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
