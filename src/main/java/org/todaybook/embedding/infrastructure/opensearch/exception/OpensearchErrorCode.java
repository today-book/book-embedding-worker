package org.todaybook.embedding.infrastructure.opensearch.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.todaybook.commoncore.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum OpensearchErrorCode implements ErrorCode {
  INTERNAL_SERVER_ERROR(
      "OPENSEARCH.INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  NULL_PARAMETER("OPENSEARCH.NULL_PARAMETER", HttpStatus.BAD_REQUEST.value()),
  INVALID_DOCUMENT("OPENSEARCH.INVALID_DOCUMENT", HttpStatus.BAD_REQUEST.value()),
  ;

  private final String code;
  private final int status;
}
