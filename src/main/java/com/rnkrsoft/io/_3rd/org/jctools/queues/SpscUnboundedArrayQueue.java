/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rnkrsoft.io._3rd.org.jctools.queues;

import com.rnkrsoft.io._3rd.org.jctools.util.Pow2;
import com.rnkrsoft.io._3rd.org.jctools.util.UnsafeRefArrayAccess;

public class SpscUnboundedArrayQueue<E> extends BaseSpscLinkedArrayQueue<E>
{

    public SpscUnboundedArrayQueue(int chunkSize)
    {
        int chunkCapacity = Math.max(Pow2.roundToPowerOfTwo(chunkSize), 16);
        long mask = chunkCapacity - 1;
        E[] buffer = CircularArrayOffsetCalculator.allocate(chunkCapacity + 1);
        producerBuffer = buffer;
        producerMask = mask;
        consumerBuffer = buffer;
        consumerMask = mask;
        producerBufferLimit = mask - 1; // we know it's all empty to start with
    }

    @Override
    final boolean offerColdPath(E[] buffer, long mask, long pIndex, long offset, E v, Supplier<? extends E> s)
    {
        // use a fixed lookahead step based on buffer capacity
        final long lookAheadStep = (mask + 1) / 4;
        long pBufferLimit = pIndex + lookAheadStep;

        // go around the buffer or add a new buffer
        if (null == UnsafeRefArrayAccess.lvElement(buffer, CircularArrayOffsetCalculator.calcElementOffset(pBufferLimit, mask)))
        {
            producerBufferLimit = pBufferLimit - 1; // joy, there's plenty of room
            writeToQueue(buffer, v == null ? s.get() : v, pIndex, offset);
        }
        else if (null == UnsafeRefArrayAccess.lvElement(buffer, CircularArrayOffsetCalculator.calcElementOffset(pIndex + 1, mask)))
        { // buffer is not full
            writeToQueue(buffer, v == null ? s.get() : v, pIndex, offset);
        }
        else
        {
            // we got one slot left to write into, and we are not full. Need to link new buffer.
            // allocate new buffer of same length
            final E[] newBuffer = CircularArrayOffsetCalculator.allocate((int) (mask + 2));
            producerBuffer = newBuffer;
            producerBufferLimit = pIndex + mask - 1;

            linkOldToNew(pIndex, buffer, offset, newBuffer, offset, v == null ? s.get() : v);
        }
        return true;
    }

    @Override
    public int capacity()
    {
        return UNBOUNDED_CAPACITY;
    }
}
