/**
 * *******************************************
 * CONFIDENTIAL AND PROPRIETARY
 * <p/>
 * The source code and other information contained herein is the confidential and the exclusive property of
 * ZIH Corp. and is subject to the terms and conditions in your end user license agreement.
 * This source code, and any other information contained herein, shall not be copied, reproduced, published,
 * displayed or distributed, in whole or in part, in any medium, by any means, for any purpose except as
 * expressly permitted under such license agreement.
 * <p/>
 * Copyright ZIH Corp. 2010
 * <p/>
 * ALL RIGHTS RESERVED
 * *********************************************
 */
package com.pra.sendfilepdfdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.w3c.dom.Text;

import java.util.List;

/**
 * @author mlim3 This class handles the display of dialogs.
 */
public class UIHelper {
    ProgressDialog loadingDialog;
    Activity activity;

    public UIHelper(Activity activity) {
        this.activity = activity;
    }

    public void showLoadingDialog(final String message) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    loadingDialog = new ProgressDialog(activity, R.style.ButtonAppearance);
                    loadingDialog.setMessage(message);
                    loadingDialog.show();
                    TextView tv1 = (TextView) loadingDialog.findViewById(android.R.id.message);
                    tv1.setTextAppearance(activity, R.style.ButtonAppearance);

                }
            });
        }
    }

    public void updateLoadingDialog(final String message) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    loadingDialog.setMessage(message);
                }
            });
        }
    }

    public boolean isDialogActive() {
        if (loadingDialog != null) {
            return loadingDialog.isShowing();
        } else {
            return false;
        }
    }

    public void dismissLoadingDialog() {
        if (activity != null && loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }


    public static void requestingMultiplePermission(boolean isEnableBluetooth,final Activity activity, List<String> multiPermission, final GrantedAllPermission permissionAllGranted) {
        Dexter.withContext(activity)
                .withPermissions(multiPermission)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted() && permissionAllGranted != null) {
                            permissionAllGranted.onGrantedAllPermission(isEnableBluetooth);
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog(activity);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                    }
                })
                .onSameThread()
                .check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private static void showSettingsDialog(final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.label_title_need_permission));
        builder.setMessage(activity.getString(R.string.label_msg_permission_grant_by_app_setting));
        builder.setPositiveButton(activity.getString(R.string.label_goto_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openAppSettings(activity);
            }
        });
        builder.setNegativeButton(activity.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }


    // navigating user to app settings
    private static void openAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }


    public void showErrorDialog(String errorMessage) {
        new AlertDialog.Builder(activity).setMessage(errorMessage).setTitle("Error").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public void showErrorDialogOnGuiThread(final String errorMessage) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.ErrorButtonAppearance);
                    builder.setMessage(errorMessage).setTitle("Error").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            dismissLoadingDialog();
                        }

                    }).create();
                    Dialog d = builder.show();
                    TextView tv1 = (TextView) d.findViewById(android.R.id.message);
                    tv1.setTextAppearance(activity, R.style.ErrorButtonAppearance);
                }
            });
        }
    }

}
