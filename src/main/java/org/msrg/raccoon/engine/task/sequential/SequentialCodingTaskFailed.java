/*
 * Raccoon Network Coding Engine
 * @author Reza Sherafat (reza.sherafat@gmail.com)
 * Copyright (c) 2012, MSRG, University of Toronto. All rights reserved.
 */

package org.msrg.raccoon.engine.task.sequential;

import org.jetbrains.annotations.NotNull;
import org.msrg.raccoon.engine.task.CodingTask;
import org.msrg.raccoon.engine.task.CodingTaskFailed;

public class SequentialCodingTaskFailed extends CodingTaskFailed {

    public final SequentialCodingTask _seqCodingTask;

    public SequentialCodingTaskFailed(SequentialCodingTask seqCodingTask) {
        this(seqCodingTask, null);
    }

    public SequentialCodingTaskFailed(SequentialCodingTask seqCodingTask, CodingTask cTask) {
        super(cTask);
        _seqCodingTask = seqCodingTask;
    }

    @NotNull

    public String toString() {
        return "SeqCodingTaskFailure:" + _seqCodingTask + "@" + _cTask;
    }
}
