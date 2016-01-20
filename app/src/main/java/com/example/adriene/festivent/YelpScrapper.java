package com.example.adriene.festivent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Hardeep on 1/20/2016.
 */
public class YelpScrapper {

    // tools
    public ArrayList<String> details = new ArrayList<String>();
    public ArrayList<String> dateArray = new ArrayList<String>();
    public ArrayList<String> descriptionArray = new ArrayList<String>();

    // List to send to database
    public ArrayList<String> titleList = new ArrayList<String>();
    public ArrayList<String> dateList = new ArrayList<String>();
    public ArrayList<String> cityList = new ArrayList<String>();
    public ArrayList<String> referenceList = new ArrayList<String>();
    public ArrayList<String> imageList = new ArrayList<String>();
    public ArrayList<String> interestNumArray = new ArrayList<String>();

    // Need to figure out how User Interface will get the city, state,
    // country
    int pagevalue = 0;
    String usercity, userstate, userdate, urlcity, hyphenCity, urldate;

    public YelpScrapper() {
        usercity = "";
        userstate = "";
        userdate = "";

        if (usercity.contains(" ")) {
            hyphenCity = usercity.replaceAll(" ", "-");
            String temp = hyphenCity + "-" + userstate + "-us";
            urlcity = temp.toLowerCase();
        } else {
            String temp = usercity + "-" + userstate + "-us";
            urlcity = temp.toLowerCase();
        }

        String[] dateparse = userdate.split("-");
        urldate = dateparse[2] + dateparse[0] + dateparse[1];

        int indexTracker = 0;
        int outputcount = 0;

        try {
            Document start = Jsoup.connect("http://www.yelp.com/events/" + urlcity + "/browse?start_date=" + urldate).get();
            Elements totalEvents = start.select("td.range-of-total span");
            String number = totalEvents.text();
            String events[] = number.split("of ");
            pagevalue = Integer.parseInt(events[1]);

            int iteration = pagevalue - (pagevalue % 10);

            for (int pages = 0; pages <= iteration + 1; pages += 10) {

                Document doc = Jsoup.connect("http://www.yelp.com/events/" + urlcity + "/browse?start_date=" + urldate + "&audience=everybody&start=" + Integer.toString(pages)).get();
                Elements spans = doc.select("h2.title span[itemprop]"); // headers
                Elements links = doc.select("h2.title a[href]"); // grabs reference link
                Elements images = doc.select("a.event-photo img"); // grabs the src for image.
                Elements info = doc.select("div[class=\"media-story\"] p");

                // loop to get the event names
                for (Element span : spans) {
                    titleList.add(span.text());
                }

                // info
                for (Iterator<Element> iterator = info.iterator(); iterator.hasNext(); ) {
                    Element element = iterator.next();
                    String text = element.text();
                    String dontneed = element.select("a").text();
                    String trimmedInfo = text.replaceFirst(dontneed, "").trim();
                    details.add(trimmedInfo);
                }

                // links loop
                for (Element link : links) {
                    referenceList.add(link.attr("abs:href"));
                }

                // image source loop
                for (Element image : images) {
                    imageList.add(image.attr("abs:src"));
                }
                indexTracker = titleList.size();
            }

            for (int i = 0; i < details.size(); i++) {
                if (i == details.size())
                    break;
                if (i == 0 || i % 3 == 0)
                    dateArray.add(details.get(i));
                else if (i == 1 || i % 3 == 1)
                    descriptionArray.add(details.get(i));
                else
                    interestNumArray.add(details.get(i));
            }

            // split date and the city
            for (int j = 0; j < dateArray.size(); j++) {
                String[] eventdate = null;
                if (dateArray.get(j).contains("  ")) {
                    eventdate = dateArray.get(j).split("  ");
                } else {
                    for (int k = 0; k < dateArray.get(j).length(); k++) {

                        if (dateArray.get(j).charAt(k) == 'p' && dateArray.get(j).charAt(k + 1) == 'm' && dateArray.get(j).charAt(k + 2) == ' '
                                && Character.isUpperCase(dateArray.get(j).charAt(k + 3))) {

                            eventdate = extraDataParse(k, dateArray.get(j)).split("  ");

                        } else if (dateArray.get(j).charAt(k) == 'a' && dateArray.get(j).charAt(k + 1) == 'm' && dateArray.get(j).charAt(k + 2) == ' '
                                && Character.isUpperCase(dateArray.get(j).charAt(k + 3))) {

                            eventdate = extraDataParse(k, dateArray.get(j)).split("  ");
                        }
                    }
                }

                String date = eventdate[0];
                dateList.add(date);
                String string2 = eventdate[1];
                String temp1[] = string2.split(", ");
                if (temp1.length > 2) {
                    String temp2 = temp1[1] + ", " + temp1[2];
                    cityList.add(temp2);
                } else {
                    cityList.add(string2);
                }
            }

            // final output test put everything together
            System.out.println("\n" + pagevalue + " Results for " + usercity + ", " + userstate + " on " + userdate);
            for (int i = 0; i < indexTracker; i++) {
                if (i == titleList.size()) {
                    break;
                }
                System.out.println();
                System.out.println("---------------------------------------------------------");
                System.out.println("Event Title: " + titleList.get(i));
                System.out.println("Date and Time: " + dateList.get(i));
                System.out.println("City: " + cityList.get(i));
                System.out.println("Description: " + descriptionArray.get(i));
                System.out.println("Reference Link: " + referenceList.get(i));
                System.out.println("Image source File: " + imageList.get(i));
                System.out.println("Interest Level: " + interestNumArray.get(i));
                outputcount++;
            }
        } catch (Exception e) {
            System.out.println("There was an error processing your request... \nPlease try again.");
        }
    }

    private static String extraDataParse(int index, String test) {
        int counter = index;
        String city = "";
        String temp1 = test.substring(0, counter);
        String[] temp2 = test.substring(counter).split(", ");

        if (temp2.length > 2) {
            city = temp2[1] + ", " + temp2[2];
        }
        return temp1 + " " + city;
    }
}