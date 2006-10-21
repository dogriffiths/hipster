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
public final class OPMLWriter implements IdeaWriter {
    /**
     * Date format to use for string conversion.
     */
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "E, dd MMM yyyy hh:mm:ss z");
    private DocumentBuilder db;
    private Writer out;
    
    public OPMLWriter(Writer out) {
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
        try {
            
            db = DocumentBuilderFactory.newInstance(
                    ).newDocumentBuilder();
            
            Document xmlDocument = db.newDocument();
            xmlDocument.setXmlVersion("1.0");
            Element opml = xmlDocument.createElement("opml");
            opml.setAttribute("version", "1.0");
            xmlDocument.appendChild(opml);
            Element head = xmlDocument.createElement("head");
            opml.appendChild(head);
            Element title = xmlDocument.createElement("title");
            head.appendChild(title);
            Element body = xmlDocument.createElement("body");
            opml.appendChild(body);
            appendIdea(xmlDocument, body, idea);
            
            Transformer transformer = null;
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }
            DOMSource source = new DOMSource(xmlDocument);
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
        int i = ideaIndex.get(idea);
        ideaElement.setAttribute("text", idea.getText());
        ideaElement.setAttribute("id", "" + i);
        ideaElement.setAttribute("angle", "" + idea.getAngle());
        if (idea.getStartDate() != null) {
            ideaElement.setAttribute("startDate", DATE_FORMAT.format(idea.getStartDate()));
        }
        if (idea.getEndDate() != null) {
            ideaElement.setAttribute("endDate", DATE_FORMAT.format(idea.getEndDate()));
        }
        String notes = idea.getNotes();
        if ((notes != null) && (notes.length() != 0)) {
            ideaElement.setAttribute("notes", idea.getNotes());
        }
        String description = idea.getDescription();
        if ((description != null) && (description.length() != 0)) {
            ideaElement.setAttribute("description", idea.getDescription());
        }
        String url = idea.getUrl();
        if ((url != null) && (url.length() != 0)) {
            ideaElement.setAttribute("type", "link");
            ideaElement.setAttribute("url", idea.getUrl());
        }
        element.appendChild(ideaElement);
        for (Idea subIdea: idea.getSubIdeas()) {
            appendIdea(document, ideaElement, subIdea);
        }
        for (IdeaLink link: idea.getLinks()) {
            appendLink(document, ideaElement, idea, link);
        }
    }
    
    private void appendLink(Document document, Element element, Idea idea, IdeaLink link) throws IOException {
        Element linkElement = document.createElement("outline");
        int i = ideaIndex.get(idea);
        linkElement.setAttribute("text", link.getTo().getText());
        linkElement.setAttribute("type", "link");
        linkElement.setAttribute("url", "#" + ideaIndex.get(link.getTo()));
        element.appendChild(linkElement);
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
