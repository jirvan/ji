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

import java.util.*;

public class StringUtils {

    public static String commaList(Collection<String> strings) {
        return join(strings, ',');
    }

    public static String commaEllipsesList(Collection<String> strings, int maxItems) {
        if (strings.size() <= maxItems) {
            return join(strings, ',');
        } else {
            List<String> list = new ArrayList<String>();
            Iterator<String> iterator = strings.iterator();
            int count = 0;
            while (iterator.hasNext() && count < maxItems) {
                count++;
                list.add(iterator.next());
            }
            list.add("...");
            return join(list, ',');
        }
    }

    public static String commaEllipsesListToString(Collection strings, int maxItems) {
        List<String> list = new ArrayList<String>();
        Iterator iterator = strings.iterator();
        int count = 0;
        while (iterator.hasNext() && count < maxItems) {
            count++;
            list.add(iterator.next().toString());
        }
        if (strings.size() > maxItems) {
            list.add("...");
        }
        return join(list, ',');
    }

    public static String commaSpaceList(Collection<String> strings) {
        return join(strings, ", ");
    }

    public static String join(Collection<String> strings, String joinString) {
        StringBuffer buf = new StringBuffer();
        for (String string : strings) {
            if (buf.length() != 0) buf.append(joinString);
            buf.append(string);
        }
        return buf.toString();
    }

    public static String join(Collection<String> strings, char joinChar) {
        StringBuffer buf = new StringBuffer();
        for (String string : strings) {
            if (buf.length() != 0) buf.append(joinChar);
            buf.append(string);
        }
        return buf.toString();
    }

}
