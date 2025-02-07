package com.ssafy.Split.domain.bowling.exception;

import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;

public class ProgressNotFoundException extends SplitException {
    public ProgressNotFoundException(String progressId) {
        super(ErrorCode.PROGRESS_NOT_FOUND, progressId);
    }
}

