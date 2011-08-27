/*
 * Copyright 2018 Gerrit Meinders
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.meinders.common.test;

import com.github.meinders.common.*;

class TestWorker extends GenericWorker<TestObject> {
    private int index;
    private boolean slowly;

    /**
     * Constructs a worker that calculates the nth number from the Fibonacci
     * number sequence, where n is the given <code>index</code>.
     */
    public TestWorker(int index) {
        this(index, true);
    }

    public TestWorker(int index, boolean slowly) {
        assert index >= 0 : "Invalid index: index=" + index;
        this.index = index;
        this.slowly = slowly;
    }

    public TestObject construct() {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY-1);

        fireWorkerStarted("Fibonacci calculator started.");
        if (slowly) {
            fireWorkerMessage("I'll be doing this slowly.");
        } else {
            fireWorkerMessage("I'll be doing this quickly.");
        }
        long start = System.nanoTime();
        long uMinusTwo = 0;
        long uMinusOne = 1;
        boolean moduloWarning = true;
        for (int i=0; i<index; i++) {
            // update progress (but no more than 1000 times)
            if (index / 1000 == 0 || i % (index / 1000) == 0) {
                fireWorkerProgress(i, index, "F[" + i + "] = " + uMinusTwo);
            }
            if (moduloWarning && uMinusTwo > uMinusOne) {
                fireWorkerError("64-bit integer size exceeded");
                fireWorkerWarning("The following values are given modulo 2^64.");
                moduloWarning = false;
            }

            if (slowly) {
                try {
                    Thread.currentThread().sleep(50);
                } catch (InterruptedException e) {
                    fireWorkerInterrupted();
                    return null;
                }
            } else if (Thread.interrupted()) {
                return null;
            }

            long uZero = uMinusTwo + uMinusOne;
            uMinusTwo = uMinusOne;
            uMinusOne = uZero;
        }
        long end = System.nanoTime();
        return new TestObject(uMinusTwo, start, end);
    }

    public void finished() {
        if (get() != null) {
            fireWorkerMessage("Fibonacci number " + index + " is " + get().getValue() + ".");
            fireWorkerMessage("This was calculated in " + get().getDuration() + " seconds.");
            fireWorkerFinished("Calculations completed.");
        }
    }
}

