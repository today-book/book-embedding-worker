package org.todaybook.embedding.infrastructure.opensearch.exception;

import org.todaybook.commoncore.error.AbstractServiceException;

public class OpensearchNullParameterException extends AbstractServiceException {

  public OpensearchNullParameterException(Object... errorArgs) {
    super(OpensearchErrorCode.NULL_PARAMETER, errorArgs);
  }
}
