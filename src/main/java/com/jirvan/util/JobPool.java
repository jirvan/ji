/*

Copyright (c) 2013, Jirvan Pty Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Jirvan Pty Ltd nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.jirvan.util;

import com.jirvan.lang.*;

import java.util.*;

public class JobPool {

    private Map<Long, Job> currentJobs = new HashMap<Long, Job>();
    private int mostRecentJobId = 0;
    private boolean forkLogToStdout;

    public JobPool() {
        this(false);
    }


    public JobPool(boolean forkLogToStdout) {
        this.forkLogToStdout = forkLogToStdout;
    }

    public Job startNewJobForTask(Task task) {
        return startNewJobForTask(false, task);
    }

    public Job startNewJobForTask(boolean throwExceptions, Task task) {
        Job job = new Job(++mostRecentJobId, forkLogToStdout);
        Runnable runnable = new JobRunnable(job, task, throwExceptions);
        new Thread(runnable).start();
        currentJobs.put(job.getJobId(), job);
        return job;
    }

    public Job getJob(long jobId) {
        return currentJobs.get(jobId);
    }

    public static abstract class Task {

        protected LogBuffer outputBuffer;

        public abstract void perform();

        public void output(String string, Object... args) {
            String formattedString = String.format(string, args);
            outputBuffer.append(formattedString);
        }

        public void outputLine(String string, Object... args) {
            if (outputBuffer.length() > 0) {
                outputBuffer.append('\n');
            }
            String formattedString = String.format(string, args);
            outputBuffer.append(formattedString);
        }

    }

    public static class Job {

        private long jobId;
        private Status status;
        private LogBuffer logBuffer;

        public Job(long jobId, boolean forkLogToStdout) {
            this.jobId = jobId;
            this.status = Status.inProgress;
            this.logBuffer = new LogBuffer(forkLogToStdout);
        }

        public static enum Status {

            inProgress,
            finishedSuccessfully,
            finishedWithError;

        }

        public long getJobId() {
            return jobId;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public String getLog() {
            return logBuffer.toString();
        }

    }

    public static class LogBuffer {

        private StringBuffer internalStringBuffer = new StringBuffer();
        private boolean forkLogToStdout;

        public LogBuffer(boolean forkLogToStdout) {
            this.forkLogToStdout = forkLogToStdout;
        }

        public synchronized int length() {
            return internalStringBuffer.length();
        }

        public synchronized LogBuffer append(String str) {
            internalStringBuffer.append(str);
            if (forkLogToStdout) {
                System.out.append(str);
            }
            return this;
        }

        public synchronized LogBuffer append(char c) {
            internalStringBuffer.append(c);
            if (forkLogToStdout) {
                System.out.append(c);
            }
            return this;
        }

        @Override public String toString() {
            return internalStringBuffer.toString();
        }
    }

    private static class JobRunnable implements Runnable {

        private Job job;
        private Task task;
        private boolean throwExceptions;

        public JobRunnable(Job job, Task task, boolean throwExceptions) {
            this.job = job;
            this.task = task;
            this.throwExceptions = throwExceptions;
            task.outputBuffer = job.logBuffer;
        }

        public void run() {
            try {
                task.perform();
                job.setStatus(Job.Status.finishedSuccessfully);
            } catch (Throwable t) {
                job.setStatus(Job.Status.finishedWithError);
                if (throwExceptions) {
                    if (t instanceof RuntimeException) {
                        throw (RuntimeException) t;
                    } else if (t instanceof Error) {
                        throw (Error) t;
                    } else {
                        throw new RuntimeException(t);
                    }
                } else {
                    if (job.logBuffer.length() > 0) {
                        job.logBuffer.append('\n');
                    }
                    if (t instanceof MessageException) {
                        job.logBuffer.append("\nJob finished with ERROR: ");
                        job.logBuffer.append(t.getMessage());
                    } else {
                        job.logBuffer.append("\nJob finished with ERROR\n\n");
                        job.logBuffer.append(Utl.getStackTrace(t));
                    }
                }
            }
        }

    }

}
