package com.view.servlet;

import com.octetstring.vde.util.Base64;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.json.JSONObject;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Timestamp;

import javax.naming.Context;

import javax.naming.InitialContext;

import javax.naming.NamingException;

import javax.servlet.http.HttpSession;

import javax.sql.DataSource;

import org.json.JSONException;


public class DashBoardServlet extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
    private static final String TASK_FLOW_ID ="faces/FilmStrip";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = null;
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>DashBoard Servlet</title></head>");
        out.println("<body>");
        String url = request.getRequestURL().toString();
        String subUrl =url.substring(0, url.lastIndexOf("/"));
        String jwtToken = request.getParameter("jwt");
        HttpSession session = request.getSession();
        if (jwtToken != null) { 
            session.setAttribute("jwt", jwtToken); 
        }else{
        }
        session.setAttribute("comingFrom","SAAS");
        path = subUrl + "/" + TASK_FLOW_ID;
        System.err.println("path====="+path);
        response.sendRedirect(path);
        out.println("<p>Redirecting to Dashboard taskflow</p>");
        out.println("</body></html>");
        out.close();
    }
}

