package com.dr3amr2.jxtable;/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class FilterParser extends DefaultHandler {
    static final Logger logger = Logger.getLogger(FilterParser.class.getName());

    private static final String[] CATEGORIES_IN = {
            "test"
    };

    private static final String[] CATEGORIES_OUT = {
            "Test"
    };


    private String tempVal;

    //to maintain context
    private FilterDataBean tempFilterDataBean;
        
    private int count = 0;
    
    public int getCount() {
        return count;
    }

    public void parseDocument(URL oscarURL) {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            InputStream is = new BufferedInputStream(oscarURL.openStream());
            sp.parse(is, this);
            is.close();

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    //Event Handlers
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        for (int i = 0; i < CATEGORIES_IN.length; i++) {
            if (qName.equalsIgnoreCase(CATEGORIES_IN[i])) {
                tempFilterDataBean = new FilterDataBean(CATEGORIES_OUT[i]);
                return;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("name")) {
            tempFilterDataBean.setName(tempVal);
        } else if (qName.equalsIgnoreCase("description")) {
            tempFilterDataBean.setDescription(tempVal);
        } else if (qName.equalsIgnoreCase("user")) {
            tempFilterDataBean.setUser(tempVal);
        } else if (qName.equalsIgnoreCase("filter")) {
            tempFilterDataBean.setFilter(tempVal);
        } else {
            // find category
            for (String category : CATEGORIES_IN) {
                if (qName.equalsIgnoreCase(category)) {
                    //add it to the list
                    count++;
                    addCandidate(tempFilterDataBean);
                    break;
                }
            }
        }
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
        logger.log(Level.SEVERE, "error parsing ibs filter data ", ex);
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        logger.log(Level.SEVERE, "fatal error parsing ibs filter data ", ex);
    }

    @Override
    public void warning(SAXParseException ex) {
        logger.log(Level.WARNING, "warning occurred while parsing ibs filter data ", ex);
    }

    @Override
    public void endDocument() throws SAXException {
        logger.log(Level.FINER, "parsed to end of ibs filter data.");
    }

    protected abstract void addCandidate(FilterDataBean candidate);
}

