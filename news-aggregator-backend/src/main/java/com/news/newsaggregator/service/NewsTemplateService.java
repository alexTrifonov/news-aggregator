package com.news.newsaggregator.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.news.newsaggregator.entity.NewsTemplate;
import com.news.newsaggregator.repo.NewsTemplateRepository;

/**
 * Класс из уровня обслуживания для доступа к объектам Новость.
 * @author Alexandr Trifonov
 *
 */
@Service
public class NewsTemplateService {
	/**
	 * Репозиторий Шаблонов источников новостей.
	 */
	private NewsTemplateRepository newsTemplateRepo;
	
	@Autowired
	public NewsTemplateService(NewsTemplateRepository newsTemplateRepo) {
		this.newsTemplateRepo = newsTemplateRepo;
	}
	
	/**
	 * Получить Шаблон источника новостей по идентификатору
	 * @param id Идентификатор Шаблона источника новостей
	 * @return Шаблон источника новостей
	 */
	public NewsTemplate getNewsTemplate(int id) {
		return newsTemplateRepo.findById(id).get();
	}
	
	/**
	 * Получить все Шаблоны источников новостей
	 * @return
	 */
	public List<NewsTemplate> getAll() {
		List<NewsTemplate> list = new ArrayList<>();
		Iterable<NewsTemplate> iterableNewsTemplate = newsTemplateRepo.findAll();
		iterableNewsTemplate.forEach(list::add);
		return list;
	}
	
	/**
	 * Сохранить Шаблон источника новостей
	 * @param newsTemplate Шаблон источника новостей
	 * @return Сохраненный Шаблон источника новостей
	 */
	public NewsTemplate save(NewsTemplate newsTemplate) {
		return newsTemplateRepo.save(newsTemplate);
	}
	
	/**
	 * Проверка корректности полей Шаблона источника новостей
	 * @param newsTemplate Шаблон источника новостей
	 * @return Подтверждение валидности полей Шаблона источника новостей
	 */
	public boolean checkNewsTemplate(NewsTemplate newsTemplate) {
		boolean valid = true;
		
		if (!newsTemplate.getSourceUrl().matches("^https?:\\/\\/[\\da-z\\.-]+\\.[a-z\\.]{2,6}.+")) {
			valid = false;
		} 
		if (!newsTemplate.getTimeZone().matches("UTC[-+][012]\\d:[30]0")) {
			valid = false;
		} 
		if (newsTemplate.getItemTemplate().isEmpty() 
				|| newsTemplate.getLinkTemplate().isEmpty()
				|| newsTemplate.getTimeTemplate().isEmpty()
				|| newsTemplate.getTitleTemplate().isEmpty() 
				|| newsTemplate.getType().isEmpty() ) {
			valid = false;
		}
		return valid;
	}
	
	
}
