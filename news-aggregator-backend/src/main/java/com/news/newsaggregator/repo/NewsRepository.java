package com.news.newsaggregator.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.news.newsaggregator.entity.News;
import com.news.newsaggregator.entity.NewsTemplate;

/**
 * Репозиторий объектов Новость.
 * @author Alexandr Trifonov
 *
 */
public interface NewsRepository extends PagingAndSortingRepository<News, Long> {
	/**
	 * Получить все новости, отсортированные по убыванию времени новости
	 * @return Список новостей
	 */
	List<News> findAllByOrderByPubDateDesc();
	
	/**
	 * Получить все новости с указанной подстрокой в заголовке новости, отсортированные по убыванию времени новости
	 * @param title Искомая подстрока в заголовке
	 * @return Список новостей
	 */
	List<News> findAllByTitleIgnoreCaseContainingOrderByPubDateDesc(String title);
	
	/**
	 * Получить все новости по данному источнику, отсортированные по убыванию времени новости
	 * @param newsTemplate Источник новостей
	 * @return Список новостей
	 */
	List<News> findAllByNewsTemplateOrderByPubDateDesc(NewsTemplate newsTemplate);
	
	/**
	 * Получить все новости по данному источнику и с указанной подстрокой в заголовке новости,
	 *  отсортированные по убыванию времени новости
	 * @param newsTemplate Источник новостей
	 * @param title Искомая подстрока в заголовке
	 * @return Список новостей
	 */
	List<News> findAllByNewsTemplateAndTitleIgnoreCaseContainingOrderByPubDateDesc(NewsTemplate newsTemplate, String title);
}
