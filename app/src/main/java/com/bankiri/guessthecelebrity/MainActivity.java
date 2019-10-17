package com.bankiri.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    String correctAnswer;
    String htmlText;
    List<String> imagesArray = new ArrayList<>();
    List<String> peopleArray = new ArrayList<>();
    int numberOfCelebrity;

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(in);

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void celebrityQuest() {
        GridLayout gridLayout = findViewById(R.id.gridAnswer);
        List<String> answers = new ArrayList<>();

        Random rand = new Random();
        int randNumber = rand.nextInt(numberOfCelebrity);
        correctAnswer = peopleArray.get(randNumber);
        Log.i("Correct answer", correctAnswer);
        answers.add(correctAnswer);


        while(answers.size() < 4){
            randNumber = rand.nextInt(numberOfCelebrity);
            String person = peopleArray.get(randNumber);
            if(!answers.contains(person)){
                answers.add(person);
            }
        }

        ImageDownloader task = new ImageDownloader();
        Bitmap celebrity;

        try {
            celebrity = task.execute(imagesArray.get(randNumber)).get();
            imageView.setImageBitmap(celebrity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Log.i("Correct answer array", answers.toString());
        Collections.shuffle(answers);
        Log.i("Correct answer array", answers.toString());

        int count = gridLayout.getChildCount();
        for (int i = 0; i < count; i++){
            Button child = (Button) gridLayout.getChildAt(i);
            child.setText(answers.get(i));
        }
    }

    public void checkCelebrity(View view) {
        Button button = (Button) view;

        Log.i("Correct... function", correctAnswer);
        Log.i("Correct... function", (String) button.getText());

        if(correctAnswer.equals(button.getText())) {
            Log.i("Correct function else", correctAnswer);
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("Correct function else", correctAnswer);
            Toast.makeText(this, String.format("Wrong, this is %s.", correctAnswer), Toast.LENGTH_SHORT).show();
        }
        celebrityQuest();
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1) {
                    char current = (char) data;
                    result += current;

                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        DownloadTask task = new DownloadTask();


        try {
            htmlText = task.execute("http://www.posh24.se/kandisar").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Pattern imagesPattern = Pattern.compile("img src=\"(.*?)\"");
        Pattern peoplePattern = Pattern.compile("alt=\"(.*?)\"");

        Matcher images = imagesPattern.matcher(htmlText);
        Matcher people = peoplePattern.matcher(htmlText);

        while(images.find()){
            //Log.i("urlImages: ", images.group());
            imagesArray.add(images.group(1));
        }
        //Log.i("imagesArray", imagesArray.toString());
        //Log.i("imagesArray", String.valueOf(imagesArray.size()));

        while(people.find()){
            //Log.i("urlPeople: ", people.group());
            peopleArray.add(people.group(1));
        }
        //Log.i("peopleArray", peopleArray.toString());
        //Log.i("peopleArray", String.valueOf(peopleArray.size()));

        numberOfCelebrity = peopleArray.size();

        celebrityQuest();
    }
}
