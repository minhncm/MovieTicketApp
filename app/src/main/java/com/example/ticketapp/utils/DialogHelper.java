package com.example.ticketapp.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.ticketapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogHelper {

    private static ProgressDialog progressDialog;

    /**
     * Hiển thị loading dialog
     */
    public static void showLoadingDialog(Context context) {
        showLoadingDialog(context, context.getString(R.string.msg_loading));
    }

    /**
     * Hiển thị loading dialog với message tùy chỉnh
     */
    public static void showLoadingDialog(Context context, String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * Ẩn loading dialog
     */
    public static void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * Hiển thị success dialog
     */
    public static void showSuccessDialog(Context context, String message) {
        showSuccessDialog(context, context.getString(R.string.txt_success), message, null);
    }

    /**
     * Hiển thị success dialog với callback
     */
    public static void showSuccessDialog(Context context, String title, String message, Runnable onDismiss) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.txt_ok, (dialog, which) -> {
                    dialog.dismiss();
                    if (onDismiss != null) {
                        onDismiss.run();
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Hiển thị error dialog
     */
    public static void showErrorDialog(Context context, String message) {
        showErrorDialog(context, context.getString(R.string.txt_error), message, null);
    }

    /**
     * Hiển thị error dialog với callback
     */
    public static void showErrorDialog(Context context, String title, String message, Runnable onDismiss) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.txt_ok, (dialog, which) -> {
                    dialog.dismiss();
                    if (onDismiss != null) {
                        onDismiss.run();
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Hiển thị confirmation dialog
     */
    public static void showConfirmDialog(Context context, String title, String message,
                                         Runnable onConfirm, Runnable onCancel) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.txt_confirm, (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton(R.string.txt_cancel, (dialog, which) -> {
                    if (onCancel != null) {
                        onCancel.run();
                    }
                })
                .show();
    }

    /**
     * Hiển thị confirmation dialog với custom button text
     */
    public static void showConfirmDialog(Context context, String title, String message,
                                         String positiveText, String negativeText,
                                         Runnable onConfirm) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Hiển thị info dialog
     */
    public static void showInfoDialog(Context context, String title, String message) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.txt_ok, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
