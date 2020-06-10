package com.news.newsaggregator.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.news.newsaggregator.entity.NewsTemplate;

/**
 * Репозиторий объектов Шаблон источника новостей.
 * @author Alexandr Trifonov
 *
 */
public interface NewsTemplateRepository extends PagingAndSortingRepository<NewsTemplate, Integer> {
	
}
