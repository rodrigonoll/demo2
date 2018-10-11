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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

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

    // send to an explicit URI
    public void customSendAndReceive() {


        RestTemplate restTemplate =  new RestTemplate();
        //Create a list for the message converters
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        //Add the String Message converter
        messageConverters.add(new StringHttpMessageConverter());
        //Add the message converters to the restTemplate
        restTemplate.setMessageConverters(messageConverters);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<String>(MESSAGE, headers);

        final ResponseEntity<String> response = restTemplate.postForEntity("http://servicosweb.cnpq.br/srvcurriculo/WSCurriculo", request, String.class);

        System.out.println(response);
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