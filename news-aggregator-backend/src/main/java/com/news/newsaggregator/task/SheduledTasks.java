package com.news.newsaggregator.task;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.news.newsaggregator.entity.News;
import com.news.newsaggregator.entity.NewsTemplate;
import com.news.newsaggregator.service.NewsService;
import com.news.newsaggregator.service.NewsTemplateService;
import com.news.newsaggregator.util.NewsTemplateUtil;

/**
 * Исполнитель получения списка новостей с определенной периодичностью
 * @author Alexandr Trifonov
 *
 */
@Component
public class SheduledTasks {
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(SheduledTasks.class);
	
	/**
	 * Формат времени.
	 */
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	/**
	 * Тип источника новостей rss.
	 */
	private final static String RSS = "rss";
	
	/**
	 * Тип источника новостей веб-сайт.
	 */
	private final static String WEBSITE = "website";
	
	/**
	 * Сервис Шаблонов источников новостей
	 */
	private NewsTemplateService newsTemplateService;
	
	/**
	 * Сервис Новостей
	 */
	private NewsService newsService;
	
	/**
	 * Пул потоков
	 */
	private ThreadPoolTaskExecutor executor;
	
	/**
	 * Утилита для парсинга данных с источника новостей.
	 */
	private NewsTemplateUtil newsTemplateUtil;
	
	
	@Autowired
	public SheduledTasks(NewsTemplateService newsTemplateService, NewsService newsService, NewsTemplateUtil newsTemplateUtil) {
		this.newsTemplateService = newsTemplateService;
		this.newsService = newsService;
		this.newsTemplateUtil = newsTemplateUtil;
		executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("news_parse_executor_thread");
        executor.initialize();
	}
	
	/**
	 * Получение очередной партии новостей.
	 */
	@Scheduled(fixedRate = 600000)
	public void getNextNews() {
		
		List<NewsTemplate> newsTemplateList = newsTemplateService.getAll();
		
		newsTemplateList.forEach(newsTemplate -> {
			Runnable task = () -> {
				List<News> newsList = new ArrayList<>();
				if (WEBSITE.equals(newsTemplate.getType())) {
					newsList = newsTemplateUtil.parseWebSite(newsTemplate);
				} else if (RSS.equals(newsTemplate.getType())) {
					newsList = newsTemplateUtil.parseRss(newsTemplate);
				}
				ZonedDateTime lastDate = newsTemplate.getLastDate();				
				newsList = newsTemplateUtil.getOrderedLastNews(newsList, lastDate);
				if (!newsList.isEmpty()) {
					News lastNews = newsList.stream().reduce((first, second) -> second).get();
					lastDate = lastNews.getPubDate();
					newsTemplate.setLastDate(lastDate);
					newsTemplateService.save(newsTemplate);
				}
				newsService.saveAll(newsList);
			};
			executor.execute(task);
		});
		
		log.info("The search news started at {}", dateFormat.format(new Date()));
	}
}
