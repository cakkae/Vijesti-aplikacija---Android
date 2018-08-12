package ba.lilium.vijestiaplikacija;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class FeedActivity extends AppCompatActivity implements OnFeedListener {

    static final String URL = "https://globalpolitics.co/api/get_posts/";
    ListView listView;
    FeedAdapter adapter;
    ArrayList<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        listView = (ListView) findViewById(R.id.listView);

        // Data -> Adapter -> ListView
        adapter = new FeedAdapter(getApplicationContext(), R.layout.layout_feed_item);


        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Post post = posts.get(position);
               Intent intent = new Intent(getApplicationContext(), PostActivity.class);
               intent.putExtra("content", post.content);
               startActivity(intent);
            }
        });

        FeedTask task = new FeedTask(this);
        task.execute(URL);
    }

    @Override
    public void onFeed(JSONArray array) {

        posts = Post.parse(array);
        adapter.addAll(posts);
        adapter.notifyDataSetChanged();
    }



    public class FeedTask extends AsyncTask<String , Void, JSONArray> {


        private OnFeedListener listener ;

        public FeedTask(OnFeedListener listener) {
            this.listener = listener;
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            String url = params[0];

            com.squareup.okhttp.OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
            com.squareup.okhttp.Request.Builder builder = new com.squareup.okhttp.Request.Builder();

           com.squareup.okhttp.Request request = builder.url(url).build();

            try {

                Response response = client.newCall(request).execute();
                String json = response.body().string();
                System.out.println(json);

               try {

                JSONObject object = new JSONObject(json);
                JSONArray array = object.optJSONArray("posts");


                return array;


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
             catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            super.onPostExecute(array);

            if (null == array)
                return;

            if (null != listener)
                listener.onFeed(array);
        }
    }

    public class FeedAdapter extends ArrayAdapter<Post>
    {
        private int resource;

        public FeedAdapter(Context context, int resource) {
            super(context, resource);
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView (int position, View convertView, ViewGroup parent){

            // Convert View -> Reuse
            if (null == convertView) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(resource, null);
            }

            // View Holder
            ViewHolder vh;
            if(null != convertView.getTag())
            {
                vh = (ViewHolder) convertView.getTag();
            }
            else
            {
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            }

            // Binding data
            Post post = getItem(position);

            vh.title.setText(post.title);
            vh.desc.setText(post.description);

            String url = post.thumbnail;

            Glide.with(getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(vh.thumbnail);

            return convertView;
        }

        private class ViewHolder
        {
            public TextView title;
            public TextView desc;
            public ImageView thumbnail;

            public ViewHolder(View view)
            {
                 title = (TextView) view.findViewById(R.id.title);
                 desc = (TextView) view.findViewById(R.id.description);
                 thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            }
        }
    }



}

