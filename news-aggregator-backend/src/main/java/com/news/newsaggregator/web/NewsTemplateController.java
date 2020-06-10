package com.news.newsaggregator.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.news.newsaggregator.entity.NewsTemplate;
import com.news.newsaggregator.service.NewsTemplateService;

/**
 * REST контроллер для обработки запросов связанных с Шаблонами источников новостей.
 * @author Alexandr Trifonov
 *
 */
@RestController
@RequestMapping("/api")
public class NewsTemplateController {
	/**
	 * Logger
	 */
	private Logger log = LoggerFactory.getLogger(NewsTemplateController.class);
	
	/**
	 * ObjectMapper для создания json ответов при перехвате исключений.
	 */
	private ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Сервис Шаблонов источников новостей
	 */
	private NewsTemplateService newsTemplateService;
	
	
	/**
	 * Константа для обозначения кода ошибки на стророне сервера.
	 */
	private final static String SERVER_ERROR = "SERVER_ERROR";
	
	
	@Autowired
	public NewsTemplateController(NewsTemplateService newsTemplateService) {
		this.newsTemplateService = newsTemplateService;
	}
	
	/**
	 * Сохранение Шаблона источника новостей
	 * @param newsTemplate Шаблон источника новостей
	 * @return Сохраненный Шаблон источника новостей
	 */
	@PostMapping(path = "/news-template", consumes = "application/json")
	public ResponseEntity<?> saveNewsTemplate(@RequestBody NewsTemplate newsTemplate) {
		log.info("saveNewsTemplate. newsTemplate = {}", newsTemplate);
		ResponseEntity<?> responseEntity;
		try {
			if (!newsTemplateService.checkNewsTemplate(newsTemplate)) {				
				throw new IllegalArgumentException("Incorrect news template");
			}
			newsTemplateService.save(newsTemplate);
			responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {			
			log.error("addExpense. error = {}, error class = {}", e.getMessage(), e.getClass(), e);
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", "Ошибка добавления шаблона новостей. Возможные причины: источник уже добавлен, не все поля введены, URL не корректный, временная зона не корректная");
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
	/**
	 * Получение списка всех Шаблонов источников новостей
	 * @return Список Шаблонов источников новостей.
	 */
	@GetMapping(path = "/news-templates", produces = "application/json")
	public ResponseEntity<?> getAllNewsTemplate() {
		ResponseEntity<?> responseEntity;		
		try {			
			responseEntity = new ResponseEntity<List<NewsTemplate>>(newsTemplateService.getAll(), HttpStatus.OK);
		} catch (Exception e) {
			log.error("getAllNews. error = {}, error class = {}", e.getMessage(), e.getClass(), e);			
			ObjectNode errorBody = mapper.createObjectNode();
			errorBody.put("error", SERVER_ERROR);
			errorBody.put("error_message", e.getMessage());			
			responseEntity = new ResponseEntity<String>(errorBody.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
}
