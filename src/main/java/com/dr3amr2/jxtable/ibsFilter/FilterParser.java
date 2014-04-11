package com.dr3amr2.jxtable.ibsFilter;

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
    private IbsContact tempIbsContact;
        
    private int count = 0;
    
    public int getCount() {
        return count;
    }

    public void parseDocument(URL ibsFilterURL) {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            InputStream is = new BufferedInputStream(ibsFilterURL.openStream());
            sp.parse(is, this);
            is.close();

        } catch (SAXException | ParserConfigurationException | IOException se) {
            se.printStackTrace();
        }
    }

    //Event Handlers
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        for (int i = 0; i < CATEGORIES_IN.length; i++) {
            if (qName.equalsIgnoreCase(CATEGORIES_IN[i])) {
                tempIbsContact = new IbsContact(CATEGORIES_OUT[i]);
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
            tempIbsContact.setName(tempVal);
        } else if (qName.equalsIgnoreCase("description")) {
            tempIbsContact.setDescription(tempVal);
        } else if (qName.equalsIgnoreCase("user")) {
            tempIbsContact.setUser(tempVal);
        } else if (qName.equalsIgnoreCase("filter")) {
            tempIbsContact.setFilter(tempVal);
        } else {
            // find category
            for (String category : CATEGORIES_IN) {
                if (qName.equalsIgnoreCase(category)) {
                    //add it to the list
                    count++;
                    addCandidate(tempIbsContact);
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

    protected abstract void addCandidate(IbsContact candidate);
}

