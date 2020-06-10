package com.news.newsaggregator.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.news.newsaggregator.entity.News;
import com.news.newsaggregator.entity.NewsTemplate;
import com.news.newsaggregator.repo.NewsRepository;

/**
 * Класс из уровня обслуживания для доступа к объектам Новость.
 * @author Alexandr Trifonov
 *
 */
@Service
public class NewsService {
	/**
	 * Репозиторий Новостей.
	 */
	private NewsRepository newsRepo;
	
	@Autowired
	public NewsService(NewsRepository newsRepository) {
		this.newsRepo = newsRepository;
	}
	
	
	/**
	 * Сохранение всех новостей
	 * @param newsList Список новостей.
	 */
	public void saveAll(List<News> newsList) {
		this.newsRepo.saveAll(newsList);
	}
	
	/**
	 * Получение новостей.
	 * @return Список новостей.
	 */
	public List<News> findAll() {		
		return newsRepo.findAllByOrderByPubDateDesc();
	}
	
	/**
	 * Получение новостей по указанному источнику
	 * @param newsTemplate Источник новостей.
	 * @return Список новостей.
	 */
	public List<News> findAllByNewsTemplate(NewsTemplate newsTemplate) {
		return newsRepo.findAllByNewsTemplateOrderByPubDateDesc(newsTemplate);
	}
	
	/**
	 * Получение всех новостей по данному источнику и с указанной подстрокой в заголовке новости
	 * @param newsTemplate Источник новостей.
	 * @param title Искомая подстрока в заголовке
	 * @return Список новостей.
	 */
	public List<News> findAllByNewsAndTitle(NewsTemplate newsTemplate, String title) {
		return newsRepo.findAllByNewsTemplateAndTitleIgnoreCaseContainingOrderByPubDateDesc(newsTemplate, title);
	}
	
	/**
	 * Получение всех новостей с указанной подстрокой в заголовке новости
	 * @param title Искомая подстрока в заголовке
	 * @return Список новостей.
	 */
	public List<News> findAllByTitle(String title) {
		return newsRepo.findAllByTitleIgnoreCaseContainingOrderByPubDateDesc(title);
	}
}
