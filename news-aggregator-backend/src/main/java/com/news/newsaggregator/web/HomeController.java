package com.news.newsaggregator.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для отображения основной страницы приложения.
 * @author Alexandr Trifonov
 *
 */
@Controller
public class HomeController {
	
	/**
	 * Переход к основной странице приложения.
	 * @return Название основной страницы
	 */
	@GetMapping("/")
	public String home() {
		return "index";
	}
}
