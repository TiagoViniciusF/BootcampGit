package clientHttp;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;



public class ClientHttpEx {

    static ExecutorService executar = Executors.newFixedThreadPool(6, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            System.out.println("Nova Thread criada "+ (thread.isDaemon() ? "daemon" : "") + " Thread Group : "+ thread.getThreadGroup());
            return thread;
        }
    });

    public static void main(String[] args) throws Exception {
        connectAkanaiHttpClient();
    }


    private static void connectAkanaiHttpClient() throws Exception {
        System.out.println("Running HTTP/1.1 example ...");
        try{
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .proxy(ProxySelector.getDefault())
                    .build();

            long start = System.currentTimeMillis();

            HttpRequest mainRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://http2.akamai.com/demo/h2_demo_frame.html"))
                    .build();

            HttpResponse<String> response = httpClient.send(mainRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code :: "+ response.statusCode());
            System.out.println("Response Headers ::"+response.headers());
            String responseBody = response.body();
            System.out.println(responseBody);

            List<Future<?>> future = new ArrayList<>();
            responseBody
                    .lines()
                    .filter(line -> line.trim().startsWith("<img height"))
                    .map(line -> line.substring(line.indexOf("src='") + 5, line.indexOf("'/>")))
                    .forEach(image -> {
                        Future<?> imgFuture = executar.submit(() -> {
                            HttpRequest imgRequest = HttpRequest.newBuilder()
                                    .uri(URI.create("https://http2.akamai.com"+ image))
                                    .build();

                            try {
                                HttpResponse<String> imageResponse = httpClient.send(imgRequest, HttpResponse.BodyHandlers.ofString());
                                System.out.println("Imagem Carregada::"+image+", status code :: "+imageResponse.statusCode());
                            } catch (IOException | InterruptedException e) {
                                System.out.println("Erro durante a requisicão para recuperar a imagem" + image);
                            }
                        });
                        future.add(imgFuture);
                        System.out.println("Submetido um futuro para image :: "+image);
                    });

            future.forEach(f ->{
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("Erros ao esperar, carregar imagens do futuro");

                }
            });
            long end = System.currentTimeMillis();
            System.out.println("Tempo de carregamento total :: "+(end - start)+" ms");
        }finally{
            executar.shutdown();
        }

    }

}




  /*  private static void connectAndPrintURL() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET().uri(URI.create("https://docs.oracle.com/javase/10/language/"))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status code : "+ response.statusCode());
        System.out.println("Headers response : "+response.headers());
        System.out.println(response.body());


       /* try{
            var url = new URL("https://docs.oracle.com/javase/10/language/");
            var urlConnection = url.openConnection();
            try(var bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))){
                System.out.println(bufferedReader.lines().collect(Collectors.joining()).replaceAll(">", ">\n"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    */




