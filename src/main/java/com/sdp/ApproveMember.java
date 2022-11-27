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

import Entity.Group;
import Entity.User;

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
			preparedStatement = con.prepareStatement("select * from user where is_admin = 1 and user_id ="+request.getParameter("admin_id"));
			if(preparedStatement.executeUpdate() == 0) {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "FALSE");
				jsonObject.addProperty("MESSAGE", "Only Admin can approve new member to application");
				out.print(jsonObject.toString());
				return;	
			}
			
			preparedStatement = con.prepareStatement("update user set active = 1 where user_id ="+request.getParameter("user_id"));
			if(preparedStatement.executeUpdate() == 0) {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "FALSE");
				jsonObject.addProperty("MESSAGE", "Error updating member status, please try after sometime");
				out.print(jsonObject.toString());

				return;	
			}else {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "TRUE");
				
				preparedStatement = con.prepareStatement("select * from user where active = 0;");
				ResultSet userResultSet = preparedStatement.executeQuery();
				ArrayList<User> userList = new ArrayList<>();
				while(userResultSet.next()) {
					userList.add(new User(
							userResultSet.getLong("id"),
							userResultSet.getString("name"),
							userResultSet.getString("email"),
							"DUMMY",
							userResultSet.getInt("active"),
							userResultSet.getInt("is_admin")
							));
				}
				JsonArray jarray = gson.toJsonTree(userList).getAsJsonArray();
				jsonObject.add("USERLIST",jarray);
				out.print(jsonObject.toString());
				return;	
			}
		}
		catch(Exception e) {
			jsonObject = new JsonObject();
			jsonObject.addProperty("SUCCESS", "FALSE");
			jsonObject.addProperty("MESSAGE", "Exception occured");
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

}
