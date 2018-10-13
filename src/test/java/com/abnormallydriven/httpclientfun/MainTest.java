package com.abnormallydriven.httpclientfun;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class MainTest {

  @Test
  public void syncGETRequest() throws IOException, InterruptedException {
    var httpClient = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.myapp.com/someEndPoint"))
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

    System.out.println(response.body());

  }

  @Test
  public void syncPUTRequest() throws IOException, InterruptedException {
    var httpClient = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.myapp.com/someEndPoint"))
        .PUT(BodyPublishers.ofString("the string version of my request body"))
        .build();

    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

    System.out.println(response.body());
  }

  @Test
  public void syncPOSTRequest() throws IOException, InterruptedException {
    var httpClient = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.myapp.com/someEndPoint"))
        .POST(BodyPublishers.ofString("the string version of my request body"))
        .build();

    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

    System.out.println(response.body());
  }

  @Test
  public void simpleHeader() throws IOException, InterruptedException {
    var httpClient = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.myapp.com/someEndPoint"))
        .header("Authorization", "Bearer my-secret-key")
        .POST(BodyPublishers.ofString("the string version of my request body"))
        .build();

    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

    System.out.println(response.body());
  }

  @Test
  public void moreHeaders(){
    var httpClient = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.myapp.com/someEndPoint"))
        .headers("Authorization", "Bearer only-my-secret-key-please", "Content-Type", "application/json", "Accept", "application/json")
        .POST(BodyPublishers.ofString("the string version of my request body"))
        .build();

    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

    System.out.println(response.body());
  }

  @Test
  public void overwriteHeaders(){
    var httpClient = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.myapp.com/someEndPoint"))
        .setHeader("Authorization", "Bearer only-my-secret-key-please")
        .POST(BodyPublishers.ofString("the string version of my request body"))
        .build();

    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

    System.out.println(response.body());
  }

  @Test
  public void sendAsyncExample() throws IOException, InterruptedException, ExecutionException {
    var httpClient = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.myapp.com/someEndPoint"))
        .setHeader("Authorization", "Bearer only-my-secret-key-please")
        .POST(BodyPublishers.ofString("the string version of my request body"))
        .build();

    var completableFuture = httpClient.sendAsync(request, BodyHandlers.ofString());
    completableFuture.thenAccept(response -> System.out.println(response.body()));
  }

  @Test
  public void combinedRequests(){
    var httpClient = HttpClient.newHttpClient();

    HttpRequest firstRequest = HttpRequest.newBuilder(URI.create("https://api.myapp.com/someEndPoint"))
        .GET()
        .build();

    HttpRequest secondRequest = HttpRequest.newBuilder(URI.create("https://api.myapp.com/someOtherEndPoint"))
        .GET()
        .build();

    var firstFuture = httpClient.sendAsync(firstRequest, BodyHandlers.ofString());
    var secondFuture = httpClient.sendAsync(secondRequest, BodyHandlers.ofString());

    BiFunction<HttpResponse<String>, HttpResponse<String>, String> combinerFunction =
        (firstResponse, secondResponse) -> firstResponse.body() + " : " + secondResponse.body();

    firstFuture
        .thenCombine(secondFuture, combinerFunction)
        .thenAccept(System.out::println);

  }

  @Test
  public void websocketsToo(){
    var httpClient = HttpClient.newHttpClient();

    httpClient.newWebSocketBuilder().buildAsync(
        URI.create("https://api.myapp.com/someWebsocketEndPoint"), new Listener() {
          @Override
          public void onOpen(WebSocket webSocket) {
            System.out.println("Web socket open for business");
            webSocket.sendText("We've seen our onOpen callback", true);
          }

          @Override
          public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            return null;
          }

          @Override
          public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
            return null;
          }

          @Override
          public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
            return null;
          }

          @Override
          public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
            return null;
          }

          @Override
          public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            return null;
          }

          @Override
          public void onError(WebSocket webSocket, Throwable error) {
            System.out.println("Our websocket produced an error :(");
          }
        });

  }

}