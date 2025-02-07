package com.ssafy.Split.domain.bowling.exception;

import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;

public class FrameNotFoundException extends SplitException {
    public FrameNotFoundException(String frameNum) {
        super(ErrorCode.FRAME_NOT_FOUND,frameNum);
    }
}
