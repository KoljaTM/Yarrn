package de.vanmar.android.yarrn;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import de.vanmar.android.yarrn.ravelry.IRavelryActivity;

@EFragment(R.layout.fragment_settings)
public class SettingsFragment extends SherlockFragment {
    public interface SettingsFragmentListener extends IRavelryActivity {
    }

    private SettingsFragmentListener listener;

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
    }

    @Click(R.id.send_feedback)
    public void menuSendFeedbackClicked() {
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
    public void menuChangeUserClicked() {
        listener.requestToken();
    }
}