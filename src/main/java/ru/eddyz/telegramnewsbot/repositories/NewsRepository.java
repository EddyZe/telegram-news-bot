package ru.eddyz.telegramnewsbot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.eddyz.telegramnewsbot.domain.entities.NewsUrl;

import java.util.Optional;

public interface NewsRepository extends JpaRepository<NewsUrl, Long> {

    Optional<NewsUrl> findByUrl(String url);

}
