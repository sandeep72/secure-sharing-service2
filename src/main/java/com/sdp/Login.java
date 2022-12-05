package com.sdp;


import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Entity.Group;
import Entity.User;
import at.favre.lib.crypto.bcrypt.BCrypt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Gson gson = new Gson();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);	
		
		setAccessControlHeaders(request, response);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String employeeJsonString = null;
		JsonObject jsonObject = null;
		User user = null;
		Connection con = null;
		Gson gson = new Gson();   
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://34.70.183.176:3306/securess", "root", "Mysql@123");
			
			preparedStatement = con.prepareStatement("select * from user where email ='"+request.getParameter("username")+
					"' and password = '"+request.getParameter("password")+"';");
			
			resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next() ) {
				if(resultSet.getInt("active") == 2 ) {
					
					user = new User(
							resultSet.getLong("user_id"),
							resultSet.getString("name"),
							resultSet.getString("email"),
							resultSet.getString("password"),
							resultSet.getInt("active"),
							resultSet.getString("type")
							);
					jsonObject = new JsonObject();
					jsonObject.add("USER", gson.toJsonTree(user).getAsJsonObject());
					jsonObject.addProperty("SUCCESS", "TRUE");
					jsonObject.addProperty("TOKEN", createJWT(user));
					out.print(jsonObject.toString());
					out.flush();
				}
				else if(resultSet.getInt("active") == 0) {
					jsonObject = new JsonObject();
					jsonObject.addProperty("SUCCESS", "FALSE");
					jsonObject.addProperty("MESSAGE", "Admin has rejected your acceptance");
					out.print(jsonObject.toString());
					return;	
				}
				else {// user is not yet approved by admin
					jsonObject = new JsonObject();
					jsonObject.addProperty("SUCCESS", "FALSE");
					jsonObject.addProperty("MESSAGE", "Admin approval is pending");
					out.print(jsonObject.toString());
					return;	
				}
			}else {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "FALSE");
				jsonObject.addProperty("MESSAGE", "email or password incorrect");
				jsonObject.addProperty("username", request.getParameter("username"));
				out.print(jsonObject.toString());
				return;	
			}
			
		} catch (Exception e) {
			jsonObject = new JsonObject();
			jsonObject.addProperty("SUCCESS", "FALSE");
			jsonObject.addProperty("MESSAGE", e.toString());
			out.print(jsonObject.toString());
		} finally {
			out.close();
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private void setAccessControlHeaders(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-control-allows-headers", "Content-type");
        response.addHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST, DELETE");
	  }
	
	public String createJWT(User user) {
		  
	    //The JWT signature algorithm we will be using to sign the token
	    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	    long nowMillis = System.currentTimeMillis();
	    Date now = new Date(nowMillis);

	    //We will sign our JWT with our ApiKey secret
	    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("somerandomtext");
	    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

	    //Let's set the JWT Claims
	    JwtBuilder builder = Jwts.builder().setIssuedAt(now)
	            .claim("user_id",String.valueOf(user.getUser_id()))
	            .claim("password",String.valueOf(user.getPassword()))
	            .claim("type",String.valueOf(user.getType()))
	            .claim("name",String.valueOf(user.getName()))
	            .claim("email",String.valueOf(user.getEmail()))
	            .setIssuer("http://secure-sharing-service.com/")
	            .signWith(signatureAlgorithm, signingKey);
	  
	    //if it has been specified, let's add the expiration
	  
	    //Builds the JWT and serializes it to a compact, URL-safe string
	    return builder.compact();
//	    https://developer.okta.com/blog/2018/10/31/jwts-with-java
	}

	public boolean check(String string) {
		if(string.contains("select") || string.contains("delete") || string.contains("update") || string.contains("insert") || string.contains("create")) {
			return false;
		}
		if(string.length()>100)
			return false;
		return true;
	}
}
