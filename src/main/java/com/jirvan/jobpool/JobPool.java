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

package com.jirvan.jobpool;

import com.jirvan.io.OutputWriter;
import com.jirvan.lang.MessageException;
import com.jirvan.util.Utl;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;

import java.util.HashMap;
import java.util.Map;

import static com.jirvan.util.Assertions.*;

public class JobPool {

    public static final JobPool commonJobPool = new JobPool();

    private Map<Long, Job> currentJobs = new HashMap<>();
    private int mostRecentJobId = 0;

    public static Job start(OutputWriter output, Task task) {
        return commonJobPool.startNewJobForTask(false, output, null, null, task);
    }

    public static Job start(OutputWriter output, Logger logger, Task task) {
        return commonJobPool.startNewJobForTask(false, output, logger, null, task);
    }

    private Job startNewJobForTask(boolean throwExceptions, OutputWriter output, Logger logger, Level level, Task task) {
        Job job = new Job(++mostRecentJobId, output, logger, level);
        Runnable runnable = new JobRunnable(job, task, throwExceptions);
        new Thread(runnable).start();
        currentJobs.put(job.getJobId(), job);
        return job;
    }

    public Job getJob(long jobId) {
        return currentJobs.get(jobId);
    }

    public static abstract class Task {

        public abstract void perform(OutputWriter output);

    }

    public static class Job {

        private long jobId;
        private Status status;
        private OutputWriter output;

        public Job(long jobId, OutputWriter output, Logger logger, Level level) {
            this.jobId = jobId;
            this.output = output;
            this.status = Status.inProgress;
            assertNotNull(logger, "logger must be provided");

            if (logger != null) {
                WriterAppender writerAppender = new WriterAppender(new EnhancedPatternLayout("%m\n"), output.getWriterProxy());
                if (level != null) writerAppender.setThreshold(level);
                logger.addAppender(writerAppender);
            }

        }

        public static enum Status {
            inProgress,
            finishedSuccessfully,
            finishedWithError
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

    }

    private static class JobRunnable implements Runnable {

        private Job job;
        private Task task;
        private boolean throwExceptions;

        public JobRunnable(Job job, Task task, boolean throwExceptions) {
            this.job = job;
            this.task = task;
            this.throwExceptions = throwExceptions;
        }

        public void run() {
            try {
                task.perform(job.output);
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
                    if (t instanceof MessageException) {
                        System.err.printf("\nJob finished with ERROR: %s\n\n", t.getMessage());
                        job.output.printf("\nJob finished with ERROR: %s\n\n", t.getMessage());
                    } else {
                        System.err.printf("\nJob finished with ERROR:\n\n %s\n\n", Utl.getStackTrace(t));
                        job.output.printf("\nJob finished with ERROR:\n\n %s\n\n", Utl.getStackTrace(t));
                    }
                }
            }
        }

    }

}
