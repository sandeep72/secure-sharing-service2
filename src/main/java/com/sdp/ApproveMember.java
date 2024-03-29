package com.sdp;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

/**
 * Servlet implementation class ApproveMember
 */
@WebServlet("/ApproveMember")
public class ApproveMember extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApproveMember() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		setAccessControlHeaders(request, response);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Gson gson = new Gson();
		Connection con=null;
		JsonObject jsonObject = null;
		
		
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://34.70.183.176:3306/securess", "root", "Mysql@123");
			
			String token = request.getParameter("token");
			try {
				Claims claims = Jwts.parser().setSigningKey("somerandomtext").parseClaimsJws(token).getBody();
				String email = claims.get("email", String.class);
				String password = claims.get("password", String.class);
//				user_id = claims.get("user_id", String.class);
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
			
			if(request.getParameter("status").equals("Accept"))
            	preparedStatement = con.prepareStatement("update user set active = 2 where user_id ="+request.getParameter("user_id"));
            else if(request.getParameter("status").equals("Reject"))
            	preparedStatement = con.prepareStatement("update user set active = 0 where user_id ="+request.getParameter("user_id"));
            else if(request.getParameter("status").equals("Delete")) {
            	preparedStatement = con.prepareStatement("delete from user where user_id  ="+request.getParameter("user_id"));
            }else {
            	jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "FALSE");
				jsonObject.addProperty("MESSAGE", "Invalid Status for User");
				out.print(jsonObject.toString());
				return;
            }
            	
			if(preparedStatement.executeUpdate() == 0) {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "FALSE");
				jsonObject.addProperty("MESSAGE", "Error updating member status, please try after sometime");
				out.print(jsonObject.toString());
				return;	
			}else {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "TRUE");
				out.print(jsonObject.toString());
				return;	
			}
		}
		catch(Exception e) {
			jsonObject = new JsonObject();
			jsonObject.addProperty("SUCCESS", "FALSE");
			jsonObject.addProperty("MESSAGE", e.toString());
			out.print(jsonObject.toString());
		}
		finally {
			out.close();
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private void setAccessControlHeaders(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-control-allows-headers", "Content-type");
        response.addHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST, DELETE");
	  }
}
