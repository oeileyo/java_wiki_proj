import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args){
        String encoding = "UTF-8";
        StringWriter sw = new StringWriter();

        while (true) {

            try {
                System.out.println("Type your request below...");
                String searchText = scanner.nextLine();
                if (searchText == null)
                    continue;

                System.out.printf("Your request is \"%s\". \nSearching...\n\n", searchText.toUpperCase());
                searchText += " wikipedia";
                //searchText.replace(" ", "+");

                //получаем файл с кодом искомой страницы
                Document google_ = Jsoup.connect("https://www.google.com/search?q=" + searchText).get();

                String g = google_.text();
                if (g.contains("ничего не найдено") | g.contains("примерно 0")){
                    System.out.println("Sorry, but there is no information about this on Wiki...\nTry entering a new request.\n\n");
                } else {
                    //путь до страницы об этом на вики (en.wikipedia.org > wiki > Puppy)
                    String wikiURL = google_.getElementsByTag("cite").get(0).text();

                    //json из статьи на вики
                    String wikiApiJSON = "https://www.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles="
                            + URLEncoder.encode(wikiURL.substring(wikiURL.lastIndexOf(" ") + 1, wikiURL.length()), encoding);

                    //"переходим" по ссылочке
                    HttpURLConnection httpcon = (HttpURLConnection) new URL(wikiApiJSON).openConnection();
                    httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
                    BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));

                    //читаем и соединяем строки
                    String responseSB = in.lines().collect(Collectors.joining());
                    in.close();

                    if (responseSB.charAt(39) == '-' | !responseSB.contains("\"extract\":\"")) {
                        System.out.println("Sorry, but there is no information about this on Wiki...\nTry entering a new request.\n\n");
                    } else {
                        //выделяем заголовок
                        String title = responseSB.split("\"title\":\"")[1];
                        title = title.substring(0, title.indexOf('\"'));
                        //выделяем extract - первй краткий абзац инфы
                        String result = responseSB.split("\"extract\":\"")[1];
                        result = result.substring(0, result.length() - 5);

                        System.out.println("========== Here is the main information about " + title.toUpperCase() + " ==========\n");
                        sw.printString(result);
                        System.out.println("\n============================================================================");
                        System.out.println("You can read more about it here");
                        System.out.println("http://" + wikiURL.substring(0, wikiURL.indexOf("›")-1) + "/"
                                + wikiURL.substring(wikiURL.indexOf("›") + 2, wikiURL.lastIndexOf("›")-1) + "/"
                                + wikiURL.substring(wikiURL.lastIndexOf("›") + 2, wikiURL.length()));
                        System.out.println("============================================================================\n\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}