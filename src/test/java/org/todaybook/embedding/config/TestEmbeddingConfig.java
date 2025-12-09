package org.todaybook.embedding.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestEmbeddingConfig {

  @Bean
  @Primary
  public EmbeddingModel mockEmbeddingModel() {
    return new EmbeddingModel() {

      private static final float[] EMBEDDING = {1f, 2f, 3f};

      @Override
      public EmbeddingResponse call(EmbeddingRequest request) {
        List<Embedding> embeddings = new ArrayList<>();

        for (int i = 0; i < request.getInstructions().size(); i++) {
          embeddings.add(new Embedding(EMBEDDING, i));
        }

        return new EmbeddingResponse(embeddings);
      }

      @Override
      public float[] embed(Document document) {
        return EMBEDDING;
      }
    };
  }
}
