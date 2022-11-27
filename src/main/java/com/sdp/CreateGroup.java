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

/**
 * Servlet implementation class CreateGroup
 */
@WebServlet("/CreateGroup")
public class CreateGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Gson gson = new Gson();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateGroup() {
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
			preparedStatement = con.prepareStatement("insert into group_table (user_id, group_name) values(?,?);");
			preparedStatement.setString(1,request.getParameter("user_id"));
			preparedStatement.setString(2, request.getParameter("group_name"));
			int affectedRows = preparedStatement.executeUpdate();
			
//			check if user is an active user:
			preparedStatement = con.prepareStatement("select * from user where user_id ="+request.getParameter("user_id")+
					" and active = 1;");
			if(preparedStatement.executeUpdate() == 0) {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "FALSE");
				jsonObject.addProperty("MESSAGE", "Error creating group, please try after sometime");
				out.print(jsonObject.toString());
				out.close();
				return;	
			}
			
			
			if(affectedRows == 1 ) {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "TRUE");
				preparedStatement = con.prepareStatement("select * from member where user_id ="+request.getParameter("user_id"));
				ResultSet groupResultSet = preparedStatement.executeQuery();
				ArrayList<Group> groupList = new ArrayList<>();
				while(groupResultSet.next()) {
					groupList.add(new Group(
							resultSet.getLong("id"),
							resultSet.getLong("user_id"),
							resultSet.getString("name")
							));
				}
				JsonArray jarray = gson.toJsonTree(groupList).getAsJsonArray();
				jsonObject.add("GROUPLIST",jarray);
				out.print(jsonObject.toString());
				return;	
			}else {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "FALSE");
				jsonObject.addProperty("MESSAGE", "Error creating group, please try after sometime");
				out.print(jsonObject.toString());
				out.close();
				return;	
			}
			
		}
		catch(Exception e) {
			jsonObject = new JsonObject();
			jsonObject.addProperty("SUCCESS", "FALSE");
			jsonObject.addProperty("MESSAGE", "Exception occured");
			out.print(jsonObject.toString());
		}finally {
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
