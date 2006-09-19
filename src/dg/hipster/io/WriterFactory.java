/*
 * WriterFactory.java
 *
 * Created on September 15, 2006, 7:30 AM
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
 * * Neither the name of the David Griffiths nor the names of his contributors
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

import dg.hipster.model.Idea;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author davidg
 */
public final class WriterFactory {
    private static WriterFactory instance = new WriterFactory();

    /** Creates a new instance of ReaderFactory */
    private WriterFactory() {
    }

    public static WriterFactory getInstance() {
        return instance;
    }

    public void write(File f, Idea idea) throws ReaderException {
        try {
            IdeaWriter writer = null;
            if (f.getName().toLowerCase().endsWith(".opml")) {
                writer = new OPMLWriter(new FileWriter(f));
            }
            if (writer != null) {
                try {
                    writer.write(idea);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new ReaderException("Unknown file type for " + f, null);
            }
        } catch (Exception e) {
            throw new ReaderException("Unable to write file " + f, e);
        }
    }
}
