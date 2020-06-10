package com.news.newsaggregator.util;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.news.newsaggregator.entity.News;
import com.news.newsaggregator.entity.NewsTemplate;

/**
 * Утилита для парсинга новостей.
 * @author Alexandr Trifonov
 *
 */
@Component
public class NewsTemplateUtil {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(NewsTemplateUtil.class);

	/**
	 * Парсинг новостей с веб-сайта
	 * @param newsTemplate Шаблон источника новостей
	 * @return Список новостей.
	 */
	public List<News> parseWebSite(NewsTemplate newsTemplate) {
		List<News> newsList = new ArrayList<>();
		ZoneId newsTimeZone = ZoneId.of(newsTemplate.getTimeZone());
		try (WebClient client = new WebClient();) {
			client.getOptions().setCssEnabled(false);
			client.getOptions().setJavaScriptEnabled(false);
			HtmlPage page = client.getPage(newsTemplate.getSourceUrl());

			List<Object> itemsObj = page.getByXPath(newsTemplate.getItemTemplate());
			List<HtmlElement> items = new ArrayList<>();
			for (Object obj : itemsObj) {
				HtmlElement el = (HtmlElement) obj;
				items.add(el);
			}
			if (items.isEmpty()) {
				log.info("No news found");
			} else {				
				for (HtmlElement htmlItem : items) {
					HtmlElement itemTime = htmlItem.getFirstByXPath(newsTemplate.getTimeTemplate());
					String itemTimeText = itemTime.asText();
					String timeStr = findTime(itemTimeText);

					LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(newsTimeZone),
							LocalTime.parse(timeStr));
					ZonedDateTime pubDate = ZonedDateTime.of(localDateTime, newsTimeZone);


					HtmlElement itemTitle = htmlItem.getFirstByXPath(newsTemplate.getTitleTemplate());
					String title = itemTitle.asText();
					HtmlAnchor itemLink = (HtmlAnchor) htmlItem.getFirstByXPath(newsTemplate.getLinkTemplate());
					String link = itemLink.getHrefAttribute();
					
					if (!link.matches("^https?:\\/\\/[\\da-z\\.-]+\\.[a-z\\.]{2,6}.+")) {						
						link = absLink(newsTemplate.getSourceUrl(), link);
					}
					News news = new News(title, pubDate, link, newsTemplate);
					newsList.add(news);
				}
			}

		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error("FailingHttpStatusCodeException | IOException.  newsTemplate = {}", newsTemplate, e);
		} catch (Exception e) {
			log.error("Exception. newsTemplate = {}", newsTemplate, e);
		}
		
		return newsList;
	}
	
	/**
	 * Фильтрация новостей по дате публикации последней новости по данному Шаблону источника новостей
	 * @param newsList Список новостей
	 * @param lastTime Дата публикации последней новости в базе данных.
	 * @return Список отфильтрованных новостей.
	 */
	public List<News> getOrderedLastNews(List<News> newsList, ZonedDateTime lastTime) {
		newsList = newsList.stream().sorted(Comparator.comparing(News::getPubDate)).collect(Collectors.toList());
		newsList.removeIf(e -> e.getPubDate().compareTo(lastTime) < 0);

		return newsList;
	}
	
	/**
	 * Парсинг новостей с rss ленты.
	 * @param newsTemplate Шаблон источника новостей
	 * @return Список новостей.
	 */
	public List<News> parseRss(NewsTemplate newsTemplate) {
		List<News> newsList = new ArrayList<>();
		try {
			RestTemplate restTemplate = new RestTemplate();
			String lenta = restTemplate.getForObject(newsTemplate.getSourceUrl(), String.class);

			InputSource source = new InputSource(new StringReader(lenta));

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document document;
			document = db.parse(source);

			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath;
			xpath = xpathFactory.newXPath();

			NodeList itemList = (NodeList) xpath.evaluate(newsTemplate.getItemTemplate(), document,
					XPathConstants.NODESET);

			int length = itemList.getLength();			
			for (int i = 0; i < length; i++) {
				Node node = itemList.item(i);
				Node nodeLink = (Node) xpath.evaluate(newsTemplate.getLinkTemplate(), node, XPathConstants.NODE);
				Node nodeTitle = (Node) xpath.evaluate(newsTemplate.getTitleTemplate(), node, XPathConstants.NODE);
				Node nodeTime = (Node) xpath.evaluate(newsTemplate.getTimeTemplate(), node, XPathConstants.NODE);
				
				String link = nodeLink.getTextContent();
				String title = nodeTitle.getTextContent();
				String timeStr = nodeTime.getTextContent();
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
				ZonedDateTime pubDate = ZonedDateTime.parse(timeStr, formatter);
				
				News news = new News(title, pubDate, link, newsTemplate);
				newsList.add(news);
			}

		} catch (RestClientException e) {
			log.error("RestClientException. newsTemplate = {}", newsTemplate, e);
		} catch (ParserConfigurationException e) {
			log.error("ParserConfigurationException. newsTemplate = {}", newsTemplate, e);
		} catch (SAXException e) {
			log.error("SAXException e). newsTemplate = {}", newsTemplate, e);
		} catch (IOException e) {
			log.error("IOException. newsTemplate = {}", newsTemplate, e);
		} catch (XPathExpressionException e) {
			log.error("XPathExpressionException/ newsTemplate = {}", newsTemplate, e);

		}
		return newsList;
	}
	
	/**
	 * Поиск времени публикации новости в строке.
	 * @param time Строка для поиска времени.
	 * @return Строка со временем.
	 */
	private String findTime(String time) {
		Pattern p = Pattern.compile("\\d\\d:\\d\\d");
		Matcher matcher = p.matcher(time);		
		while(matcher.find()) {
			time = matcher.group();
		}
		return time;
	}
	
	/**
	 * Формирование абсолютного URL для новости с относительным URL.
	 * @param sourceUrl Шаблон источника новостей
	 * @param link Относительный URL новости
	 * @return Абсолютный URL новости.
	 */
	private String absLink(String sourceUrl, String link) {
		Pattern patternHttp = Pattern.compile("^https?:\\/\\/[\\da-z\\.-]+\\.[a-z\\.]{2,6}");
		Matcher matcherHttp = patternHttp.matcher(sourceUrl);
		String domainUrl = "";
		while(matcherHttp.find()) {
		 	domainUrl = matcherHttp.group();
		}
		return domainUrl + link;
	}
}
