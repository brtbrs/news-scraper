package id.ihaesge.tagger.model;

import java.util.UUID;

public record TagCandidate(
        UUID contentId,
        String ticker
//        String alias
) {}
