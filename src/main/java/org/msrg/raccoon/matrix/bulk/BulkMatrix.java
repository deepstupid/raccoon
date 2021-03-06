/*
 * Raccoon Network Coding Engine
 * @author Reza Sherafat (reza.sherafat@gmail.com)
 * Copyright (c) 2012, MSRG, University of Toronto. All rights reserved.
 */

package org.msrg.raccoon.matrix.bulk;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.SecureRandom;
import java.util.Random;

public class BulkMatrix {

    public static final int _SLICE_LENGTH = 100;
    protected static final Random _RANDOM = new SecureRandom();

    public static int _MAX_ROWS_PRINT = 3;
    public static int _MAX_SLICES_PRINT = 3;
    public final int _size;
    public final int _cols;
    public final int _rows;
    protected final SliceMatrix[] _slices;

    public BulkMatrix(SliceMatrix[] slices) {
        _slices = slices;
        _rows = _slices.length;
        _cols = _slices.length == 0 ? 0 : _slices[0]._cols;
        for (SliceMatrix sm : _slices)
            if (_cols != sm._cols)
                throw new IllegalArgumentException("Cols mismatch: " + _cols + " vs. " + sm._cols);

        _size = _cols * _rows;
    }

    public BulkMatrix(@NotNull byte[][] b) {
        _rows = b.length;
        _slices = new SliceMatrix[_rows];
        _cols = _rows == 0 ? 0 : b[0].length;

        for (int i = 0; i < b.length; i++) {
            SliceMatrix sm = new SliceMatrix(b[i]);
            _slices[i] = sm;
        }

        _size = _cols * _rows;
    }

    public BulkMatrix(int rows, int cols) {
        _size = rows * cols;
        _rows = rows;
        _cols = cols;

        _slices = new SliceMatrix[_rows];
        for (int i = 0; i < _slices.length; i++)
            _slices[i] = createOneEmtpySlice(_cols);
    }

    @NotNull
    public static BulkMatrix createBulkMatixRandomData(int rows, int cols) {
        BulkMatrix bm = new BulkMatrix(rows, cols);
        byte[] b = new byte[cols];

        for (int j = 0; j < bm.getSliceCount(); j++) {
            SliceMatrix slice = bm.slice(j);
            BulkMatrix._RANDOM.nextBytes(b);
            slice.loadWithCopy(b, 0);
        }

        return bm;
    }

    @NotNull
    public static BulkMatrix createBulkMatixIncrementalData(int rows, int cols) {
        BulkMatrix bm = new BulkMatrix(rows, cols);
        int val = 0;
        for (int j = 0; j < bm.getSliceCount(); j++) {
            SliceMatrix slice = bm.slice(j);
            byte[] b = new byte[slice._cols];
            for (int k = 0; k < slice._cols; k++)
                b[k] = (byte) val++;
            slice.loadNoCopy(b);
        }

        return bm;
    }

    @NotNull
    public BulkMatrix createEmptyMatrix(int rows, int cols) {
        return new BulkMatrix(rows, cols);
    }

    @NotNull
    public SliceMatrix createOneEmtpySlice(int cols) {
        return new SliceMatrix(cols);
    }

    public int getSliceCount() {
        return _slices.length;
    }

    public SliceMatrix slice(int i) {
        return _slices[i];
    }


    public boolean equals(@Nullable Object obj) {
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;

        BulkMatrix smObj = (BulkMatrix) obj;
        int sliceCount = getSliceCount();
        if (sliceCount != smObj.getSliceCount())
            return false;

        for (int i = 0; i < sliceCount; i++)
            if (_slices[i] != null) {
                if (!_slices[i].equals(smObj._slices[i]))
                    return false;
            } else if (smObj._slices[i] != null)
                return false;

        return true;
    }

    public byte getByte(int i) {
        int row = i / _cols;
        int col = i % _cols;

        return _slices[row].getByte(col);
    }

    public int getSize() {
        return _size;
    }

    public void add(int i, SliceMatrix sm) {
        _slices[i] = sm;
    }


    public String toString() {
        return toString(BulkMatrix._MAX_ROWS_PRINT, BulkMatrix._MAX_SLICES_PRINT);
    }

//	public String toStringFull() {
//		return toString(_rows, _cols);
//	}

    protected String toString(int maxRows, int maxCols) {
        Writer ioWriter = new StringWriter();
        try {
            for (int j = 0; j < _rows && j < maxRows; j++) {
                int strLenSoFar = 0;
                if (j != 0)
                    ioWriter.append("\n");

                if (_slices[j] == null)
                    ioWriter.append(" NULL ");

                strLenSoFar += _slices[j].toString(ioWriter);
            }

            int remainingRows = _rows - maxRows;
            if (remainingRows > 0)
                ioWriter.append("\n...(" + remainingRows + ")");
        } catch (IOException iox) {
            return "ERROR";
        }
        return ioWriter.toString();
    }

    public int getSizeInByteBuffer() {
        int slicesSize = 0;

        for (SliceMatrix slice : _slices)
            slicesSize += slice.getSizeInByteBuffer();

        return 4 + 4 + 4 + slicesSize;
    }

    // Change this in the future to allow sub-block segmentation
//	public int putObjectInByteBuffer(ByteBuffer bb, int offset) {
//		int inputOffset = offset;
//		
//		bb.putInt(offset, _rows);
//		offset += 4;
//
//		bb.putInt(offset, _cols);
//		offset += 4;
//
//		bb.putInt(offset, _size);
//		offset += 4;
//
//		for(SliceMatrix slice : _slices)
//			offset += slice.putObjectInByteBuffer(bb, offset);
//				
//		return (offset - inputOffset);
//	}

    @NotNull
    public byte[][] getContent() {
        byte[][] content = new byte[_rows][];
        for (int i = 0; i < _slices.length; i++)
            content[i] = _slices[i].getContent();

        return content;
    }

    public SliceMatrix[] getSlices() {
        return _slices;
    }
}