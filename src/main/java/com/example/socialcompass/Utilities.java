package com.example.socialcompass;
import android.app.Activity;
import android.app.AlertDialog;

public class Utilities {
    public static boolean alertShown;
    public Utilities() {
        this.alertShown = false;
    }
    public static void showAlert (Activity activity, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Ok", (dialog,id) -> {
                    dialog.cancel();
                })
                .setCancelable(true);
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
        alertShown = true;
    }
}