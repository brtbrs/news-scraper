package id.ihaesge.apiservice.service;

import id.ihaesge.apiservice.dto.CreateContentTagRequest;
import id.ihaesge.apiservice.dto.TagAliasResponse;
import id.ihaesge.apiservice.entity.ContentEntity;
import id.ihaesge.apiservice.entity.ContentTagEntity;
import id.ihaesge.apiservice.repository.ContentRepository;
import id.ihaesge.apiservice.repository.ContentTagRepository;
import id.ihaesge.apiservice.repository.StockAliasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaggingService {
    private final StockAliasRepository stockAliasRepository;
    private final ContentRepository contentRepository;
    private final ContentTagRepository contentTagRepository;

    public TaggingService(
            StockAliasRepository stockAliasRepository,
            ContentRepository contentRepository,
            ContentTagRepository contentTagRepository
    ) {
        this.stockAliasRepository = stockAliasRepository;
        this.contentRepository = contentRepository;
        this.contentTagRepository = contentTagRepository;
    }

    public List<TagAliasResponse> getTagAliases() {
        return stockAliasRepository.findAll().stream()
                .map(alias -> new TagAliasResponse(alias.getTag(), alias.getAlias()))
                .toList();
    }

    @Transactional
    public void createContentTag(CreateContentTagRequest request) {
        ContentEntity content = contentRepository.findById(request.contentId())
                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + request.contentId()));

        ContentTagEntity entity = new ContentTagEntity();
        entity.setContent(content);
        entity.setTag(request.tag());
        contentTagRepository.save(entity);
    }
}
