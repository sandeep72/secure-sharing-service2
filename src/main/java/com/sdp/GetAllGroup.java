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
 * Servlet implementation class GetAllGroup
 */
@WebServlet("/GetAllGroup")
public class GetAllGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllGroup() {
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
		String employeeJsonString = null;
		JsonObject jsonObject = null;
		User user = null;
		Connection con = null;
		Gson gson = new Gson();
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://34.70.183.176:3306/securess", "root", "Mysql@123");
			
			preparedStatement = con.prepareStatement("select * from user where user_id ="+request.getParameter("user_id")+
					" and active = 2;");
            resultSet = preparedStatement.executeQuery();
			if(!resultSet.next()) {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "FALSE");
				jsonObject.addProperty("MESSAGE", "User is inactive");
				out.print(jsonObject.toString());
				out.close();
				return;	
			}
			boolean admin = resultSet.getString("type").equals("admin") ? true: false;
			
			jsonObject = new JsonObject();
			jsonObject.addProperty("SUCCESS", "TRUE");
			if(admin) {
			preparedStatement = con.prepareStatement("select user.name as user_name, group_id, group_table.active as active, group_name from user, group_table where user.user_id = group_table.user_id; ");
			}else {
			preparedStatement = con.prepareStatement("select member.group_id as group_id, member.name as group_name, T.user_name as user_name,T.active as active from member, "+
			" (select user.name as user_name, user.user_id as user_id, group_id, group_table.active as active from user, group_table where user.user_id = group_table.user_id and group_table.active = 1) T "
			+" where member.group_id = T.group_id and "
			+ " member.user_id ="+request.getParameter("user_id"));	
			}
			ResultSet groupResultSet = preparedStatement.executeQuery();
			ArrayList<Group> groupList = new ArrayList<>();
			while(groupResultSet.next()) {
				groupList.add(new Group(
						groupResultSet.getLong("group_id"),
						groupResultSet.getString("user_name"),
						groupResultSet.getString("group_name"),
						groupResultSet.getInt("active")
						));
			}
			JsonArray jarray = gson.toJsonTree(groupList).getAsJsonArray();
			jsonObject.add("GROUPLIST",jarray);
			out.print(jsonObject.toString());
			return;
			
			
		}catch(Exception e) {
			jsonObject = new JsonObject();
			jsonObject.addProperty("SUCCESS", "FALSE");
			jsonObject.addProperty("MESSAGE", e.toString());
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

	private void setAccessControlHeaders(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-control-allows-headers", "Content-type");
        response.addHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST, DELETE");
	  }
}
