package org.todaybook.embedding.infrastructure.opensearch.exception;

import org.todaybook.commoncore.error.AbstractServiceException;

public class OpensearchInternalServerException extends AbstractServiceException {

  public OpensearchInternalServerException(String message) {
    super(OpensearchErrorCode.INTERNAL_SERVER_ERROR, message);
  }
}
