package com.sdp;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Connection con = null;
		JsonObject jsonObject = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://34.70.183.176:3306/securess", "root", "Mysql@123");
			
			if(request.getParameter("name").length()<=100 && request.getParameter("email").length()<100 && request.getParameter("password").length()<=128)
			{
				preparedStatement = con.prepareStatement("select * from catalog where isbn ="+request.getParameter("isbn")+";");
				resultSet = preparedStatement.executeQuery();
				if(!resultSet.next()) {
					preparedStatement = con.prepareStatement("insert into user (name, email, password, active,is_admin)"
							+ " values( ?,?,?,1,0)");
					preparedStatement.setString(1,request.getParameter("name"));
					preparedStatement.setString(2, request.getParameter("email"));
					preparedStatement.setString(3, request.getParameter("password"));
					int affectedRows = preparedStatement.executeUpdate();
					if(affectedRows == 0) {
						jsonObject = new JsonObject();
						jsonObject.addProperty("SUCCESS", "FALSE");
						jsonObject.addProperty("MESSAGE", "Portal is down, please try after sometime");
						out.print(jsonObject.toString());
						out.close();
						return;	
					}else {
						jsonObject = new JsonObject();
						jsonObject.addProperty("SUCCESS", "TRUE");
						jsonObject.addProperty("MESSAGE", "Registration successful, please wait for admin approval");
						out.print(jsonObject.toString());
						out.close();
						return;	
					}
				}else {
					jsonObject = new JsonObject();
					jsonObject.addProperty("SUCCESS", "FALSE");
					jsonObject.addProperty("MESSAGE", "EMAIL IS ALREADY REGISTERED");
					out.print(jsonObject.toString());
					out.close();
					return;
				}
			}
			else {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "FALSE");
				jsonObject.addProperty("MESSAGE", "Invalid user data");
				out.print(jsonObject.toString());
				out.close();
				return;
			}
		}catch (Exception e) {
			jsonObject = new JsonObject();
			jsonObject.addProperty("SUCCESS", "FALSE");
			jsonObject.addProperty("MESSAGE", "Exception occured");
			out.print(jsonObject.toString());
		} finally {
			out.close();
			
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);
	
	}
	
	

}
