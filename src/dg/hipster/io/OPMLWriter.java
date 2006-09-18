/*
 * OPMLWriter.java
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
import java.io.IOException;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author davidg
 */
public class OPMLWriter implements IdeaWriter {
    private DocumentBuilder db;
    private Writer out;
    
    public OPMLWriter(Writer out) {
        this.out = out;
    }
    
    public void write(Idea idea) throws IOException {
        save(idea);
        out.flush();
        out.close();
    }
    
    private void save(Idea idea) throws IOException {
        try {
            
            db = DocumentBuilderFactory.newInstance(
                    ).newDocumentBuilder();
            
            Document document = db.newDocument();
            document.setXmlVersion("1.0");
            Element opml = document.createElement("opml");
            opml.setAttribute("version", "1.0");
            document.appendChild(opml);
            Element head = document.createElement("head");
            opml.appendChild(head);
            Element title = document.createElement("title");
            head.appendChild(title);
            Element body = document.createElement("body");
            opml.appendChild(body);
            appendIdea(document, body, idea);
            
            Transformer transformer = null;
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(out);
            try{
                transformer.transform(source,result);
            } catch (TransformerException e){
                e.printStackTrace();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void appendIdea(Document document, Element element, Idea idea) throws IOException {
        Element ideaElement = document.createElement("outline");
        ideaElement.setAttribute("text", idea.getText());
        element.appendChild(ideaElement);
        for (Idea subIdea: idea.getSubIdeas()) {
            appendIdea(document, ideaElement, subIdea);
        }
    }
}
