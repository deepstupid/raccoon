/*
 * Raccoon Network Coding Engine
 * @author Reza Sherafat (reza.sherafat@gmail.com)
 * Copyright (c) 2012, MSRG, University of Toronto. All rights reserved.
 */

package org.msrg.raccoon.engine.task.sequential;

import org.msrg.raccoon.engine.CodingEngine;
import org.msrg.raccoon.engine.ICodingListener;
import org.msrg.raccoon.engine.task.CodingId;
import org.msrg.raccoon.engine.task.CodingTask;
import org.msrg.raccoon.engine.task.CodingTaskType;
import org.msrg.raccoon.engine.task.result.CodingResult;

import java.util.HashSet;
import java.util.Set;


public abstract class SequentialCodingTask extends CodingTask implements ICodingListener {

    public final SequentialCodingTaskType _seqTaskType;
    protected final Set<CodingTask> _activeSubTasks = new HashSet<CodingTask>();
    protected final CodingEngine _engine;
    protected int _currentStage = -1;
    private long[] _stageTimes;

    protected SequentialCodingTask(CodingEngine engine, ICodingListener listener, CodingId id,
                                   SequentialCodingTaskType seqTaskType) {
        super(listener, id, CodingTaskType.SEQUENCIAL);

        _seqTaskType = seqTaskType;
        _engine = engine;
        _stageTimes = new long[getFinalStage() + 2];
    }

    public synchronized final void runInitialSequencialTasks() {
        if (_currentStage != -1)
            throw new IllegalStateException("Expected: " + -1 + ", found: " + _currentStage);

        runStagePrivately();
        if (reachedFinalStage())
            finished();
    }

    protected abstract void runStagePrivately();


    public abstract void codingFailed(CodingResult result);


    public abstract void codingFinished(CodingResult result);


    public abstract void codingStarted(CodingResult result);

    protected final boolean reachedFinalStage() {
        return _currentStage >= getFinalStage();
    }


    public synchronized final void finished() {
        super.finished();

        ((CodingEngine) _engine).sequentialCodingTaskFinished(this);
    }


    public final void failed() {
        super.failed();

        ((CodingEngine) _engine).sequentialCodingTaskFailed(this);
    }

    public long[] getStageTimes() {
        return _stageTimes;
    }

    protected abstract int getFinalStage();

    protected final void setCurrentStage(int i) {
        if (i < _currentStage && _currentStage != -1)
            throw new IllegalArgumentException(_currentStage + " vs. " + i);

        _currentStage = i;
        _stageTimes[_currentStage + 1] = System.nanoTime();
    }


    public void codingPreliminaryStageCompleted(CodingResult result) {
        throw new UnsupportedOperationException(toString() + " vs. " + result);
    }
}
