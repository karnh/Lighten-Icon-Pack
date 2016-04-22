package com.webh.lightentheme;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemClickListener, MissingDialogFragment.NoticeDialogListener {

    // APEX Launcher intent attributes
    private static final String ACTION_SET_APEX_THEME = "com.anddoes.launcher.SET_THEME";
    private static final String EXTRA_PACKAGE_NAME = "com.anddoes.launcher.THEME_PACKAGE_NAME";

    // Nova launcher intent attributes
    private static final String ACTION_APPLY_ICON_THEME = "com.teslacoilsw.launcher.APPLY_ICON_THEME";
    private static final String NOVA_PACKAGE = "com.teslacoilsw.launcher";
    private static final String EXTRA_ICON_THEME_PACKAGE = "com.teslacoilsw.launcher.extra.ICON_THEME_PACKAGE";

    // ADW launcher intent attributes
    private static final String ACTION_SET_ADW_THEME = "org.adw.launcher.SET_THEME";
    private static final String EXTRA_NAME = "org.adw.launcher.theme.NAME";

    // Array to hold launcher details
    private LauncherDetails[] appLaunchers = new LauncherDetails[4];

    // List view adapter
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Details of supported Launchers
        appLaunchers[0] = new LauncherDetails("adw", "ADW Launcher", "org.adw.launcher", "adw");
        appLaunchers[1] = new LauncherDetails("adwfreak", "ADW Launcher Ex", "org.adwfreak.launcher", "adwfreak");
        appLaunchers[2] = new LauncherDetails("apex", "Apex Launcher", "com.anddoes.launcher", "apex");
        appLaunchers[3] = new LauncherDetails("nova", "Nova Launcher", "com.teslacoilsw.launcher", "nova");

        // Create list view with launcher details
        ListView listView = (ListView) findViewById(R.id.listview);

        String[] statusStrings = getInstallStatus(appLaunchers);
        adapter = new ListViewAdapter(this, appLaunchers, statusStrings);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // Update list view
        String[] statusStrings = getInstallStatus(appLaunchers);
        adapter.refreshEvents(statusStrings);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = view.getContext();
        TextView textViewItem = ((TextView) view.findViewById(R.id.firstLine));
        TextView textViewAttr = ((TextView) view.findViewById(R.id.secondLine));

        // get the clicked item texts
        String listItemText = textViewItem.getText().toString();
        String listItemAttr = textViewAttr.getText().toString();

        for (LauncherDetails appLauncher : appLaunchers) {

            if (listItemText.equals(appLauncher.name)) {
                // If not installed
                if (listItemAttr.toLowerCase().contains("not")) {
                    FragmentManager fm = getSupportFragmentManager();

                    // Show dialog to allow installing launcher
                    Bundle args = new Bundle();
                    args.putString("launcher", appLauncher.name);
                    args.putString("link", appLauncher.package_name);
                    MissingDialogFragment dialog = new MissingDialogFragment();
                    dialog.setArguments(args);
                    dialog.show(fm, "fragment_install_dialog");
                } else {
                    // Apply theme in launcher
                    Intent intent = null;

                    switch (appLauncher.id) {
                        case "adw":
                        case "adwfreak":
                            // ADW intent
                            intent = new Intent(ACTION_SET_ADW_THEME);
                            intent.putExtra(EXTRA_NAME, context.getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            break;
                        case "apex":
                            // Apex intent
                            intent = new Intent(ACTION_SET_APEX_THEME);
                            intent.putExtra(EXTRA_PACKAGE_NAME, getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            break;
                        case "nova":
                            // Nova intent
                            intent = new Intent(ACTION_APPLY_ICON_THEME);
                            intent.setPackage(NOVA_PACKAGE);
                            intent.putExtra(EXTRA_ICON_THEME_PACKAGE, getPackageName());
                            break;
                    }

                    // Open launcher to apply theme
                    try {
                        if (intent != null) {
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Unknown Launcher" + appLauncher.name, Toast.LENGTH_SHORT).show();
                        }
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, appLauncher.name + " is not installed!", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }
        }
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(String link) {
        // Positive event. Open google play store for corresponding launcher
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String marketLink = "market://details?id=".concat(link);
        intent.setData(Uri.parse(marketLink));
        startActivity(intent);
    }

    @Override
    public void onDialogNegativeClick(String link) {
        // User touched the dialog's negative button
    }

    /**
     * Get install status of each supported launcher
     *
     * @param supportedLaunchers List of supported launchers
     * @return Array of status for each launcher
     */
    public String[] getInstallStatus(LauncherDetails[] supportedLaunchers) {

        String installedLaunchers = "";
        String[] statusStrings = new String[supportedLaunchers.length];

        // Get list o all installed launchers
        PackageManager pm = getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);

        List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
        for (ResolveInfo resolveInfo : lst) {
            installedLaunchers = installedLaunchers.concat(resolveInfo.activityInfo.packageName);
        }

        // Get current default launcher
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);

        String defaultLauncher = resolveInfo.activityInfo.packageName;

        // Check install/default status for each launcher
        for (int index = 0; index < supportedLaunchers.length; index++) {
            String launcherPackage = supportedLaunchers[index].package_name;

            if (installedLaunchers.toLowerCase().contains(launcherPackage)) {
                statusStrings[index] = "Installed";
                if (defaultLauncher.toLowerCase().contains(launcherPackage)) {
                    statusStrings[index] = "Installed, Default";
                }
            } else {
                statusStrings[index] = "Not Installed";
            }
        }
        return statusStrings;
    }
}
