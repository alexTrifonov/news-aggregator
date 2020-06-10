package com.news.newsaggregator.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.news.newsaggregator.serializer.PubDateSerializer;


/**
 * Сущность Новость.
 * @author Alexandr Trifonov
 *
 */
@Entity
@Table(name="news")
public class News {
	/**
	 * Идентификатор новости
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	/**
	 * Заголовок новости
	 */
	@Column(name = "title", nullable = false)
	private String title;
	
	/**
	 * Дата публикации новости
	 */
	@Column(name = "pub_date", nullable = false)
	@JsonSerialize(using = PubDateSerializer.class)
	private ZonedDateTime pubDate;
	
	/**
	 * URL новости
	 */
	@Column(name = "link", nullable = false)
	private String link;
	
	/**
	 * Шаблон источника новости
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "news_template_id", foreignKey = @ForeignKey(name = "fk_news_template_id"), nullable = false)
	protected NewsTemplate newsTemplate;
	
	public News() {
		
	}

	public News(String title, ZonedDateTime pubDate, String link, NewsTemplate newsTemplate) {
		this.title = title;
		this.pubDate = pubDate;
		this.link = link;
		this.newsTemplate = newsTemplate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	public ZonedDateTime getPubDate() {
		return pubDate;
	}

	public void setPubDate(ZonedDateTime pubDate) {
		this.pubDate = pubDate;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Long getId() {
		return id;
	}
	
	
	
	public NewsTemplate getNewsTemplate() {
		return newsTemplate;
	}

	public void setNewsTemplate(NewsTemplate newsTemplate) {
		this.newsTemplate = newsTemplate;
	}

	@Override
	public String toString() {
		return "News [title=" + title + ", pubDate=" + pubDate + ", link=" + link + "]";
	}
	
	
}
