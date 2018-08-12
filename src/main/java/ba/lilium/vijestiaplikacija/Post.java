package ba.lilium.vijestiaplikacija;

import android.text.Html;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Adnan on 11/21/2017.
 */

public class Post
{
    public String title;
    public String description;
    public String thumbnail; // URL slike
    public String content;

   public static Post parse(JSONObject object)
   {
       Post post = new Post();
       post.title = object.optString("title");
       post.description = Html.fromHtml(object.optString("description")).toString();
       post.thumbnail = object.optString("thumbnail");
       post.content = object.optString("content");

       return post;
   }

   public static ArrayList<Post> parse(JSONArray array)
   {
       ArrayList<Post> posts = new ArrayList<>();

       int length = array.length();
       for(int i=0; i < length; ++i)
       {
           org.json.JSONObject object = array.optJSONObject(i);
           Post post = Post.parse(object);
           posts.add(post);
       }

       return posts;
   }
}