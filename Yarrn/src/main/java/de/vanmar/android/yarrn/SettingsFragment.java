package de.vanmar.android.yarrn;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.yarrn.components.WebViewDialog;
import de.vanmar.android.yarrn.ravelry.IRavelryActivity;

@EFragment(R.layout.fragment_settings)
public class SettingsFragment extends Fragment {
    public interface SettingsFragmentListener extends IRavelryActivity {
    }

    private SettingsFragmentListener listener;
    @ViewById(R.id.send_error_reports)
    CheckBox sendErrorReports;
    @Pref
    YarrnPrefs_ prefs;


    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof SettingsFragmentListener) {
            listener = (SettingsFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement SettingsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.settings_title);

        sendErrorReports.setChecked(prefs.sendErrorReports().get());
    }

    @AfterViews
    public void afterViews() {
        sendErrorReports.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.sendErrorReports().put(isChecked);
            }
        });
    }

    @Click(R.id.send_feedback)
    public void onSendFeedbackClicked() {
        final Intent emailIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        final String emailList[] = {getString(R.string.feedback_mail)};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailList);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_mail_subject));
        emailIntent.setType("plain/text");
        try {
            startActivity(emailIntent);
        } catch (final ActivityNotFoundException e) {
            Toast.makeText(getActivity(),
                    R.string.cannot_send_mail,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Click(R.id.change_user)
    public void onChangeUserClicked() {
        listener.requestToken();
    }

    @Click(R.id.about)
    public void onAboutClicked() {
        new WebViewDialog(getActivity(), "file:///android_asset/about.html").show();
    }
}