package ru.eddyz.telegramnewsbot.domain.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news_url")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

}
