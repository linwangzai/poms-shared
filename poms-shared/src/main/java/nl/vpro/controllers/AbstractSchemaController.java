package nl.vpro.controllers;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * @author Michiel Meeuwissen
 * @since 3.4
 */
@Slf4j
public abstract class AbstractSchemaController {



    protected abstract File getFileForNamespace(String namespace);

    protected void el(XMLStreamWriter w, String name, String chars) throws XMLStreamException {
        w.writeStartElement(name);
        w.writeCharacters(chars);
        w.writeEndElement();
    }

    protected void h2(XMLStreamWriter w, String chars) throws XMLStreamException {
        el(w, "h2", chars);
    }

    protected void a(XMLStreamWriter w, String href, String chars) throws XMLStreamException {
        w.writeStartElement("a");
        w.writeAttribute("href", href);
        w.writeCharacters(chars);
        w.writeEndElement();
    }

    protected void li_a(XMLStreamWriter w, String href, String chars) throws XMLStreamException {
        w.writeStartElement("li");
        a(w, href, chars);
        w.writeEndElement();
    }

    protected void getXSD(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String namespace) throws JAXBException, IOException {
        File file = getFileForNamespace(namespace);
        serveXml(file, request, response);
    }


    protected void serveXml(File file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        Date fileDate = DateUtils.round(new Date(file.lastModified()), Calendar.SECOND);
        if (ifModifiedSince > fileDate.getTime()) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            response.setDateHeader("Last-Modified", fileDate.getTime());
        } else {
            response.setContentType("application/xml");
            response.setDateHeader("Last-Modified", fileDate.getTime());
            IOUtils.copy(new FileInputStream(file), response.getOutputStream());
        }
    }


}
