package amzmon;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Parser {

	public void printSinglePageItems(String baseUrl, int page_no) throws IOException{
		Document doc = Jsoup.connect(baseUrl + page_no).userAgent("Chrome").timeout(6000).get();
		
		Item item;
		int i = 0 + (page_no-1)*24;
		while((item = getItemFromSearchResult(doc, "result_"+i))!= null){
			item.printItem();
			i = i + 1;
		}
	}

	public void printHTMLFileItems(String fn, int page_no) throws IOException{
		File input = new File(fn);
		Document doc = Jsoup.parse(input, null, "");
		
		Item item;
		int i = 0 + (page_no-1)*24;
		while((item = getItemFromSearchResult(doc, "result_"+i))!= null){
			item.printItem();
			i = i + 1;
		}
	}
	
	public Item[] getSinglePageItems(String baseUrl, int page_no, int itemsPerPage) throws IOException{
		Document doc = Jsoup.connect(baseUrl + page_no).userAgent("Chrome").timeout(6000).get();
		
		Item[] items = new Item[itemsPerPage];
		int baseIndex = (page_no - 1) * itemsPerPage;
		int index = baseIndex;
		while((index < baseIndex + itemsPerPage) 
				&& (items[index - baseIndex] = getItemFromSearchResult(doc, "result_"+ index)) != null){
			index++;
		}
		return items;
	}
	
	public Item getItemFromSearchResult(Document doc, String elementId) throws IOException{

		Element itemElement = doc.getElementById(elementId);
		if(itemElement == null)
			return null;
		String itemId = itemElement.attr("name");
		
		Element itemData = itemElement.children().get(0);
		while(itemData != null && !itemData.attr("class").equalsIgnoreCase("data"))
			itemData = itemData.nextElementSibling();
		String itemName = "";
		if(itemData != null)
			itemName = itemData.children().get(0).children().get(0).childNodes().get(0).toString();
		
		String category  = doc.getElementById("nav-search-in-content").text();
		Item item = new Item(itemId, itemName, new Category(category));
		
		Elements itemNewPrices = itemData.getElementsByAttributeValue("class", "newPrice");
		if(itemNewPrices != null && !itemNewPrices.isEmpty()){	
			Elements prices = itemNewPrices.get(0).getElementsByAttributeValue("class", "price addon");
			String price = "";
			if(prices != null && !prices.isEmpty()){
				price = prices.text();
			}
			if(prices == null || prices.isEmpty() || !price.startsWith("$")){
				prices = itemNewPrices.get(0).getElementsByAttributeValue("class", "addon");
				price = prices.text();
				if(prices != null && !prices.isEmpty()){
					Element priceElement = prices.get(0);
					while(priceElement != null && !priceElement.text().startsWith("$"))
						priceElement = priceElement.nextElementSibling();
					if(priceElement != null)
						price = priceElement.text();
				}
			}
			if(!price.equals("") && price.startsWith("$")){
				price = price.split(" ")[0];
				item.setPrice(Float.parseFloat(price.substring(1).replace(",", "")));
			}
		}
		Elements itemMinPrices = itemData.getElementsByAttributeValue("class", "usedNewPrice");
		if(itemMinPrices != null && !itemMinPrices.isEmpty()){
			Elements minPrices = itemMinPrices.get(0).getElementsByAttributeValue("class", "subPrice");
			if(minPrices != null && !minPrices.isEmpty())
			for(int i = 0; i < minPrices.size(); i++){
				Element minPrice = minPrices.get(i);
				Elements prices = minPrice.getElementsByAttributeValue("class", "price");
				if(prices != null && !prices.isEmpty()){
					if(minPrice.getElementsByTag("a") != null && !minPrice.getElementsByTag("a").isEmpty()
							&& minPrice.getElementsByAttributeValue("class", "price") != null){
						String wholePrice = minPrice.getElementsByAttributeValue("class", "price").text();
						if(minPrice.getElementsByTag("a").get(0).text().contains("new") && wholePrice.startsWith("$"))
							item.setMinNewPrice(Float.parseFloat(wholePrice.substring(1).replace(",", "")));
						else if(minPrice.getElementsByTag("a").get(0).text().contains("used"))
							item.setMinUsedPrice(Float.parseFloat(wholePrice.substring(1).replace(",", "")));
					}
				}
			}
		}
		Elements fastTrack = itemData.getElementsByAttributeValue("class", "fastTrack");
		if(fastTrack != null && !fastTrack.isEmpty()){
			String track = fastTrack.text();
			if(track.toLowerCase().contains(" days")){
				item.setWaitingUnit(Item.Unit.Day);
				int end = track.indexOf(" days");
				int begin = end-1;
				while(track.charAt(begin) != ' ' && track.charAt(begin) != '-')
					begin = begin -1;
				begin = begin + 1;
				item.setWaitingTimeLatest(Integer.parseInt(track.substring(begin, end)));
				String newTrack = track.substring(0, end);
				end = newTrack.indexOf(" to");
				if(end < 0) end = newTrack.indexOf(" or");
				if(end < 0) end = newTrack.indexOf("-");
				if(end >=0 ){
					begin = end -1;
					while(track.charAt(begin) != ' ')
						begin = begin -1;
					begin = begin + 1;
					item.setWaitingTimeEarliest(Integer.parseInt(track.substring(begin, end)));
				}
			}
			if(track.toLowerCase().contains(" week")){
				item.setWaitingUnit(Item.Unit.Week);
				int end = track.indexOf(" week");
				int begin = end-1;
				while(track.charAt(begin) != ' ' && track.charAt(begin) != '-')
					begin = begin -1;
				begin = begin + 1;
				item.setWaitingTimeLatest(Integer.parseInt(track.substring(begin, end)));
				String newTrack = track.substring(0, end);
				end = newTrack.indexOf(" to");
				if(end < 0) end = newTrack.indexOf(" or");
				if(end < 0) end = newTrack.indexOf("-");
				if(end >= 0){
					begin = end -1;
					while(track.charAt(begin) != ' ')
						begin = begin -1;
					begin = begin + 1;
					item.setWaitingTimeEarliest(Integer.parseInt(track.substring(begin, end)));
				}
			}
			if(track.toLowerCase().contains("in stock on")){
				String dayValue = track.substring(12);
				Date day;
				String pattern = "";
				dayValue = dayValue.replace(",", "");
				dayValue = dayValue.replace("-", " ");
				dayValue = dayValue.replace("/", " ");
				String[] values = dayValue.split(" ");
				for(int i = 0; i < values.length; i++){
					for(int j = 0; j < values[i].length(); j++)
						switch(i){
							case 0:
								pattern = pattern + "M";
								break;
							case 1:
								pattern = pattern + "d";
								break;
							case 2:
								pattern = pattern + "y";
								break;
							default:
								break;
						}
					pattern = pattern + " ";						
				}
				pattern = pattern.substring(0, pattern.length()-1);
				try{
					day = new SimpleDateFormat(pattern).parse(dayValue);
				}
				catch(ParseException e){
					day = new Date();
				}
				if(day.after(new Date()))
					item.setOutOfStock(true);
			}
			if(track.toLowerCase().contains("out of stock") || item.getWaitingUnit() == Item.Unit.Week || item.getWaitingTimeEarliest() >= 7)
				item.setOutOfStock(true);
		}
		return item;
	}
	
	public static void main(String args[]) throws IOException{
		String baseUrl = "http://www.amazon.com/s?rh=n%3A172282&page=";
		new Parser().printSinglePageItems(baseUrl, 38);
		// String fn = "search_result_p1.htm";
		// new Parser().printHTMLFileItems(fn, 1);		
	}
}
