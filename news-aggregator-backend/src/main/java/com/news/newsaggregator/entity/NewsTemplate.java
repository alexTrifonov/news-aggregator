package com.news.newsaggregator.entity;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Сущность Шаблон источника новостей.
 * @author Alexandr Trifonov
 *
 */
@Entity
@Table(name="news_template")
public class NewsTemplate {
	/**
	 * Идентификатор Шаблона источника новостей
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	/**
	 * URL источника
	 */
	@Column(name = "source_url", nullable = false, unique = true)
	private String sourceUrl;
	
	
	@Column(name = "type", nullable = false)
	private String type;
	
	/**
	 * Тип источника
	 */
	@Column(name = "item_template", nullable = false)
	private String itemTemplate;
	
	/**
	 * Шаблон для поиска заголовка новости
	 */
	@Column(name = "title_template", nullable = false)
	private String titleTemplate;
	
	/**
	 * Временная зона источника
	 */
	@Column(name = "time_zone", nullable = false)
	private String timeZone;
	
	/**
	 * Шаблон для поиска времени новости
	 */
	@Column(name = "time_template", nullable = false)
	private String timeTemplate;
	
	/**
	 * Шаблон для поиска URL новости
	 */
	@Column(name = "link_template", nullable = false)
	private String linkTemplate;
	
	/**
	 * Время последней полученной новости из данного источника	
	 */
	@Column(name = "last_date", nullable = false)
	private ZonedDateTime lastDate = ZonedDateTime.now(ZoneId.systemDefault());
	
	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getItemTemplate() {
		return itemTemplate;
	}

	public void setItemTemplate(String itemTemplate) {
		this.itemTemplate = itemTemplate;
	}

	public String getTitleTemplate() {
		return titleTemplate;
	}

	public void setTitleTemplate(String titleTemplate) {
		this.titleTemplate = titleTemplate;
	}
	
	
	
	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getTimeTemplate() {
		return timeTemplate;
	}

	public void setTimeTemplate(String timeTemplate) {
		this.timeTemplate = timeTemplate;
	}

	public String getLinkTemplate() {
		return linkTemplate;
	}

	public void setLinkTemplate(String linkTemplate) {
		this.linkTemplate = linkTemplate;
	}

	
	public ZonedDateTime getLastDate() {
		return lastDate;
	}

	public void setLastDate(ZonedDateTime lastDate) {
		this.lastDate = lastDate;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public String toString() {
		return "NewsTemplate [sourceUrl=" + sourceUrl + ", type=" + type + ", itemTemplate=" + itemTemplate
				+ ", titleTemplate=" + titleTemplate + ", timeZone=" + timeZone + ", timeTemplate=" + timeTemplate
				+ ", linkTemplate=" + linkTemplate + ", lastDate=" + lastDate + "]";
	}

	

	
	
	
}
