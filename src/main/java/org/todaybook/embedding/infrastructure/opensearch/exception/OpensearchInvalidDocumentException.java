package org.todaybook.embedding.infrastructure.opensearch.exception;

import org.todaybook.commoncore.error.AbstractServiceException;

public class OpensearchInvalidDocumentException extends AbstractServiceException {

  public OpensearchInvalidDocumentException(Object... errorArgs) {
    super(OpensearchErrorCode.INVALID_DOCUMENT, errorArgs);
  }

  public OpensearchInvalidDocumentException(String message) {
    super(OpensearchErrorCode.INVALID_DOCUMENT, message);
  }
}
