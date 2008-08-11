/*
 * WikiWriter.java
 *
 * Created on September 15, 2006, 7:26 AM
 *
 * Copyright (c) 2006, David Griffiths
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this Vector of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this Vector of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of David Griffiths nor the names of his contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package dg.hipster.io;

import dg.hipster.model.IdeaDocument;
import dg.hipster.model.Idea;
import dg.hipster.model.IdeaLink;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author davidg
 */
public final class WikiWriter implements IdeaWriter {
    /**
     * Date format to use for string conversion.
     */
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "E, dd MMM yyyy hh:mm:ss z");
    private Writer out;

    public WikiWriter(Writer out) {
        this.out = out;
    }

    public void write(IdeaDocument document) throws IOException {
        save(document);
        out.flush();
        out.close();
    }

    private void save(final IdeaDocument document) throws IOException {
        Idea idea = document.getIdea();
        index(idea);
        appendIdea(out, 0, idea);
    }

    private void appendIdea(Writer out, int indent, Idea idea) throws IOException {
        int i = ideaIndex.get(idea);
        String title = (blank(idea.getDescription()) ? idea.getText() : idea.getDescription());
        String url = idea.getUrl();
        if ((url != null) && (url.length() != 0)) {
            title = "[" + url + " " + title + "]";
        }
        String heading = "";
        if ((indent < 3) && (title.length() < 40)) {
            heading = "====================".substring(0, indent + 2);
            out.write(heading + title + heading + "\n\n");
        } else {
            heading = "***********".substring(0, Math.max(indent - 2, 1));
            out.write(heading + title + "\n");
        }
        if (!blank(idea.getNotes())) {
            out.write(idea.getNotes() + "\n\n");
        }
        for (Idea subIdea: idea.getSubIdeas()) {
            appendIdea(out, indent + 1, subIdea);
        }
        if (idea.getLinks().size() > 0) {
            out.write("See also:" + "\n\n");
        }
        for (IdeaLink link: idea.getLinks()) {
            out.write("* [[#" + link.getTo().getText() + "]]\n");
        }
    }
    
    private static boolean blank(String s) {
        return (s == null) || (s.length() == 0);
    }

    private Map<Idea, Integer> ideaIndex;
    int count;

    private void index(Idea idea) {
        ideaIndex = new HashMap<Idea, Integer>();
        count = 0;
        indexWithSubs(idea);
    }

    private void indexWithSubs(Idea idea) {
        ideaIndex.put(idea, count++);
        for (Idea subIdea : idea.getSubIdeas()) {
            indexWithSubs(subIdea);
        }
    }
}
