package id.ihaesge.apiservice.controller;

import id.ihaesge.apiservice.dto.CreateContentTagRequest;
import id.ihaesge.apiservice.dto.TagAliasResponse;
import id.ihaesge.apiservice.service.TaggingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tagging")
public class TaggingController {
    private final TaggingService taggingService;

    public TaggingController(TaggingService taggingService) {
        this.taggingService = taggingService;
    }

    @GetMapping("/tag-aliases")
    public List<TagAliasResponse> getTagAliases() {
        return taggingService.getTagAliases();
    }

    @PostMapping("/content-tags")
    @ResponseStatus(HttpStatus.CREATED)
    public void createContentTag(@Valid @RequestBody CreateContentTagRequest request) {
        taggingService.createContentTag(request);
    }
}
