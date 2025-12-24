package org.todaybook.embedding.application.batch.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.todaybook.embedding.domain.BookKeyset;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeysetInitializeTasklet implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    StepExecution execution = contribution.getStepExecution();
    ExecutionContext context = execution.getJobExecution().getExecutionContext();

    BookKeyset keyset;

    if (BookKeyset.exists(context)) {
      keyset = BookKeyset.from(context);
    } else {
      keyset = BookKeyset.initial();
      keyset.put(context);
    }

    log.info("[TODAY-BOOK] Keyset 초기화 완료 - {}", keyset);

    return RepeatStatus.FINISHED;
  }
}
