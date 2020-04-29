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

import com.rnkrsoft.io._3rd.org.jctools.util.PortableJvmInfo;
import com.rnkrsoft.io._3rd.org.jctools.util.UnsafeAccess;
import com.rnkrsoft.io._3rd.org.jctools.util.UnsafeRefArrayAccess;

abstract class SpmcArrayQueueL1Pad<E> extends ConcurrentCircularArrayQueue<E>
{
    long p01, p02, p03, p04, p05, p06, p07;
    long p10, p11, p12, p13, p14, p15, p16, p17;

    public SpmcArrayQueueL1Pad(int capacity)
    {
        super(capacity);
    }
}

//$gen:ordered-fields
abstract class SpmcArrayQueueProducerIndexField<E> extends SpmcArrayQueueL1Pad<E>
{
    protected final static long P_INDEX_OFFSET;

    static
    {
        try
        {
            P_INDEX_OFFSET =
                UnsafeAccess.UNSAFE.objectFieldOffset(SpmcArrayQueueProducerIndexField.class.getDeclaredField("producerIndex"));
        }
        catch (NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected long producerIndex;

    public final long lvProducerIndex()
    {
        return UnsafeAccess.UNSAFE.getLongVolatile(this, P_INDEX_OFFSET);
    }

    protected final void soProducerIndex(long newValue)
    {
        UnsafeAccess.UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, newValue);
    }

    public SpmcArrayQueueProducerIndexField(int capacity)
    {
        super(capacity);
    }
}

abstract class SpmcArrayQueueL2Pad<E> extends SpmcArrayQueueProducerIndexField<E>
{
    long p01, p02, p03, p04, p05, p06, p07;
    long p10, p11, p12, p13, p14, p15, p16, p17;

    public SpmcArrayQueueL2Pad(int capacity)
    {
        super(capacity);
    }
}

//$gen:ordered-fields
abstract class SpmcArrayQueueConsumerIndexField<E> extends SpmcArrayQueueL2Pad<E>
{
    protected final static long C_INDEX_OFFSET;

    static
    {
        try
        {
            C_INDEX_OFFSET =
                UnsafeAccess.UNSAFE.objectFieldOffset(SpmcArrayQueueConsumerIndexField.class.getDeclaredField("consumerIndex"));
        }
        catch (NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    }

    private volatile long consumerIndex;

    public SpmcArrayQueueConsumerIndexField(int capacity)
    {
        super(capacity);
    }

    public final long lvConsumerIndex()
    {
        return consumerIndex;
    }

    protected final boolean casConsumerIndex(long expect, long newValue)
    {
        return UnsafeAccess.UNSAFE.compareAndSwapLong(this, C_INDEX_OFFSET, expect, newValue);
    }
}

abstract class SpmcArrayQueueMidPad<E> extends SpmcArrayQueueConsumerIndexField<E>
{
    long p01, p02, p03, p04, p05, p06, p07;
    long p10, p11, p12, p13, p14, p15, p16, p17;

    public SpmcArrayQueueMidPad(int capacity)
    {
        super(capacity);
    }
}

//$gen:ordered-fields
abstract class SpmcArrayQueueProducerIndexCacheField<E> extends SpmcArrayQueueMidPad<E>
{
    // This is separated from the consumerIndex which will be highly contended in the hope that this value spends most
    // of it's time in a cache line that is Shared(and rarely invalidated)
    private volatile long producerIndexCache;

    public SpmcArrayQueueProducerIndexCacheField(int capacity)
    {
        super(capacity);
    }

    protected final long lvProducerIndexCache()
    {
        return producerIndexCache;
    }

    protected final void svProducerIndexCache(long newValue)
    {
        producerIndexCache = newValue;
    }
}

abstract class SpmcArrayQueueL3Pad<E> extends SpmcArrayQueueProducerIndexCacheField<E>
{
    long p01, p02, p03, p04, p05, p06, p07;
    long p10, p11, p12, p13, p14, p15, p16, p17;

    public SpmcArrayQueueL3Pad(int capacity)
    {
        super(capacity);
    }
}

public class SpmcArrayQueue<E> extends SpmcArrayQueueL3Pad<E>
{

    public SpmcArrayQueue(final int capacity)
    {
        super(capacity);
    }

    @Override
    public boolean offer(final E e)
    {
        if (null == e)
        {
            throw new NullPointerException();
        }
        final E[] buffer = this.buffer;
        final long mask = this.mask;
        final long currProducerIndex = lvProducerIndex();
        final long offset = calcElementOffset(currProducerIndex, mask);
        if (null != UnsafeRefArrayAccess.lvElement(buffer, offset))
        {
            long size = currProducerIndex - lvConsumerIndex();

            if (size > mask)
            {
                return false;
            }
            else
            {
                // spin wait for slot to clear, buggers wait freedom
                while (null != UnsafeRefArrayAccess.lvElement(buffer, offset))
                {
                    ;
                }
            }
        }
        UnsafeRefArrayAccess.spElement(buffer, offset, e);
        // single producer, so store ordered is valid. It is also required to correctly publish the element
        // and for the consumers to pick up the tail value.
        soProducerIndex(currProducerIndex + 1);
        return true;
    }

    @Override
    public E poll()
    {
        long currentConsumerIndex;
        long currProducerIndexCache = lvProducerIndexCache();
        do
        {
            currentConsumerIndex = lvConsumerIndex();
            if (currentConsumerIndex >= currProducerIndexCache)
            {
                long currProducerIndex = lvProducerIndex();
                if (currentConsumerIndex >= currProducerIndex)
                {
                    return null;
                }
                else
                {
                    currProducerIndexCache = currProducerIndex;
                    svProducerIndexCache(currProducerIndex);
                }
            }
        }
        while (!casConsumerIndex(currentConsumerIndex, currentConsumerIndex + 1));
        // consumers are gated on latest visible tail, and so can't see a null value in the queue or overtake
        // and wrap to hit same location.
        return removeElement(buffer, currentConsumerIndex, mask);
    }

    private E removeElement(final E[] buffer, long index, final long mask)
    {
        final long offset = calcElementOffset(index, mask);
        // load plain, element happens before it's index becomes visible
        final E e = UnsafeRefArrayAccess.lpElement(buffer, offset);
        // store ordered, make sure nulling out is visible. Producer is waiting for this value.
        UnsafeRefArrayAccess.soElement(buffer, offset, null);
        return e;
    }

    @Override
    public E peek()
    {
        final long mask = this.mask;
        final long currProducerIndexCache = lvProducerIndexCache();
        long currentConsumerIndex;
        E e;
        do
        {
            currentConsumerIndex = lvConsumerIndex();
            if (currentConsumerIndex >= currProducerIndexCache)
            {
                long currProducerIndex = lvProducerIndex();
                if (currentConsumerIndex >= currProducerIndex)
                {
                    return null;
                }
                else
                {
                    svProducerIndexCache(currProducerIndex);
                }
            }
        }
        while (null == (e = UnsafeRefArrayAccess.lvElement(buffer, calcElementOffset(currentConsumerIndex, mask))));
        return e;
    }

    @Override
    public boolean relaxedOffer(E e)
    {
        if (null == e)
        {
            throw new NullPointerException("Null is not a valid element");
        }
        final E[] buffer = this.buffer;
        final long mask = this.mask;
        final long producerIndex = lvProducerIndex();
        final long offset = calcElementOffset(producerIndex, mask);
        if (null != UnsafeRefArrayAccess.lvElement(buffer, offset))
        {
            return false;
        }
        UnsafeRefArrayAccess.spElement(buffer, offset, e);
        // single producer, so store ordered is valid. It is also required to correctly publish the element
        // and for the consumers to pick up the tail value.
        soProducerIndex(producerIndex + 1);
        return true;
    }

    @Override
    public E relaxedPoll()
    {
        return poll();
    }

    @Override
    public E relaxedPeek()
    {
        final E[] buffer = this.buffer;
        final long mask = this.mask;
        final long consumerIndex = lvConsumerIndex();
        return UnsafeRefArrayAccess.lvElement(buffer, calcElementOffset(consumerIndex, mask));
    }

    @Override
    public int drain(final Consumer<E> c)
    {
        final int capacity = capacity();
        int sum = 0;
        while (sum < capacity)
        {
            int drained = 0;
            if ((drained = drain(c, PortableJvmInfo.RECOMENDED_POLL_BATCH)) == 0)
            {
                break;
            }
            sum += drained;
        }
        return sum;
    }

    @Override
    public int fill(final Supplier<E> s)
    {
        return fill(s, capacity());
    }

    @Override
    public int drain(final Consumer<E> c, final int limit)
    {
        final E[] buffer = this.buffer;
        final long mask = this.mask;
        long currProducerIndexCache = lvProducerIndexCache();
        int adjustedLimit = 0;
        long currentConsumerIndex;
        do
        {
            currentConsumerIndex = lvConsumerIndex();
            // is there any space in the queue?
            if (currentConsumerIndex >= currProducerIndexCache)
            {
                long currProducerIndex = lvProducerIndex();
                if (currentConsumerIndex >= currProducerIndex)
                {
                    return 0;
                }
                else
                {
                    currProducerIndexCache = currProducerIndex;
                    svProducerIndexCache(currProducerIndex);
                }
            }
            // try and claim up to 'limit' elements in one go
            int remaining = (int) (currProducerIndexCache - currentConsumerIndex);
            adjustedLimit = Math.min(remaining, limit);
        }
        while (!casConsumerIndex(currentConsumerIndex, currentConsumerIndex + adjustedLimit));

        for (int i = 0; i < adjustedLimit; i++)
        {
            c.accept(removeElement(buffer, currentConsumerIndex + i, mask));
        }
        return adjustedLimit;
    }


    @Override
    public int fill(final Supplier<E> s, final int limit)
    {
        final E[] buffer = this.buffer;
        final long mask = this.mask;
        long producerIndex = this.producerIndex;

        for (int i = 0; i < limit; i++)
        {
            final long offset = calcElementOffset(producerIndex, mask);
            if (null != UnsafeRefArrayAccess.lvElement(buffer, offset))
            {
                return i;
            }
            producerIndex++;
            UnsafeRefArrayAccess.soElement(buffer, offset, s.get()); // StoreStore
            soProducerIndex(producerIndex); // ordered store -> atomic and ordered for size()
        }
        return limit;
    }

    @Override
    public void drain(final Consumer<E> c, final WaitStrategy w, final ExitCondition exit)
    {
        int idleCounter = 0;
        while (exit.keepRunning())
        {
            if (drain(c, PortableJvmInfo.RECOMENDED_POLL_BATCH) == 0)
            {
                idleCounter = w.idle(idleCounter);
                continue;
            }
            idleCounter = 0;
        }
    }

    @Override
    public void fill(final Supplier<E> s, final WaitStrategy w, final ExitCondition e)
    {
        final E[] buffer = this.buffer;
        final long mask = this.mask;
        long producerIndex = this.producerIndex;
        int counter = 0;
        while (e.keepRunning())
        {
            for (int i = 0; i < 4096; i++)
            {
                final long offset = calcElementOffset(producerIndex, mask);
                if (null != UnsafeRefArrayAccess.lvElement(buffer, offset))
                {// LoadLoad
                    counter = w.idle(counter);
                    continue;
                }
                producerIndex++;
                counter = 0;
                UnsafeRefArrayAccess.soElement(buffer, offset, s.get()); // StoreStore
                soProducerIndex(producerIndex); // ordered store -> atomic and ordered for size()
            }
        }
    }
}
