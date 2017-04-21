package com.javacodegeeks.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
 
@Controller
@RequestMapping("/")
public class ApplicationController {
 
	@RequestMapping(value="/Test", method = RequestMethod.GET)
	public String welcome(ModelMap model) { 
		model.addAttribute("msgArgument", "Aplicacao Web Rotas: Sucesso!");
		return "index";
	}
 
	@RequestMapping(value="/direc/{origin}", method = RequestMethod.GET)
	public String welcomeName(@PathVariable String origin, ModelMap model) {
		model.addAttribute("msgArgument", origin);
		return "index";
	}
 
	/*
	 *   How to call this:  
     *   http://localhost:8080/RotasWebApp/directions?formato=xml&origem=3%20sheldon%20st,%20billerica,%20ma&destino=7%20new%20england%20executive%20park,%20burlington,%20ma	 
	 */
	@RequestMapping(value="/directions", method = RequestMethod.GET)
	public @ResponseBody String getDirections(HttpServletRequest request, HttpServletResponse response, @RequestParam("formato") String format, @RequestParam("origem") String origin, @RequestParam("destino") String dest, ModelMap model) {
		RotasSet rotasSet = getRoutes(origin, dest);;
		String result = null;
		StringWriter writer = new StringWriter();
		
		if (format.toLowerCase().equals("json")) {
			Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Rota.class, new RotaJsonSerializer()).create();
			result = gson.toJson(rotasSet); 
		}
		else
		if (format.toLowerCase().equals("xml") ) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(RotasSet.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");

				// output pretty printed
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				jaxbMarshaller.marshal(rotasSet, writer);
				result = writer.toString();

				// print results on the standard output
				//jaxbMarshaller.marshal(RotasSet, System.out);
			} 
			catch (JAXBException e) {
				e.printStackTrace();
			}
		}
				
		// only solution to the accent problem (for json and for xml)
		response.setCharacterEncoding("ISO-8859-1"); 
		return result; 
	}
 
	private String genJsonRoutes(String origin, String dest) throws IOException {
		origin = replaceBlanks(origin);
		dest = replaceBlanks(dest);
		String url = "http://maps.googleapis.com/maps/api/directions/json?origin=";
		url = url + origin;
		url = url + "&destination=";
		url = url + dest;
		url = url + "&mode=driving&language=pt-BR&alternatives=true&sensor=false";
		 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.connect();
 
		// optional default is GET
		//con.setRequestMethod("GET");
 		//int responseCode = con.getResponseCode();
		//System.out.println("\nURL chamada: " + url);
 
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		in.close();

		//print result
		String resultXml = response.toString();
		return resultXml;
	}
	
	private RotasSet getRoutes(String origin, String dest) {
		JsonArray jRoutes = null;
	    JsonArray jLegs = null;
	    String jsonLine = "";
	    
		try {
			jsonLine = genJsonRoutes(origin, dest);
		} catch (IOException e1) {
			System.out.println("Error calling Directions API: "+e1.getMessage());
		}
	    
	    JsonElement jElement = new JsonParser().parse(jsonLine);
	    JsonObject  jObject = jElement.getAsJsonObject();
	    RotasSet rotasSet = new RotasSet();
	    
	    try {           
	        jRoutes = jObject.getAsJsonArray("routes");
	        System.out.println("Number of routes: "+jRoutes.size());
	        Rota[] rotas = new Rota[jRoutes.size()];
	        
	        /** Traversing all routes */
	        for(int i=0;i<jRoutes.size();i++){  
	        	JsonObject jRouteObject = (JsonObject)jRoutes.get(i);
            	String name = jRouteObject.get("summary").getAsString();
            	Rota rota = new Rota();
            	rota.setName(name);
            	//rota.setO
	        	jLegs = jRouteObject.getAsJsonArray("legs");

	            /** Traversing all legs */
	            for(int j=0;j<jLegs.size();j++){
		        	JsonObject jLegObject = (JsonObject)jLegs.get(j);
		        	JsonObject jDistanceObject = jLegObject.get("distance").getAsJsonObject();
                	String distance_text = jDistanceObject.get("text").getAsString();
    	        	String orig = jLegObject.get("start_address").getAsString();
                	String desti = jLegObject.get("end_address").getAsString();
                	rota.setOrigin(orig);
                	rota.setDest(desti);
                	//System.out.println("distance text: "+distance_text);
                	rota.setDistance(distance_text);
	            }
	            rotas[i] = rota;
	        }
		    rotasSet.setRotas(rotas);
	        String status = jObject.get("status").getAsString();
	        rotasSet.setStatus(status);
	    } 
	    catch (JsonParseException e) {         
	        System.out.println("Json Parsing error: "+e.getMessage());
	    }
	
	    return rotasSet;
	}
	
	public String replaceBlanks(String source) {
		String res = source.replace(" ", "%20");
		return res;
	}
	
}