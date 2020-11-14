/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryrunner;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * QueryRunner takes a list of Queries that are initialized in it's constructor
 * and provides functions that will call the various functions in the QueryJDBC class 
 * which will enable MYSQL queries to be executed. It also has functions to provide the
 * returned data from the Queries. Currently the eventHandlers in QueryFrame call these
 * functions in order to run the Queries.
 */
public class QueryRunner {

    
    public QueryRunner()
    {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_error="";

        
        // You will need to put your Project Application in the below variable

        this.m_projectTeamApplication="Trail Seeker";
        //this.m_projectTeamApplication="CITYELECTION";    // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        
        // Each row that is added to m_queryArray is a separate query. It does not work on Stored procedure calls.
        // The 'new' Java keyword is a way of initializing the data that will be added to QueryArray. Please do not change
        // Format for each row of m_queryArray is: (QueryText, ParamaterLabelArray[], LikeParameterArray[], IsItActionQuery, IsItParameterQuery)
        
        //    QueryText is a String that represents your query. It can be anything but Stored Procedure
        //    Parameter Label Array  (e.g. Put in null if there is no Parameters in your query, otherwise put in the Parameter Names)
        //    LikeParameter Array  is an array I regret having to add, but it is necessary to tell QueryRunner which parameter has a LIKE Clause. If you have no parameters, put in null. Otherwise put in false for parameters that don't use 'like' and true for ones that do.
        //    IsItActionQuery (e.g. Mark it true if it is, otherwise false)
        //    IsItParameterQuery (e.g.Mark it true if it is, otherwise false)

        // Query 1
        m_queryArray.add(new QueryData(
                "Select hiker_fname, hiker_lname, SUM(distance) as full_milage " +
                "from hiker " +
                "left join hike_instance on hiker.hiker_id = hike_instance.hiker_id " +
                "left join trail on hike_instance.trail_id = trail.trail_id " +
                "where hiker.hiker_id = ?",
                new String[] {"HIKER_ID"}, new boolean[] {false}, false, true));
        // Query 2
        m_queryArray.add(new QueryData(
                "select hiker_fname, hiker_lname, sum(elevation) as full_elevation " +
                        "from hiker " +
                        "left join hike_instance on hiker.hiker_id = hike_instance.hiker_id " +
                        "left join trail on hike_instance.trail_id = trail.trail_id " +
                        "where hike_instance.hiker_id = ?",
                new String[] {"HIKER_ID"}, new boolean[] {false}, false, true));
        // Query 3
        m_queryArray.add(new QueryData(
                "select hiker_fname, hiker_lname, count(*) as full_trail " +
                        "from hiker " +
                        "left join hike_instance on hiker.hiker_id  = hike_instance.hiker_id " +
                        "group by hiker.hiker_id " +
                        "order by full_trail desc " +
                        "limit 5",
                null, null, false, false));
        // Query 4
        m_queryArray.add(new QueryData(
                "select mtn_range_name, trail_name, rating, distance, elevation, date_format(hike_instance_date, '%m/%d/%y') as date " +
                        "from hike_instance " +
                        "left join trail on hike_instance.trail_id = trail.trail_id " +
                        "left join mountain_range on trail.mtn_range_id = mountain_range.mtn_range_id " +
                        "left join rating on hike_instance.hike_instance_id = rating.hike_instance_id " +
                        "where trail.trail_id in (" +
                        "select distinct trail_id " +
                        "from hike_instance " +
                        "where hiker_id = ?) " +
                        "order by year(hike_instance_date), month(hike_instance_date)",
                new String[] {"HIKER_ID"}, new boolean[] {false}, false, true));
        // Query 5
        m_queryArray.add(new QueryData(
                "select year(invoice_date) as year, month(invoice_date) as month, sum(line_quantity*product_price) as total_product_revenue " +
                "from line " +
                "left join invoice on line.invoice_id = invoice.invoice_id " +
                "left join product on line.product_id = product.product_id " +
                "group by year, month " +
                "order by year, month",
                null, null, false, false));
        // Query 6
        m_queryArray.add(new QueryData(
                "select hiker_fname, hiker_lname, age, gender_name, subscription_type_description " +
                        "from hiker " +
                        "left join gender on hiker.gender_id = gender.gender_id " +
                        "left join subscription on hiker.hiker_id = subscription.hiker_id " +
                        "left join subscription_type on subscription.subscription_type_id " +
                        "where is_active = 1 " +
                        "order by subscription_type_description",
        null, null, false, false));
        // Query 7
        m_queryArray.add(new QueryData(
                "select mtn_range_name, trail_name, distance, elevation, state_name " +
                        "from trail " +
                        "left join state on trail.state_abbrv = state.state_abbrv " +
                        "left join mountain_range on trail.mtn_range_id = mountain_range.mtn_range_id " +
                        "where dog_friendly = 1 " +
                        "and trail_id not in (" +
                        "select distinct trail_id " +
                        "from hike_instance " +
                        "where hiker_id = ? ) " +
                        "and mountain_range.mtn_range_id = (" +
                        "select mtn_range_id " +
                        "from hiker " +
                        "where hiker_id = ? )",
        new String[] {"HIKER_ID", "ENTER AGAIN"}, new boolean[] {false, false}, false, true));
        // Query 8
        m_queryArray.add(new QueryData(
                "select mtn_range_name, trail_name, distance, elevation, state_name " +
                        "from trail " +
                        "left join state on trail.state_abbrv = state.state_abbrv " +
                        "left join mountain_range on trail.mtn_range_id = mountain_range.mtn_range_id " +
                        "where trail_id not in (" +
                        "select distinct trail_id " +
                        "from hike_instance " +
                        "where hiker_id = ? ) " +
                        "and trail.state_abbrv = (select state_abbrv from hiker where hiker_id = ? ) " +
                        "order by distance desc, elevation desc",
         new String[] {"HIKER_ID", "ENTER AGAIN"}, new boolean[] {false, false}, false, true));
        // Query 9
        m_queryArray.add(new QueryData(
                "select mtn_range_name, trail_name, avg(rating) as average_rating, count(*) as hike_count " +
                        "from hike_instance " +
                        "left join trail on hike_instance.trail_id = trail.trail_id " +
                        "left join mountain_range on trail.mtn_range_id = mountain_range.mtn_range_id " +
                        "left join rating on hike_instance.hike_instance_id = rating.hike_instance_id " +
                        "group by trail.trail_id " +
                        "order by hike_count desc " +
                        "limit 10",
        null, null, false, false));
        // Query 10
        m_queryArray.add(new QueryData(
                "select mtn_range_name, trail_name, distance, elevation, state_name, avg(rating) as average_rating " +
                        "from trail join state using (state_abbrv) " +
                        "join mountain_range using (mtn_range_id) " +
                        "join hike_instance using (trail_id) " +
                        "join rating using (hike_instance_id) " +
                        "where distance between ? and ? " +
                        "group by trail_id " +
                        "order by distance",
        new String[] {"DIST. LOWER LIMIT", "DIST. UPPER LIMIT"}, new boolean[] {false, false}, false, true));
        //m_queryArray.add(new QueryData("Select * from contact", null, null, false, false));   // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        //m_queryArray.add(new QueryData("Select * from contact where contact_id=?", new String [] {"CONTACT_ID"}, new boolean [] {false},  false, true));        // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        //m_queryArray.add(new QueryData("Select * from contact where contact_name like ?", new String [] {"CONTACT_NAME"}, new boolean [] {true}, false, true));        // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        //m_queryArray.add(new QueryData("insert into contact (contact_id, contact_name, contact_salary) values (?,?,?)",new String [] {"CONTACT_ID", "CONTACT_NAME", "CONTACT_SALARY"}, new boolean [] {false, false, false}, true, true));// THIS NEEDS TO CHANGE FOR YOUR APPLICATION
                       
    }
    //TODO remove
       //cpscproject.cntkecpiahit.us-east-1.rds.amazonaws.com

    public int GetTotalQueries()
    {
        return m_queryArray.size();
    }
    
    public int GetParameterAmtForQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetParmAmount();
    }
              
    public String  GetParamText(int queryChoice, int parmnum )
    {
       QueryData e=m_queryArray.get(queryChoice);        
       return e.GetParamText(parmnum); 
    }   

    public String GetQueryText(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    /**
     * Function will return how many rows were updated as a result
     * of the update query
     * @return Returns how many rows were updated
     */
    
    public int GetUpdateAmount()
    {
        return m_updateAmount;
    }
    
    /**
     * Function will return ALL of the Column Headers from the query
     * @return Returns array of column headers
     */
    public String [] GetQueryHeaders()
    {
        return m_jdbcData.GetHeaders();
    }
    
    /**
     * After the query has been run, all of the data has been captured into
     * a multi-dimensional string array which contains all the row's. For each
     * row it also has all the column data. It is in string format
     * @return multi-dimensional array of String data based on the resultset 
     * from the query
     */
    public String[][] GetQueryData()
    {
        return m_jdbcData.GetData();
    }

    public String GetProjectTeamApplication()
    {
        return m_projectTeamApplication;        
    }
    public boolean  isActionQuery (int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryAction();
    }
    
    public boolean isParameterQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryParm();
    }
    
     
    public boolean ExecuteQuery(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteQuery(e.GetQueryString(), parms, e.GetAllLikeParams());
        return bOK;
    }
    
     public boolean ExecuteUpdate(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        m_updateAmount = m_jdbcData.GetUpdateCount();
        return bOK;
    }   
    
      
    public boolean Connect(String szHost, String szUser, String szPass, String szDatabase)
    {

        boolean bConnect = m_jdbcData.ConnectToDatabase(szHost, szUser, szPass, szDatabase);
        if (bConnect == false)
            m_error = m_jdbcData.GetError();        
        return bConnect;
    }
    
    public boolean Disconnect()
    {
        // Disconnect the JDBCData Object
        boolean bConnect = m_jdbcData.CloseDatabase();
        if (bConnect == false)
            m_error = m_jdbcData.GetError();
        return true;
    }
    
    public String GetError()
    {
        return m_error;
    }

    public void PrintMenu() {
        System.out.println("Select query (number) to run: ");
        System.out.println("1  Total distance hiked for user");
        System.out.println("2  Total elevation hiked for user");
        System.out.println("3  Top 5 hikers with the most hikes");
        System.out.println("4  List of all trails user has hiked");
        System.out.println("5  Total product revenue");
        System.out.println("6  List of all current subscriptions");
        System.out.println("7  List of dog-friendly trails the user has not hiked");
        System.out.println("8  List of trails the user has not hiked in their state");
        System.out.println("9  List of top 10 most hiked trails");
        System.out.println("10 List of trails within given distance range");
    }
 
    private QueryJDBC m_jdbcData;
    private String m_error;    
    private String m_projectTeamApplication;
    private ArrayList<QueryData> m_queryArray;  
    private int m_updateAmount;
            
    /**
     * @param args the command line arguments
     */
    

    
    public static void main(String[] args) {
        // TODO code application logic here


        final QueryRunner queryrunner = new QueryRunner();
        
        if (args.length == 0)
        {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {

                    new QueryFrame(queryrunner).setVisible(true);
                }            
            });
        }
        else
        {
            if (args[0].equals ("-console"))
            {
                Scanner c = new Scanner(System.in);
                System.out.println("Welcome to the Trail Seeker Console!");
                System.out.println("Enter info to connect to the database.");
                System.out.println("Host name: ");
                String connection = c.nextLine();
                System.out.println("Username: ");
                String username = c.nextLine();
                System.out.println("Password: ");
                String password = c.nextLine();
                System.out.println("Database: ");
                String db = c.nextLine();

                boolean success = queryrunner.Connect(connection, username, password, db);
                if (success) {
                    int n = queryrunner.GetTotalQueries();
                    for (int i = 0; i < n; i++) { // TODO question so we loop through each query once?
                        int amt = queryrunner.GetParameterAmtForQuery(i);
                        String[] params = new String[amt]; // TODO question what to do if there are no params?
                        if (queryrunner.isParameterQuery(i)){

                            for (int j = 0; j < amt; j++) {
                                String label = queryrunner.GetParamText(i, j);
                                System.out.println("Parameter " + (j+1) + " " + label + ": ");
                                params[j] = c.nextLine();
                            }
                        }
                        if (queryrunner.isActionQuery(i)) {
                            queryrunner.ExecuteUpdate(i, params);
                            int changed = queryrunner.GetUpdateAmount();
                            System.out.println(changed + " rows were updated.");
                        } else {
                            queryrunner.ExecuteQuery(i, params);
                            String[][] result = queryrunner.GetQueryData();
                            String[] header = queryrunner.GetQueryHeaders();
                            for (int h = 0; h < header.length; h++) {
                                System.out.print(header[h] + " | ");
                            }
                            System.out.println();
                            for (int k = 0; k < result.length; k++) {
                                for (int l = 0; l < result[k].length; l++) {
                                    System.out.print(result[k][l] + " | ");
                                }
                                System.out.println();
                            }
                        }
                        System.out.println();
                    }
                    System.out.println("Thanks for using Trail Seeker Console!");
                    boolean disconnect_sucess = queryrunner.Disconnect();
                    if (!disconnect_sucess) {
                        System.out.println(queryrunner.GetError());
                    }

                } else {
                    System.out.println(queryrunner.GetError());
                }


            	// System.out.println("Nothing has been implemented yet. Please implement the necessary code");
               // TODO 
                // You should code the following functionality:

                //    You need to determine if it is a parameter query. If it is, then
                //    you will need to ask the user to put in the values for the Parameters in your query
                //    you will then call ExecuteQuery or ExecuteUpdate (depending on whether it is an action query or regular query)
                //    if it is a regular query, you should then get the data by calling GetQueryData. You should then display this
                //    output. 
                //    If it is an action query, you will tell how many row's were affected by it.
                // 
                //    This is Psuedo Code for the task:  
                //    Connect()
                //    n = GetTotalQueries()
                //    for (i=0;i < n; i++)
                //    {
                //       Is it a query that Has Parameters
                //       Then
                //           amt = find out how many parameters it has
                //           Create a paramter array of strings for that amount
                //           for (j=0; j< amt; j++)
                //              Get The Paramater Label for Query and print it to console. Ask the user to enter a value
                //              Take the value you got and put it into your parameter array
                //           If it is an Action Query then
                //              call ExecuteUpdate to run the Query
                //              call GetUpdateAmount to find out how many rows were affected, and print that value
                //           else
                //               call ExecuteQuery 
                //               call GetQueryData to get the results back
                //               print out all the results
                //           end if
                //      }
                //    Disconnect()


                // NOTE - IF THERE ARE ANY ERRORS, please print the Error output
                // NOTE - The QueryRunner functions call the various JDBC Functions that are in QueryJDBC. If you would rather code JDBC
                // functions directly, you can choose to do that. It will be harder, but that is your option.
                // NOTE - You can look at the QueryRunner API calls that are in QueryFrame.java for assistance. You should not have to 
                //    alter any code in QueryJDBC, QueryData, or QueryFrame to make this work.
//                System.out.println("Please write the non-gui functionality");
                
            }
        }
 
    }    
}
