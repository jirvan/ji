package com.jirvan.util;

import com.google.common.base.Strings;
import com.jirvan.io.OutputWriter;

public class ProgressCounter {

    public OutputWriter output;
    public int total;
    public int progress = 0;
    public int lineCounter = 0;
    public int maxCountsPerLine;
    public String indent;
    public String lineFormat;

    public ProgressCounter(OutputWriter output,
                           int total) {
        this(output, total, 3, 80);
    }

    public ProgressCounter(OutputWriter output,
                           int total,
                           int indentLength,
                           int lineLength) {
        this.output = output;
        this.total = total;
        this.indent = Strings.padEnd("", indentLength, ' ');
        this.lineFormat = " %d%%\n" + indent;
        this.maxCountsPerLine = lineLength - indentLength - 4;
    }

    public void incrementByOne(char displayChar) {
        if (progress == 0) {
            output.printf(indent);
        }
        progress++;
        if (++lineCounter > maxCountsPerLine) {
            output.printf(lineFormat, (progress * 100) / total);
            lineCounter = 1;
        }
        if (progress < total) {
            output.printf("%c", displayChar);
        }
    }

}
