import com.google.gson.Gson;

import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {

        Transcripts transcripts = new Transcripts();
        transcripts.setAudio_url("https://raw.githubusercontent.com/SylerEdd/REST-API/refs/heads/main/Thirsty.mp4");
        Gson gson = new Gson();

        String jsonRequest = gson.toJson(transcripts);

        System.out.println(jsonRequest);

        HttpRequest postRequest = (HttpRequest) HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", Constants.API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpClient client = HttpClient.newHttpClient();


        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(postResponse.body());

        transcripts = gson.fromJson(postResponse.body(), Transcripts.class);

        System.out.println(transcripts. getId());

        HttpRequest getRequest = (HttpRequest) HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcripts.getId()))
                .header("Authorization", Constants.API_KEY)
                .build();

        while(true){
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcripts = gson.fromJson(getResponse.body(), Transcripts.class);

            System.out.println(transcripts.getStatus());

            if("completed".equals(transcripts.getStatus()) || "error".equals(transcripts.getStatus())){
                break;
            }

            TimeUnit.SECONDS.sleep(1);
        }

        System.out.println("Transcription Completed!");
        System.out.println(transcripts.getText());

    }
}