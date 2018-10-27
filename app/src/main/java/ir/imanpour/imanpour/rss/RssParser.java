package ir.imanpour.imanpour.rss;


import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ir.imanpour.imanpour.main.G;


public class RssParser extends DefaultHandler {

  private StringBuilder content;
  private boolean inChannel;
  private boolean inImage;
  private boolean inItem;

  private ArrayList<Item> items = new ArrayList<Item>();
  private Channel channel = new Channel();

  private Item lastItem;


  public RssParser(String url) {
    execute(url);
  }

  private int i = 0;

  private void execute(String url) {
    try {
      i++;
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);
      SAXParser sp = spf.newSAXParser();
      XMLReader xr = sp.getXMLReader();
      xr.setContentHandler(this);
     /* HttpClient client = new DefaultHttpClient();
      HttpPost httpPost=new HttpPost(url);*/
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestMethod("GET");
      connection.setDoOutput(true);
      connection.connect();
      InputStream inputStream = connection.getInputStream();
      String xmlString = convertInputStreamToString(inputStream);
      xr.parse(new InputSource(new StringReader(xmlString)));
    } catch (ParserConfigurationException e) {
      if (i == 3) {
        if (G.onRefereshFalse != null) {
          G.onRefereshFalse.run();
        }
        return;
      }
      execute(url);
      e.printStackTrace();
    } catch (SAXException e) {
      if (i == 3) {
        if (G.onRefereshFalse != null) {
          G.onRefereshFalse.run();
        }
        return;
      }
      execute(url);
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      if (G.onRefereshFalse != null) {
        G.onRefereshFalse.run();
      }
    }
  }

  private String convertInputStreamToString(InputStream inputStream) {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String s;
    StringBuilder builder = new StringBuilder();
    try {
      while ((s = bufferedReader.readLine()) != null) {
        builder.append(s);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return builder.toString();
  }


  public static class Item {

    public String title;
    public String description;
    public String link;
    public String category;
    public String pubDate;
    public String guid;
    public String enclosure = "";
    public String creator;
    public int like;
  }


  public class Channel {

    public String title;
    public String description;
    public String link;
    public String lastBuildDate;
    public String generator;
    public String imageUrl;
    public String imageTitle;
    public String imageLink;
    public String imageWidth;
    public String imageHeight;
    public String imageDescription;
    public String language;
    public String copyright;
    public String pubDate;
    public String category;
    public String ttl;
    public String enclosure;
  }


  @Override
  public void startDocument() throws SAXException {
  }


  @Override
  public void endDocument() throws SAXException {
    try {
      for (Item item : items) {
        G.sqLiteDatabase.execSQL("INSERT INTO RSS " +
          "(title,description,link,category,pubDate,guid,enclosure,creator,like,unread)" +
          " VALUES ('" + item.title + "','" + item.description + "','" + item.link + "','" + item.category + "','" + item.pubDate + "','" + item.guid + "','"
          + item.enclosure + "','" + item.creator + "','" + 0 + "','" + 0 + "')");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if (localName.equalsIgnoreCase("image")) {
      inImage = true;
    }

    if (localName.equalsIgnoreCase("channel")) {
      inChannel = true;
    }

    if (localName.equalsIgnoreCase("item")) {
      lastItem = new Item();
      items.add(lastItem);
      inItem = true;
    }
    if (localName.equalsIgnoreCase("enclosure") || localName.equalsIgnoreCase("content"))
      lastItem.enclosure = atts.getValue("url");

    /*if (localName.equalsIgnoreCase("enclosure")) {
      inEnclosure=true;
    content = new StringBuilder();
    }*/
    content = new StringBuilder();
  }


  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (localName.equalsIgnoreCase("image")) {
      inImage = false;
    }

    if (localName.equalsIgnoreCase("channel")) {
      inChannel = false;
    }

    if (localName.equalsIgnoreCase("item")) {
      inItem = false;
    }

    if (localName.equalsIgnoreCase("title")) {
      if (content == null) {
        return;
      }

      if (inItem) {
        lastItem.title = content.toString();
      } else if (inImage) {
        channel.imageTitle = content.toString();
      } else if (inChannel) {
        channel.title = content.toString();
      }

      content = null;
    }
    /*if(inEnclosure){
      if (content == null) {
        return;
      }
      inEnclosure=false;
      Log.i("TEST","DO");
        lastItem.enclosure = content.toString();
    };*/
    if (localName.equalsIgnoreCase("description")) {
      if (content == null) {
        return;
      }

      if (inItem) {
        lastItem.description = content.toString();
      } else if (inImage) {
        channel.imageDescription = content.toString();
      } else if (inChannel) {
        channel.description = content.toString();
      }

      content = null;
    }
    if (localName.equalsIgnoreCase("lastBuildDate")) {
      if (content == null) {
        return;
      }
      if (inChannel) {
        channel.lastBuildDate = content.toString();
      }

      content = null;
    }

    if (localName.equalsIgnoreCase("link")) {
      if (content == null) {
        return;
      }

      if (inItem) {
        lastItem.link = content.toString();
      } else if (inImage) {
        channel.imageLink = content.toString();
      } else if (inChannel) {
        channel.link = content.toString();
      }

      content = null;
    }

    if (localName.equalsIgnoreCase("category")) {
      if (content == null) {
        return;
      }

      if (inItem) {
        lastItem.category = content.toString();
      } else if (inChannel) {
        channel.category = content.toString();
      }

      content = null;
    }

    if (localName.equalsIgnoreCase("creator")) {
      if (content == null) {
        return;
      }
      lastItem.creator = content.toString();
      content = null;
    }
    if (localName.equalsIgnoreCase("pubDate")) {
      if (content == null) {
        return;
      }

      if (inItem) {
        lastItem.pubDate = content.toString();
      } else if (inChannel) {
        channel.pubDate = content.toString();
      }

      content = null;
    }

    if (localName.equalsIgnoreCase("guid")) {
      if (content == null) {
        return;
      }

      lastItem.guid = content.toString();
      content = null;
    }

    if (localName.equalsIgnoreCase("url")) {
      if (content == null) {
        return;
      }

      channel.imageUrl = content.toString();
      content = null;
    }

    if (localName.equalsIgnoreCase("width")) {
      if (content == null) {
        return;
      }

      channel.imageWidth = content.toString();
      content = null;
    }

    if (localName.equalsIgnoreCase("height")) {
      if (content == null) {
        return;
      }

      channel.imageHeight = content.toString();
      content = null;
    }

    if (localName.equalsIgnoreCase("language")) {
      if (content == null) {
        return;
      }

      channel.language = content.toString();
      content = null;
    }

    if (localName.equalsIgnoreCase("copyright")) {
      if (content == null) {
        return;
      }

      channel.copyright = content.toString();
      content = null;
    }

    if (localName.equalsIgnoreCase("ttl")) {
      if (content == null) {
        return;
      }

      channel.ttl = content.toString();
      content = null;
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (content == null) {
      return;
    }
    content.append(ch, start, length);
  }


  public Item getItem(int index) {
    return items.get(index);
  }


  public ArrayList<Item> getItems() {
    return items;
  }

}
