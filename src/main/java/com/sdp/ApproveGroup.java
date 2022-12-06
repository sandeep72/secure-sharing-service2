package com.sdp;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

/**
 * Servlet implementation class ApproveGroup
 */
@WebServlet("/ApproveGroup")
public class ApproveGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApproveGroup() {
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
			
			preparedStatement = con.prepareStatement("select * from user where type = 'admin' and user_id ="+user_id);
			resultSet =  preparedStatement.executeQuery();
            if( !resultSet.next()) {
				jsonObject = new JsonObject();
				jsonObject.addProperty("SUCCESS", "FALSE");
				jsonObject.addProperty("MESSAGE", "Only Admin can approve new groups");
				out.print(jsonObject.toString());
				return;	
			}
			
            if(request.getParameter("status").equals("Accept"))
            	preparedStatement = con.prepareStatement("update group_table set active = 1 where group_id ="+request.getParameter("group_id"));
            else if(request.getParameter("status").equals("Reject"))
            	preparedStatement = con.prepareStatement("update group_table set active = 0 where group_id ="+request.getParameter("group_id"));
            else if(request.getParameter("status").equals("Delete")) {
            	preparedStatement = con.prepareStatement("delete from group_table, member  where group_table.group_id = member.group.id and group_id ="+request.getParameter("group_id"));
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
