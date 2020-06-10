package com.news.newsaggregator.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.news.newsaggregator.entity.News;
import com.news.newsaggregator.entity.NewsTemplate;
import com.news.newsaggregator.service.NewsService;
import com.news.newsaggregator.service.NewsTemplateService;

/**
 * REST контроллер для обработки запросов связанных с новостями.
 * @author Alexandr Trifonov
 *
 */
@RestController
@RequestMapping("/api")
public class NewsController {
	/**
	 * Logger
	 */
	private Logger logger = LoggerFactory.getLogger(NewsController.class);
	
	/**
	 * Константа для обозначения кода ошибки со стороны клиента.
	 */
	private final static String USER_ERROR = "USER_ERROR";
	
	/**
	 * Константа для обозначения кода ошибки на стророне сервера.
	 */
	private final static String SERVER_ERROR = "SERVER_ERROR";
	
	/**
	 * ObjectMapper для создания json ответов при перехвате исключений.
	 */
	private ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Сервис Новостей
	 */
	private NewsService newsService;
	
	/**
	 * Сервис Шаблонов источников новостей
	 */
	private NewsTemplateService newsTemplateService;
	
	@Autowired
	public NewsController(NewsService newsService, NewsTemplateService newsTemplateService) {
		this.newsService = newsService;
		this.newsTemplateService = newsTemplateService;
	}
	
	/**
	 * Получение списка новостей в виде объекта Json.
	 * @param idStr Идентификатор Шаблона источника новостей.
	 * @param title Подзаголовок в строке заголовка новости.
	 * @return Список новостей
	 */
	@GetMapping(path = "/news", produces = "application/json")
	public ResponseEntity<?> getAllNews(@RequestParam(required = false) String idStr, @RequestParam(required = false) String title) {
		
		ResponseEntity<?> responseEntity;
		
		
		try {
			if (idStr == null && title == null) {
				responseEntity = new ResponseEntity<List<News>>(newsService.findAll(), HttpStatus.OK);
			} else if (idStr != null && title == null) {
				Integer id = Integer.parseInt(idStr);
				NewsTemplate newsTemplate = newsTemplateService.getNewsTemplate(id);
				responseEntity = new ResponseEntity<List<News>>(newsService.findAllByNewsTemplate(newsTemplate), HttpStatus.OK);
			} else if (idStr == null && title != null) {
				responseEntity = new ResponseEntity<List<News>>(newsService.findAllByTitle(title), HttpStatus.OK);
			} else {
				Integer id = Integer.parseInt(idStr);
				NewsTemplate newsTemplate = newsTemplateService.getNewsTemplate(id);
				responseEntity = new ResponseEntity<List<News>>(newsService.findAllByNewsAndTitle(newsTemplate, title), HttpStatus.OK);
			}
			
		} catch(NumberFormatException e) {
			logger.error("getAllNews. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", USER_ERROR);
			errorBody.put("error_message", e.getMessage());
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("getAllNews. error = {}, error class = {}", e.getMessage(), e.getClass(), e);			
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	
	
}
