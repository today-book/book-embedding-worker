package org.todaybook.embedding.application.batch.tasklet;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.application.batch.service.EmbeddingBatchService;
import org.todaybook.embedding.domain.Book;
import org.todaybook.embedding.domain.BookKeyset;
import org.todaybook.embedding.domain.service.BookQueryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingTasklet implements Tasklet {

  private final BookQueryService bookQueryService;
  private final EmbeddingBatchService embeddingBatchService;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    ExecutionContext context =
        contribution.getStepExecution().getJobExecution().getExecutionContext();

    BookKeyset keyset = BookKeyset.from(context);

    log.info("[TODAY-BOOK] 임베딩 조회 시작 - keyset={}", keyset);

    List<Book> books = bookQueryService.findByCursor(keyset, 50);

    if (books.isEmpty()) {
      contribution.setExitStatus(ExitStatus.NOOP);
      return RepeatStatus.FINISHED;
    }

    embeddingBatchService.save(books);

    Book last = books.getLast();
    BookKeyset next = BookKeyset.of(last.id(), last.updatedAt());
    next.put(context);

    log.info("[TODAY-BOOK] 임베딩 저장 완료 - keyset={}", next);

    return RepeatStatus.CONTINUABLE;
  }
}
