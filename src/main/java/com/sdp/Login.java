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
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession();
		
		ArrayList<User> user= new ArrayList<>();
		
		
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://:3306/securess", "root", "");
			PreparedStatement preparedStatement = con.prepareStatement("select * from User;");
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				user.add(new User(
						Long.parseLong(resultSet.getString("id")),
						resultSet.getString("name"),
						resultSet.getString("email"),
						resultSet.getString("password"),
						resultSet.getInt("active"),
						resultSet.getInt("is_admin")
						));
			}
			PrintWriter out = response.getWriter();
			String json = new Gson().toJson(user);
			out.print(json);
			out.flush();
		    
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
//			out.close();
		}
		
		
	}

}
