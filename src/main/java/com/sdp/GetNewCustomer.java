package com.sdp;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

/**
 * Servlet implementation class GetNewCustomer
 */
@WebServlet("/GetNewCustomer")
public class GetNewCustomer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetNewCustomer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		setAccessControlHeaders(request, response);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Connection con = null;
		JsonObject jsonObject = null;
		Gson gson = new Gson();
		
		String user_id="0";
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://34.70.183.176:3306/securess", "root", "Mysql@123");
			String token = request.getParameter("token");
			try {
				Claims claims = Jwts.parser().setSigningKey("somerandomtext").parseClaimsJws(token).getBody();
				String email = claims.get("email", String.class);
				String password = claims.get("password", String.class);
				user_id = claims.get("user_id", String.class);
				preparedStatement = con.prepareStatement("select * from user where type='admin' and email ='"+email+
						"' and password = '"+password+"';");
				resultSet = preparedStatement.executeQuery();
				if(!resultSet.next() ) {
					jsonObject = new JsonObject();
					jsonObject.addProperty("SUCCESS", "FALSE");
					jsonObject.addProperty("MESSAGE", "Authentication failed");
					out.print(jsonObject.toString());
					return;	
				}
			}
			catch (SignatureException e) {  
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "FALSE");
				jsonObject.addProperty("MESSAGE", "Authentication failed");
				out.print(jsonObject.toString());
			}
			
            jsonObject = new JsonObject();
			jsonObject.addProperty("SUCCESS", "TRUE");
			preparedStatement = con.prepareStatement("select * from user where active = 1;");
			ResultSet userResultSet = preparedStatement.executeQuery();
			ArrayList<User> userList = new ArrayList<>();
			while(userResultSet.next()) {
				userList.add(new User(
						userResultSet.getLong("user_id"),
						userResultSet.getString("name"),
						userResultSet.getString("email"),
						"DUMMY",
						userResultSet.getInt("active"),
						userResultSet.getString("type")
						));
			}
			JsonArray jarray = gson.toJsonTree(userList).getAsJsonArray();
			jsonObject.add("USERLIST",jarray);
			out.print(jsonObject.toString());
			return;
            
			
			
		}catch(Exception e) {
			jsonObject = new JsonObject();
			jsonObject.addProperty("SUCCESS", "FALSE");
			jsonObject.addProperty("MESSAGE", e.toString());
			out.print(jsonObject.toString());
		}finally {
			out.close();
		}
		
	}

	private void setAccessControlHeaders(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-control-allows-headers", "Content-type");
        response.addHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST, DELETE");
	  }
}
