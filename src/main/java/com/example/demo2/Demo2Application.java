package com.example.demo2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Demo2Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo2Application.class, args);
    }
}


@RestController
class HomeController{

    private final String url = "http://servicosweb.cnpq.br/srvcurriculo/WSCurriculo";

    private static final String MESSAGE ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.servico.repositorio.cnpq.br/\">\n" +
            "   <soapenv:Header/>\n" +
            "   <soapenv:Body>\n" +
            "      <ws:getCurriculoCompactado>\n" +
            "         <id>0917360107022796</id>\n" +
            "      </ws:getCurriculoCompactado>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

    private final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    public void setDefaultUri(String defaultUri) {
        webServiceTemplate.setDefaultUri(defaultUri);
    }

    public SOAPEnvelope createSoapEnvelope() throws SOAPException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope envelope = null;
        try {
            envelope = soapPart.getEnvelope();
            SOAPBody body = envelope.getBody();

            QName bodyName = new QName("http://ws.servico.repositorio.cnpq.br/","xmlns","ws");
            SOAPBodyElement bodyElement = body.addBodyElement(bodyName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.newDocument();

            // Marshal the Object to a Document
            JAXBContext jaxbContext = JAXBContext.newInstance(String.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            JAXBElement elem =new JAXBElement(
                    new QName("getCurriculoCompactado", "ws"), String.class, "<id>0917360107022796</id>");
            marshaller.marshal(elem, document);


            body.addDocument(document);
            //body.setValue(MESSAGE);
            return envelope;
        }catch (Exception e){
            e.printStackTrace();
        }
        return envelope;
    }


    public void customSendAndReceive() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("user", authentication.getName());
//        map.add("password", authentication.getCredentials().toString());

        HttpEntity<String> request = new HttpEntity(MESSAGE);

        String remoteAuth = restTemplate.postForObject(url, request ,String.class);

//        JsonElement jelement = new JsonParser().parse(remoteAuth);
//        return jelement.getAsJsonObject().get("result").getAsString();
    }

    // send to an explicit URI
    public void customSendAndReceive2() {


        MultiValueMap headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT,"text/xml;charset=UTF-8");
        headers.set(HttpHeaders.CONTENT_TYPE,"text/xml;charset=UTF-8");
        headers.set(HttpHeaders.ACCEPT_ENCODING,"gzip,deflate");
        headers.set("SOAPAction", "SOAPAction:");
        HttpEntity<SOAPEnvelope> soapEntity = null;
        try {
            soapEntity = new HttpEntity<>(createSoapEnvelope(),headers);
        } catch (SOAPException e) {
            e.printStackTrace();
        }

        RestTemplate restTemplate =  new RestTemplate();
        //Create a list for the message converters
//        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
//        //Add the String Message converter
//        messageConverters.add(new StringHttpMessageConverter());
//        //Add the message converters to the restTemplate
//        restTemplate.setMessageConverters(messageConverters);


        //HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_XML);
        //HttpEntity<String> request = new HttpEntity<String>(MESSAGE, headers);

        ResponseEntity<SOAPEnvelope> future=
                restTemplate.postForEntity("http://servicosweb.cnpq.br/srvcurriculo/WSCurriculo"
                        ,soapEntity, SOAPEnvelope.class);


        //final ResponseEntity<String> response = restTemplate.postForEntity("http://servicosweb.cnpq.br/srvcurriculo/WSCurriculo", request, String.class);

//        System.out.println(future);
//
//
//        StreamSource source = new StreamSource(new StringReader(MESSAGE));
//        StreamResult result = new StreamResult(System.out);
//        webServiceTemplate.sendSourceAndReceiveToResult("http://servicosweb.cnpq.br/srvcurriculo/WSCurriculo",
//                source, result);
    }


    @GetMapping("")
    public void home(){
        customSendAndReceive();
    }
}