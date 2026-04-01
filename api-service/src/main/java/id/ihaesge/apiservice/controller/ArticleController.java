package id.ihaesge.apiservice.controller;

import id.ihaesge.apiservice.dto.ArticleResponse;
import id.ihaesge.apiservice.dto.CreateArticleRequest;
import id.ihaesge.apiservice.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleResponse create(@Valid @RequestBody CreateArticleRequest request) {
        return articleService.createArticle(request);
    }

    @GetMapping
    public List<ArticleResponse> getAll() {
        return articleService.getArticles();
    }

    @GetMapping("/{id}")
    public ArticleResponse getById(@PathVariable UUID id) {
        return articleService.getArticle(id);
    }
}
