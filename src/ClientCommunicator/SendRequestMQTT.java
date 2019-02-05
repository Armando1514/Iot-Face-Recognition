package ClientCommunicator;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;



/**
 * Servlet implementation class SendRequestMQTT
 */
@WebServlet("/SendRequestMQTT")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB 
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*50)   // 50MB
public class SendRequestMQTT extends HttpServlet implements MqttCallback {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendRequestMQTT() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	  private static String getBoundary() {
	        StringBuilder sb = new StringBuilder();
	        Random random = new Random();
	        for(int i = 0; i < 32; ++i) {
	            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
	        }
	        return sb.toString();
	    }
	    private static String encode(String value) throws Exception{
	        return URLEncoder.encode(value, "UTF-8");
	    }
	    
	    public static byte[] getBytesFromFile(File f) {
	        if (f == null) {
	            return null;
	        }
	        try {
	            FileInputStream stream = new FileInputStream(f);
	            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
	            byte[] b = new byte[1000];
	            int n;
	            while ((n = stream.read(b)) != -1)
	                out.write(b, 0, n);
	            stream.close();
	            out.close();
	            return out.toByteArray();
	        } catch (IOException e) {
	        }
	        return null;
	    }
	
	    private String extractFileName(Part part) {
	        String contentDisp = part.getHeader("content-disposition");
	        String[] items = contentDisp.split(";");
	        for (String s : items) {
	            if (s.trim().startsWith("filename")) {
	                return s.substring(s.indexOf("=") + 2, s.length()-1);
	            }
	        }
	        return "";
	    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String appPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
        String savePath = appPath + File.separator + "IoT";
         
        // creates the save directory if it does not exists
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdir();
        }
        String fileName=null;
        for (Part part : request.getParts()) {
            fileName = extractFileName(part);
            // refines the fileName in case it is an absolute path
            fileName = new File(fileName).getName();
            part.write(savePath + File.separator + fileName);
        }
System.out.println(savePath + File.separator +fileName);
		File file=new File(savePath + File.separator +fileName);
		
		    byte[] buff = getBytesFromFile(file);
			String url = "https://api-us.faceplusplus.com/facepp/v3/detect";
	        HashMap<String, String> map = new HashMap<>();
	        HashMap<String, byte[]> byteMap = new HashMap<>();
	        map.put("api_key", "bmuiHUCB12RO8lQBcFDNSCQGGuZiHt8R");
	        map.put("api_secret", "LdIRsvtxBo9rVVPmqQZ4lh9d3Rod6MLc");
	        map.put("return_attributes", "gender,age,smiling,headpose,facequality,blur,eyestatus,emotion,ethnicity,beauty,mouthstatus,eyegaze,skinstatus");
	
	        byteMap.put("image_file", buff);


			MqttClient client;
			
			
	        try{
	            byte[] bacd = post(url, map, byteMap);
	            String str = new String(bacd);
	            System.out.println(str);
	            response.sendRedirect("analysis.html");
	            client = new MqttClient("tcp://192.168.137.94:1883", "Sending");
	            client.connect();
	            client.setCallback(this);
	            client.subscribe("iot/analysis/face");
	            MqttMessage message = new MqttMessage();
	            message.setPayload(bacd);
	            client.publish("iot/analysis/face", message);
	            client.disconnect();
	            
	        }catch (Exception e) {
	        	e.printStackTrace();
			}
	     
		}
		
		private final static int CONNECT_TIME_OUT = 30000;
	    private final static int READ_OUT_TIME = 50000;
	    private static String boundaryString = getBoundary();
	    protected static byte[] post(String url, HashMap<String, String> map, HashMap<String, byte[]> fileMap) throws Exception {
	        HttpURLConnection conne;
	        URL url1 = new URL(url);
	        conne = (HttpURLConnection) url1.openConnection();
	        conne.setDoOutput(true);
	        conne.setUseCaches(false);
	        conne.setRequestMethod("POST");
	        conne.setConnectTimeout(CONNECT_TIME_OUT);
	        conne.setReadTimeout(READ_OUT_TIME);
	        conne.setRequestProperty("accept", "*/*");
	        conne.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
	        conne.setRequestProperty("connection", "Keep-Alive");
	        conne.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
	        DataOutputStream obos = new DataOutputStream(conne.getOutputStream());
	        Iterator iter = map.entrySet().iterator();
	        while(iter.hasNext()){
	            Map.Entry<String, String> entry = (Map.Entry) iter.next();
	            String key = entry.getKey();
	            String value = entry.getValue();
	            obos.writeBytes("--" + boundaryString + "\r\n");
	            obos.writeBytes("Content-Disposition: form-data; name=\"" + key
	                    + "\"\r\n");
	            obos.writeBytes("\r\n");
	            obos.writeBytes(value + "\r\n");
	        }
	        if(fileMap != null && fileMap.size() > 0){
	            Iterator fileIter = fileMap.entrySet().iterator();
	            while(fileIter.hasNext()){
	                Map.Entry<String, byte[]> fileEntry = (Map.Entry<String, byte[]>) fileIter.next();
	                obos.writeBytes("--" + boundaryString + "\r\n");
	                obos.writeBytes("Content-Disposition: form-data; name=\"" + fileEntry.getKey()
	                        + "\"; filename=\"" + encode(" ") + "\"\r\n");
	                obos.writeBytes("\r\n");
	                obos.write(fileEntry.getValue());
	                obos.writeBytes("\r\n");
	            }
	        }
	        obos.writeBytes("--" + boundaryString + "--" + "\r\n");
	        obos.writeBytes("\r\n");
	        obos.flush();
	        obos.close();
	        InputStream ins = null;
	        int code = conne.getResponseCode();
	        try{
	            if(code == 200){
	                ins = conne.getInputStream();
	            }else{
	                ins = conne.getErrorStream();
	            }
	        }catch (SSLException e){
	            e.printStackTrace();
	            return new byte[0];
	        }
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        byte[] buff = new byte[4096];
	        int len;
	        while((len = ins.read(buff)) != -1){
	            baos.write(buff, 0, len);
	         
	        }
	        byte[] bytes = baos.toByteArray();
	        ins.close();
	        return bytes;
	    }

		@Override
		public void connectionLost(Throwable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			 System.out.println(topic+", "+message);   
			
		}
	  
	    
	  
	}



