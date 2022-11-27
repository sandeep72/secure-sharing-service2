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
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Entity.Group;
import Entity.User;

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
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String employeeJsonString = null;
		JsonObject jsonObject = null;
		User user = null;
		Connection con = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://34.70.183.176:3306/securess", "root", "Mysql@123");

			preparedStatement = con.prepareStatement("select * from user where email ='"+request.getParameter("email")+
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
							resultSet.getInt("is_admin")
							);
					jsonObject = new JsonObject();
					jsonObject.add("USER", this.gson.toJsonTree(user).getAsJsonObject());
					jsonObject.addProperty("SUCCESS", "TRUE");
					preparedStatement = con.prepareStatement("select * from member where user_id ="+resultSet.getLong("user_id")+";");
					ResultSet groupResultSet = preparedStatement.executeQuery();
					ArrayList<Group> groupList = new ArrayList<>();
					
					while(groupResultSet.next()) {
						groupList.add(new Group(
                                    groupResultSet.getLong("group_id"),
                                    groupResultSet.getLong("user_id"),
                                    groupResultSet.getString("name")
								));
					}
					JsonArray jarray = gson.toJsonTree(groupList).getAsJsonArray();
					jsonObject.add("GROUPLIST",jarray);
					out.print(jsonObject.toString());
					out.flush();
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);	
		
	}

}
