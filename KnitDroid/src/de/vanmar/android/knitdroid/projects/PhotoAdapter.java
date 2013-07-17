package de.vanmar.android.knitdroid.projects;

import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidquery.AQuery;

import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.util.JSONAdapter;
import de.vanmar.android.knitdroid.util.JSONHelper;

public class PhotoAdapter extends JSONAdapter {

	private class ViewHolder {
		private ImageView photo;
	}

	private final Activity context;

	public PhotoAdapter(final Activity context) {
		this.context = context;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final View view;
		final ViewHolder holder;
		if (convertView != null) {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		} else {
			view = context.getLayoutInflater().inflate(
					R.layout.photogallery_item, parent, false);
			holder = new ViewHolder();
			holder.photo = (ImageView) view.findViewById(R.id.photo);
			view.setTag(holder);
		}
		final JSONObject photoJson = getObject(position);
		final String imageUrl = JSONHelper.optString(photoJson, "square_url");
		new AQuery(view).id(holder.photo).image(imageUrl);

		return view;
	}
}